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

Currently there is no direct way to simply deploy the Cloud Object Storage package from the IBM Cloud Functions UI.  However, there is a workaround that provides you with a way to install the cloud-object-storage package in the NodeJS runtime. 

To do this you will have to install the **COS Image Upload** template which installs the **cloud-object-storage** package as a prerequisite for the template. 

To do this follow these steps:
1. Navigate to the [IBM Cloud Functions UI](https://console.bluemix.net/openwhisk)
2. Click on the **Start Creating** button which can be found on the homepage. You may see a **Log In** button instead, if you do, first log in and then you should see the **Start Creating** button after a successful login.
3. Click on the **Quickstart Templates** section
4. Choose the template **COS Image Upload**
5. From the first page of the Template click on the **Next** button located at the bottom right of the page
6. On the following page you should see a selector that contains 3 types of configuration:
   * **Create an new instance**: Selecting this option will take you to the IBM Cloud page for creating Cloud Object Storage instances. It is important that after you create your instance you create a set of Service Credentials that contain the needed HMAC keys (See below)
   * **Input your own credentials**: Selecting this will prompt you to manually enter your own credentials for a COS instance
   * **Existing Instances**: If you already have any COS instances created they should be automatically populated in the dropdown. Clicking an existing instance will attempt to fetch the credentials as well as any Buckets existing on that instance
  
   * **Important Note**
   In order for the COS Template to be deployed properly your COS instance should have HMAC keys present as well as an already existing bucket.  For information on creating HMAC keys refer to this documentation: [Create COS Service Credentials](https://console.bluemix.net/docs/services/cloud-object-storage/iam/service-credentials.html#service-credentials)
7. After inputting all the correct information the **Deploy** button should be enabled and you can deploy the template. 
8. Upon a succesful deployment you will notice that you now have the Package **cloud-object-storage** present in your namespace.  You can set up use these Actions as you would any other Actions.

### License
Apache-2.0
