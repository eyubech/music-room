# Sourced by the Makefile before any Gradle / adb / emulator command.
# Finds a JDK and the Android SDK in their default install locations when
# JAVA_HOME / ANDROID_HOME are not already set, so `make` works out of the box
# on Linux, macOS and Windows (Git Bash).

if [ -z "${JAVA_HOME:-}" ]; then
  for d in \
    "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
    "/opt/android-studio/jbr" \
    "$HOME/android-studio/jbr" \
    "/snap/android-studio/current/jbr" \
    "/c/Program Files/Android/Android Studio/jbr"; do
    if [ -x "$d/bin/java" ] || [ -x "$d/bin/java.exe" ]; then
      JAVA_HOME="$d"
      export JAVA_HOME
      break
    fi
  done
fi

if [ -z "${ANDROID_HOME:-}" ]; then
  for d in \
    "${ANDROID_SDK_ROOT:-}" \
    "$HOME/Library/Android/sdk" \
    "$HOME/Android/Sdk" \
    "${LOCALAPPDATA:-}/Android/Sdk"; do
    if [ -n "$d" ] && [ -d "$d/platform-tools" ]; then
      ANDROID_HOME="$d"
      export ANDROID_HOME
      break
    fi
  done
fi

ADB="${ANDROID_HOME:-}/platform-tools/adb"
EMULATOR="${ANDROID_HOME:-}/emulator/emulator"
