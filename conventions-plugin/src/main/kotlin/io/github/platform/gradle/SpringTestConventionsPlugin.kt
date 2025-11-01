package io.github.platform.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that adds Spring Boot test dependencies and configuration.
 *
 * This plugin provides a comprehensive testing setup for Spring Boot applications including
 * - Spring Boot Starter Test (includes JUnit 5, Mockito, AssertJ, etc.)
 * - JUnit Platform Launcher for IDE integration
 * - MockK for Kotlin-style mocking (works with Java too)
 * - Proper logging exclusions to avoid conflicts
 *
 * This plugin automatically applies `io.github.platform.java-conventions` to inherit
 * base Java configuration including JUnit Platform setup.
 *
 * **Usage: **
 * ```
 * plugins {
 *     id("io.github.platform.spring-test-conventions")
 * }
 * ```
 *
 * **Configured Dependencies: **
 * Dependency versions are managed in `gradle.properties` at the project root.
 *
 * **What's Included in spring-boot-starter-test:**
 * - JUnit 5 (Jupiter)
 * - Spring Test & Spring Boot Test
 * - AssertJ - fluent assertion library
 * - Hamcrest - matcher library
 * - Mockito - mocking framework
 * - JSONassert - JSON assertion library
 * - JsonPath - JSON path expressions
 */
class SpringTestConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyBaseConventions()
            addTestDependencies()
            excludeConflictingDependencies()
        }
    }

    /** Applies Java conventions plugin for base configuration. */
    private fun Project.applyBaseConventions() {
        pluginManager.apply("io.github.platform.java-conventions")
    }

    /** Adds Spring Boot Starter Test, MockK, and JUnit Platform Launcher dependencies. */
    private fun Project.addTestDependencies() {
        dependencies {
            // Spring Boot Starter Test - comprehensive testing support
            add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${GeneratedVersions.SPRING_BOOT}")

            // MockK - powerful mocking library for Kotlin and Java
            add("testImplementation", "io.mockk:mockk:${GeneratedVersions.MOCKK}")

            // JUnit Platform Launcher - required for IDE integration
            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }
    }

    /** Excludes conflicting logging dependencies to avoid conflicts with Log4j2. */
    private fun Project.excludeConflictingDependencies() {
        // Exclude logging implementations that conflict with Log4j2
        // This is important when projects use spring-boot-starter-log4j2
        configurations.all {
            exclude(mapOf("group" to "ch.qos.logback", "module" to "logback-classic"))
            exclude(mapOf("group" to "org.apache.logging.log4j", "module" to "log4j-to-slf4j"))
            exclude(mapOf("module" to "spring-boot-starter-logging"))
        }
    }
}
