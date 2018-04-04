#!/bin/bash

set -e

SCRIPTDIR=$(cd $(dirname "$0") && pwd)
ROOTDIR="$SCRIPTDIR/../.."
HOMEDIR="$SCRIPTDIR/../../../"
DEPLOYDIR="$HOMEDIR/openwhisk/catalog/extra-packages/packageDeploy"

# clone utilties repo. in order to run scanCode.py
cd ${HOMEDIR}
git clone --depth 1 https://github.com/apache/incubator-openwhisk-utilities.git

# shallow clone OpenWhisk repo.
git clone --depth 1 https://github.com/apache/incubator-openwhisk.git openwhisk

# shallow clone deploy package repo.
git clone --depth 1 https://github.com/apache/incubator-openwhisk-package-deploy $DEPLOYDIR

cd openwhisk

# use runtimes.json that defines python-jessie & IBM Node.js 8
rm -f ansible/files/runtimes.json
cp ${ROOTDIR}/ansible/files/runtimes.json ansible/files/runtimes.json

./tools/travis/setup.sh
