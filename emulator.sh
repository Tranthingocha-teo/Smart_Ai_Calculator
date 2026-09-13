#!/usr/bin/env bash
set -euo pipefail

AVD_NAME="Pixel_14"

# Check if emulator is already running
if adb devices | grep -q "emulator-"; then
    echo " Máy ảo Android ($AVD_NAME) đã đang chạy."
    exit 0
fi

# Clean up any stale lock files from previous crashes
tmux kill-session -t android_emulator 2>/dev/null || true
rm -f "$HOME/.android/avd/${AVD_NAME}.avd/"*.lock 2>/dev/null || true
rm -rf /run/user/"$(id -u)"/avd/running/* 2>/dev/null || true

echo " Đang khởi chạy máy ảo Android: $AVD_NAME qua tmux session..."
tmux new-session -d -s android_emulator "emulator -avd '$AVD_NAME' -gpu swiftshader -no-snapshot-load -no-boot-anim -accel on > /tmp/emulator.log 2>&1"

echo " Đang chờ thiết bị boot xong..."
adb wait-for-device
while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
    sleep 2
done

echo " Máy ảo Android $AVD_NAME đã sẵn sàng!"
adb devices
