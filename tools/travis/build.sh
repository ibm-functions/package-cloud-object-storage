#!/bin/bash
# Build script for Travis-CI.

set -ex

# Build script for Travis-CI.

SCRIPTDIR=$(cd $(dirname "$0") && pwd)
ROOTDIR="$SCRIPTDIR/../.."
WHISKDIR="$ROOTDIR/../openwhisk"
UTILDIR="$ROOTDIR/../incubator-openwhisk-utilities"

export OPENWHISK_HOME=$WHISKDIR

IMAGE_PREFIX="testing"

# run scancode using the ASF Release configuration
# FIXME: Setup ibm-functions scancode cfg
#cd $UTILDIR
#scancode/scanCode.py --config scancode/ASF-Release-v2.cfg $ROOTDIR

#pull down images
docker pull openwhisk/controller
docker tag openwhisk/controller ${IMAGE_PREFIX}/controller
docker pull openwhisk/invoker
docker tag openwhisk/invoker ${IMAGE_PREFIX}/invoker

# Build OpenWhisk
cd $OPENWHISK_HOME
TERM=dumb ./gradlew install
