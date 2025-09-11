#!/bin/bash

set -e

DIR="$1"
sudo -S "/Applications/$3.app/Contents/Resources/scripts/uninstall.sh" || true
sudo -S installer -verboseR -allowUntrusted -pkg "$DIR/build/dist/artifacts/xpipe-installer-macos-$2.pkg" -target /