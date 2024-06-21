/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.utils

import io.ktor.http.content.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ktask.base.env.Tracer
import java.io.File

/**
 * A generic handler for multipart form data processing.
 *
 * Sample usage:
 * ```
 * fun Route.multipartTaskRouteExample() {
 *
 *     post("some-endpoint") {
 *         val multipart: MultiPartData = call.receiveMultipart()
 *
 *         MultipartHandler <SomeRequest>().receive(
 *             multipart = multipart,
 *             serializer = SomeRequest.serializer()
 *         ).let { response ->
 *             response.request?.let { request ->
 *                 SomeService.doSomething(request = request)
 *
 *                 call.respond(
 *                     status = HttpStatusCode.OK,
 *                     message = "Some message."
 *                 )
 *             } ?: call.respond(
 *                 status = HttpStatusCode.BadRequest,
 *                 message = "Invalid request."
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * @param T The type of the object expected in the request part of the form.
 * @property uploadsPath Optional path where uploaded files are stored.
 */
class MultipartHandler<T : Any>(private val uploadsPath: String = DEFAULT_UPLOADS_PATH) {
    private val tracer = Tracer<MultipartHandler<T>>()

    init {
        verifyPath()
    }

    /**
     * Data class to encapsulate the response from processing multipart data.
     *
     * @property request The parsed request object of type T.
     * @property files A list of [FileDetails] objects containing the file description and file.
     */
    data class MultipartResponse<T>(
        val request: T?,
        val files: List<FileDetails>
    )

    /**
     * Handles parsing multipart form data to extract a request object, a file description, and a file.
     *
     * @param multipart The MultiPartData from a client's request.
     * @param serializer The serializer for the type T, used to parse the request object.
     * @return A [MultipartResponse] object containing the parsed data.
     */
    suspend fun receive(multipart: MultiPartData, serializer: KSerializer<T>): MultipartResponse<T> {
        var requestObject: T? = null
        val requestData = mutableMapOf<String, String>()
        val fileDetailsList = mutableListOf<FileDetails>()
        var fileDescription: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        REQUEST_KEY -> {
                            requestObject = Json.decodeFromString(
                                deserializer = serializer,
                                string = part.value
                            )
                        }

                        FILE_DESCRIPTION_KEY -> {
                            fileDescription = part.value
                        }

                        else -> {
                            requestData[part.name!!] = part.value
                        }
                    }
                }

                is PartData.FileItem -> {
                    if (part.name!!.startsWith(FILE_KEY)) {
                        val file = File("$uploadsPath/${part.originalFileName}")

                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }

                        val fileDetails = FileDetails(description = fileDescription, file = file)
                        fileDetailsList.add(fileDetails)
                        fileDescription = null // Reset for subsequent files.
                    } else {
                        tracer.warning("Unexpected file item received: ${part.name}")
                    }
                }

                else -> {
                    tracer.warning("Unknown part type: $part")
                }
            }

            part.dispose()
        }

        // If requestObject is not set by 'request' key, try to decode from separate keys.
        if (requestObject == null && requestData.isNotEmpty()) {
            try {
                val encodedJson: String = Json.encodeToString(requestData)
                requestObject = Json.decodeFromString(
                    deserializer = serializer,
                    string = encodedJson
                )
            } catch (e: SerializationException) {
                throw IllegalArgumentException("Error deserializing request data: $e")
            }
        }

        return MultipartResponse(
            request = requestObject,
            files = fileDetailsList
        )
    }

    /**
     * Verifies the path where uploaded files are stored and creates it if it does not exist.
     */
    private fun verifyPath() {
        if (uploadsPath.isBlank()) {
            throw IllegalArgumentException("The uploads path cannot be empty.")
        }

        File(uploadsPath).let { path ->
            if (!path.exists()) {
                path.mkdirs()
            }
        }
    }

    companion object {
        /** The path where uploaded files are stored. */
        private const val DEFAULT_UPLOADS_PATH = "uploads"

        /** The key for the request part in the multipart form data. */
        private const val REQUEST_KEY = "request"

        /** The key for the file description part in the multipart form data. */
        private const val FILE_DESCRIPTION_KEY = "file-description"

        /** The key for the file part in the multipart form data. */
        private const val FILE_KEY = "file"
    }
}

/**
 * Data class to encapsulate the details of a file uploaded via multipart form data.
 *
 * @property description The description of the file, if provided.
 * @property file The File object representing the uploaded file.
 */
data class FileDetails(
    val description: String?,
    val file: File?
)
