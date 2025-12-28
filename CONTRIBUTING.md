# Contributing to Gradle Plugins

This document outlines the process and guidelines for contributing to this project.

## Development Setup

### Prerequisites

- Java 21 or higher
- Git
- Python 3.12+ (for pre-commit hooks)
- Homebrew (recommended for macOS)

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd gradle-plugins
   ```

2. **Install Python 3.12+ (if not already installed)**
   ```bash
   brew install python@3.12
   ```

3. **Install pre-commit hooks**
   ```bash
   make setup-hooks
   ```

   This will automatically:
   - Install pipx (if not present)
   - Install pre-commit via pipx
   - Set up git hooks

   Or manually:
   ```bash
   brew install pipx
   pipx install pre-commit
   pre-commit install
   pre-commit install --hook-type commit-msg
   ```

4. **Build the project**
   ```bash
   make build
   ```

## Development Workflow

### Making Changes

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write clean, readable code
   - Follow the existing code style
   - Add tests for new functionality
   - Update documentation as needed

3. **Test your changes**
   ```bash
   make test
   ```

4. **Test locally**
   ```bash
   make publish-local
   ```
   Then test the plugins in a local project.

### Code Style

This project uses **Spotless** for automatic code formatting:

**Java Formatting (google-java-format 1.31.0):**
- Google Java Format style
- Automatic removal of unused imports
- Proper import ordering
- Trailing whitespace removal
- Files end with newline

**Kotlin Formatting (ktlint 1.7.1):**
- 4-space indentation
- Max line length: 150 characters
- Wildcard imports allowed
- Trailing commas allowed
- Automatic removal of unused imports
- Line Endings: LF (Unix-style)

All style rules are configured in `.editorconfig` and enforced via Spotless.

**Format your code:**
```bash
# Check formatting issues
./gradlew spotlessCheck

# Auto-fix all formatting issues
./gradlew spotlessApply

# Or use Make commands
make format-all        # Format all files
make format-diff       # Format only changed files
```

### Commit Messages

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. Your commit messages should follow this format:

```
<type>(<scope>): <short summary>

<optional body>

<optional footer>
```

**Types:**
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks, dependency updates

**Examples:**
```bash
feat(spring): add support for custom MapStruct configuration
fix(java): correct JaCoCo coverage thresholds
docs: update README with new plugin options
chore: update Spring Boot to 3.4.1
```

### Pre-commit Hooks

Pre-commit hooks run automatically before each commit to ensure code quality and **auto-fix** issues:

- **Trailing whitespace removal** - Auto-fixed ✨
- **End-of-file fixer** - Auto-fixed ✨
- **YAML validation** - Check only
- **Large file check** (max 500KB) - Check only
- **Merge conflict detection** - Check only
- **Kotlin formatting** (ktlint 1.5.0) - Auto-fixed ✨
- **Commit message linting** (conventional commits) - Check only

**How it works:**
1. When you commit, hooks run automatically
2. Most formatting issues are **auto-fixed** (files modified in place)
3. If files are modified, you'll need to `git add` them and commit again
4. Commit message linting runs when you commit (enforces conventional commits format)

### Running Hooks Manually

```bash
# Run all hooks on all files
pre-commit run --all-files
make format-all  # Shortcut

# Run hooks on changed files only
pre-commit run
make format-diff  # Shortcut

# Run specific hook
pre-commit run trailing-whitespace --all-files
pre-commit run ktlint --all-files

# Run Spotless formatting (for Java/Kotlin in plugins)
./gradlew spotlessCheck  # Check formatting
./gradlew spotlessApply  # Auto-fix formatting
```

## Plugin Development

### Project Structure

```
gradle-plugins/
├── plugins/
│   ├── java-conventions/
│   │   ├── src/main/kotlin/io/github/platform/gradle/
│   │   │   ├── JavaConventionsPlugin.kt      # Base Java configuration
│   │   │   └── GeneratedVersions.kt          # Auto-generated version constants
│   │   └── build.gradle.kts                  # Java conventions plugin build config
│   └── spring-conventions/
│       ├── src/main/kotlin/io/github/platform/gradle/spring/
│       │   ├── SpringTestConventionsPlugin.kt    # Spring test configuration
│       │   ├── SpringCoreConventionsPlugin.kt    # Core Spring Boot setup
│       │   ├── SpringWebConventionsPlugin.kt     # Spring MVC/REST configuration
│       │   ├── SpringWebFluxConventionsPlugin.kt # Spring WebFlux/Reactive configuration
│       │   └── PlatformCommonsVersions.kt        # Auto-generated platform-commons version
│       └── build.gradle.kts                      # Spring conventions plugin build config
├── build.gradle.kts                              # Root build configuration
└── gradle/
    └── libs.versions.toml                        # Centralized version management
```

### Adding a New Plugin

1. Create a new plugin class in:
   - `plugins/java-conventions/src/main/kotlin/io/github/platform/gradle/` for base Java plugins
   - `plugins/spring-conventions/src/main/kotlin/io/github/platform/gradle/spring/` for Spring-specific plugins
2. Register it in the respective module's `build.gradle.kts` under `gradlePlugin.plugins`
3. Add tests if applicable
4. Update README.md with usage instructions

### Updating Dependencies

All dependency versions are centralized in `gradle/libs.versions.toml` (Gradle Version Catalog) and auto-generated into:
- `GeneratedVersions.kt` in the java-conventions module
- `PlatformCommonsVersions.kt` in the spring-conventions module

To update:

1. Modify the version in `gradle/libs.versions.toml`
2. Run `make build` (this regenerates version files)
3. Test thoroughly with `make test`
4. Commit with: `chore: update <dependency> to <version>`

**Note:** The auto-generated version files should never be edited manually. Always update `gradle/libs.versions.toml` instead.

## Testing

### Local Testing

```bash
# Build and test
make test

# Publish to Maven Local
make publish-local

# Test in another project
# Add to your test project's settings.gradle.kts:
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

## Pull Request Process

1. **Update your branch**
   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Ensure all tests pass**
   ```bash
   make test
   ```

3. **Push your changes**
   ```bash
   git push origin feature/your-feature-name
   ```

4. **Create a Pull Request**
   - Provide a clear description of the changes
   - Reference any related issues
   - Ensure all CI checks pass

5. **Code Review**
   - Address review comments
   - Keep the PR up to date with main branch

## Getting Help

- Open an issue for bug reports or feature requests
- Check existing issues and pull requests
- Review the README.md for plugin usage

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members
