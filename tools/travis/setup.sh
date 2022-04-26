#!/bin/bash

set -ex

# Build script for Travis-CI.

SCRIPTDIR=$(cd $(dirname "$0") && pwd)
ROOTDIR="$SCRIPTDIR/../.."
WHISKDIR="$ROOTDIR/../openwhisk"
UTILDIR="$ROOTDIR/../openwhisk-utilities"
HOMEDIR="$SCRIPTDIR/../../../"

# clone utilties repo. in order to run scanCode.py
cd ${HOMEDIR}
git clone --depth 1 https://github.com/apache/openwhisk-utilities.git

# clone OpenWhisk repo.
git clone https://github.com/apache/openwhisk.git openwhisk

# shallow clone deploy package repo.
git clone --depth 1 https://github.com/apache/openwhisk-package-deploy

cd openwhisk

# Use a fixed commit to run the tests, to explicitly control when changes are consumed.
# Commit: minor version bump of azure-storage-blob to fix builds (#5150)
git checkout 3e6138d088fbd502a69c31314ad7c0089c5f5283

./tools/travis/setup.sh
