import com.diffplug.gradle.spotless.SpotlessExtension
import java.util.Properties

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm")
    id("com.diffplug.spotless") version "8.0.0"
}

group = "io.github.platform"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val springBootVersion: String by project
val springDependencyManagementVersion: String by project
val spotlessVersion: String by project
val kotlinVersion = "2.2.20"

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$springDependencyManagementVersion")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}

gradlePlugin {
    plugins {
        register("javaConventions") {
            id = "io.github.platform.java-conventions"
            implementationClass = "io.github.platform.gradle.JavaConventionsPlugin"
            displayName = "Java Conventions Plugin"
            description = "Provides base Java and Kotlin configuration with Lombok, Spotless (google-java-format, ktlint), and JaCoCo"
        }

        register("springTestConventions") {
            id = "io.github.platform.spring-test-conventions"
            implementationClass = "io.github.platform.gradle.SpringTestConventionsPlugin"
            displayName = "Spring Test Conventions Plugin"
            description = "Provides Spring Boot test dependencies and configuration"
        }

        register("springCoreConventions") {
            id = "io.github.platform.spring-core-conventions"
            implementationClass = "io.github.platform.gradle.SpringCoreConventionsPlugin"
            displayName = "Spring Core Conventions Plugin"
            description = "Provides core Spring Boot configuration with MapStruct"
        }

        register("springWebConventions") {
            id = "io.github.platform.spring-web-conventions"
            implementationClass = "io.github.platform.gradle.SpringWebConventionsPlugin"
            displayName = "Spring Web Conventions Plugin"
            description = "Provides Spring Boot Web (MVC) configuration"
        }

        register("springWebFluxConventions") {
            id = "io.github.platform.spring-webflux-conventions"
            implementationClass = "io.github.platform.gradle.SpringWebFluxConventionsPlugin"
            displayName = "Spring WebFlux Conventions Plugin"
            description = "Provides Spring Boot WebFlux (Reactive) configuration"
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
    val propsFile = rootProject.file("gradle.properties")
    val outputFile = file("src/main/kotlin/io/github/platform/gradle/GeneratedVersions.kt")

    inputs.file(propsFile)
    outputs.file(outputFile)

    doLast {
        val props = Properties()
        propsFile.inputStream().use { props.load(it) }

        outputFile.parentFile.mkdirs()

        val content =
            buildString {
                appendLine("// AUTO-GENERATED - DO NOT EDIT")
                appendLine("// Generated from gradle.properties")
                appendLine("package io.github.platform.gradle")
                appendLine()
                appendLine("internal object GeneratedVersions {")
                appendLine("    const val SPRING_BOOT = \"${props.getProperty("springBootVersion")}\"")
                appendLine("    const val SPRING_DEPENDENCY_MANAGEMENT = \"${props.getProperty("springDependencyManagementVersion")}\"")
                appendLine("    const val LOMBOK = \"${props.getProperty("lombokVersion")}\"")
                appendLine("    const val JACKSON = \"${props.getProperty("jacksonVersion")}\"")
                appendLine("    const val MAPSTRUCT = \"${props.getProperty("mapstructVersion")}\"")
                appendLine("    const val COMMONS_LANG3 = \"${props.getProperty("commonsLang3Version")}\"")
                appendLine("    const val SPOTLESS = \"${props.getProperty("spotlessVersion")}\"")
                appendLine("    const val GOOGLE_JAVA_FORMAT = \"${props.getProperty("googleJavaFormatVersion")}\"")
                appendLine("    const val KTLINT = \"${props.getProperty("ktlintVersion")}\"")
                appendLine("    const val JACOCO = \"${props.getProperty("jacocoVersion")}\"")
                appendLine("    const val MOCKK = \"${props.getProperty("mockkVersion")}\"")
                appendLine("    const val SWAGGER_ANNOTATIONS = \"${props.getProperty("swaggerAnnotationsVersion")}\"")
                appendLine("    const val SPRINGDOC_OPENAPI = \"${props.getProperty("springdocOpenapiVersion")}\"")
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
        ktlint("1.7.1")
            .editorConfigOverride(
                mapOf(
                    "indent_size" to "4",
                    "max_line_length" to "150",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
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
