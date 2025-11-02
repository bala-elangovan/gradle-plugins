package io.github.platform.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin that provides base Java and Kotlin configuration for all projects.
 *
 * This plugin establishes foundational build standards including
 * - Java 21 and Kotlin JVM toolchain configuration
 * - Kotlin JVM plugin automatically applied
 * - Lombok support for reducing boilerplate code
 * - Google Java Format enforcement via Spotless
 * - Kotlin formatting with ktlint via Spotless (with unused imports removal)
 * - Apache Commons Lang3 for common utilities
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
 * - Apache Commons Lang3 for common utilities
 * - Spotless with google-java-format for Java formatting
 * - Spotless with ktlint for Kotlin formatting
 * - JaCoCo for code coverage analysis
 */
class JavaConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyPlugins()
            configureJavaToolchain()
            configureRepositories()
            configureDependencies()
            configureSpotless()
            configureJacoco()
            configureTesting()
        }
    }

    /** Applies java-library, kotlin, spotless, and jacoco plugins. */
    private fun Project.applyPlugins() {
        pluginManager.apply("java-library")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("com.diffplug.spotless")
        pluginManager.apply("jacoco")
    }

    /** Configures Java 21 toolchain for both Java and Kotlin. */
    private fun Project.configureJavaToolchain() {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        // Configure Kotlin JVM toolchain (configured after Kotlin plugin is applied)
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
                jvmToolchain(21)
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

    /** Configures common dependencies: Lombok and Apache Commons Lang3. */
    private fun Project.configureDependencies() {
        dependencies {
            // Lombok for reducing boilerplate
            add("compileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("annotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testCompileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testAnnotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")

            // Apache Commons Lang3 for common utilities (StringUtils, etc.)
            add("implementation", "org.apache.commons:commons-lang3:${GeneratedVersions.COMMONS_LANG3}")
        }
    }

    /** Configures Spotless with google-java-format for Java and ktlint for Kotlin. */
    private fun Project.configureSpotless() {
        extensions.configure<SpotlessExtension> {
            // Configure Java formatting with google-java-format
            java {
                // Use google-java-format
                googleJavaFormat(GeneratedVersions.GOOGLE_JAVA_FORMAT)

                // Target all Java source files
                target("src/**/*.java")

                // Remove unused imports
                removeUnusedImports()

                // Format imports
                importOrder()

                // Trim trailing whitespace
                trimTrailingWhitespace()

                // Ensure files end with a newline
                endWithNewline()
            }

            // Configure Kotlin formatting with ktlint
            kotlin {
                // Use ktlint
                ktlint(GeneratedVersions.KTLINT)
                    .editorConfigOverride(
                        mapOf(
                            "indent_size" to "4",
                            "max_line_length" to "150",
                            "ktlint_standard_no-wildcard-imports" to "disabled",
                            "ij_kotlin_allow_trailing_comma" to "true",
                            "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                        ),
                    )

                // Target all Kotlin source files
                target("src/**/*.kt", "**/*.kts")

                // Trim trailing whitespace
                trimTrailingWhitespace()

                // Ensure files end with a newline
                endWithNewline()
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
