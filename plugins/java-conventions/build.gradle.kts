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
    implementation(libs.spotless)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("javaConventions") {
            id = "io.github.balaelangovan.java-conventions"
            implementationClass = "io.github.balaelangovan.gradle.JavaConventionsPlugin"
            displayName = "Java Conventions Plugin"
            description = "Provides base Java and Kotlin configuration with Lombok, Spotless (google-java-format, ktlint), and JaCoCo"
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
    val catalogFile = rootProject.file("gradle/libs.versions.toml")
    val outputFile = file("src/main/kotlin/io/github/balaelangovan/gradle/GeneratedVersions.kt")

    inputs.file(catalogFile)
    outputs.file(outputFile)

    doLast {
        val versions = mutableMapOf<String, String>()
        var inVersionsSection = false

        catalogFile.readLines().forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("[versions]") -> inVersionsSection = true
                trimmed.startsWith("[") -> inVersionsSection = false
                inVersionsSection && trimmed.contains("=") -> {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim().removeSurrounding("\"")
                        versions[key] = value
                    }
                }
            }
        }

        outputFile.parentFile.mkdirs()

        val content =
            buildString {
                appendLine("// AUTO-GENERATED - DO NOT EDIT")
                appendLine("package io.github.balaelangovan.gradle")
                appendLine()
                appendLine("object GeneratedVersions {")
                appendLine("    const val LOMBOK = \"${versions["lombok"]}\"")
                appendLine("    const val COMMONS_LANG3 = \"${versions["commons-lang3"]}\"")
                appendLine("    const val GOOGLE_JAVA_FORMAT = \"${versions["google-java-format"]}\"")
                appendLine("    const val KTLINT = \"${versions["ktlint"]}\"")
                appendLine("    const val JACOCO = \"${versions["jacoco"]}\"")
                appendLine("}")
            }

        outputFile.writeText(content)
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
