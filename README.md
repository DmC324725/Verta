# Verta 📱🎬

**Verta** is a modern Android application that transforms your local video library into an immersive, infinite vertical video feed (similar to TikTok, YouTube Shorts, or Instagram Reels). Built natively with **Jetpack Compose**, **AndroidX Media3 ExoPlayer**, and **Paging 3**, Verta lets you pick a folder of local videos, shuffle them endlessly, and enjoy seamless, zero-friction playback.

---

## ✨ Key Features

- **Immersive Vertical Video Feed**: Swipe vertically through your local videos in a polished full-screen feed with smooth pagination.
- **Folder Picker & Persistence**: Select any local directory using the Android Storage Access Framework (SAF); Verta remembers and automatically reloads your preferred folder across sessions.
- **Smart Shuffling & Paging 3**: Leverages Paging 3 and robust use-case pipelines to deliver a seamless, infinite shuffled stream of your video files.
- **High-Performance Playback**: Powered by **AndroidX Media3 ExoPlayer** and Compose UI integration for optimal hardware-accelerated video rendering.
- **Material 3 Design**: Clean, modern UI adhering to Material 3 guidelines with custom adaptive iconography and polished overlays.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
- **Video Playback**: [AndroidX Media3 ExoPlayer](https://developer.android.com/guide/topics/media/media3)
- **Data & Pagination**: [AndroidX Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- **Concurrency**: Kotlin Coroutines & `StateFlow`
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture use-case separation

---

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/Verta.git
   ```
2. **Open in Android Studio**:
   - Open Android Studio and select **Open**.
   - Navigate to the cloned `Verta` directory.
3. **Build and Run**:
   - Sync Gradle and run the app on an emulator or physical device running **Android 10 (API 29)** or higher.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
