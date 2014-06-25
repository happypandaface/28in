call gradlew --stop
call gradlew android:build
adb install -r android\build\apk\android-debug-unaligned.apk
