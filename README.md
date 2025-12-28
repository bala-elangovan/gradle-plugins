# Gradle Plugins

**Production-Ready Gradle Convention Plugins for Java & Spring Boot Applications**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Gradle](https://img.shields.io/badge/Gradle-9.2.1-blue.svg)](https://gradle.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)

Reusable Gradle convention plugins that enforce best practices, standardize build configurations, and provide production-ready defaults for Java and Spring Boot projects.

---

## Overview

These plugins eliminate boilerplate build configuration and enforce consistent standards across projects by providing:

- **Java 21** toolchain configuration
- **Code formatting** via Spotless (google-java-format for Java, ktlint for Kotlin)
- **Automatic removal of unused imports** for both Java and Kotlin
- **JaCoCo code coverage** with comprehensive reporting
- **Lombok** for reducing boilerplate
- **Apache Commons Lang3** for common utilities (StringUtils, etc.)
- **MapStruct** for type-safe object mapping
- **Spring Boot** with dependency management
- **Centralized version management** via Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Modular plugin architecture** (core, web, webflux)
- **Strict dependency resolution** (fails on conflicts)
- **Comprehensive testing setup** (JUnit 5, MockK, Spock, Spring Test)

---

## Quick Start

### **1. Publish to Maven Local**

```bash
git clone https://github.com/gobelango/gradle-plugins.git
cd gradle-plugins

# Build and publish
./gradlew publishToMavenLocal
```

### **2. Use in Your Project**

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "my-spring-app"
```

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20"  // Required: Apply Kotlin plugin first
    id("io.github.gobelango.spring-web-conventions") version "1.0.0"
}

group = "com.example"
version = "1.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    // Spring Boot manages versions - no need to specify!
}
```

**That's it!** You now have:
- Java 21 toolchain
- Lombok support
- Apache Commons Lang3 utilities
- Code formatting with Spotless (google-java-format + ktlint)
- Automatic unused imports removal
- JaCoCo code coverage
- Spring Boot with Web (MVC)
- MapStruct mapping
- Comprehensive test setup (JUnit 5, MockK, Spock)
- Strict dependency management

---

## Available Plugins

### **Plugin 1: `io.github.gobelango.java-conventions`**

Foundation plugin that provides base Java and Kotlin configuration.

**What it configures:**
- Java 21 toolchain
- Kotlin JVM toolchain (if Kotlin plugin is applied)
- Lombok (compile + annotation processing)
- Apache Commons Lang3 for common utilities
- Spotless code formatting (google-java-format for Java, ktlint for Kotlin)
- Automatic removal of unused imports
- JaCoCo code coverage (XML + HTML + CSV)
- JUnit Platform for testing
- Maven Central + Maven Local repositories

**Important:** This plugin does NOT apply the Kotlin plugin automatically to avoid conflicts in multi-module projects. You must apply Kotlin yourself:

**For single-module projects:**
```kotlin
plugins {
    kotlin("jvm") version "2.2.20"
    id("io.github.gobelango.java-conventions") version "1.0.0"
}
```

**For multi-module projects:**
```kotlin
// Root build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20" apply false
}

// Subproject build.gradle.kts
plugins {
    kotlin("jvm")
    id("io.github.gobelango.java-conventions")
}
```

---

### **Plugin 2: `io.github.gobelango.spring-test-conventions`**

Adds comprehensive testing support for Spring Boot applications.

**What it configures:**
- Applies `java-conventions` automatically
- Spring Boot Starter Test (includes JUnit 5, Mockito, AssertJ)
- MockK for powerful mocking
- JUnit Platform Launcher
- Excludes conflicting logging implementations

**Usage:**
```kotlin
plugins {
    id("io.github.gobelango.spring-test-conventions")
}
```

---

### **Plugin 3: `io.github.gobelango.spring-core-conventions`**

Core Spring Boot setup that's common to all Spring applications.

**What it configures:**
- Applies both `java-conventions` and `spring-test-conventions`
- Spring Boot plugin (`bootJar`, `bootRun` tasks)
- Spring Dependency Management (consistent versions)
- MapStruct (type-safe object mapping)
- Strict dependency resolution (fails on dynamic versions)

**Usage:**
```kotlin
plugins {
    id("io.github.gobelango.spring-core-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Add your specific dependencies
}
```

---

### **Plugin 4: `io.github.gobelango.spring-web-conventions`**

Complete setup for Spring Boot Web (MVC) applications.

**What it configures:**
- Applies `spring-core-conventions` (includes all base Java and Spring setup)
- Spring Boot Starter Web (with embedded Tomcat)
- Spring Boot Starter Validation
- Spring Boot Starter AspectJ (AOP support)

**Usage:**
```kotlin
plugins {
    id("io.github.gobelango.spring-web-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
```

---

### **Plugin 5: `io.github.gobelango.spring-webflux-conventions`**

Complete setup for Spring Boot WebFlux (Reactive) applications.

**What it configures:**
- Applies `spring-core-conventions` (includes all base Java and Spring setup)
- Spring Boot Starter WebFlux (with Netty)
- Spring Boot Starter Validation
- Reactor Test for reactive testing

**Usage:**
```kotlin
plugins {
    id("io.github.gobelango.spring-webflux-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
}
```

**Note:** WebFlux uses Netty instead of Tomcat. Don't mix with `spring-web-conventions` - choose one or the other.

---

## Plugin Composition

```
io.github.gobelango.spring-web-conventions
    │
    └──> io.github.gobelango.spring-core-conventions
            │
            ├──> io.github.gobelango.java-conventions
            │       ├─> java-library
            │       ├─> spotless (google-java-format + ktlint)
            │       ├─> jacoco
            │       ├─> Java 21 toolchain
            │       ├─> Lombok
            │       └─> JUnit Platform
            │
            ├──> io.github.gobelango.spring-test-conventions
            │       ├─> Spring Boot Starter Test
            │       ├─> MockK
            │       └─> JUnit Platform Launcher
            │
            ├──> org.springframework.boot
            ├──> io.spring.dependency-management
            ├──> MapStruct
            └──> Strict dependency resolution

io.github.gobelango.spring-webflux-conventions
    │
    └──> io.github.gobelango.spring-core-conventions
            (same as above)
```

---

## Code Quality: Spotless Formatting

This plugin enforces **consistent code formatting** via Spotless with **google-java-format** for Java and **ktlint** for Kotlin.

**Java Formatting (google-java-format):**
- Google Java Format style
- Automatic removal of unused imports
- Proper import ordering
- Trailing whitespace removal
- Files end with newline

**Kotlin Formatting (ktlint):**
- 4-space indentation
- Max line length: 120 characters
- Trailing commas allowed
- Automatic removal of unused imports
- Wildcard imports disallowed (enforces explicit imports)

**Check formatting:**
```bash
./gradlew spotlessCheck
```

**Auto-fix formatting:**
```bash
./gradlew spotlessApply
```

**Integration with build:**
```bash
./gradlew build  # Automatically runs spotlessCheck
```

---

## Code Coverage: JaCoCo

Automatic code coverage reporting after every test run.

**What you get:**
- XML report (for CI/CD tools)
- HTML report (human-readable)
- CSV report (for data analysis)

**Run tests and generate coverage:**
```bash
./gradlew test

# Coverage report location:
# build/reports/jacoco/test/html/index.html
```

**Customize coverage thresholds (in your project):**
```kotlin
tasks.named<JacocoReport>("jacocoTestReport") {
    doLast {
        val report = reports.xml.outputLocation.get().asFile
        // Add custom verification logic
    }
}
```

---

## Strict Dependency Management

The `spring-core-conventions` plugin enforces strict dependency resolution to prevent version conflicts and ensure reproducible builds.

**What's enforced:**
```kotlin
configurations.all {
    resolutionStrategy {
        failOnVersionConflict()    // Fail if dependencies request different versions
        failOnDynamicVersions()    // Fail on "1.+", "latest.release", etc.
    }
}
```

**Why this matters:**
- Catches dependency conflicts **at build time** (not runtime)
- Ensures **reproducible builds** across environments
- Prevents unexpected behavior from version mismatches
- Forces explicit dependency management decisions

**Resolving conflicts:**
```kotlin
// In your project's build.gradle.kts
configurations.all {
    resolutionStrategy {
        force("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    }
}
```

## Dependency Version Management

All dependency versions are centrally managed using **Gradle Version Catalog** in `gradle/libs.versions.toml`.

The version catalog provides a modern, type-safe approach to dependency management with:

**Structure:**
- `[versions]` - All version numbers (Spring Boot, Kotlin, libraries, tools)
- `[libraries]` - Library dependency declarations with version references
- `[plugins]` - Plugin declarations with version references
- `[bundles]` - Groups of commonly used dependencies

**Example version catalog structure:**
```toml
[versions]
spring-boot = "..."
kotlin = "..."
# ... more versions

[libraries]
spring-boot-gradle-plugin = { module = "...", version.ref = "spring-boot" }
# ... more libraries

[plugins]
kotlin-jvm = { id = "...", version.ref = "kotlin" }
# ... more plugins

[bundles]
spring-web = ["spring-boot-starter-web", "spring-boot-starter-validation", "spring-boot-starter-aspectj"]
# ... more bundles
```

**Benefits:**
- **Single source of truth** - All versions defined in one location
- **Type-safe accessors** - IDE autocomplete for dependencies
- **Automatic generation** - Versions auto-generated into module-specific version files
  - `GeneratedVersions.kt` for java-conventions module
  - `SpringConventionsVersions.kt` for spring-conventions module
- **Dependency bundles** - Group related dependencies together
- **Easy updates** - Change version once, applies everywhere
- **Better IDE support** - Full autocomplete and refactoring support

**To update versions:** Edit `gradle/libs.versions.toml` and rebuild

---

## Testing Setup

**What's included:**

| Library           | Purpose                                      |
|-------------------|----------------------------------------------|
| JUnit 5 (Jupiter) | Modern testing framework                     |
| Spring Boot Test  | Spring-specific test utilities               |
| Spring Test       | Core Spring testing support                  |
| Mockito           | Traditional Java mocking                     |
| MockK             | Kotlin-style mocking (works with Java)       |
| Spock Framework   | BDD-style testing with given-when-then       |
| Groovy            | Required for Spock tests                     |
| AssertJ           | Fluent assertions                            |
| Hamcrest          | Matcher library                              |
| JSONassert        | JSON assertion library                       |
| JsonPath          | JSON path expressions                        |

**Example test:**
```java
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        // Given
        OrderRequest request = OrderRequest.builder()
            .customerId("CUST-123")
            .build();

        // When
        Order order = orderService.createOrder(request);

        // Then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
```

---

## MapStruct Integration

MapStruct is automatically configured for type-safe object mapping between domain models and DTOs.

**Example mapper:**
```java
@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    Order toDomain(OrderRequest request);

    @Mapping(target = "id", ignore = true)
    void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
```

**No additional configuration needed!** MapStruct annotation processor is already set up.

---

## Example Projects

### **Spring Boot REST API (MVC)**

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20"
    id("io.github.gobelango.spring-web-conventions") version "1.0.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
```

### **Spring Boot Reactive API (WebFlux)**

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20"
    id("io.github.gobelango.spring-webflux-conventions") version "1.0.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
}
```

### **Spring Boot Core (No Web)**

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20"
    id("io.github.gobelango.spring-core-conventions") version "1.0.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
}
```

### **Library/Module (No Spring Boot)**

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.20"
    id("io.github.gobelango.java-conventions") version "1.0.0"
}

dependencies {
    api("tools.jackson.core:jackson-databind")
    implementation("org.apache.commons:commons-lang3")
}
```

