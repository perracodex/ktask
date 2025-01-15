/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

group = "taskmanager.core"
version = "1.0.0"

dependencies {
    api(project(":taskmanager-core:base"))
    api(project(":taskmanager-core:database"))
    api(project(":taskmanager-core:scheduler"))
}
