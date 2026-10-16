#!/bin/bash

set -e

PROJECT_DIR="$1"
ARCH="$2"
APPIMAGE_ARCH="$3"
APPDIR="$4"
DIST_NAME="$5"
cd "$PROJECT_DIR/build/appimage"

# TODO: The continuous build is not pinned and not verified.
# Consider pinning a specific appimagetool release and verifying its checksum
if [[ ! -f "appimagetool-${APPIMAGE_ARCH}.AppImage" ]]; then
  wget "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-${APPIMAGE_ARCH}.AppImage"
  chmod a+x "appimagetool-${APPIMAGE_ARCH}.AppImage"
fi
"./appimagetool-${APPIMAGE_ARCH}.AppImage" -n "$APPDIR" "${PROJECT_DIR}/build/dist/artifacts/$DIST_NAME-portable-linux-$ARCH.AppImage"
