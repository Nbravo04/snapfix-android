# CLAUDE.md - SpotFix Android

## Project Overview

SpotFix is an AI-powered home-repair diagnostic camera app for Android. Users point their camera at household items to receive real-time object detection and context-aware repair advice. All ML inference runs on-device using TensorFlow Lite.

**Key Features:**
- Real-time object detection (90+ COCO classes)
- On-device ML inference (no internet required)
- Repair advice generation for detected items
- Image capture and sharing

## Quick Reference

```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device
./gradlew installDebug

# Clean build
./gradlew clean
```

## Architecture

**Pattern:** MVVM with Jetpack Compose and StateFlow

```
Navigation.kt (NavHost)
    │
    ├── SplashScreen.kt
    ├── CameraScreen.kt ─────┐
    ├── ResultScreen.kt ─────┼── SharedViewModel (StateFlow)
    └── AdviceScreen.kt ─────┘
                                    │
                              EfficientDetDetector (TFLite)
                              SpotFixAnalyzer (CameraX)
```

**State Flow:** User Action → Screen Composable → ViewModel Update → StateFlow Emission → UI Recomposition

## Project Structure

```
app/src/main/java/com/spotfix/android/
├── MainActivity.kt              # Activity entry point
├── Navigation.kt                # Navigation routes (sealed class Screen)
├── SharedViewModel.kt           # Cross-screen state management
├── CameraScreen.kt              # Live camera detection UI
├── ResultScreen.kt              # Detection results display
├── AdviceScreen.kt              # Repair advice UI
├── SplashScreen.kt              # Splash animation
├── EfficientDetDetector.kt      # TFLite model wrapper
├── SpotFixAnalyzer.kt           # ImageAnalysis.Analyzer
├── Utils.kt                     # Detection info mapping
├── BitmapExtensions.kt          # Image conversion helpers
└── ui/theme/                    # Material3 theming
```

**Key Resources:**
- `assets/efficientdet_lite0.tflite` - Primary ML model (4.5 MB)
- `res/drawable/spotfix_logo.png` - App logo
- `res/mipmap-*/` - Launcher icons (multiple densities)

## Tech Stack

| Category | Technologies |
|----------|-------------|
| Language | Kotlin 100% |
| UI | Jetpack Compose, Material3 |
| ML | TensorFlow Lite 2.16.1 |
| Camera | CameraX 1.4.0 |
| State | ViewModel, StateFlow |
| Navigation | Compose Navigation 2.8.5 |
| Async | Kotlin Coroutines |
| Testing | JUnit, MockK, Turbine, Robolectric |

## Build Configuration

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 15)
- **Compile SDK:** 36
- **JVM Target:** Java 11
- **Package:** `com.spotfix.android`

## Code Conventions

### Naming
- Screens: `*Screen.kt` (CameraScreen, ResultScreen)
- ViewModels: `*ViewModel.kt`
- Extensions: `*Extensions.kt`
- Tests: `*Test.kt`

### Patterns

**Screen Composables:**
```kotlin
@Composable
fun ExampleScreen(
    viewModel: SharedViewModel,
    onNavigate: () -> Unit
) {
    val state by viewModel.someState.collectAsState()
    // UI content
}
```

**ViewModel State:**
```kotlin
private val _state = MutableStateFlow<Type?>(null)
val state: StateFlow<Type?> = _state.asStateFlow()

fun updateState(value: Type) {
    _state.value = value
}
```

**Extension Functions:**
```kotlin
fun Context.uriToBitmap(uri: Uri): Bitmap?
fun Bitmap.drawDetections(detections: List<Detection>): Bitmap
```

### Data Classes
- `Detection(boundingBox: RectF, label: String, score: Float)`
- `CapturedData(bitmap: Bitmap, detections: List<Detection>)`
- `DetectionInfo(iconName: String, helpText: String)`
- `RepairAdvice(title: String, description: String, steps: List<String>)`

## Testing

**Test Location:** `app/src/test/java/com/spotfix/android/`

**Run Tests:**
```bash
./gradlew test                    # All unit tests
./gradlew testDebugUnitTest       # Debug variant only
./gradlew connectedAndroidTest    # Instrumented tests
```

**Test Files:**
- `EfficientDetDetectorTest.kt` - Detection validation
- `SharedViewModelTest.kt` - StateFlow testing
- `SpotFixAnalyzerTest.kt` - Image analysis
- `UtilsTest.kt` - Utility functions
- `BitmapExtensionsTest.kt` - Image conversions
- `AdviceGenerationTest.kt` - Advice logic

**Testing Patterns:**
```kotlin
@Test
fun `example test with descriptive name`() = runTest {
    val result = viewModel.state.first()
    assertEquals(expected, result)
}
```

## ML Model Details

**EfficientDet-Lite0:**
- Input: 320x320 UINT8 tensor
- Output: 25 max detections
- Classes: 90 COCO dataset objects
- Inference: ~50-100ms on modern devices

**Detection Pipeline:**
1. CameraX captures frame → `SpotFixAnalyzer`
2. Convert ImageProxy to Bitmap
3. Resize to 320x320, normalize
4. Run TFLite inference
5. Filter by confidence threshold (0.5)
6. Transform coordinates to display space

## Common Tasks

### Adding a New Screen
1. Create `NewScreen.kt` with `@Composable` function
2. Add route to `Navigation.kt` sealed class
3. Add navigation case in `NavHost`
4. Update `SharedViewModel` if state needed

### Modifying Detection Logic
- Detection filtering: `EfficientDetDetector.kt`
- Camera analysis: `SpotFixAnalyzer.kt`
- Coordinate mapping: `Utils.kt`

### Updating Theme
- Colors: `ui/theme/Color.kt`
- Typography: `ui/theme/Type.kt`
- Theme config: `ui/theme/Theme.kt`

### Adding Detection Categories
1. Update `getDetectionInfo()` in `Utils.kt`
2. Add corresponding icon mapping
3. Update `getRepairAdvice()` for new category

## Dependencies Management

Dependencies use version catalog in `gradle/libs.versions.toml`. Add new dependencies:

```kotlin
// In app/build.gradle.kts
dependencies {
    implementation(libs.new.library)
}
```

## Important Notes

### Permissions
- `CAMERA` - Required for live detection
- `READ_MEDIA_IMAGES` - Android 13+ gallery access
- `READ_EXTERNAL_STORAGE` - Pre-Android 13 gallery

### Resource Management
- Always call `image.close()` in `SpotFixAnalyzer`
- Call `interpreter?.close()` when done with detector
- Use `withContext(Dispatchers.IO)` for file operations

### Performance
- ImageAnalysis uses `STRATEGY_KEEP_ONLY_LATEST` for backpressure
- Model loaded once in ViewModel lifecycle
- Bitmap operations on background thread

## Git Workflow

- Main branch: `main`
- Feature branches: `claude/feature-name-*` or `feature/description`
- Commit messages: Imperative mood, concise description

## Troubleshooting

**Build fails with TFLite:**
- Ensure `useLegacyPackaging = true` in build.gradle.kts
- Check model file exists in `assets/`

**Camera not working:**
- Verify CAMERA permission granted
- Check CameraX lifecycle binding

**Tests fail:**
- Run with `--info` flag for details
- Check MockK relaxed settings for Bitmap mocks
