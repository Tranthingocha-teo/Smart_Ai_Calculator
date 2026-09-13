# ADR 0003: Local Persistence with Room Database

## Status
Accepted

## Context
Users perform frequent calculations (scientific expressions, loan amortization plans, GPA records, and AI math queries) that need to be retrieved offline, searched, and reviewed.

## Decision
We use **Android Jetpack Room Database** for offline-first structured persistence:
- `CalculatorDatabase` provides schema management and type converters.
- Reactive data streaming via Kotlin Coroutines `Flow<List<CalculatorHistoryEntity>>`.
- Schema evolution using explicit Room Migrations when expanding columns across milestones.

## Consequences
### Positive
- Compile-time SQL verification and type-safe DAO queries.
- Reactive Compose UI updates as new calculations are inserted.

### Trade-offs
- Schema changes require careful version incrementing and migration strategies.
