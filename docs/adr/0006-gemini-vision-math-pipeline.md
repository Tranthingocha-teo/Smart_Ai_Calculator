# ADR 0006: Gemini Multimodal Vision & Pedagogical Math Solving Pipeline

## Status
Accepted

## Context
Milestone 3 requires an interactive Smart AI Calculator capable of solving math problems via Camera scan (printed & handwritten) and conversational chat. The system needs to securely handle API credentials, provide structured mathematical explanations, and maintain conversational continuity between scanning and follow-up questioning.

## Decisions

1. **Credential Management**:
   - Google Gemini API Key is configured via `local.properties` (`GEMINI_API_KEY=...`).
   - Injected at compile time into `BuildConfig.GEMINI_API_KEY` through Gradle.
   - `local.properties` is strictly ignored by Git to prevent key exposure.

2. **Camera & Image Processing Pipeline**:
   - CameraX `ImageCapture` with flashlight support and system Gallery picker (`ActivityResultContracts.GetContent`).
   - Interactive Crop Box overlay allowing users to isolate specific equations/problems before submission.
   - Bitmap scaling and compression prior to sending to Gemini Multimodal API to optimize latency and token cost.

3. **Pedagogical Solution Formatting**:
   - System prompt instructs Gemini 1.5 Flash to format responses into 3 distinct sections:
     - **Final Answer**: Prominently displayed summary box at the top.
     - **Step-by-step Solution**: Detailed algebraic/calculus transitions with explanations.
     - **Key Formulas & Concepts**: Underlying mathematical properties used.
   - Formatted in clean Markdown with LaTeX-style notation.

4. **Seamless Scan-to-Chat Transition**:
   - Captured problem images and their solutions are immediately appended as active chat items in `AiChatScreen`.
   - Users can ask continuous follow-up questions within the same thread (e.g. "Explain step 2 further", "Solve using an alternative method").

5. **Localization & Network Resilience**:
   - Auto-detects problem language, defaulting to Vietnamese/English based on app locale.
   - Graceful offline fallback with retry action banners and non-destructive input caching.

## Consequences
### Positive
- Enterprise-grade API key security.
- High accuracy for handwritten and complex math formulas.
- Superior student learning experience with step-by-step guidance.
