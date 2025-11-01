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

// Read versions from gradle.properties
val springBootVersion: String by project
val dependencyManagementVersion: String by project

dependencies {
    // Spring Boot Gradle Plugin
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")

    // Spring Dependency Management Plugin
    implementation("io.spring.gradle:dependency-management-plugin:$dependencyManagementVersion")
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

        register("springConventions") {
            id = "io.github.platform.spring-conventions"
            implementationClass = "io.github.platform.gradle.SpringConventionsPlugin"
            displayName = "Spring Boot Conventions Plugin"
            description =
                "Provides complete Spring Boot application configuration with MapStruct and strict dependency management"
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

// Task to display current dependency versions
tasks.register("displayVersions") {
    group = "help"
    description = "Displays current dependency versions from gradle.properties"

    doLast {
        println("\nCurrent Dependency Versions (from gradle.properties):")
        println("=====================================================\n")

        println("Build Plugins:")
        println("  Spring Boot Plugin:          ${project.property("springBootVersion")}")
        println("  Dependency Management:       ${project.property("dependencyManagementVersion")}")

        println("\nJava Dependencies:")
        println("  Lombok:                      ${project.property("lombokVersion")}")
        println("  Checkstyle (Google Style):   ${project.property("checkstyleVersion")}")
        println("  JaCoCo:                      ${project.property("jacocoVersion")}")

        println("\nSpring Dependencies:")
        println("  Jackson:                     ${project.property("jacksonVersion")}")
        println("  MapStruct:                   ${project.property("mapstructVersion")}")
        println("  MockK:                       ${project.property("mockkVersion")}")

        println("\n💡 To update versions, edit gradle.properties\n")
    }
}