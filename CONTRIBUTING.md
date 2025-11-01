# Contributing to Platform Gradle Plugins

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
   cd platform-gradle-plugins
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

- **Kotlin**: We use ktlint 1.5.0 for Kotlin formatting. The pre-commit hooks will automatically format your code.
- **Line Length**: No strict limit (ktlint max-line-length rule disabled for flexibility)
- **Wildcard Imports**: Allowed (ktlint no-wildcard-imports rule disabled)
- **Indentation**: 4 spaces for Kotlin/Java, 2 spaces for YAML
- **Line Endings**: LF (Unix-style)

All style rules are configured in `.editorconfig`.

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

# Run specific hook
pre-commit run trailing-whitespace --all-files
```

## Plugin Development

### Project Structure

```
platform-gradle-plugins/
├── conventions-plugin/
│   ├── src/main/kotlin/io/github/platform/gradle/
│   │   ├── JavaConventionsPlugin.kt          # Base Java configuration
│   │   ├── SpringTestConventionsPlugin.kt    # Spring test configuration
│   │   ├── SpringCoreConventionsPlugin.kt    # Core Spring Boot setup
│   │   ├── SpringWebConventionsPlugin.kt     # Spring MVC/REST configuration
│   │   ├── SpringWebFluxConventionsPlugin.kt # Spring WebFlux/Reactive configuration
│   │   └── GeneratedVersions.kt              # Auto-generated version constants
│   └── build.gradle.kts                      # Convention plugin build config
├── build.gradle.kts                          # Root build configuration
└── gradle.properties                         # Centralized version management
```

### Adding a New Plugin

1. Create a new plugin class in `conventions-plugin/src/main/kotlin/io/github/platform/gradle/`
2. Register it in `conventions-plugin/build.gradle.kts` under `gradlePlugin.plugins`
3. Add tests if applicable
4. Update README.md with usage instructions

### Updating Dependencies

All dependency versions are centralized in `gradle.properties` and auto-generated into `GeneratedVersions.kt`. To update:

1. Modify the version in `gradle.properties`
2. Run `make build` (this regenerates `GeneratedVersions.kt`)
3. Test thoroughly with `make test`
4. Commit with: `chore: update <dependency> to <version>`

**Note:** The `GeneratedVersions.kt` file is auto-generated - never edit it manually. Always update `gradle.properties` instead.

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
