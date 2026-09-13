# Specification: 2D Interactive Function Graphing Calculator

## 1. Overview & Objectives
The **2D Interactive Function Graphing Calculator** enables users (students, educators, engineers) to visually analyze mathematical equations on an interactive Cartesian plane. It provides simultaneous multi-curve plotting, hardware-accelerated pan and pinch-to-zoom gestures, intelligent curve tracing with snap detection, dedicated non-intrusive math keypads, equation history persistence in Room Database, and PNG image export.

---

## 2. User Stories & Acceptance Criteria

### User Stories
1. **Multi-Curve Visualization**: As a student, I want to plot multiple functions (e.g., $f(x) = x^2 - 4$, $g(x) = 2x - 1$) simultaneously with distinct colors to compare their behaviors.
2. **Interactive Touch Exploration**: As a user, I want to pan and pinch-to-zoom across the graph smoothly, with axis gridlines and coordinate numbers adjusting automatically.
3. **Point Tracing & Snapping**: As a user, I want to drag my finger along a plotted curve to inspect exact coordinates $(x, y)$, and have the cursor snap into roots ($y = 0$), local extrema, and curve intersections.
4. **Dedicated Math Keypad**: As a user, I want to enter functions like $\sin(x)$, $\sqrt{x}$, or $x^3$ using a dedicated math keypad without an Android system keyboard popping up and obscuring the screen.
5. **Asymptote & Discontinuity Resilience**: As a student, when plotting $f(x) = 1/x$ or $\tan(x)$, I do not want erroneous vertical connecting lines across asymptotes.
6. **History & Presets**: As a user, I want my plotted equations saved to my calculation history and have quick access to preset functions (e.g., Parabola, Sine Wave, Exponential).
7. **Image Export**: As a student, I want to export high-resolution PNG snapshots of my plotted graphs to share with classmates or attach to homework.

