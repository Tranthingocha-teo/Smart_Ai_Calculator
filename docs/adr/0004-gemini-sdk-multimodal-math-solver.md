# ADR 0004: Multimodal Math Solving with Google Gemini 1.5 Flash SDK

## Status
Accepted

## Context
Solving handwritten equations, complex geometry, physics formulas, and word problems from camera photos cannot be adequately handled by traditional OCR engines (like ML Kit Text Recognition), which struggle with vertical fractions, square roots, integrals, and matrices.

## Decision
We adopted the official **Google Generative AI Android SDK (`com.google.ai.client.generativeai:generativeai`)** using **Gemini 1.5 Flash**:
- Direct image byte streaming from CameraX to Gemini Multimodal Vision API.
- Prompt-engineered output instructing the model to provide both final numerical results and structured step-by-step reasoning.
- API Key secured via Android `local.properties` and Gradle `BuildConfig`.

## Consequences
### Positive
- Unmatched reasoning power for complex math formulas and handwritten exam problems.
- Sub-second latency and high accuracy compared to OCR pipeline cascades.

### Trade-offs
- Requires internet connection for AI solving (offline fallback provides local calculation engines).
