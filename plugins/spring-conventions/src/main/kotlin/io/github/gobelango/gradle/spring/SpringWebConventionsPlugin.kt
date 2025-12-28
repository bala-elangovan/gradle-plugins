package io.github.gobelango.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot Web (MVC) applications with platform-commons autoconfiguration.
 *
 * This plugin provides complete setup for traditional Spring MVC/REST applications:
 * - Spring Boot Starter Web (with embedded Tomcat)
 * - Spring Boot Starter Validation
 * - Spring Boot Starter AspectJ (AOP support)
 * - Platform Commons WebMVC Starter (autoconfiguration, REST clients, security, logging, metrics)
 * - All Spring core conventions
 *
 * **Usage:**
 * ```
 * plugins {
 *     id("io.github.gobelango.spring-web-conventions")
 * }
 *
 * dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-data-jpa")
 *     // Add your specific dependencies
 * }
 * ```
 *
 * **What's Included from Platform Commons:**
 * - Auto-configuration for logging (MDC, transaction IDs)
 * - Global exception handlers
 * - Security (Authorization annotation, header-based auth)
 * - OAuth2-enabled REST client
 * - Metrics collection
 * - Standardized error responses
 */
class SpringWebConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebDependencies()
            addPlatformCommonsDependency()
        }
    }

    private fun Project.applyRequiredPlugins() {
        // Apply by class reference instead of ID to avoid plugin resolution issues in consumer projects
        pluginManager.apply(SpringCoreConventionsPlugin::class.java)
    }

    private fun Project.addWebDependencies() {
        dependencies {
            // Spring Boot Web - traditional MVC with Tomcat
            add("implementation", "org.springframework.boot:spring-boot-starter-web")

            // Validation
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")

            // AspectJ support (renamed from spring-boot-starter-aop in Spring Boot 4)
            add("implementation", "org.springframework.boot:spring-boot-starter-aspectj")
        }
    }

    /**
     * Automatically adds platform-commons spring-boot-webmvc-starter dependency.
     * This provides autoconfiguration, REST clients, security, logging, and metrics.
     */
    private fun Project.addPlatformCommonsDependency() {
        dependencies {
            add("implementation", "io.github.platform:spring-boot-webmvc-starter:${SpringConventionsVersions.PLATFORM_COMMONS}")
        }
    }
}
