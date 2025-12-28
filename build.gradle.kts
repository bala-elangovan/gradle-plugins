plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

group = "io.github.gobelango"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Root project is a container for java-conventions and spring-conventions modules
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes all plugins to Maven Local"
    dependsOn(":plugins:java-conventions:publishToMavenLocal", ":plugins:spring-conventions:publishToMavenLocal")
}
