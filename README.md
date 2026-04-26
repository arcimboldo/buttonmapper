# ButtonMapper (Sony Bravia 4K Edition)

A minimalist, "one-shot" Android TV application designed specifically for remapping remote control buttons on a **Sony Bravia 4K**. 

> [!WARNING]
> This app was developed for a very specific use case and hardware set. It is not guaranteed to work on other TV brands or different Sony models.

> [!WARNING]
> This app was **vibe-coded** using antigravity. This app might be a huge mess, read the source code at your own risk!

## Installation & Setup

1. **Build & Install**:
   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Enable Accessibility Service (ADB Bypass)**:
   Sony TVs may hide third-party accessibility services. Use these commands to force-enable the service:
   ```bash
   adb shell settings put secure enabled_accessibility_services com.github.arcimboldo.buttonmapper/com.github.arcimboldo.buttonmapper.ButtonMapperService
   adb shell settings put secure accessibility_enabled 1
   ```

3. **Launch**:
   ```bash
   adb shell am start -n com.github.arcimboldo.buttonmapper/.MainActivity
   ```

## Usage
- Open the app and click **Add Mapping**.
- Press any button on your remote (e.g., Prime Video, Netflix).
- Select the application you want to launch instead.

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0). See the [LICENSE](LICENSE) file for details.
