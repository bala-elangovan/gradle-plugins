plugins {
    kotlin("jvm") version "2.2.20"
}

group = "io.github.platform"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Root project is just a container for the conventions-plugin module
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes all plugins to Maven Local"
    dependsOn(":conventions-plugin:publishToMavenLocal")
}
