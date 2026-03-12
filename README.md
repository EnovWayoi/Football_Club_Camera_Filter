# ⚽ Football Club Camera Filter

An Android camera app that detects faces in real-time and randomly assigns football clubs to each person on screen — complete with animated club cards, badge overlays, and team colors.

Point your camera at one or more faces, hit **SPIN**, and watch the slot-machine-style animation cycle through hundreds of clubs before landing on a final pick for every face detected.

## ✨ Features

- **Real-Time Face Detection** — Uses Google ML Kit to detect and track multiple faces simultaneously through the camera feed.
- **Spin & Assign** — A slot-machine animation rapidly shuffles through clubs before assigning a unique club to each detected face.
- **Club Card Overlays** — Beautifully styled cards displayed above each face showing the club name, badge, and team colors.
- **Club Dashboard** — Browse and filter the full club database by **nation**, **league**, and **star rating**.
- **Selection History** — View a log of all previously assigned clubs across spins.
- **Front & Rear Camera** — Switch between lenses seamlessly.
- **700+ Clubs** — Extensive database covering clubs from leagues around the world.

## 📸 How It Works

1. **Open the app** and grant camera permission.
2. **Point the camera** at one or more faces.
3. **Tap SPIN** — the app runs a 2.5-second shuffle animation, then assigns a random club to each face.
4. Each face gets a **unique club card overlay** showing the club badge, name, league, and nation.
5. Use the **Dashboard** to filter which clubs are included in the spin pool.
6. Check **History** to see all past assignments.

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Camera** | CameraX |
| **Face Detection** | Google ML Kit |
| **Image Loading** | Coil |
| **Architecture** | MVVM (ViewModel + StateFlow) |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |

## 🗂️ Project Structure

```
app/src/main/kotlin/com/craziers/clubpicker/
├── MainActivity.kt                  # Entry point, navigation host
├── camera/
│   ├── CameraManager.kt            # CameraX setup & lifecycle binding
│   └── FaceDetectionAnalyzer.kt     # ML Kit face detection image analyzer
├── data/
│   ├── Club.kt                      # Club data class
│   └── ClubRepository.kt           # Loads clubs from assets/clubs.json
├── ui/
│   ├── CameraScreen.kt             # Main camera view with overlays
│   ├── ClubCardOverlay.kt          # Club card rendered above each face
│   ├── DashboardScreen.kt          # Club browsing with filters
│   ├── FaceOverlayCanvas.kt        # Canvas drawing face bounding boxes
│   ├── HistoryScreen.kt            # List of previously assigned clubs
│   └── SpinButton.kt               # Animated pulsing spin button
├── utils/
│   └── OverlayCoordinateMapper.kt  # Maps face coords to screen overlay
└── viewmodel/
    ├── ClubPickerViewModel.kt       # Core business logic & state
    └── MappedFace.kt                # Face data with screen-mapped bounds
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 17+**
- An Android device or emulator running **API 26+**

### Build & Run

```bash
# Clone the repository
git clone https://github.com/EnovWayoi/Football_Club_Camera_Filter.git
cd Football_Club_Camera_Filter

# Open in Android Studio and sync Gradle, or build from CLI:
./gradlew assembleDebug

# Install on a connected device:
./gradlew installDebug
```

## 🤝 Contributing

Contributions are welcome! Feel free to open issues and submit pull requests.

## ☕ Support

If you enjoy this app, consider supporting the developer:

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?logo=ko-fi&logoColor=white)](https://ko-fi.com/craziers)

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
