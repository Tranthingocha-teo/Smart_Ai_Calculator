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

## Development Lifecycle & Git Workflow

Whenever an agent picks up a ticket `#<issue_number>` to implement:
1. **Branch**: Create and switch to a dedicated branch: `git checkout -b feature/<issue_number>-<slug>` from updated `main`.
2. **TDD**: Write failing unit tests first, implement the minimal solution, refactor, and verify `./gradlew testDebugUnitTest`.
3. **Commit**: Use Conventional Commits with issue reference: `feat(<scope>): <description> (closes #<issue_number>)`.
4. **Pull Request**: Push branch to origin and open a PR via `gh pr create` following `.github/pull_request_template.md` with `Closes #<issue_number>`. Leave the PR open for user review.
