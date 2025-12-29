# Plugin Composition Architecture

This document visualizes how our Gradle convention plugins compose together to provide layered functionality.

## Plugin Dependency Graph

![img.png](diagrams/plugin-dependency-graph.png)

## Detailed Plugin Stack

```mermaid
graph LR
    subgraph Application["Application Layer"]
        WebApp[spring-web-conventions]
        WebFluxApp[spring-webflux-conventions]
    end

    subgraph Framework["Framework Layer"]
        SpringCore[spring-core-conventions]
        SpringTest[spring-test-conventions]
    end

    subgraph Foundation["Foundation Layer"]
        JavaConv[java-conventions]
    end

    subgraph Gradle["Gradle Plugins"]
        JavaLib[java-library]
        SpringBoot[Spring Boot]
        Spotless[Spotless]
        JaCoCo[JaCoCo]
    end

    subgraph DepMgmt["Dependency Management"]
        SpringDepMgmt[Spring Dependency Management]
    end

    WebApp --> SpringCore
    WebFluxApp --> SpringCore
    SpringCore --> SpringTest
    SpringCore --> JavaConv
    SpringTest --> JavaConv
    JavaConv --> JavaLib
    JavaConv --> Spotless
    JavaConv --> JaCoCo
    SpringCore --> SpringBoot
    SpringCore --> SpringDepMgmt

    style Application fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px
    style Framework fill:#fff9c4,stroke:#f57f17,stroke-width:2px
    style Foundation fill:#bbdefb,stroke:#0277bd,stroke-width:2px
    style Gradle fill:#e1bee7,stroke:#6a1b9a,stroke-width:2px
    style DepMgmt fill:#ffccbc,stroke:#d84315,stroke-width:2px
```

## Plugin Responsibilities

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'primaryColor':'#4fc3f7','primaryTextColor':'#ffffff','primaryBorderColor':'#0288d1','secondaryColor':'#81c784','secondaryTextColor':'#ffffff','tertiaryColor':'#ffb74d','tertiaryTextColor':'#ffffff','noteBkgColor':'#fff9c4','fontSize':'16px'}}}%%
mindmap
  root((Platform Plugins))
    Java Conventions
      Java 21 Toolchain
      Lombok Support
      Spotless Formatting
        google-java-format
        ktlint
      JaCoCo Coverage
      JUnit Platform
    Spring Test
      Spring Boot Test
      MockK
      Test Dependencies
    Spring Core
      Spring Boot
      Dependency Management
      MapStruct
      Strict Resolution
    Spring Web
      Web Starter
      Validation
      AspectJ
    Spring WebFlux
      WebFlux Starter
      Validation
      Reactor Test
