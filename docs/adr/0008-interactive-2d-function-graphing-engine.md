# ADR 0008: Interactive 2D Function Graphing Engine & Touch Visualization Architecture

## Status
Accepted

## Context
The Smart AI Calculator suite includes arithmetic, unit conversions, financial algorithms, and AI tutoring, but lacked high-order visual mathematical tooling for high school and university students. Graphing equations (e.g. parabolas, rational functions, trigonometry) is critical for pedagogical clarity. Rendering continuous mathematical functions on mobile requires responsive touch exploration, handling asymptotes without graphic glitches, and maintaining numerical evaluation performance at 60fps without triggering Android soft keyboard overlays.

## Decision
1. **Pure Kotlin Math & Sampling Engine (`GraphingEngine`)**:
   - Extend the Shunting-Yard tokenizer and postfix evaluator with `Token.Variable("x")`.
   - Parse mathematical formulas once into postfix token lists; evaluate $f(x)$ dynamically across viewport coordinates $X_{screen} \to x \to y$.
   - Implement adaptive sampling with asymptote/discontinuity detection (e.g., $1/x$, $\tan(x)$) to split curve paths and avoid artificial vertical connecting strokes.
   - Provide numerical solvers for roots ($f(x) = 0$), local extrema ($f'(x) = 0$), and curve intersections ($f(x) = g(x)$).
   - Maintain zero Android dependencies in math evaluation, strictly following ADR 0005 for 100% JVM unit testability.

2. **Hardware-Accelerated Jetpack Compose Canvas**:
   - Build `GraphCanvas` handling bidirectional linear transforms between screen pixel coordinates and Cartesian math coordinates $(x, y) \leftrightarrow (P_x, P_y)$.
   - Support simultaneous multi-function plotting (2–3 curves with distinct Material 3 color palettes).
   - Implement gesture-driven Pan and Pinch-to-Zoom with dynamic grid spacing (1, 2, 5, 10, ...) and axis labels.
   - Implement touch point tracing with magnetic snap detection targeting roots, extrema, and intersections, rendering an interactive coordinate tooltip $(x, y)$.

3. **Dedicated On-Screen Mathematical Keypad (`CustomMathKeypad`)**:
   - Avoid triggering the Android system soft keyboard (IME), ensuring the graphing canvas remains fully visible during formula composition.
   - Include direct-access keys for variables (`x`), exponents (`^`, `x²`), trigonometric functions (`sin`, `cos`, `tan`), roots (`√`), logarithms (`ln`, `log`), constants ($\pi$, $e$), parentheses, and operators.

4. **Persistence & Preset Library**:
   - Extend `HistorySource` with `GRAPHING_CALCULATOR` to store plotted formulas and viewport bounds in Room Database.
   - Provide a built-in repository of mathematical presets (Linear, Quadratic Parabola, Trigonometric Wave, Rational Hyperbola, Exponential/Logarithmic).

5. **Visual Export & Sharing (`GraphImageExporter`)**:
   - Provide a one-tap action to render the current Canvas view into a high-resolution PNG image file and launch an Android Share Intent.

## Consequences
### Positive
- Smooth 60fps graphical evaluation with cached postfix token parsing.
- Precise curve rendering without visual discontinuity artifacts.
- Zero keyboard overlay clutter, keeping viewport visible and responsive.
- Adheres strictly to ADR 0005 and ADR 0003 for modular testing and Room persistence.

### Trade-offs
- Dynamic sampling density must balance evaluation speed on lower-end Android devices with curve smoothness at high zoom levels.
- Numerical root-finding requires tolerance thresholds ($10^{-5}$) to prevent infinite convergence loops.
