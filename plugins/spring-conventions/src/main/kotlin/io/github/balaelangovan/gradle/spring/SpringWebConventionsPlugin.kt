package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot Web (MVC) applications.
 *
 * Configures Spring Boot Starter Web (Tomcat), Validation, and AspectJ.
 * Includes spring-commons WebMVC starter for autoconfiguration, REST clients, security, and logging.
 * Automatically applies spring-core-conventions.
 */
class SpringWebConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebDependencies()
            addSpringCommonsDependency()
        }
    }

    /**
     * Applies SpringCoreConventionsPlugin by class reference to avoid plugin resolution issues.
     */
    private fun Project.applyRequiredPlugins() {
        pluginManager.apply(SpringCoreConventionsPlugin::class.java)
    }

    /**
     * Adds Spring Boot Web, Validation, and AspectJ starters.
     */
    private fun Project.addWebDependencies() {
        dependencies {
            add("implementation", "org.springframework.boot:spring-boot-starter-web")
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")
            add("implementation", "org.springframework.boot:spring-boot-starter-aspectj")
        }
    }

    /**
     * Adds spring-commons WebMVC starter for autoconfiguration, REST clients, security, and logging.
     */
    private fun Project.addSpringCommonsDependency() {
        dependencies {
            add("implementation", "io.github.balaelangovan:spring-boot-webmvc-starter:${SpringConventionsVersions.SPRING_COMMONS}")
        }
    }
}
