package io.github.platform.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot WebFlux (Reactive) applications.
 *
 * This plugin provides complete setup for reactive Spring applications:
 * - Spring Boot Starter WebFlux (with Netty)
 * - Spring Boot Starter Validation
 * - Reactor Test for reactive testing
 * - All Spring core conventions
 *
 * **Usage:**
 * ```
 * plugins {
 *     id("io.github.platform.spring-webflux-conventions")
 * }
 *
 * dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
 *     // Add your specific reactive dependencies
 * }
 * ```
 *
 * **Note:** WebFlux uses Netty instead of Tomcat and requires reactive dependencies.
 * Do not mix with spring-web-conventions - choose one or the other.
 */
class SpringWebFluxConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebFluxDependencies()
        }
    }

    private fun Project.applyRequiredPlugins() {
        pluginManager.apply("io.github.platform.spring-core-conventions")
    }

    private fun Project.addWebFluxDependencies() {
        dependencies {
            // Spring Boot WebFlux - reactive web with Netty
            add("implementation", "org.springframework.boot:spring-boot-starter-webflux")

            // Validation
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")

            // Reactor Test - for testing reactive streams
            add("testImplementation", "io.projectreactor:reactor-test")
        }
    }
}
