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
 * Convention plugin providing base Java and Kotlin configuration.
 *
 * Configures Java 21 toolchain, Lombok, Apache Commons Lang3, Spotless formatting
 * (google-java-format + ktlint), JaCoCo coverage, and JUnit Platform testing.
 *
 * The Kotlin plugin is NOT applied automatically to avoid conflicts in multi-module projects.
 * Apply it in your root build.gradle.kts with `apply false` and then apply to individual modules.
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
     *
     * The Kotlin plugin must be applied by the consumer project to avoid version conflicts.
     * If present, this plugin will configure Kotlin JVM toolchain automatically.
     */
    private fun Project.applyPlugins() {
        pluginManager.apply("java-library")
        pluginManager.apply("com.diffplug.spotless")
        pluginManager.apply("jacoco")
    }

    /**
     * Configures Java 21 toolchain and Kotlin JVM toolchain if Kotlin plugin is applied.
     */
    private fun Project.configureJavaToolchain() {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
                jvmToolchain(21)
            }
        }
    }

    /**
     * Adds Maven Central and Maven Local repositories.
     */
    private fun Project.configureRepositories() {
        repositories {
            mavenCentral()
            mavenLocal()
        }
    }

    /**
     * Configures Lombok for compile and test, and Apache Commons Lang3 as implementation dependency.
     */
    private fun Project.configureDependencies() {
        dependencies {
            add("compileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("annotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testCompileOnly", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("testAnnotationProcessor", "org.projectlombok:lombok:${GeneratedVersions.LOMBOK}")
            add("implementation", "org.apache.commons:commons-lang3:${GeneratedVersions.COMMONS_LANG3}")
        }
    }

    /**
     * Configures Spotless with google-java-format for Java and ktlint for Kotlin.
     *
     * Java: google-java-format with unused imports removal and import ordering.
     * Kotlin: ktlint with 4-space indent, 120 char line length, and trailing commas allowed.
     */
    private fun Project.configureSpotless() {
        extensions.configure<SpotlessExtension> {
            java {
                googleJavaFormat(GeneratedVersions.GOOGLE_JAVA_FORMAT)
                target("src/**/*.java")
                removeUnusedImports()
                importOrder()
                trimTrailingWhitespace()
                endWithNewline()
            }

            kotlin {
                ktlint(GeneratedVersions.KTLINT)
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
    }

    /**
     * Configures JaCoCo with XML, HTML, and CSV report formats.
     */
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

    /**
     * Configures JUnit Platform testing with parallel execution and automatic coverage reporting.
     */
    private fun Project.configureTesting() {
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
            finalizedBy(tasks.named("jacocoTestReport"))

            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = false
            }
        }
    }
}