### Acceptance Criteria
- [ ] Pure Kotlin `GraphingEngine` parses formulas with variable `x`, evaluating 500–1000 sample points per frame in $< 10$ms on JVM.
- [ ] Curve discontinuities at poles/asymptotes (e.g., $x = 0$ for $1/x$, $x = \pi/2$ for $\tan(x)$) are detected and split into separate Path segments.
- [ ] Real roots ($f(x) = 0$), extrema ($f'(x) = 0$), and intersections ($f(x) = g(x)$) are computed and highlighted with clickable snap dots.
- [ ] Pan and zoom gestures smoothly transform between Screen Pixels $(P_x, P_y)$ and Math Coordinates $(x, y)$.
- [ ] Custom on-screen keypad supports `x`, `^`, `sin`, `cos`, `tan`, `sqrt`, `ln`, `log`, `π`, `e`, numbers, parentheses, and backspace.
- [ ] Room Database stores graphing equations under `HistorySource.GRAPHING_CALCULATOR`.
- [ ] 100% test coverage for math evaluation and sampling logic in JVM unit test suites (`./gradlew testDebugUnitTest`).

---

## 3. Architecture & Domain Models

### 3.1 Domain Models
```kotlin
data class ViewportBounds(
    val minX: Double = -10.0,
    val maxX: Double = 10.0,
    val minY: Double = -10.0,
    val maxY: Double = 10.0
)

data class PlottedFunction(
    val id: String = UUID.randomUUID().toString(),
    val expression: String,
    val colorHex: String,
    val isVisible: Boolean = true
)

data class GraphPoint(
    val x: Double,
    val y: Double
)

enum class SpecialPointType {
    ROOT,
    LOCAL_MIN,
    LOCAL_MAX,
    INTERSECTION
}

data class SpecialPoint(
    val point: GraphPoint,
    val type: SpecialPointType,
    val label: String
)

data class SampledCurve(
    val functionId: String,
    val continuousSegments: List<List<GraphPoint>>,
    val specialPoints: List<SpecialPoint>
)
```

---

## 4. Algorithmic Design & GraphingEngine API

### 4.1 Interface Contract & Separation of Concerns
The engine strictly separates formula compilation (one-time upon typing) from sampling (per-frame on gesture):
```kotlin
interface GraphingEngine {
    fun compile(expression: String): Result<CompiledFunction>
    fun sample(
        function: CompiledFunction,
        viewport: ViewportBounds,
        screenPixelWidth: Int = 500
    ): SampledCurve
    fun findIntersections(
        f: CompiledFunction,
        g: CompiledFunction,
        viewport: ViewportBounds
    ): List<SpecialPoint>
}

data class CompiledFunction(
    val id: String = UUID.randomUUID().toString(),
    val rawExpression: String,
    val postfixTokens: List<Token>
) {
    fun evaluate(x: Double): Double
}
```

### 4.2 Tokenization with Implicit Multiplication & Unicode Exponents
- Enhance `Token.kt` with `data class Variable(val name: String = "x") : Token()`.
- Add automatic implicit multiplication insertion:
  - Digit preceding variable: `2x` $\implies$ `2 * x`
  - Variable preceding parenthesis or function: `x(x+1)` $\implies$ `x * (x+1)`, `3sin(x)` $\implies$ `3 * sin(x)`
  - Adjacent parentheses: `(x+1)(x-1)` $\implies$ `(x+1) * (x-1)`
- Support Unicode superscripts: `x²` $\implies$ `x^2`, `x³` $\implies$ `x^3`.

### 4.3 Screen-Pixel Uniform Sampling (400–600 points)
- For viewport $[x_{min}, x_{max}]$ on a canvas of width $W$, step size is $\Delta x = \frac{x_{max} - x_{min}}{\text{screenPixelWidth} / 2}$.
- Produces 400–600 samples per curve per frame, guaranteeing consistent $< 0.5$ms evaluation at 60–120fps with zero garbage collection spikes during pan/zoom gestures.

### 4.4 Discontinuity & Asymptote Detection
- When stepping from $(x_i, y_i)$ to $(x_{i+1}, y_{i+1})$:
  1. If $y_{i}$ or $y_{i+1}$ is `NaN` or `Infinite` $\implies$ terminate active segment.
  2. If $y_i$ and $y_{i+1}$ have opposite signs ($y_i \cdot y_{i+1} < 0$) AND both points exceed the visible vertical viewport ($y_i < y_{min}$ and $y_{i+1} > y_{max}$ or vice versa) $\implies$ terminate active segment (asymptote detected, preventing artificial vertical connecting lines).
  3. Otherwise, append $(x_{i+1}, y_{i+1})$ to the active continuous path segment.

### 4.5 Numerical Solvers (Bisection Method)
- **Roots ($f(x) = 0$)**: Scan adjacent sample pairs for sign changes ($y_i \cdot y_{i+1} < 0$). Refine with 15–20 iterations of Bisection:
  - Guarantees unconditional convergence with tolerance $\epsilon < 10^{-5}$ without derivative divergence.
- **Local Extrema ($f'(x) = 0$)**: Compute numerical central finite difference $f'(x) \approx \frac{f(x+h) - f(x-h)}{2h}$ with $h = 10^{-4}$. Locate sign flips in $f'(x)$, refine with Bisection on $f'(x) = 0$, and classify into `LOCAL_MIN` ($f''(x) > 0$) or `LOCAL_MAX` ($f''(x) < 0$).
- **Intersections ($f(x) = g(x)$)**: Form difference function $h(x) = f(x) - g(x)$ and locate its roots via the above solver.


---

## 5. UI/UX & Jetpack Compose Components

### 5.1 `GraphCanvas`
- Handles custom drawing via `Canvas`:
  - Background & dynamic gridlines (minor and major lines based on $\log_{10}(\text{range})$).
  - Cartesian Axes $Ox$ and $Oy$ with numerical labels.
  - Multi-curve rendering with anti-aliased paths.
  - Highlight circles for roots, extrema, and intersections.
  - Interactive touch drag: displays vertical crosshair line and coordinate bubble `(x: 2.50, y: 6.25)`.
  - Double tap to reset viewport back to default $[-10, 10]$.

### 5.2 `CustomMathKeypad`
- Docked panel beneath formula cards.
- Rows of keys:
  - Row 1: `f(x)`, `x`, `^`, `√`, `(`, `)`
  - Row 2: `sin`, `cos`, `tan`, `ln`, `log`, `π`
  - Row 3: `7`, `8`, `9`, `÷`, `AC`
  - Row 4: `4`, `5`, `6`, `×`, `⌫`
  - Row 5: `1`, `2`, `3`, `−`, `+`
  - Row 6: `0`, `.`, `e`, `Clear`, `Graph / Hide`

---

## 6. Persistence & Image Export

### 6.1 Database Persistence
- `HistorySource.GRAPHING_CALCULATOR` added to `enum/HistorySource.kt`.
- Equation entries stored with serialized JSON or comma-delimited strings to recall plotted equations upon app relaunch.

### 6.2 Image Export
- Utility `GraphImageExporter.exportCanvasToPng(context, bitmap): Uri`.
- Generates a bitmap capture of the canvas and triggers `Intent.ACTION_SEND` with `image/png`.

---

## 7. Implementation Plan & Multi-Member Workload Distribution

Following `AGENTS.md`, the epic is broken down into 3 parallel, cohesive tracks:

| Ticket | Assignee | Track / Responsibilities |
| :--- | :--- | :--- |
| **Ticket 1: Core Graphing Engine & Math Algorithms** | `@dinhthianhsang` | `GraphingEngine`, variable tokenization, postfix evaluator with `x`, adaptive sampling, discontinuity detection, root/extrema solvers, JVM unit tests. |
| **Ticket 2: Interactive GraphCanvas, Touch Tracing & Keypad** | `@Tranthingocha-teo` | `GraphCanvas` Composable, coordinate mapping, dynamic grid scaling, pan/zoom gestures, point tracing & snapping tooltip, `CustomMathKeypad`, screen navigation. |
| **Ticket 3: Graphing History Persistence, Presets & PNG Exporter** | `@thuyydung` | Room schema update with `HistorySource.GRAPHING_CALCULATOR`, preset function repository, ViewModel state binding, PNG snapshot exporter & share intent. |
