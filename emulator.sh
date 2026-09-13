#!/usr/bin/env bash
set -euo pipefail

AVD_NAME="Pixel_14"

# Check if emulator is already running
if adb devices | grep -q "emulator-"; then
    echo " Máy ảo Android ($AVD_NAME) đã đang chạy."
    exit 0
fi

echo " Đang khởi chạy máy ảo Android: $AVD_NAME (GPU Host, KVM)..."
nohup emulator -avd "$AVD_NAME" -gpu host -no-boot-anim -accel on > /tmp/emulator.log 2>&1 &

echo " Đang chờ thiết bị boot xong..."
adb wait-for-device
while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
    sleep 1
done

echo " Máy ảo Android $AVD_NAME đã sẵn sàng!"
