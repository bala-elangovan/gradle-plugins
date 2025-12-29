package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Spring Boot WebFlux (Reactive) applications.
 *
 * Configures Spring Boot Starter WebFlux (Netty), Validation, and Reactor Test.
 * Includes spring-commons WebFlux starter for autoconfiguration, reactive REST clients, security, and logging.
 * Automatically applies spring-core-conventions.
 *
 * Note: WebFlux uses Netty instead of Tomcat. Do not mix with spring-web-conventions.
 */
class SpringWebFluxConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyRequiredPlugins()
            addWebFluxDependencies()
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
     * Adds Spring Boot WebFlux, Validation starters and Reactor Test for reactive testing.
     */
    private fun Project.addWebFluxDependencies() {
        dependencies {
            add("implementation", "org.springframework.boot:spring-boot-starter-webflux")
            add("implementation", "org.springframework.boot:spring-boot-starter-validation")
            add("testImplementation", "io.projectreactor:reactor-test")
        }
    }

    /**
     * Adds spring-commons WebFlux starter for autoconfiguration, reactive REST clients, security, and logging.
     */
    private fun Project.addSpringCommonsDependency() {
        dependencies {
            add("implementation", "io.github.balaelangovan:spring-boot-webflux-starter:${SpringConventionsVersions.SPRING_COMMONS}")
        }
    }
}
