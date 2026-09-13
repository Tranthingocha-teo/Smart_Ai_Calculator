# AGENTS.md

Welcome to the **Smart AI Calculator** project. This file provides guidelines, operating instructions, and architectural context for AI agents working in this repository.

## Repository Overview
- **Project**: Smart AI Calculator (`dhn.intern.smart_ai_caculator_app`)
- **Tech Stack**: Android Jetpack Compose, Kotlin Coroutines & Flow, Material 3, CameraX, Room Database, Koin DI.
- **Git Remote**: `https://github.com/Tranthingocha-teo/Smart_Ai_Calculator.git`
- **Main Branch**: `main`

## Commands & Verification
- **Run Unit Tests**: `./gradlew testDebugUnitTest`
- **Build Debug APK**: `./gradlew assembleDebug`
- **Install & Launch on Emulator**: `./gradlew installDebug && adb shell am start -n dhn.intern.smart_ai_caculator_app/.MainActivity`

## Agent skills

### Issue tracker

GitHub Issues via `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

Canonical 5-role triage vocabulary. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context layout (`CONTEXT.md` + `docs/adr/`). See `docs/agents/domain.md`.
