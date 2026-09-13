# ADR 0007: Unified Offline-First Unit & Currency Conversion Architecture

## Status
Accepted

## Context
The application lacked a functional unit conversion engine (existing `UnitCalculatorScreen` contained static placeholder UI without calculation logic) and completely lacked currency conversion. Users need immediate calculations across 14+ measurement domains and live foreign exchange rates, while preserving instant offline usability and responsive UI typing without creating database clutter in calculation history.

## Decision
1. **Unified Screen & Category Paradigm**:
   - Merge Currency as a first-class category alongside physical measurement categories (Length, Mass, Temperature, etc.) within `UnitCalculatorScreen`.
   - Temperature utilizes non-linear affine transformation formulas ($y = ax + b$), while other physical units utilize pure ratio-to-base conversions.

2. **Offline-First Exchange Rate Caching**:
   - Fetch currency rates asynchronously from an open public exchange API (`open.er-api.com` or `frankfurter.dev`).
   - Persist fetched rates with a timestamp in Room Database.
   - Bundle a static fallback exchange rate dataset so the converter functions instantly even on a fresh install without internet connection.

3. **Smart Precision & Formatting**:
   - Dynamically maintain 6–8 significant digits, trim trailing zeros, and switch to scientific E-notation ($1.25\times 10^{-7}$) for extreme magnitudes.
   - Apply specialized currency formatting (integer rounding for VND/JPY, 2–4 decimal places for major global currencies).

4. **Debounced History Recording**:
   - Add `UNIT_CONVERTER` to `HistorySource` enum.
   - Persist conversions to Room DB only after a 1.5–2.0 second typing pause (debounce) or upon unit/tab switching when input $> 0$, preventing keystroke pollution.

5. **Enhanced Picker UX**:
   - Upgrade `UnitPickerBottomSheet` with real-time text searching (by unit code, country, or description), country flag icons for currencies, and pinned popular units.

## Consequences
### Positive
- Unified, consistent UX for physical units and monetary conversions.
- Fully functional offline with zero crash risk when network is absent.
- Clean database history without redundant partial-keystroke entries.
- Adheres to ADR 0005 by keeping mathematical conversion logic pure and 100% testable in JVM unit tests.

### Trade-offs
- Currency rates require periodic network synchronization to stay strictly accurate to real-world market movements.
- Non-linear unit conversions require custom bidirectional mapping logic compared to simple linear multiplication factors.
