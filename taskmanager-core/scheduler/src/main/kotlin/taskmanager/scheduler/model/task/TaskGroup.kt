/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.scheduler.model.task

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Represents a task group.
 *
 * @property groupId The group of the task.
 * @property description The description of the task.
 */
@Serializable
public data class TaskGroup internal constructor(
    val groupId: Uuid,
    val description: String
)
