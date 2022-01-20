// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin(Kotlin.jvmId)
    kotlin(Kotlin.kaptId)
    dokka(Kotlin.dokkaId) version Kotlin.version
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Kotlin.stdlibJdk8)
    implementation(Logger.core)
    implementation(Logger.api)
    testImplementation(kotlin("test"))
    testImplementation(platform(Junit.platform))
    testImplementation(Junit.core)
    kapt(Logger.core)
    dokkaPlugin(Kotlin.dokka)
}

java.withSourcesJar()

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("The Log4J Appender for the JTextArea")
        }
    }
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-doc")
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = project.properties["ossrhUsername"] as String? ?: "Unknown user"
                password = project.properties["ossrhPassword"] as String? ?: "Unknown pass"
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            version = project.version.toString()
            groupId = project.group.toString()
            artifactId = "log4j-textarea-appender"
            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)
        }
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("flatlaf-themes-combobox-model")
                description.set("The Log4J Appender for the JTextArea.")
                url.set("https://github.com/kolod/log4j-textarea-appender")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://raw.githubusercontent.com/kolod/log4j-textarea-appender/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("Kolod")
                        name.set("Oleksandr Kolodkin")
                        email.set("alexandr.kolodkin@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/kolod/log4j-textarea-appender.git")
                    developerConnection.set("scm:git:ssh:git@github.com:kolod/log4j-textarea-appender.git")
                    url.set("https://github.com/kolod/log4j-textarea-appender")
                }
            }
        }
    }
}

configure<SigningExtension> {
    val signingKeyId = project.properties["signing.keyId"] as String?
    val signingPassword = project.properties["signing.password"] as String?
    val signingSecretKeyRingFile = project.properties["signing.secretKeyRingFile"] as String?

    isRequired = if (
        !signingKeyId.isNullOrEmpty() and
        !signingPassword.isNullOrEmpty() and
        !signingSecretKeyRingFile.isNullOrEmpty()
    ) {
        logger.info("Signing key id provided. Sign artifacts for $project.")
        true
    } else {
        logger.info("${project.name}: Signing key not provided. Disable signing for  $project.")
        false
    }
}

signing {
    sign(publishing.publications)
}
