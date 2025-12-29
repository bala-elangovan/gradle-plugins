package io.github.balaelangovan.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin that provides base Java and Kotlin configuration for all projects.
 *
 * This plugin establishes foundational build standards including:
 * - Java 21 toolchain configuration
 * - Kotlin JVM toolchain configuration (if Kotlin plugin is applied)
 * - Lombok support for reducing boilerplate code
 * - Google Java Format enforcement via Spotless
 * - Kotlin formatting with ktlint via Spotless (with unused imports removal)
 * - Apache Commons Lang3 for common utilities
 * - JaCoCo code coverage reporting (XML, HTML, CSV)
 * - JUnit Platform for modern testing
 * - Standard repository configuration
 *
 * **Important:** This plugin does NOT apply the Kotlin plugin automatically to avoid conflicts
 * in multi-module projects. Apply Kotlin in your root build.gradle.kts with `apply false`:
 *
 * ```kotlin
 * // Root build.gradle.kts
 * plugins {
 *     kotlin("jvm") version "2.2.20" apply false
 * }
 *
 * // Subproject build.gradle.kts
 * plugins {
 *     kotlin("jvm")
 *     id("io.github.balaelangovan.java-conventions")
 * }
 * ```
 *
 * **Version Management:**
 * Dependency versions are managed in `gradle/libs.versions.toml` (Gradle Version Catalog).
 *
 * Key dependencies configured:
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

    /**
     * Applies java-library, spotless, and jacoco plugins.
     * Note: Kotlin plugin is NOT applied automatically to avoid conflicts in multi-module projects.
     * Users should apply Kotlin plugin in their root build.gradle.kts with 'apply false' and then
     * apply it to individual modules as needed. The convention plugin will configure Kotlin if present.
     */
    private fun Project.applyPlugins() {
        pluginManager.apply("java-library")
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

        // Configure Kotlin JVM toolchain (configured after the Kotlin plugin is applied)
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
                            "max_line_length" to "120",
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
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = false
            }
        }
    }
}
