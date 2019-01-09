# package-cloud-object-storage
[![Build Status](https://travis-ci.org/ibm-functions/package-cloud-object-storage.svg?branch=master)](https://travis-ci.org/ibm-functions/package-cloud-object-storage)

### Overview
This repository allows you to deploy a Cloud Object Storage Package for IBM Functions.
The package contains a set of simple functions to get your started composing IBM Functions Applications.

### Available Languages
This package is available in Node.js 10 and Python 3.7

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
bx wsk action invoke cloud-object-storage/bucket-cors-get -b -p bucket myBucket
```
Delete CORS from a bucket `myBucket`:
```
bx wsk action invoke cloud-object-storage/bucket-cors-delete -b -p bucket myBucket
```

## CI/CD
Use helper script [`deploy.sh`](./deploy.sh)
```
deploy.sh
```

# Deploy Cloud Object Storage Package with IBM Cloud Console

## Deploy Package from the UI
In the Cloud Functions console, go to the [Create page](https://console.bluemix.net/openwhisk/create).

Using the Cloud Foundry Org and Cloud Foundry Space lists, select the namespace that you want to install the Object Storage package into. Namespaces are formed from the combined org and space names.

Click Install Packages.

Click on the IBM Cloud Object Storage Package group, and then click on the IBM Cloud Object Storage Package.

In the Available Runtimes section, select either NodeJS or Python from the drop-down list and then click Install.

Once the Package has been installed you will be redirected to the Actions page and can search for your new Package, which is named cloud-object-storage.

## Bind Service Credentials

To use the actions in the cloud-object-storage package, you must bind service credentials to the actions.
Note: You must complete the following steps for each action that you want to use.

1. Click on an Action from the cloud-object-storage Package that you want to use. The details page for that Action opens.
2. In the left-hand navigation, click on the Parameters section.
3. Enter a new parameter. For the key, enter `__bx_creds`. For the value, paste in the service credentials JSON object from the service instance that you created earlier.

Upon a successful deployment you will notice that you now have the Package **cloud-object-storage** present in your namespace.  You can use these Actions as you would any other Actions as well as edit their code to suit any specific behavior you may need from the IBM Cloud Object Storage package.

### License
Apache-2.0
