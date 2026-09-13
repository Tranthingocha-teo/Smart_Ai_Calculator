# ADR 0005: Modular Mathematics & Financial Calculation Engines

## Status
Accepted

## Context
Calculator logic was initially coupled inside Compose UI screens (`remember` blocks), making automated unit testing difficult and leading to duplicated edge-case logic.

## Decision
We extracted all calculation logic into pure, stateless Kotlin calculation engines under `dhn.intern.smart_ai_caculator_app.util.calculator.*`:
- `TipCalculatorUtil`: Tax, tip percentage, post-tax and pre-tax splitting, multi-person allocation.
- `DateCalculatorUtil`: Duration between dates, date arithmetic (adding/subtracting days, weeks, months, years) using `java.time.LocalDate`.
- `LoanCalculatorUtil`: Equal Principal amortization and Equal Monthly Installment (EMI) formulas.
- `GpaCalculatorUtil`: Weighted Grade Point Average with support for 4.0, 10.0, and 100.0 grade scales.

## Consequences
### Positive
- 100% testable in local JVM test suites (`./gradlew testDebugUnitTest`) without needing Android device emulator.
- Guaranteed numerical accuracy and resilience against timezone, leap year, and rounding edge-cases.

### Trade-offs
- UI Composables must pass raw inputs to utility engines and observe immutable result data classes.
