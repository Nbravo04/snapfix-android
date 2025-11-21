# SpotFix

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/TensorFlow%20Lite-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white" alt="TensorFlow Lite">
</p>

## 🏠 Overview

**SpotFix** is an AI-powered home-repair diagnostic camera application for Android that leverages real-time object detection to help homeowners identify and troubleshoot household items and potential repair issues. Simply point your camera at appliances, plumbing fixtures, or other home items, and SpotFix will instantly detect and classify them, providing helpful context and repair advice.

Built with modern Android development practices, SpotFix combines **CameraX**, **Jetpack Compose**, and **TensorFlow Lite** to deliver a seamless, production-ready experience with on-device machine learning.

## ✨ Key Features

### 🎥 Real-Time Object Detection
- **Live camera preview** with instant object detection overlay
- **On-device AI processing** using EfficientDet-Lite TFLite model
- **Color-coded bounding boxes**:
  - 🔵 Blue: Plumbing & appliances (sink, toilet, refrigerator, oven, microwave, etc.)
  - 🟠 Orange: Safety-related items (person detection)
  - 🟢 Green: General household items
- **High-accuracy detection** with confidence scores displayed in real-time

### 📸 Capture & Analyze
- **Snap photos** with the large circular shutter button
- **Import from gallery** to analyze existing photos
- **Persistent detection overlay** on captured images
- **Detailed results screen** with:
  - Full-screen annotated image
  - Scrollable list of all detections sorted by confidence
  - Contextual icons and helpful descriptions for each detected item
  - Share functionality to export annotated images

### 🛠️ Smart Repair Advice
- **AI-generated repair tips** (placeholder for future integration with Grok API)
- **Context-aware suggestions** based on detected objects
- **Easy sharing** of advice and diagnostic results
- **Professional guidance** for common household issues

### 🎨 Modern UI/UX
- **100% Jetpack Compose** with Material Design 3
- **Blue and orange theme** optimized for home repair context
- **Intuitive navigation** with smooth transitions
- **Responsive design** that handles configuration changes gracefully
- **Custom splash screen** with house icon branding

## 🏗️ Technical Stack

### Core Technologies
- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material3)
- **Camera**: CameraX API
- **Machine Learning**: TensorFlow Lite (EfficientDet-Lite0/Lite2)
- **Architecture**: MVVM with ViewModel + StateFlow
- **Image Loading**: Coil
- **Build System**: Gradle with Kotlin DSL

### Technical Specifications
- **compileSdk**: 36
- **minSdk**: 24 (Android 7.0+)
- **targetSdk**: 36
- **On-device inference**: No internet required for detection
- **Model**: EfficientDet-Lite (included in assets)

## 📁 Project Structure

```
spotfix-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/spotfix/android/
│   │   │   ├── ui/
│   │   │   │   ├── screen/
│   │   │   │   │   ├── CameraScreen.kt          # Main camera interface
│   │   │   │   │   ├── ResultScreen.kt          # Detection results display
│   │   │   │   │   └── AdviceScreen.kt          # Repair advice interface
│   │   │   │   └── theme/                       # Material3 theme configuration
│   │   │   ├── viewmodel/
│   │   │   │   └── CameraViewModel.kt           # State management
│   │   │   ├── utils/
│   │   │   │   └── EfficientDetDetector.kt      # TFLite model wrapper
│   │   │   ├── model/
│   │   │   │   └── Detection.kt                 # Detection data class
│   │   │   └── MainActivity.kt                  # App entry point
│   │   ├── assets/
│   │   │   └── efficientdet.tflite              # TensorFlow Lite model
│   │   ├── res/
│   │   │   ├── drawable/                        # Icons and graphics
│   │   │   └── values/                          # Colors, strings, themes
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with API level 36
- Physical Android device or emulator running Android 7.0 (API 24) or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Nbravo04/spotfix-android.git
   cd spotfix-android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle
   - If not, click "Sync Project with Gradle Files"

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`
   - Grant camera and storage permissions when prompted

### Required Permissions
The app requires the following runtime permissions:
- **Camera**: For live object detection and photo capture
- **Read External Storage**: For importing photos from gallery (API < 33)
- **Read Media Images**: For gallery access on Android 13+ (API 33+)

## 🎯 How It Works

### Detection Pipeline
1. **Camera Preview**: CameraX provides real-time camera frames
2. **Preprocessing**: Frames are converted to bitmap and resized for model input
3. **Inference**: EfficientDetDetector runs TFLite model on-device
4. **Post-processing**: Detections are filtered by confidence threshold (default: 0.4)
5. **Rendering**: Bounding boxes and labels are drawn using Canvas in Compose

### Detection Categories
The app recognizes 90+ object classes from the COCO dataset, with special focus on:
- **Plumbing**: sink, toilet, bathtub
- **Kitchen Appliances**: refrigerator, oven, microwave, dishwasher
- **Containers**: bottle, cup, bowl
- **Furniture**: chair, couch, table, bed
- **Electronics**: TV, laptop, cell phone
- **Safety**: person detection for workspace awareness

### State Management
- **CameraViewModel** manages:
  - Camera state and permissions
  - Detected objects list
  - Captured image bitmap
  - Navigation flow
- **StateFlow** ensures reactive UI updates
- **Lifecycle-aware** components prevent memory leaks

## 🔧 Configuration

### Model Settings
Edit `EfficientDetDetector.kt` to adjust:
- **Confidence threshold**: Minimum score for displaying detections
- **IOU threshold**: Non-maximum suppression overlap threshold
- **Max detections**: Maximum number of objects to detect per frame

### Theme Customization
Modify colors in `ui/theme/Color.kt`:
- Primary blue for plumbing/appliances
- Secondary orange for safety alerts
- Custom color scheme for home repair context

## 🔮 Future Enhancements

- [ ] **AI Repair Advisor**: Integration with Grok API for intelligent repair suggestions
- [ ] **Damage Assessment**: Computer vision for detecting cracks, leaks, or damage
- [ ] **AR Measurements**: Augmented reality for measuring objects and spaces
- [ ] **Repair History**: Local database to track past inspections and fixes
- [ ] **Expert Network**: Connect with local repair professionals
- [ ] **Video Analysis**: Record and analyze video for more complex diagnostics
- [ ] **Multi-language Support**: Localization for international users
- [ ] **Cloud Backup**: Optional cloud sync for inspection history

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Nbravo04**

## 🙏 Acknowledgments

- **TensorFlow Lite** for efficient on-device ML
- **Google's CameraX** for modern camera API
- **Jetpack Compose** for declarative UI
- **EfficientDet** model from TensorFlow Model Garden
- **COCO Dataset** for object detection training data

---

**Built with ❤️ for homeowners who want to understand and fix their homes**
