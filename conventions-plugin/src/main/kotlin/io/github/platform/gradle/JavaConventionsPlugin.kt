package io.github.platform.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin that provides base Java configuration for all projects.
 *
 * This plugin establishes foundational build standards including
 * - Java 21 toolchain configuration
 * - Lombok support for reducing boilerplate code
 * - Google Java Style Guide enforcement via Checkstyle
 * - JaCoCo code coverage reporting (XML, HTML, CSV)
 * - JUnit Platform for modern testing
 * - Standard repository configuration
 *
 * **Usage: **
 * ```
 * plugins {
 *     id("io.github.platform.java-conventions")
 * }
 * ```
 *
 * **Configured Dependencies: **
 * Dependency versions are managed in `gradle.properties` at the project root.
 * This ensures a single source of truth for all version management.
 *
 * Key dependencies:
 * - Lombok for annotation processing
 * - Checkstyle for Google Java Style enforcement
 * - JaCoCo for code coverage analysis
 */
class JavaConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyPlugins()
            configureJavaToolchain()
            configureRepositories()
            configureLombok()
            configureCheckstyle()
            configureJacoco()
            configureTesting()
        }
    }

    /** Applies java-library, checkstyle, and jacoco plugins. */
    private fun Project.applyPlugins() {
        pluginManager.apply("java-library")
        pluginManager.apply("checkstyle")
        pluginManager.apply("jacoco")
    }

    /** Configures Java 21 toolchain. */
    private fun Project.configureJavaToolchain() {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    /** Adds Maven Central and Maven Local repositories. */
    private fun Project.configureRepositories() {
        repositories {
            mavenCentral()
            mavenLocal()
        }
    }

    /** Adds Lombok dependencies for both main and test source sets. */
    private fun Project.configureLombok() {
        dependencies {
            add("compileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("annotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testCompileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testAnnotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
        }
    }

    /** Configures Checkstyle with Google Java Style Guide and strict enforcement. */
    private fun Project.configureCheckstyle() {
        extensions.configure<CheckstyleExtension> {
            toolVersion = GeneratedVersions.CHECKSTYLE

            // Enforce Google Java Style Guide
            config =
                resources.text.fromUri(
                    "https://raw.githubusercontent.com/checkstyle/checkstyle/checkstyle-$toolVersion/src/main/resources/google_checks.xml",
                )

            // Strict enforcement - fail on any violation
            maxWarnings = 0
            maxErrors = 0

            // Apply to both main and test source sets
            isShowViolations = true
        }

        // Configure checkstyle tasks
        tasks.withType<Checkstyle>().configureEach {
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }

    /** Configures JaCoCo code coverage with XML, HTML, and CSV reports. */
    private fun Project.configureJacoco() {
        extensions.configure<JacocoPluginExtension> {
            toolVersion = GeneratedVersions.JACOCO
        }

        tasks.named<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.withType<Test>())

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(true)
            }

            doLast {
                logger.lifecycle("Code coverage report generated: ${reports.html.outputLocation.get()}/index.html")
            }
        }
    }

    /** Configures test tasks with JUnit Platform, parallel execution, and coverage reporting. */
    private fun Project.configureTesting() {
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()

            // Run tests with parallel execution
            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

            // Always generate a coverage report after tests
            finalizedBy(tasks.named("jacocoTestReport"))

            // Configure test logging
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                showStandardStreams = false
            }
        }
    }
}
