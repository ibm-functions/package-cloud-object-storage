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
cd $UTILDIR
scancode/scanCode.py --config scancode/ASF-Release-v2.cfg $ROOTDIR

# Build OpenWhisk
cd $WHISKDIR

#pull down images
docker pull openwhisk/controller
docker tag openwhisk/controller ${IMAGE_PREFIX}/controller
docker pull openwhisk/invoker
docker tag openwhisk/invoker ${IMAGE_PREFIX}/invoker
docker pull openwhisk/nodejs6action
docker tag openwhisk/nodejs6action ${IMAGE_PREFIX}/nodejs6action
docker pull ibmfunctions/action-nodejs-v8
docker tag ibmfunctions/action-nodejs-v8 ${IMAGE_PREFIX}/action-nodejs-v8
docker pull ibmfunctions/action-python-v3
docker tag ibmfunctions/action-python-v3 ${IMAGE_PREFIX}/action-python-v3

TERM=dumb ./gradlew install


cd $WHISKDIR/ansible

# Deploy Openwhisk
ANSIBLE_CMD="ansible-playbook -i ${ROOTDIR}/ansible/environments/local -e docker_image_prefix=${IMAGE_PREFIX}"

$ANSIBLE_CMD setup.yml
$ANSIBLE_CMD prereq.yml
$ANSIBLE_CMD couchdb.yml
$ANSIBLE_CMD initdb.yml
$ANSIBLE_CMD wipe.yml
$ANSIBLE_CMD openwhisk.yml

cd $WHISKDIR

#update whisk.properties to add tests/credentials.json file to vcap.services.file, which is needed in tests
WHISKPROPS_FILE="$WHISKDIR/whisk.properties"
cat whisk.properties

WSK_CLI=$WHISKDIR/bin/wsk
AUTH_KEY=$(cat $WHISKDIR/ansible/files/auth.whisk.system)
EDGE_HOST=$(grep '^edge.host=' $WHISKPROPS_FILE | cut -d'=' -f2)

# Set Environment
export OPENWHISK_HOME=$WHISKDIR

# Place this template in correct location to be included in packageDeploy
mkdir -p $PACKAGESDIR/preInstalled/ibm-functions
cp -r $ROOTDIR/package-cloud-object-storage $PACKAGESDIR/preInstalled/ibm-functions/

# Install the deploy package
cd $PACKAGESDIR/packageDeploy/packages
source $PACKAGESDIR/packageDeploy/packages/installCatalog.sh $AUTH_KEY $EDGE_HOST $WSK_CLI

# Test
cd $ROOTDIR/package-cloud-object-storage
./gradlew :tests:test
