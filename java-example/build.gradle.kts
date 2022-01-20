// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    java
    shadow(Shadow.id) version Shadow.version
}

project.ext {
    set("mainClassName", "io.github.kolod.JavaExample")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(Kolod.appender)
    implementation(Logger.core)
    implementation(Logger.api)
    implementation(Logger.slf4j)
    implementation(FlatLookAndFeel.core)
    annotationProcessor(Logger.core)

    constraints {
        implementation("org.apache.logging.log4j:log4j-core") {
            version {
                strictly("[2.17.1, 3[")
                prefer("2.17.1")
            }
            because("CVE-2021-44228: Log4j vulnerable to remote code execution")
        }
    }
}

application {
    mainClass.set(project.ext["mainClassName"] as String)
}

tasks.withType<ShadowJar> {
    transform(Log4j2PluginsCacheFileTransformer())
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
