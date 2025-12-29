import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

group = "io.github.balaelangovan"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(project(":plugins:java-conventions"))
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management)
}

gradlePlugin {
    plugins {
        register("springTestConventions") {
            id = "io.github.balaelangovan.spring-test-conventions"
            implementationClass = "io.github.balaelangovan.gradle.spring.SpringTestConventionsPlugin"
            displayName = "Spring Test Conventions Plugin"
            description = "Provides Spring Boot test dependencies and configuration"
        }

        register("springCoreConventions") {
            id = "io.github.balaelangovan.spring-core-conventions"
            implementationClass = "io.github.balaelangovan.gradle.spring.SpringCoreConventionsPlugin"
            displayName = "Spring Core Conventions Plugin"
            description = "Provides core Spring Boot configuration with MapStruct"
        }

        register("springWebConventions") {
            id = "io.github.balaelangovan.spring-web-conventions"
            implementationClass = "io.github.balaelangovan.gradle.spring.SpringWebConventionsPlugin"
            displayName = "Spring Web Conventions Plugin"
            description = "Provides Spring Boot Web (MVC) configuration with spring-commons auto-configuration"
        }

        register("springWebFluxConventions") {
            id = "io.github.balaelangovan.spring-webflux-conventions"
            implementationClass = "io.github.balaelangovan.gradle.spring.SpringWebFluxConventionsPlugin"
            displayName = "Spring WebFlux Conventions Plugin"
            description = "Provides Spring Boot WebFlux (Reactive) configuration with spring-commons auto-configuration"
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

val generateVersions by tasks.registering {
    val outputFile = file("src/main/kotlin/io/github/balaelangovan/gradle/spring/SpringConventionsVersions.kt")

    outputs.file(outputFile)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            // AUTO-GENERATED - DO NOT EDIT
            package io.github.balaelangovan.gradle.spring

            object SpringConventionsVersions {
                const val SPRING_BOOT = "${libs.versions.spring.boot.get()}"
                const val MAPSTRUCT = "${libs.versions.mapstruct.get()}"
                const val MOCKK = "${libs.versions.mockk.get()}"
                const val GROOVY = "${libs.versions.groovy.get()}"
                const val SPOCK = "${libs.versions.spock.get()}"
                const val SPRING_COMMONS = "${libs.versions.spring.commons.get()}"
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

tasks.named("spotlessKotlin") {
    dependsOn(generateVersions)
}

tasks.named("spotlessKotlinCheck") {
    dependsOn(generateVersions)
}
