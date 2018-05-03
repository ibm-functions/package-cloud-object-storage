# package-cloud-object-storage
[![Build Status](https://travis-ci.org/ibm-functions/package-cloud-object-storage.svg?branch=master)](https://travis-ci.org/ibm-functions/package-cloud-object-storage)

# Work in progress !!

Do not use for production.

### Overview
This repository allows you to deploy a Cloud Object Storage Package for IBM Functions.
The package contains a set of simple functions to get your started composing IBM Functions Applications.

### Available Languages
This package is available in Node.js 8, Node.js 6, and Python 3.

# Deploy Cloud Object Storage Package with IBM Cloud Command Line Interface (CLI)

## Configure CLI
- Make sure to execute `bx login` if not already logged in
- Install IBM Functions CLI plugin
```
bx plugin install cloud-functions
```
Make sure you are authenticated with IBM Functions and can list entities without errors
```
bx wsk list
```
## Deploy

Use `wskdeploy` to deploy using [`manifest.yml`](./manifest.yml).  You can deploy either the
Node.js or Python version of the `cloud-object-storage` package.
```
pushd runtimes/nodejs/
wskdeploy
popd
```

This will create a new package `cloud-object-storage` with the following actions:
- cloud-object-storage/object-read
- cloud-object-storage/object-write
- cloud-object-storage/object-delete
- cloud-object-storage/client-get-signed-url
- cloud-object-storage/bucket-cors-get
- cloud-object-storage/bucket-cors-put
- cloud-object-storage/bucket-cors-delete


**Future**
 The utility `wskdeploy` will be integrated into a new `wsk` plugin command `bx wsk deploy`.
For now download from here [wskdeploy download](https://github.com/apache/incubator-openwhisk-wskdeploy/releases) and add `wskdeploy` to your PATH

## Bind service credentials
You will need to bind your Cloud Object Storage service to the `cloud-object-storage` package, so that the Actions will have access to the service credentials.
```
bx wsk service bind cloud-object-storage cloud-object-storage
```


## Test
Write a file `data.txt` into bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/object-write -b -p bucket myBucket -p key data.txt -p body "Hello World"
```
Read a file `data.txt` from bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/object-read -b -p bucket myBucket -p key data.txt
```
Delete a file `data.txt` from bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/object-delete -b -p bucket myBucket -p key data.txt
```
Get a signed URL to GET a file `data.txt` from bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/client-get-signed-url -b -p bucket myBucket -p key data.txt -p operation getObject
```
Add CORS to a bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/bucket-cors-put -b -p bucket myBucket -p corsConfig "{\"CORSRules\":[{\"AllowedHeaders\":[\"*\"], \"AllowedMethods\":[\"POST\",\"GET\",\"DELETE\"], \"AllowedOrigins\":[\"*\"]}]}"
```
Read CORS on a bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/bucket-cors-get -b -p bucket myBucket"
```
Delete CORS from a bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/bucket-cors-delete -b -p bucket myBucket"
```

## CI/CD
Use helper script [`deploy.sh`](./deploy.sh)
```
deploy.sh
```

# Deploy Cloud Object Storage Package with IBM Cloud Console

** Comming Soon**
Visit [IBM Functions Templates](https://console.bluemix.net/openwhisk/create/template)


### License
Apache-2.0
