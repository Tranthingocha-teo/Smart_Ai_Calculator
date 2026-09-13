#!/usr/bin/env bash
set -euo pipefail

PACKAGE_NAME="dhn.intern.smart_ai_caculator_app"
ACTIVITY_NAME=".MainActivity"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Check if any device or emulator is connected
if ! adb devices | grep -E "(emulator|device)" | grep -v "List" | grep -q "device"; then
    echo "⚠️ Không tìm thấy thiết bị hoặc máy ảo nào đang chạy."
    echo " Tự động khởi động máy ảo Pixel_14..."
    ./emulator.sh
fi

echo " Đang build ứng dụng..."
./gradlew assembleDebug --daemon

if [ ! -f "$APK_PATH" ]; then
    echo "❌ Không tìm thấy file APK: $APK_PATH"
    exit 1
fi

echo "📲 Đang cài đặt $APK_PATH lên thiết bị..."
adb install -r "$APK_PATH"

echo " Khởi chạy ứng dụng..."
adb shell am start -n "$PACKAGE_NAME/$ACTIVITY_NAME"

echo " Ứng dụng đã được mở thành công!"
echo "--- Logcat (Ctrl+C để dừng) ---"
adb logcat -v time --pid="$(adb shell pidof -s $PACKAGE_NAME 2>/dev/null || true)"
