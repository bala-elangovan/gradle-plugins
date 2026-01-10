plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

group = "com.github.bala-elangovan"
version = "${property("major")}.${property("minor")}.${property("patch")}"

repositories {
    mavenCentral()
}

// Root project is a container for java-conventions and spring-conventions modules
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes all plugins to Maven Local"
    dependsOn(":plugins:java-conventions:publishToMavenLocal", ":plugins:spring-conventions:publishToMavenLocal")
}
