# ADR 0002: Dependency Injection with Koin

## Status
Accepted

## Context
The application requires clean decoupling of database DAOs, repositories, preferences, and viewmodels. Heavy annotation processors like Dagger/Hilt add significant Gradle build overhead, generated code boilerplate, and annotation processing latency on student development machines and CI runners.

## Decision
We chose **Koin** as the dependency injection framework:
- Pragmatic, lightweight, pure-Kotlin DSL (`module { single { ... }; viewModel { ... } }`).
- Zero annotation processing code generation at compile-time, ensuring ultra-fast build times (< 10s).
- Direct integration with Android Context and Jetpack Compose (`koinViewModel()`).

## Consequences
### Positive
- Blazing fast compilation and clean unit test mocking.
- Modules clearly organized under `injection/` (`DatabaseModule`, `AppModule`, `CalculatorModule`).

### Trade-offs
- Resolution happens at runtime, requiring thorough unit tests to catch dependency graph errors.
