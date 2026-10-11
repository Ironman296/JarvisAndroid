# JarvisAndroid

An offline voice-command assistant for Android. Built with Kotlin, no cloud APIs, no API keys, no mandatory internet.

**Version 0.1.0** — Deterministic command engine with Android's built-in speech recognition and TTS.

---

## What It Does

Tap the microphone, speak a command, and Jarvis executes it on your phone:

| Voice Command | Action |
|---|---|
| "calculator kholo" / "open calculator" | Opens the calculator app |
| "settings kholo" / "open settings" | Opens device settings |
| "torch on" / "flashlight on" | Turns on the flashlight |
| "torch off" / "flashlight off" | Turns off the flashlight |
| "battery kitni hai" / "battery status" | Speaks battery level |
| "volume up" / "volume badhao" | Increases media volume |
| "volume down" / "volume kam karo" | Decreases media volume |
| "time kya hai" / "kitne baje" | Speaks the current time |
| "wifi kholo" / "wifi settings" | Opens Wi-Fi settings |
| "bluetooth kholo" / "bluetooth settings" | Opens Bluetooth settings |

---

## Project Structure

```
JarvisAndroid/
├── .github/workflows/build.yml    ← GitHub Actions CI
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/jarvis/android/
│       │   ├── MainActivity.kt          ← UI + permission handling
│       │   ├── commands/
│       │   │   ├── Command.kt           ← Command data model
│       │   │   └── CommandEngine.kt     ← Deterministic matcher
│       │   ├── actions/
│       │   │   └── DeviceActions.kt     ← Android API actions
│       │   └── speech/
│       │       ├── SpeechRecognizerManager.kt  ← Speech input
│       │       └── TextToSpeechManager.kt      ← Speech output
│       └── res/                         ← Layout, colors, strings
├── build.gradle.kts                     ← Root build config
├── settings.gradle.kts
├── gradle.properties
└── gradle/wrapper/gradle-wrapper.properties
```

### Module Responsibilities

| Module | What it does |
|---|---|
| `speech/` | Speech recognition (input) and TextToSpeech (output) |
| `commands/` | Command data model + deterministic matching engine |
| `actions/` | Device actions via Android system APIs |
| `MainActivity` | UI, permissions, and wiring everything together |

---

## How to Build (Step-by-Step)

You do **not** need Android Studio or the Android SDK on your computer. The APK is built in the cloud using GitHub Actions.

### Step 1: Create a GitHub Repository

1. Go to [github.com/new](https://github.com/new)
2. Name it `JarvisAndroid`
3. Leave it **Public** (free Actions minutes)
4. Do **NOT** check "Add a README" (we already have one)
5. Click **Create repository**

### Step 2: Push This Code to GitHub

Open a terminal in the `JarvisAndroid` folder and run:

```bash
git init
git add .
git commit -m "Initial commit: JarvisAndroid v0.1.0"
git branch -M main
git remote add origin https://github.com/Ironman296/JarvisAndroid.git
git push -u origin main
```

### Step 3: The Build Starts Automatically

1. Go to your repository on GitHub
2. Click the **Actions** tab
3. You should see a workflow run called **"Build Debug APK"** already running
4. Wait for it to finish (usually 3–5 minutes)

If you don't see it, click **"Run workflow"** → **"Run workflow"** on the main branch.

### Step 4: Download the APK

1. Click on the completed workflow run
2. Scroll to the **Artifacts** section at the bottom
3. Click **jarvis-android-debug-apk** to download the ZIP
4. Extract the ZIP — you'll find `app-debug.apk` inside

### Step 5: Install on Your Phone

1. Transfer `app-debug.apk` to your Android phone (USB, email, Google Drive, etc.)
2. Open the APK on your phone
3. If prompted, enable **"Install from unknown sources"** for your file manager
4. Install and open the app
5. Grant microphone permission when asked
6. Tap the mic button and speak a command!

---

## Architecture Decisions

| Decision | Rationale |
|---|---|
| **No Android Studio required** | Build runs on GitHub Actions (free Ubuntu runner) |
| **Kotlin + AGP 8.2.2** | Modern, stable, well-supported |
| **Gradle 8.5** | Compatible with AGP 8.2.2, pinned via wrapper properties |
| **JDK 17** | Required by AGP 8.x |
| **compileSdk 34, minSdk 24** | Covers 98%+ of active Android devices |
| **Deterministic command engine** | No ML, no cloud, no latency — just pattern matching |
| **Android SpeechRecognizer** | Built-in, works immediately (may need internet on some devices) |
| **Android TextToSpeech** | Fully offline on most devices |

---

## Known Limitations (v0.1.0)

> **Be honest about what's not tested:**

1. **Speech recognition may need internet** — Android's built-in `SpeechRecognizer` typically uses Google's cloud service. Offline recognition depends on whether the device has downloaded offline speech data (Settings → Languages → Offline speech recognition).

2. **No wake-word detection** — "Hey Jarvis" is not implemented. You must tap the mic button.

3. **Hinglish recognition accuracy** — Whether the recognizer correctly transcribes Hinglish ("calculator kholo") depends on the device's speech engine and language settings.

4. **No launcher icon** — The app uses the system default icon in v0.1.0.

5. **No Vosk integration yet** — True offline speech recognition via Vosk is planned but not added (to keep the first build clean and compilable).

---

## Future Roadmap

- [ ] **Vosk offline speech recognition** — Replace/supplement Android SpeechRecognizer with Vosk for true offline capability
- [ ] **Wake-word detection** — "Hey Jarvis" using Porcupine or a compatible on-device detector
- [ ] **Custom launcher icon** — Branded app icon
- [ ] **More commands** — Open specific apps by name, set alarms, make calls
- [ ] **Continuous listening mode** — Auto-restart recognition after each command
- [ ] **Laya integration** — After the deterministic engine is proven, evaluate Laya for natural language understanding

---

## Dependencies

All versions are pinned for reproducible builds:

| Dependency | Version |
|---|---|
| Android Gradle Plugin | 8.2.2 |
| Kotlin | 1.9.22 |
| Gradle | 8.5 |
| AndroidX Core KTX | 1.12.0 |
| AndroidX AppCompat | 1.6.1 |
| Material Components | 1.11.0 |
| ConstraintLayout | 2.1.4 |

No external ML libraries, no API keys, no cloud services.

---

## License

This project is for personal/educational use.
