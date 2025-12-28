package io.github.gobelango.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for core Spring Boot configuration.
 *
 * This plugin provides a base Spring Boot setup that's common to all Spring applications:
 * - Spring Boot plugin with bootJar and bootRun tasks
 * - Spring Dependency Management for consistent versions
 * - MapStruct for type-safe object mapping
 * - Strict dependency resolution (fails on conflicts)
 * - All base Java and test conventions
 *
 * This plugin is a base for web-specific plugins like:
 * - spring-web-conventions (for MVC/REST APIs)
 * - spring-webflux-conventions (for reactive applications)
 *
 * **Usage:**
 * ```
 * plugins {
 *     id("io.github.gobelango.spring-core-conventions")
 * }
 * ```
 */
class SpringCoreConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addCommonDependencies()
            configureStrictDependencyResolution()
        }
    }

    private fun Project.applyRequiredPlugins() {
        // Apply by class reference instead of ID to avoid plugin resolution issues in consumer projects
        pluginManager.apply(io.github.gobelango.gradle.JavaConventionsPlugin::class.java)
        pluginManager.apply(SpringTestConventionsPlugin::class.java)
        pluginManager.apply("org.springframework.boot")
        pluginManager.apply("io.spring.dependency-management")
    }

    private fun Project.addCommonDependencies() {
        dependencies {
            // MapStruct - Compile-time bean mapping
            add("implementation", "org.mapstruct:mapstruct:${SpringConventionsVersions.MAPSTRUCT}")
            add("annotationProcessor", "org.mapstruct:mapstruct-processor:${SpringConventionsVersions.MAPSTRUCT}")
            add("testAnnotationProcessor", "org.mapstruct:mapstruct-processor:${SpringConventionsVersions.MAPSTRUCT}")
        }
    }

    private fun Project.configureStrictDependencyResolution() {
        configurations.all {
            resolutionStrategy {
                failOnDynamicVersions()
            }
        }
    }
}
