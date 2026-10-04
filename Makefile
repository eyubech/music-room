# Music Room — single entry point for the whole project.
# Every dependency is downloaded automatically from a fresh clone (subject IV.1).
# On Windows, run make from Git Bash.

APP_ID          := com.musicroom.app
GRADLE_VERSION  := 9.8.0
WRAPPER_JAR     := mobile/gradle/wrapper/gradle-wrapper.jar
WRAPPER_URL     := https://raw.githubusercontent.com/gradle/gradle/v$(GRADLE_VERSION)/gradle/wrapper/gradle-wrapper.jar
# Official checksum: https://services.gradle.org/distributions/gradle-$(GRADLE_VERSION)-wrapper.jar.sha256
WRAPPER_SHA256  := 238e777fcddd7e34f9708186085def2abd6e08e658505b38718d79d74c21abd5
SHA256          := $(shell command -v sha256sum >/dev/null 2>&1 && echo sha256sum || echo "shasum -a 256")

ENV             := . ./scripts/env.sh
GRADLE          := $(ENV) && cd mobile && sh ./gradlew --console=plain
COMPOSE         := docker compose
AVD             ?=

.DEFAULT_GOAL   := help

.PHONY: help all setup doctor up down ps logs db-shell db-reset mail \
        mobile mobile-test mobile-lint emulator install run test clean fclean re

help: ## Show this help
	@echo "Usage: make <target>"
	@awk 'BEGIN {FS = ":.*## "} /^[a-zA-Z_-]+:.*## / {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

all: setup up mobile ## Setup, start services and build the app

# --------------------------------------------------------------------------- setup

setup: .env $(WRAPPER_JAR) doctor ## Create .env, fetch the Gradle wrapper, check tools

.env:
	@cp .env.example .env
	@sed -i.bak \
		-e "s|^DB_PASSWORD=change-me$$|DB_PASSWORD=$$(openssl rand -hex 16)|" \
		-e "s|^JWT_SECRET=change-me$$|JWT_SECRET=$$(openssl rand -hex 32)|" \
		.env && rm -f .env.bak
	@echo "Created .env with generated DB_PASSWORD and JWT_SECRET"

$(WRAPPER_JAR):
	@echo "Downloading Gradle wrapper $(GRADLE_VERSION)"
	@curl -fsSL -o $@.tmp $(WRAPPER_URL)
	@echo "$(WRAPPER_SHA256)  $@.tmp" | $(SHA256) -c - >/dev/null 2>&1 \
		|| { rm -f $@.tmp; echo "Gradle wrapper checksum mismatch, aborting"; exit 1; }
	@mv $@.tmp $@

doctor: ## Check that the required tools are installed
	@$(ENV); ok=1; \
	if [ -n "$${JAVA_HOME:-}" ]; then echo "JDK          $$JAVA_HOME"; \
	elif command -v java >/dev/null 2>&1; then echo "JDK          $$(command -v java)"; \
	else echo "JDK          MISSING (install Android Studio or JDK 17+)"; ok=0; fi; \
	if [ -n "$${ANDROID_HOME:-}" ]; then echo "Android SDK  $$ANDROID_HOME"; \
	else echo "Android SDK  MISSING (install Android Studio or set ANDROID_HOME)"; ok=0; fi; \
	if docker info >/dev/null 2>&1; then echo "Docker       $$(docker --version)"; \
	else echo "Docker       NOT RUNNING"; ok=0; fi; \
	[ $$ok = 1 ]

# --------------------------------------------------------------------------- services

up: .env ## Start Postgres and Mailpit (waits until healthy)
	@$(COMPOSE) up -d --wait
	@$(MAKE) --no-print-directory ps

down: ## Stop the services (data is kept)
	@$(COMPOSE) down

ps: ## Show service status
	@$(COMPOSE) ps --format "table {{.Service}}\t{{.Status}}\t{{.Ports}}"

logs: ## Follow service logs
	@$(COMPOSE) logs -f

db-shell: ## Open psql in the database container
	@$(COMPOSE) exec db sh -c 'psql -U "$$POSTGRES_USER" -d "$$POSTGRES_DB"'

db-reset: ## Stop the services and DELETE the database volume
	@$(COMPOSE) down -v

mail: ## Print the Mailpit inbox URL
	@. ./.env && echo "Mailpit inbox: http://localhost:$${MAIL_UI_PORT:-8025}"

# --------------------------------------------------------------------------- mobile

mobile: .env $(WRAPPER_JAR) ## Build the debug APK
	@$(GRADLE) :app:assembleDebug
	@echo "APK: mobile/app/build/outputs/apk/debug/app-debug.apk"

mobile-test: .env $(WRAPPER_JAR) ## Run the Android unit tests
	@$(GRADLE) :app:testDebugUnitTest

mobile-lint: .env $(WRAPPER_JAR) ## Run Android lint
	@$(GRADLE) :app:lintDebug

emulator: ## Start an emulator (AVD=<name>, default: first AVD) and wait for boot
	@$(ENV); \
	if "$$ADB" devices | grep -q '^emulator-'; then echo "An emulator is already running"; exit 0; fi; \
	avd="$(AVD)"; [ -n "$$avd" ] || avd=$$("$$EMULATOR" -list-avds | tr -d '\r' | head -n 1); \
	[ -n "$$avd" ] || { echo "No AVD found: create one in Android Studio > Device Manager"; exit 1; }; \
	echo "Starting emulator $$avd"; \
	nohup "$$EMULATOR" -avd "$$avd" -netdelay none -netspeed full >/dev/null 2>&1 & \
	"$$ADB" wait-for-device; \
	until [ "$$("$$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do sleep 2; done; \
	echo "Emulator ready"

install: .env $(WRAPPER_JAR) ## Install the debug APK on the running device/emulator
	@$(GRADLE) :app:installDebug

run: install ## Install and launch the app
	@$(ENV); "$$ADB" shell am start -n $(APP_ID)/$(APP_ID).ui.MainActivity >/dev/null && echo "Launched $(APP_ID)"

# --------------------------------------------------------------------------- global

test: mobile-test ## Run every test suite

clean: ## Remove build outputs
	@if [ -f $(WRAPPER_JAR) ]; then $(GRADLE) clean; fi

fclean: clean ## clean + stop services + remove downloaded wrapper and Gradle caches
	@$(COMPOSE) down 2>/dev/null || true
	@rm -rf $(WRAPPER_JAR) mobile/.gradle mobile/build mobile/app/build

re: fclean all ## Rebuild everything from scratch