---

## Commands Reference

```bash
# Build plugins
./gradlew build

# Run tests
./gradlew test

# Check code formatting
./gradlew spotlessCheck

# Auto-fix code formatting
./gradlew spotlessApply

# Clean build artifacts
./gradlew clean

# Publish to Maven Local
./gradlew publishToMavenLocal

# Check dependency versions
make check-versions

# Run pre-commit hooks on all files
make format-all

# Run pre-commit hooks on changed files only
make format-diff
```

---

## Architecture Decisions

### **Why Convention Plugins?**
- Eliminates copy-paste build configuration across projects
- Enforces consistent standards organization-wide
- Updates propagate automatically when plugins are updated
- Reduces cognitive load for developers

### **Why Google Java Style?**
- Industry-standard, well-documented
- Supported by major IDEs
- Enforces readability and consistency
- Prevents bikeshedding over formatting

### **Why Strict Dependency Resolution?**
- Catches issues at build time (not runtime)
- Forces explicit dependency management
- Ensures reproducible builds
- Prevents dependency hell

### **Why MapStruct over Reflection?**
- Compile-time safety (errors caught early)
- Better performance (no reflection)
- IDE support (autocomplete, refactoring)
- Clear, debuggable generated code

---

## License

MIT License - see [LICENSE](LICENSE)

---

## Author

**Balamurugan Elangovan**

[GitHub](https://github.com/gobelango) | [LinkedIn](https://www.linkedin.com/in/balamurugan-elangovan-53791985/) | mail.bala0224@gmail.com

---

## Contributing

This is a personal portfolio project, but feedback and suggestions are welcome! Please open an issue to discuss potential changes.

---
