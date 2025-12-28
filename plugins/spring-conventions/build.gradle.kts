import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

group = "io.github.gobelango"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// Platform Commons version - update this when platform-commons is released
val platformCommonsVersion = "1.0.0-SNAPSHOT"

dependencies {
    // Dependency on java-conventions for plugin class references
    // This allows Spring plugins to apply JavaConventionsPlugin by class
    implementation(project(":plugins:java-conventions"))

    // Spring Boot dependencies
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management)

    // Note: Platform Commons and other dependencies are NOT required at compile time
    // They are injected as strings into user projects at runtime when plugins are applied
}

gradlePlugin {
    plugins {
        register("springTestConventions") {
            id = "io.github.gobelango.spring-test-conventions"
            implementationClass = "io.github.gobelango.gradle.spring.SpringTestConventionsPlugin"
            displayName = "Spring Test Conventions Plugin"
            description = "Provides Spring Boot test dependencies and configuration"
        }

        register("springCoreConventions") {
            id = "io.github.gobelango.spring-core-conventions"
            implementationClass = "io.github.gobelango.gradle.spring.SpringCoreConventionsPlugin"
            displayName = "Spring Core Conventions Plugin"
            description = "Provides core Spring Boot configuration with MapStruct"
        }

        register("springWebConventions") {
            id = "io.github.gobelango.spring-web-conventions"
            implementationClass = "io.github.gobelango.gradle.spring.SpringWebConventionsPlugin"
            displayName = "Spring Web Conventions Plugin"
            description = "Provides Spring Boot Web (MVC) configuration with platform-commons auto-configuration"
        }

        register("springWebFluxConventions") {
            id = "io.github.gobelango.spring-webflux-conventions"
            implementationClass = "io.github.gobelango.gradle.spring.SpringWebFluxConventionsPlugin"
            displayName = "Spring WebFlux Conventions Plugin"
            description = "Provides Spring Boot WebFlux (Reactive) configuration with platform-commons auto-configuration"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

kotlin {
    jvmToolchain(21)
}

// Generate version constants from the version catalog
val generateVersions by tasks.registering {
    val outputFile = file("src/main/kotlin/io/github/gobelango/gradle/spring/SpringConventionsVersions.kt")

    outputs.file(outputFile)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            // AUTO-GENERATED - DO NOT EDIT
            // This file is generated from gradle/libs.versions.toml
            package io.github.gobelango.gradle.spring

            /**
             * Dependency versions for Spring conventions plugins.
             * These versions are injected into user projects at runtime.
             */
            object SpringConventionsVersions {
                // Spring Boot
                const val SPRING_BOOT = "${libs.versions.spring.boot.get()}"

                // Core Libraries
                const val MAPSTRUCT = "${libs.versions.mapstruct.get()}"

                // Testing
                const val MOCKK = "${libs.versions.mockk.get()}"
                const val GROOVY = "${libs.versions.groovy.get()}"
                const val SPOCK = "${libs.versions.spock.get()}"

                // Platform Commons
                const val PLATFORM_COMMONS = "$platformCommonsVersion"
            }

            """.trimIndent(),
        )
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateVersions)
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
    }
}

configure<SpotlessExtension> {
    kotlin {
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(
                mapOf(
                    "indent_size" to "4",
                    "max_line_length" to "120",
                    "ij_kotlin_allow_trailing_comma" to "true",
                    "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                ),
            )
        target("src/**/*.kt", "**/*.kts")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// Ensure Spotless runs after version generation
tasks.named("spotlessKotlin") {
    dependsOn(generateVersions)
}

tasks.named("spotlessKotlinCheck") {
    dependsOn(generateVersions)
}
