# ButtonMapper (Sony Bravia 4K Edition)

A minimalist, "one-shot" Android TV application designed specifically
for remapping remote control buttons on a **Sony Bravia 4K**.

Simply put, I have a "primevideo" button on my remote, but I don't
have prime video. However, I do have Jellyfin installed, and I wanted
to remap the primevideo button to start jellyfin instead. Although
there are apps that do exactly this, they come from developers I don't
trust enough to allow them to intercept any key I press, so I decided
to write my own. Not knowing anything about GoogleTV development and
having no interest in learning that, I gave
[antigravity](https://antigravity.google/) a try, using the free
Gemini 3 flash model.

> [!WARNING]
> This app was **vibe-coded**. Nobody knows what this app
> does. Reading the source code might lead to permanent brain
> damage. Using this app might lead to being hacked by a hostile
> government and being arrested as a domestic terrorist. You have been
> warned, use this app at your own risk!

## Installation & Setup

1. **Build & Install**:

   - **Debug Build (with logging)**: Use this if you need to find keycodes (logs all key presses to Logcat).
     ```bash
     ./gradlew assembleDebug
     adb install -r app/build/outputs/apk/debug/app-debug.apk
     ```
   
   - **Production Build (privacy focused)**: No key logging.
     ```bash
     ./gradlew assembleRelease
     # Note: You may need to sign the release APK before installing
     adb install -r app/build/outputs/apk/release/app-release-unsigned.apk
     ```

2. **Enable Accessibility Service (ADB Bypass)**:

   Among the attempts made by antigravity to make this app work, it
   also ran the following commands. I have no idea what they do or if
   they were actually necessary, but the app works on my TV set, so
   here they are:
   
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
- Press any button on your remote (e.g., Prime Video, Netflix). Back
  and Home should be disabled, but who knows?
- Select the application you want to launch instead.

## License

This project is licensed under the GNU General Public License v3.0
(GPL-3.0). See the [LICENSE](LICENSE) file for details. I am sure
Antigravity doesn't mind.


