# Context: Smart AI Calculator

This document defines the ubiquitous domain vocabulary and system context for the **Smart AI Calculator** Android application.

## Domain Glossary

| Term | Definition | Primary Code Location |
| :--- | :--- | :--- |
| **Basic Calculator** | Standard arithmetic engine supporting parenthesized expressions, powers, percentages, and scientific constants using Shunting-Yard parsing. | `domain/calculator/` |
| **Tip Calculator** | Bill splitting and tip computation engine with sales tax %, tip %, and multi-person splits. | `util/calculator/TipCalculatorUtil.kt` |
| **Date Calculator** | Date arithmetic engine with 2 modes: duration between dates and adding/subtracting days/weeks/months/years from a date. | `util/calculator/DateCalculatorUtil.kt` |
| **Loan Calculator** | Financial computation engine supporting Equal Principal and Equal Monthly Installment (EMI) loan amortization. | `util/calculator/LoanCalculatorUtil.kt` |
| **GPA Calculator** | Weighted Grade Point Average computation engine with multi-scale support (4.0, 10.0, 100.0) and grade/alphabetical sorting. | `util/calculator/GpaCalculatorUtil.kt` |
| **Smart AI Scanner** | CameraX-powered image capture and Multimodal Vision AI solver powered by Google Gemini 1.5 Flash. | `ui/components/aiCalculator/` |
| **AI Chatbot** | Conversational tutor providing step-by-step math explanations and Q&A. | `ui/components/aiCalculator/AiChatScreen.kt` |
| **Calculation History** | Local persistent storage using Room Database for past calculations across all calculator engines. | `data/local/` |
| **Unit Keypad** | Custom on-screen keypad designed for rapid numeric entry without triggering soft keyboard overlays. | `ui/components/UnitKeypad.kt` |
| **Crop Box Overlay** | Interactive viewport allowing users to isolate single math problems from photos before Gemini analysis. | `ui/components/aiCalculator/` |
| **Pedagogical Math Response** | 3-tier structured solution format: (1) Final Answer, (2) Step-by-step reasoning, (3) Key formulas. | `domain/ai/` |
