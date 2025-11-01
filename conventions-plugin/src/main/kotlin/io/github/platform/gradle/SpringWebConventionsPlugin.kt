package io.github.platform.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot Web (MVC) applications.
 *
 * This plugin provides complete setup for traditional Spring MVC/REST applications:
 * - Spring Boot Starter Web (with embedded Tomcat)
 * - Spring Boot Starter Validation
 * - Spring Boot Starter AOP
 * - All Spring core conventions
 *
 * **Usage:**
 * ```
 * plugins {
 *     id("io.github.platform.spring-web-conventions")
 * }
 *
 * dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-data-jpa")
 *     // Add your specific dependencies
 * }
 * ```
 */
class SpringWebConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebDependencies()
        }
    }

    private fun Project.applyRequiredPlugins() {
        pluginManager.apply("io.github.platform.spring-core-conventions")
    }

    private fun Project.addWebDependencies() {
        dependencies {
            // Spring Boot Web - traditional MVC with Tomcat
            add("implementation", "org.springframework.boot:spring-boot-starter-web")

            // Validation
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")

            // AOP support
            add("implementation", "org.springframework.boot:spring-boot-starter-aop")
        }
    }
}
