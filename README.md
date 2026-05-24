# ComposeTasks

A modern, production-grade Android task manager built with Jetpack Compose following the **Aether-Compose** design system (Minimal Mono aesthetic).

## Features
- **Beautiful Minimal Mono UI** — Elegant high-contrast dark theme with generous spacing
- **Google Drive Auto-Sync** — Sign in with Google and automatically save all tasks to Drive
- **Full Clean Architecture** — Domain / Data / Presentation separation
- **Jetpack Compose + Material 3** with dynamic color support
- **Hilt** dependency injection
- **Room** for local persistence
- **StateFlow** + Coroutines throughout
- **Kotlin 2.0**

## Google Drive Integration
1. Go to **Settings**
2. Tap **"Sign in with Google"**
3. Grant Drive access
4. All task changes (add/edit/delete/toggle) are automatically saved to your Google Drive as `composetasks_backup.json`

## Tech Stack
- Kotlin 2.0.20
- Compose 1.7.4 + Material3 1.3.0
- Hilt 2.51.1
- Room 2.6.1
- Navigation Compose 2.8.3
- Google Play Services Auth + Drive API v3
- Target: compileSdk 35, minSdk 24

## Build Instructions

### Prerequisites
- Android Studio Hedgehog | 2023.1.1+ (or newer)
- JDK 17+
- Android SDK (API 34+ recommended)

### Steps to Build APK

1. **Clone the repository**
2. **Add your `google-services.json`** (replace the placeholder in `app/google-services.json`)
3. **Sync Gradle** in Android Studio
4. **Build APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Release build:
   ```bash
   ./gradlew bundleRelease
   ```

### Building via GitHub Actions (Recommended)
1. Push this project to GitHub
2. Add the following **Repository Secrets**:
   - `KEYSTORE_BASE64`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
3. The workflow at `.github/workflows/android-build.yml` will automatically build and upload the APK + AAB

## Project Structure
```
app/src/main/java/com/example/composetasks/
├── data/
│   ├── drive/           # Google Drive service & sync
│   ├── local/           # Room
│   └── repository/
├── domain/              # Models, UseCases, Repository interfaces
├── presentation/        # ViewModels + UI State
├── ui/                  # Theme + Composables
└── di/                  # Hilt modules
```

## Design System
This project follows the **Aether-Compose** rules:
- Ambient dark theme (#121214)
- Minimal Mono aesthetic
- Generous spacing (8dp grid)
- No generic AI slop

---

**Note**: Replace the placeholder `google-services.json` with your real Firebase/Google Cloud configuration before building.