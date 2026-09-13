# 🧮 Smart AI Calculator App

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-795548?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/DI-Koin-orange?style=for-the-badge" alt="Koin" />
  <img src="https://img.shields.io/badge/Database-Room-1572B6?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room" />
</p>

An all-in-one, modern, and intelligent calculator suite designed for Android. Built with **100% Jetpack Compose**, **Material 3**, and Clean Architecture principles, **Smart AI Calculator** delivers a seamless, pixel-perfect user experience across standard mathematics, smart AI problem solving, finance, lifestyle, and education.

---

## 🌟 Key Features

### 1. 🧮 Basic & Scientific Calculator
- Real-time arithmetic evaluation with complete parentheses handling.
- Scientific keypad support (trigonometric, power, roots, constants).
- Smooth keypad transitions and localized number formatting.

### 2. 🤖 AI Calculator
- **Camera Scanning**: Point and capture handwritten or printed math problems using CameraX.
- **AI Math Solver**: Interactive AI chat interface for step-by-step math explanations.

### 3. 💵 Tip Calculator
- **Post-tax & Pre-tax** tip calculation options via interactive dropdown.
- Real-time splitting with dedicated **People stepper** (`+` / `−`).
- Quick-select tip pills (10%, 15%, 18%, 20%) alongside custom tip inputs.
- Responsive results card: Total, Total per person, Tip, Tip per person.
- One-tap calculation sharing and instant reset.

### 4. 📅 Date Calculator
- **Start Date (Duration Mode)**:
  - Material 3 `DatePickerDialog` integration for seamless date selection.
  - Computes exact duration between dates in **Days**, **Weeks + Remainder Days**, and **Years + Remainder Days**.
  - Automatic absolute duration calculation regardless of date selection order.
- **From/To (Date Arithmetic Mode)**:
  - Add (`+`) or subtract (`−`) flexible units: Years, Months, Weeks, Days.
  - Live target date calculation with full weekday and date formatting.

### 5. 🏦 Loan Calculator
- Supports standard banking repayment methods:
  - **Equal Principal Payment** (Decreasing monthly payments with fixed principal).
  - **Equal Principal & Interest / EMI** (Fixed monthly installment).
- Inputs for Loan Principal, Annual Interest Rate (%), and Term (Months).
- Collapsible bottom numeric keypad (`UnitKeypad`).
- Instant summary breakdown: Total Payment, Monthly Payment, and Total Interest.

### 6. 🎓 GPA Calculator
- 3-column academic table: **Subject Name**, **Credits**, and **Grades**.
- Real-time **Weighted GPA** and Total Credits calculation.
- Dynamic **Add Subject** and **Edit / Delete Subject** dialogs with input validation.
- Top-bar multi-mode sorting: Sort by Grade (Descending) ➔ Alphabetical (A–Z) ➔ Default order.
- One-tap Clear All with confirmation dialog.

### 7. 📏 Unit Converter
- Real-time multi-category conversion: Length, Weight, Volume, Data, Temperature, and more.
- Dual-field interactive conversion with dedicated custom numeric keypad.

### 8. 💱 Currency Converter
- Multi-currency rate conversion with searchable currency selection dialog.

### 9. 🏷️ Discount Calculator
- Calculates original price, discount percentage, sales tax, amount saved, and final price.
- Live bill-splitting support.

### 10. 🏃 BMI (Body Mass Index) Calculator
- Comprehensive inputs: Gender, Height (ft/in), Weight (kg), and Age.
- Dedicated BMI result screen categorized with health status indicators.

### 11. 🌐 Multi-Language Support
- Fully localized in **11 languages**:
  English, Vietnamese, Arabic, Bengali, Spanish, French, Hindi, Italian, Portuguese, Russian, and Chinese.

---

## 🏗️ Architecture & Tech Stack

The app follows **Modern Android Development (MAD)** standards and Clean Architecture:

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) + StateFlow & Coroutines
- **Dependency Injection**: [Koin](https://insert-koin.io/) (`koin-android`, `koin-androidx-compose`)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with KSP
- **DataStore**: Jetpack DataStore Preferences for user settings and localization
- **Camera Integration**: [CameraX](https://developer.android.com/training/camerax) (Core, Camera2, Lifecycle, View)

---

## 📁 Project Structure

```
app/src/main/java/dhn/intern/smart_ai_caculator_app/
├── data/
│   ├── database/       # Room DB entities, DAOs, and database configuration
│   └── source/         # Menu items and static data sources
├── di/                 # Koin dependency injection modules
├── enum/               # Enums (HistorySource, Units, etc.)
├── navigation/         # NavScreen routes and AppNavHost
├── ui/
│   ├── components/     # Reusable Compose components (keypads, dialogs, buttons)
│   ├── screen/         # Feature screens (Basic, Tip, Date, Loan, GPA, BMI, etc.)
│   ├── theme/          # Color schemes, typography, and Material 3 shapes
│   └── viewmodel/      # Architecture ViewModels
└── util/               # Math parsers, datetime formatting, and extensions
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Android SDK (API 24 to 36)
- Android Emulator or physical device with USB debugging enabled

### Build & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Tranthingocha-teo/Smart_Ai_Calculator.git
   cd Smart_Ai_Calculator
   ```

2. **Launch emulator (CLI helper):**
   ```bash
   chmod +x emulator.sh run.sh
   ./emulator.sh
   ```

3. **Build and install debug APK:**
   ```bash
   ./run.sh
   ```
   Or via Gradle directly:
   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - feel free to use and adapt for personal or commercial projects.
