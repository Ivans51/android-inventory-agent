#!/usr/bin/env bash

locales=('en-US')
tests_apk_path="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
app_apk_path="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

./gradlew assembleDebug assembleAndroidTest

if [ ! -d /fastlane/metadata/android/ ]; then
  sudo mkdir -p /fastlane/metadata/android/;
fi

sudo rm -r /fastlane/metadata/android/