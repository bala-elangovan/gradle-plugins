# Migration Guide: Adopting Platform Gradle Plugins

This guide helps teams migrate from manual Gradle configuration to using platform convention plugins.

## Prerequisites

Before migrating, ensure:
- Gradle 8.0+ is installed
- Java 21+ is configured
- Your project uses Kotlin DSL (`build.gradle.kts`)

## Migration Scenarios

### Scenario 1: Plain Java Project

**Before:**
```kotlin
// build.gradle.kts
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}
```

**After:**
```kotlin
// build.gradle.kts
plugins {
    id("io.github.balaelangovan.java-conventions") version "1.0.0"
}

// That's it! Everything is pre-configured
```

**Benefits:**
- ✅ Lombok automatically configured
- ✅ Code formatting with Spotless
- ✅ JaCoCo coverage reporting
- ✅ JUnit Platform configured

---

### Scenario 2: Spring Boot Web Application

**Before:**
```kotlin
// build.gradle.kts
plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

**After:**
```kotlin
// build.gradle.kts
plugins {
    id("io.github.balaelangovan.spring-web-conventions") version "1.0.0"
}

// Only add your specific dependencies
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
```

**Benefits:**
- ✅ Spring Boot, Web, Validation, AspectJ (AOP) pre-configured
- ✅ Lombok, MapStruct, testing libraries included
- ✅ Strict dependency resolution enabled
- ✅ Code formatting + coverage reporting

---

### Scenario 3: Multi-Module Project

**Before:**
```
my-app/
├── common/
│   └── build.gradle.kts (manual Java config)
├── service-a/
│   └── build.gradle.kts (manual Spring Boot config)
└── service-b/
    └── build.gradle.kts (manual Spring Boot config)
```

**After:**
```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

// common/build.gradle.kts
plugins {
    id("io.github.balaelangovan.java-conventions") version "1.0.0"
}

// service-a/build.gradle.kts
plugins {
    id("io.github.balaelangovan.spring-web-conventions") version "1.0.0"
}

dependencies {
    implementation(project(":common"))
}

// service-b/build.gradle.kts
plugins {
    id("io.github.balaelangovan.spring-webflux-conventions") version "1.0.0"
}

dependencies {
    implementation(project(":common"))
}
```

**Benefits:**
- ✅ Consistent configuration across all modules
- ✅ Shared version management
- ✅ Reduced boilerplate by ~60%

---

## Step-by-Step Migration

### Step 1: Add Plugin Repository

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        mavenLocal()  // If testing locally
        gradlePluginPortal()
    }
}
```

### Step 2: Choose Your Plugin

| Current Setup | Recommended Plugin |
|--------------|-------------------|
| Plain Java library | `java-conventions` |
| Spring Boot (no web) | `spring-core-conventions` |
| Spring Boot + Web (MVC) | `spring-web-conventions` |
| Spring Boot + WebFlux | `spring-webflux-conventions` |

### Step 3: Replace Existing Plugins

**Remove these:**
```kotlin
plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.6"
    `java-library`
}
```

**Add this:**
```kotlin
plugins {
    id("io.github.balaelangovan.spring-web-conventions") version "1.0.0"
}
```

### Step 4: Clean Up Dependencies

**Remove these (now provided automatically):**
```kotlin
dependencies {
    // ❌ Remove - provided by plugin
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // ❌ Remove - provided by spring-web-conventions
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

**Keep only application-specific dependencies:**
```kotlin
dependencies {
    // ✅ Keep - application-specific
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}
```

### Step 5: Remove Manual Configuration

**Delete these sections (now handled by plugin):**
```kotlin
// ❌ Remove - handled by plugin
repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}
```

### Step 6: Test the Migration

```bash
# Clean build
./gradlew clean build

# Verify tests pass
./gradlew test

# Check formatting
./gradlew spotlessCheck

# Generate coverage report
./gradlew jacocoTestReport
```

---

## Common Migration Issues

### Issue 1: Dependency Conflicts

**Error:**
```
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':app:dependencies'.
> Could not resolve all dependencies for configuration ':app:compileClasspath'.
   > Conflict found for the following module: com.fasterxml.jackson.core:jackson-databind
```

**Solution:**
The plugin enables strict dependency resolution. Force a specific version:

```kotlin
configurations.all {
    resolutionStrategy {
        force("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    }
}
```

### Issue 2: Code Formatting Violations

**Error:**
```
> Task :spotlessCheck FAILED
The following files had format violations:
    src/main/java/com/example/MyService.java
```

**Solution:**
Auto-fix formatting:

```bash
./gradlew spotlessApply
```

### Issue 3: Lombok Not Working

**Symptom:** IDE shows errors for Lombok annotations

**Solution:**
1. Enable annotation processing in IDE
2. Install Lombok plugin for your IDE
3. Rebuild project: `./gradlew clean build`

### Issue 4: Different Spring Boot Version Needed

**Symptom:** Need Spring Boot 3.3.x instead of 3.5.x

**Solution:**
Override in your project:

```kotlin
// build.gradle.kts
extra["springBootVersion"] = "3.3.5"
```

---

## Rollback Strategy

If migration causes issues, you can rollback:

### Option 1: Gradual Rollback

Keep the plugin but override specific settings:

```kotlin
plugins {
    id("io.github.balaelangovan.spring-web-conventions") version "1.0.0"
}

// Temporarily disable Spotless
tasks.named("spotlessCheck") {
    enabled = false
}
```

### Option 2: Full Rollback

Restore your original `build.gradle.kts` from git:

```bash
git checkout HEAD -- build.gradle.kts
./gradlew clean build
```

---

## Post-Migration Checklist

- [ ] All tests pass (`./gradlew test`)
- [ ] Build succeeds (`./gradlew build`)
- [ ] Formatting is clean (`./gradlew spotlessCheck`)
- [ ] Coverage reports generate (`./gradlew jacocoTestReport`)
- [ ] Application runs (`./gradlew bootRun`)
- [ ] Dependencies resolve without conflicts
- [ ] CI/CD pipeline passes
- [ ] Team is trained on new workflow

---

## Best Practices After Migration

### 1. Add Formatting to CI/CD

```yaml
# .github/workflows/ci.yml
- name: Check Code Formatting
  run: ./gradlew spotlessCheck
```

### 2. Set Up Pre-Commit Hooks

```bash
# Install pre-commit hooks
pre-commit install
```

### 3. Document Project-Specific Overrides

If you override plugin defaults, document why:

```kotlin
// build.gradle.kts

// Override: We need Spring Boot 3.3.x for AWS compatibility
extra["springBootVersion"] = "3.3.5"
```

### 4. Monitor Build Performance

The plugins add minimal overhead (~2-5s):

```bash
# Profile your build
./gradlew build --profile

# Check report at: build/reports/profile/
```

---

## Need Help?

- Check [Troubleshooting Guide](./troubleshooting.md)
- Review [GitHub Issues](https://github.com/bala-elangovan/gradle-plugins/issues)
- See [Examples](../examples/) for common patterns
