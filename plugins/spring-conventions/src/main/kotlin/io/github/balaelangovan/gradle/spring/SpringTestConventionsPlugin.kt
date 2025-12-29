package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that adds Spring Boot test dependencies and configuration.
 *
 * This plugin provides a comprehensive testing setup for Spring Boot applications including
 * - Spring Boot Starter Test (includes JUnit 5, Mockito, AssertJ, etc.)
 * - Spring Boot WebMVC Test (MockMvc testing support)
 * - Spring Boot WebFlux Test (WebTestClient for reactive testing)
 * - Spring Boot RestClient Test (RestClient testing support)
 * - Spring Boot WebClient Test (WebClient testing support)
 * - JUnit Platform Launcher for IDE integration
 * - MockK for Kotlin-style mocking (works with Java too)
 * - Spock Framework for BDD-style testing with Groovy
 * - Groovy support for writing expressive tests
 * - Proper logging exclusions to avoid conflicts
 *
 * This plugin automatically applies `io.github.balaelangovan.java-conventions` to inherit
 * base Java configuration including JUnit Platform setup.
 *
 * **Usage: **
 * ```
 * plugins {
 *     id("io.github.balaelangovan.spring-test-conventions")
 * }
 * ```
 *
 * **Configured Dependencies: **
 * Dependency versions are managed in `gradle/libs.versions.toml`.
 *
 * **What's Included in spring-boot-starter-test:**
 * - JUnit 5 (Jupiter)
 * - Spring Test & Spring Boot Test
 * - AssertJ - fluent assertion library
 * - Hamcrest - matcher library
 * - Mockito - mocking framework
 * - JSONassert - JSON assertion library
 * - JsonPath - JSON path expressions
 *
 * **Additional Test Starters:**
 * - spring-boot-starter-webmvc-test - MockMvc for testing Spring MVC controllers
 * - spring-boot-starter-webflux-test - WebTestClient for testing reactive endpoints
 * - spring-boot-starter-restclient-test - RestClient testing utilities
 * - spring-boot-starter-webclient-test - WebClient testing utilities
 *
 * **Spock Framework:**
 * - spock-core - BDD-style testing with given-when-then syntax
 * - spock-spring - Spring Boot integration for @SpringBootTest
 * - Data-driven testing with where: blocks
 * - Expressive mocking and stubbing
 */
class SpringTestConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyBaseConventions()
            applyGroovyPlugin()
            addTestDependencies()
            excludeConflictingDependencies()
        }
    }

    /** Applies Java conventions plugin for base configuration. */
    private fun Project.applyBaseConventions() {
        // Apply by class reference instead of ID to avoid plugin resolution issues in consumer projects
        pluginManager.apply(io.github.balaelangovan.gradle.JavaConventionsPlugin::class.java)
    }

    /** Applies Groovy plugin for Spock tests written in Groovy. */
    private fun Project.applyGroovyPlugin() {
        pluginManager.apply("groovy")
    }

    /** Adds Spring Boot Starter Test, MockK, Spock, and JUnit Platform Launcher dependencies. */
    private fun Project.addTestDependencies() {
        dependencies {
            // Spring Boot Starter Test - comprehensive testing support
            add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${SpringConventionsVersions.SPRING_BOOT}")

            // Spring Boot Test Starters for specific testing scenarios
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webmvc-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webflux-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-restclient-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webclient-test:${SpringConventionsVersions.SPRING_BOOT}")

            // MockK - powerful mocking library for Kotlin and Java
            add("testImplementation", "io.mockk:mockk:${SpringConventionsVersions.MOCKK}")

            // Groovy - required for Spock tests
            add("testImplementation", "org.apache.groovy:groovy:${SpringConventionsVersions.GROOVY}")

            // Spock Framework - BDD-style testing with given-when-then syntax
            add("testImplementation", "org.spockframework:spock-core:${SpringConventionsVersions.SPOCK}")
            add("testImplementation", "org.spockframework:spock-spring:${SpringConventionsVersions.SPOCK}")

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
