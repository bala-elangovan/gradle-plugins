import java.util.Properties

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm")
}

group = "io.github.platform"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val springBootVersion: String by project
val springDependencyManagementVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$springDependencyManagementVersion")
}

gradlePlugin {
    plugins {
        register("javaConventions") {
            id = "io.github.platform.java-conventions"
            implementationClass = "io.github.platform.gradle.JavaConventionsPlugin"
            displayName = "Java Conventions Plugin"
            description = "Provides base Java configuration with Lombok, Checkstyle (Google Style), and JaCoCo"
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
                appendLine("    const val CHECKSTYLE = \"${props.getProperty("checkstyleVersion")}\"")
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
