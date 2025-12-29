package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin providing core Spring Boot configuration.
 *
 * Configures Spring Boot and Spring Dependency Management plugins, MapStruct for bean mapping,
 * and strict dependency resolution (fails on dynamic versions). Automatically applies
 * java-conventions and spring-test-conventions.
 *
 * This serves as the base for spring-web-conventions and spring-webflux-conventions.
 */
class SpringCoreConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addCommonDependencies()
            configureStrictDependencyResolution()
        }
    }

    /**
     * Applies JavaConventionsPlugin, SpringTestConventionsPlugin, Spring Boot, and Spring Dependency Management.
     */
    private fun Project.applyRequiredPlugins() {
        pluginManager.apply(io.github.balaelangovan.gradle.JavaConventionsPlugin::class.java)
        pluginManager.apply(SpringTestConventionsPlugin::class.java)
        pluginManager.apply("org.springframework.boot")
        pluginManager.apply("io.spring.dependency-management")
    }

    /**
     * Adds MapStruct for compile-time bean mapping.
     */
    private fun Project.addCommonDependencies() {
        dependencies {
            add("implementation", "org.mapstruct:mapstruct:${SpringConventionsVersions.MAPSTRUCT}")
            add("annotationProcessor", "org.mapstruct:mapstruct-processor:${SpringConventionsVersions.MAPSTRUCT}")
            add("testAnnotationProcessor", "org.mapstruct:mapstruct-processor:${SpringConventionsVersions.MAPSTRUCT}")
        }
    }

    /**
     * Configures failOnDynamicVersions to ensure reproducible builds.
     */
    private fun Project.configureStrictDependencyResolution() {
        configurations.all {
            resolutionStrategy {
                failOnDynamicVersions()
            }
        }
    }
}
