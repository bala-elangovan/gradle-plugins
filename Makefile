.PHONY: help build test clean publish-local check-versions

help:
	@echo "Platform Gradle Plugins - Available Commands"
	@echo "=============================================="
	@echo ""
	@echo "  build           - Build the plugins"
	@echo "  test            - Run tests"
	@echo "  clean           - Clean build artifacts"
	@echo "  publish-local   - Publish to Maven Local"
	@echo "  check-versions  - Show current dependency versions"
	@echo ""

build:
	./gradlew build

test:
	./gradlew test

clean:
	./gradlew clean

publish-local:
	@echo "📦 Publishing platform-gradle-plugins to Maven Local..."
	@./gradlew publishToMavenLocal
	@echo ""
	@echo "✅ Published successfully!"
	@echo "📍 Location: ~/.m2/repository/io/github/platform/"
	@echo ""
	@echo "📝 Use in your projects:"
	@echo ""
	@echo "  // settings.gradle.kts"
	@echo "  pluginManagement {"
	@echo "      repositories {"
	@echo "          mavenLocal()"
	@echo "          gradlePluginPortal()"
	@echo "      }"
	@echo "  }"
	@echo ""
	@echo "  // build.gradle.kts"
	@echo "  plugins {"
	@echo "      id(\"io.github.platform.spring-conventions\") version \"1.0.0\""
	@echo "  }"

check-versions:
	@./gradlew -q displayVersions