# ADR 0001: Declarative UI with Jetpack Compose

## Status
Accepted

## Context
The application requires a modern, responsive user interface with fluid dark/light theming, reactive numeric input updates, dynamic table layouts (e.g. GPA grade table), and smooth animations across 8+ calculator modules. Traditional Android XML layout files require extensive boilerplate binding, findViewById, and separate state tracking.

## Decision
We adopted **Jetpack Compose** (Material 3) as the exclusive UI toolkit for the entire application:
- Declarative state management via `remember`, `mutableStateOf`, and `StateFlow`.
- Custom reusable composables for keypads (`UnitKeypad`), headers, and result cards.
- Material 3 theming support (`Color.kt`, `Theme.kt`, `Type.kt`).

## Consequences
### Positive
- Unified Kotlin codebase with zero XML layout maintenance.
- Instant UI recomposition when calculations change.
- Native integration with modern Android libraries (CameraX compose, Navigation Compose).

### Trade-offs
- Compose snapshot state lists require explicit reassignment (`clear()` + `addAll()`) to guarantee recomposition during in-place sorting.
