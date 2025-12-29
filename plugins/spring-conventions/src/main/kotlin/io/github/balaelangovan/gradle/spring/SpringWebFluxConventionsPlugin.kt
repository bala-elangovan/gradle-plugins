package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot WebFlux (Reactive) applications with platform-commons autoconfiguration.
 *
 * This plugin provides complete setup for reactive Spring WebFlux applications:
 * - Spring Boot Starter WebFlux (with Netty)
 * - Spring Boot Starter Validation
 * - Reactor Test for reactive testing
 * - Platform Commons WebFlux Starter (autoconfiguration, reactive REST clients, security, logging, metrics)
 * - All Spring core conventions
 *
 * **Usage:**
 * ```
 * plugins {
 *     id("io.github.balaelangovan.spring-webflux-conventions")
 * }
 *
 * dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
 *     // Add your specific dependencies
 * }
 * ```
 *
 * **What's Included from Platform Commons:**
 * - Auto-configuration for reactive logging (MDC, transaction IDs)
 * - Global exception handlers for WebFlux
 * - Security (Authorization annotation, header-based auth)
 * - OAuth2-enabled WebClient REST client
 * - Metrics collection
 * - Standardized error responses
 *
 * **Note:** WebFlux uses Netty instead of Tomcat. Don't mix with spring-web-conventions.
 */
class SpringWebFluxConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebFluxDependencies()
            addPlatformCommonsDependency()
        }
    }

    private fun Project.applyRequiredPlugins() {
        // Apply by class reference instead of ID to avoid plugin resolution issues in consumer projects
        pluginManager.apply(SpringCoreConventionsPlugin::class.java)
    }

    private fun Project.addWebFluxDependencies() {
        dependencies {
            // Spring Boot WebFlux - reactive with Netty
            add("implementation", "org.springframework.boot:spring-boot-starter-webflux")

            // Validation
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")

            // Reactor Test for testing reactive streams
            add("testImplementation", "io.projectreactor:reactor-test")
        }
    }

    /**
     * Automatically adds platform-commons spring-boot-webflux-starter dependency.
     * This provides autoconfiguration, reactive REST clients, security, logging, and metrics.
     */
    private fun Project.addPlatformCommonsDependency() {
        dependencies {
            add("implementation", "io.github.platform:spring-boot-webflux-starter:${SpringConventionsVersions.PLATFORM_COMMONS}")
        }
    }
}
