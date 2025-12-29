package io.github.balaelangovan.gradle.spring

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin providing comprehensive Spring Boot test configuration.
 *
 * Configures Spring Boot test starters (web, webflux, restclient, webclient), MockK,
 * Spock Framework with Groovy, and JUnit Platform. Automatically applies java-conventions.
 *
 * Excludes conflicting logging dependencies (logback, log4j-to-slf4j, spring-boot-starter-logging)
 * to support projects using Log4j2.
 */
class SpringTestConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyBaseConventions()
            applyGroovyPlugin()
            addTestDependencies()
            excludeConflictingDependencies()
        }
    }

    /**
     * Applies JavaConventionsPlugin by class reference to avoid plugin resolution issues.
     */
    private fun Project.applyBaseConventions() {
        pluginManager.apply(io.github.balaelangovan.gradle.JavaConventionsPlugin::class.java)
    }

    /**
     * Applies Groovy plugin for Spock Framework tests.
     */
    private fun Project.applyGroovyPlugin() {
        pluginManager.apply("groovy")
    }

    /**
     * Adds Spring Boot test starters, MockK, Spock, Groovy, and JUnit Platform Launcher.
     */
    private fun Project.addTestDependencies() {
        dependencies {
            add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webmvc-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webflux-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-restclient-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "org.springframework.boot:spring-boot-starter-webclient-test:${SpringConventionsVersions.SPRING_BOOT}")
            add("testImplementation", "io.mockk:mockk:${SpringConventionsVersions.MOCKK}")
            add("testImplementation", "org.apache.groovy:groovy:${SpringConventionsVersions.GROOVY}")
            add("testImplementation", "org.spockframework:spock-core:${SpringConventionsVersions.SPOCK}")
            add("testImplementation", "org.spockframework:spock-spring:${SpringConventionsVersions.SPOCK}")
            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }
    }

    /**
     * Excludes logback, log4j-to-slf4j, and spring-boot-starter-logging to avoid conflicts.
     */
    private fun Project.excludeConflictingDependencies() {
        configurations.all {
            exclude(mapOf("group" to "ch.qos.logback", "module" to "logback-classic"))
            exclude(mapOf("group" to "org.apache.logging.log4j", "module" to "log4j-to-slf4j"))
            exclude(mapOf("module" to "spring-boot-starter-logging"))
        }
    }
}
