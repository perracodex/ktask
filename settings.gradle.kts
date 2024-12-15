pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // https://github.com/gradle/foojay-toolchains
    // https://github.com/gradle/foojay-toolchains/tags
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "KTask"

include("ktask-core:base")
include("ktask-core:database")
include("ktask-core:scheduler")
include("ktask-core")
include("ktask-notification")
include("ktask-server")