```

## Build Lifecycle Flow

```mermaid
%%{init: {'theme':'dark'}%%
sequenceDiagram
    participant Dev as Developer
    participant Gradle as Gradle Build
    participant Java as JavaConventionsPlugin
    participant Spotless as Spotless Plugin
    participant Test as Test Tasks
    participant JaCoCo as JaCoCo Plugin
    Dev ->> Gradle: ./gradlew build
    Gradle ->> Java: Apply java-conventions
    Java ->> Spotless: Configure formatting
    Java ->> JaCoCo: Configure coverage
    Gradle ->> Spotless: spotlessCheck
    Spotless -->> Gradle: ✓ Formatting OK
    Gradle ->> Test: Run tests
    Test -->> JaCoCo: Collect coverage
    JaCoCo -->> Gradle: Generate reports
    Gradle -->> Dev: Build Success
    Gradle -->> Dev: Coverage: 85%
```

## Configuration Propagation

```mermaid
graph TD
    VersionCatalog[gradle/libs.versions.toml]
    GenVersionsJava[GeneratedVersions.kt<br/>java-conventions module]
    GenVersionsSpring[SpringConventionsVersions.kt<br/>spring-conventions module]
    JavaPlugin[JavaConventionsPlugin]
    SpringPlugins[Spring Plugins]
    Consumer[Consumer Project]
    Spotless[Spotless Applied]
    Deps[Dependencies Resolved]
    Tests[Tests Configured]

    VersionCatalog -->|Build Time Generation| GenVersionsJava
    VersionCatalog -->|Build Time Generation| GenVersionsSpring
    GenVersionsJava --> JavaPlugin
    GenVersionsSpring --> SpringPlugins
    JavaPlugin --> Consumer
    SpringPlugins --> Consumer
    Consumer --> Spotless
    Consumer --> Deps
    Consumer --> Tests

    style VersionCatalog fill:#fff9c4,stroke:#f57f17,stroke-width:3px
    style GenVersionsJava fill:#ffecb3,stroke:#f57f17,stroke-width:2px
    style GenVersionsSpring fill:#ffecb3,stroke:#f57f17,stroke-width:2px
    style JavaPlugin fill:#ffe0b2,stroke:#e65100,stroke-width:2px
    style SpringPlugins fill:#ffe0b2,stroke:#e65100,stroke-width:2px
    style Consumer fill:#ffcc80,stroke:#e65100,stroke-width:2px
    style Spotless fill:#ffab91,stroke:#bf360c,stroke-width:2px
    style Deps fill:#ffab91,stroke:#bf360c,stroke-width:2px
    style Tests fill:#ffab91,stroke:#bf360c,stroke-width:2px
```

## Version Management Flow

```mermaid
stateDiagram-v2
    [*] --> VersionCatalog
    VersionCatalog --> generateVersions: Build time
    generateVersions --> JavaVersions: java-conventions
    generateVersions --> SpringVersions: spring-conventions
    JavaVersions --> JavaConventionsPlugin: Type-safe constants
    SpringVersions --> SpringPlugins: Type-safe constants
    JavaConventionsPlugin --> ConsumerBuild: Apply configuration
    SpringPlugins --> ConsumerBuild: Apply configuration
    ConsumerBuild --> [*]

    note right of VersionCatalog
        gradle/libs.versions.toml
        [versions]
        spotless = "8.1.0"
        ktlint = "1.7.1"
        spring-boot = "4.0.1"
    end note

    note right of JavaVersions
        GeneratedVersions.kt
        SPOTLESS = "8.1.0"
        KTLINT = "1.7.1"
        LOMBOK = "1.18.42"
    end note

    note right of SpringVersions
        SpringConventionsVersions.kt
        SPRING_BOOT = "4.0.1"
        MAPSTRUCT = "1.6.3"
        MOCKK = "1.14.7"
    end note
```

## Module Structure

The plugin modules are organized in a nested monorepo structure:

```
gradle-plugins/
├── plugins/
│   ├── java-conventions/          # Base Java/Kotlin conventions
│   │   ├── src/main/kotlin/
│   │   │   └── io/github/balaelangovan/gradle/
│   │   │       ├── JavaConventionsPlugin.kt
│   │   │       └── GeneratedVersions.kt (auto-generated)
│   │   └── build.gradle.kts
│   └── spring-conventions/        # Spring Boot conventions
│       ├── src/main/kotlin/
│       │   └── io/github/balaelangovan/gradle/spring/
│       │       ├── SpringTestConventionsPlugin.kt
│       │       ├── SpringCoreConventionsPlugin.kt
│       │       ├── SpringWebConventionsPlugin.kt
│       │       ├── SpringWebFluxConventionsPlugin.kt
│       │       └── SpringConventionsVersions.kt (auto-generated)
│       └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml         # Version catalog (single source of truth)
└── build.gradle.kts                # Root build with publishAllToMavenLocal task
```

**Key architectural decisions:**
- **Module independence**: Each module generates its own version constants file
- **No cross-module dependencies**: spring-conventions doesn't depend on java-conventions at build time
- **Runtime composition**: Spring plugins apply java-conventions via plugin application, not module dependency
- **Nested structure**: All plugin modules grouped under `plugins/` directory

## Key Design Principles

### 1. Layered Composition
Plugins build on each other incrementally:
- **Base Layer**: Java tooling (java-conventions)
- **Framework Layer**: Spring Boot (spring-core-conventions)
- **Application Layer**: Web/WebFlux (specific implementations)

### 2. Single Responsibility
Each plugin has a focused purpose:
- `java-conventions`: Code quality + toolchain
- `spring-test-conventions`: Testing only
- `spring-core-conventions`: Spring Boot integration
- `spring-web-conventions`: Web-specific dependencies

### 3. Module Independence
Version files are module-specific:
- `java-conventions` generates `GeneratedVersions.kt` (Lombok, Spotless, JaCoCo, etc.)
- `spring-conventions` generates `SpringConventionsVersions.kt` (Spring Boot, MapStruct, test libs, etc.)
- No build-time dependencies between modules
- Plugins compose at runtime via plugin application

### 4. Convention Over Configuration
Sensible defaults with escape hatches:
- Pre-configured but customizable
- Centralized version management via Gradle Version Catalog
- Standard repository configuration

### 5. Fail-Fast Philosophy
Catch issues early:
- Spotless fails build on formatting issues
- Strict dependency resolution fails on conflicts
- JaCoCo generates reports automatically
