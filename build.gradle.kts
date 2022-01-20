// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Kotlin.jvmId) version Kotlin.version
    kotlin(Kotlin.kaptId) version Kotlin.version
}

allprojects {
    description = "The Log4J Appender for the JTextArea"
    group = "io.github.kolod"
    version = project.properties["version"] as String? ?: "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = Jvm.version
    }
}
