#!/bin/bash

set -e

echo "Deploying Functions"
#bx deploy
pushd runtimes/nodejs
wskdeploy
popd
echo "Bind Functions to Cloud Object Storage (COS)"
bx wsk service bind cloud-object-storage cloud-object-storage
