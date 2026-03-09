SHELL := /bin/zsh

PACKAGE      := com.fittrack.app
ACTIVITY     := $(PACKAGE).MainActivity
JAVA_HOME    := $(HOME)/.asdf/installs/java/openjdk-17.0.2
ADB          := $(HOME)/Library/Android/sdk/platform-tools/adb
GRADLE       := JAVA_HOME=$(JAVA_HOME) ./gradlew

# ── Build ────────────────────────────────────────────────────────────

.PHONY: build
build: ## Build debug APK
	$(GRADLE) assembleDebug

.PHONY: release
release: ## Build release APK
	$(GRADLE) assembleRelease

.PHONY: bundle
bundle: ## Build release AAB (app bundle)
	$(GRADLE) bundleRelease

.PHONY: clean
clean: ## Clean build outputs
	$(GRADLE) clean

.PHONY: rebuild
rebuild: clean build ## Clean then build debug

# ── Install & Run ────────────────────────────────────────────────────

.PHONY: install
install: ## Build and install debug APK on connected device/emulator
	$(GRADLE) installDebug

.PHONY: run
run: install ## Install then launch the app
	$(ADB) shell am start -n $(PACKAGE)/$(ACTIVITY)

.PHONY: uninstall
uninstall: ## Uninstall the app from connected device/emulator
	$(ADB) uninstall $(PACKAGE)

# ── Debug & Logs ─────────────────────────────────────────────────────

.PHONY: logcat
logcat: ## Stream logcat filtered to this app
	$(ADB) logcat --pid=$$($(ADB) shell pidof -s $(PACKAGE)) 2>/dev/null || $(ADB) logcat | grep -iE '(fittrack|$(PACKAGE))'

.PHONY: logcat-crash
logcat-crash: ## Stream only fatal/crash logs
	$(ADB) logcat '*:E' | grep -iE '(FATAL|AndroidRuntime|$(PACKAGE))'

.PHONY: logcat-clear
logcat-clear: ## Clear logcat buffer
	$(ADB) logcat -c

.PHONY: pid
pid: ## Show PID of running app
	$(ADB) shell pidof -s $(PACKAGE)

.PHONY: force-stop
force-stop: ## Force stop the app
	$(ADB) shell am force-stop $(PACKAGE)

.PHONY: restart
restart: force-stop run ## Force stop then relaunch

# ── Testing ──────────────────────────────────────────────────────────

.PHONY: test
test: ## Run unit tests
	$(GRADLE) testDebugUnitTest

.PHONY: test-connected
test-connected: ## Run instrumented tests on device
	$(GRADLE) connectedDebugAndroidTest

# ── Code Quality ─────────────────────────────────────────────────────

.PHONY: lint
lint: ## Run Android lint
	$(GRADLE) lintDebug

.PHONY: lint-report
lint-report: lint ## Run lint and open the report
	open app/build/reports/lint-results-debug.html

# ── Gradle Utilities ─────────────────────────────────────────────────

.PHONY: deps
deps: ## Print dependency tree
	$(GRADLE) app:dependencies --configuration debugRuntimeClasspath

.PHONY: tasks
tasks: ## List available Gradle tasks
	$(GRADLE) tasks

.PHONY: stop-daemon
stop-daemon: ## Stop all Gradle daemons
	$(GRADLE) --stop

.PHONY: cache-clean
cache-clean: ## Delete Gradle caches and rebuild
	rm -rf .gradle build app/build
	$(GRADLE) assembleDebug

# ── Device Info ──────────────────────────────────────────────────────

.PHONY: devices
devices: ## List connected devices/emulators
	$(ADB) devices -l

.PHONY: screenshot
screenshot: ## Take a screenshot and pull to ./screenshot.png
	$(ADB) shell screencap -p /sdcard/screenshot.png
	$(ADB) pull /sdcard/screenshot.png ./screenshot.png
	$(ADB) shell rm /sdcard/screenshot.png
	@echo "Saved to ./screenshot.png"

.PHONY: clear-data
clear-data: ## Clear all app data (SharedPreferences, DB, etc.)
	$(ADB) shell pm clear $(PACKAGE)

# ── Help ─────────────────────────────────────────────────────────────

.PHONY: help
help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'

.DEFAULT_GOAL := help
