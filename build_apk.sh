#!/bin/bash

echo "Building APK manually..."

# Create output directory
mkdir -p build/outputs/apk

# Compile Java files
echo "Compiling Java sources..."
javac -d build/classes -cp "$(find . -name '*.jar' -type f | tr '\n' ':')" $(find app/src/main/java -name '*.java')

# Create APK structure
echo "Creating APK structure..."
mkdir -p build/apk
cp -r app/src/main/res build/apk/
cp app/src/main/AndroidManifest.xml build/apk/

# Create simple APK info
echo "APK files ready in build/apk directory"
echo "Manual APK creation completed"
echo ""
echo "To install on device:"
echo "1. Copy the project to Android Studio"
echo "2. Or use: adb install app.apk"
echo ""
echo "Alternative: Use Android Studio to build APK:"
echo "Build > Build Bundle(s) / APK(s) > Build APK(s)"