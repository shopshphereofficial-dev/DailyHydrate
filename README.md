# DailyHydrate — Water Tracker

A complete, single-screen Android hydration tracker built with **Kotlin** and
**Jetpack Compose** (Material Design 3). It is 100% offline, requests zero
runtime permissions, and carries no ad or analytics SDKs.

## Install the app

Prebuilt binaries are published on the [Releases page](https://github.com/shopshphereofficial-dev/DailyHydrate/releases/latest):

- **`app-debug.apk`** — sideload directly onto any Android 5.0+ device (enable
  "Install unknown apps" for your file manager first).
- **`app-release.aab`** — the Play-ready App Bundle.

The APK ships `arm64-v8a`, `armeabi-v7a`, `x86` and `x86_64`, so it runs on
virtually every Android phone and tablet in use.

## Features

- **Circular progress ring** — animated sweep showing `total / goal` (default
  goal 2500 ml, editable by tapping the goal chip).
- **Quick-add tiles** — `+200 ml` (cup), `+500 ml` (bottle), `+1000 ml` (flask)
  and a **Custom Add** dialog with preset chips.
- **Today's Log card** — timestamped entries with smooth add/remove animations
  and a reset action (with confirmation).
- **Persistence** — Jetpack DataStore (Preferences) survives restarts; the log
  is scoped to the current day.
- **Light & dark themes** — pastel aqua/teal palette, Material 3 components.

## Architecture

```
app/src/main/java/com/dailyhydrate/app/
├── MainActivity.kt                 # Compose host, edge-to-edge
├── data/
│   ├── WaterEntry.kt               # immutable model
│   └── WaterRepository.kt          # DataStore persistence
├── viewmodel/
│   └── WaterViewModel.kt           # StateFlow<HomeUiState> + actions
└── ui/
    ├── HomeScreen.kt               # screen + reusable composables
    └── theme/                      # Color / Type / Theme
```

Unidirectional data flow: `WaterRepository` exposes `Flow`s → `WaterViewModel`
combines them into a single `HomeUiState` → `HomeScreen` renders it and calls
back with intents (`add`, `reset`, `updateGoal`).

## Build configuration

| Setting      | Value        |
|--------------|--------------|
| `compileSdk` | 35           |
| `targetSdk`  | 35           |
| `minSdk`     | 21           |
| `versionCode`| 1            |
| `versionName`| 1.0.0        |
| AGP          | 8.7.2        |
| Kotlin       | 2.0.21       |

## Local build

```bash
# Debug APK
./gradlew assembleDebug

# Release App Bundle (unsigned unless signing env vars are set)
./gradlew bundleRelease
```

The output bundle lands in `app/build/outputs/bundle/release/app-release.aab`.

### Optional signing

Set these four environment variables (or GitHub Actions secrets) to produce a
signed AAB; leave them unset and the release build is unsigned:

```
KEYSTORE_PATH      # absolute path to your .jks / .keystore
KEYSTORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD
```

## CI

`.github/workflows/build.yml` runs on every push / PR and on manual dispatch.
It builds a debug APK as a sanity check, then `bundleRelease`, and uploads both
as workflow artifacts.

## Permissions

None. `AndroidManifest.xml` declares no `<uses-permission>` entries at all —
no internet, camera, storage, or background tracking.
