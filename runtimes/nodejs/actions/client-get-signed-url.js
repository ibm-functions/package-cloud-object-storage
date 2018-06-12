/**
 * This action will get a signed url based on the cloud object storage bucket, key,
 * and specified operation.  If the Cloud Object Storage service is not bound to this
 * action or to the package containing this action, then you must provide the
 * service information as argument input to this function.
 * Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "bucket": "your COS bucket name",
 *     "key": "Name of the object to be written",
 *     "operation":"putObject, getObject, or deleteObject"
 *   }
 */
const CloudObjectStorage = require('ibm-cos-sdk');

async function main(args) {
  const { cos, params } = getParamsCOS(args, CloudObjectStorage);
  let response;
  const result = {
    bucket: params.bucket,
    key: params.key,
  };

  try {
    response = await cos.getSignedUrl(params.operation, {
      Bucket: params.bucket,
      Key: params.key,
      Expires: params.expires,
    });
  } catch (err) {
    console.log(err);
    result.message = err.message;
    throw result;
  }
  result.body = response;
  return result;
}











function getParamsCOS(args, COS) {
  const { bucket, key, operation } = args;
  const expires = args.expires || 60 * 15; // url expires in 15 mins if not specified.
  const endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  const cosHmacKeysId = args.accessKeyId || args.__bx_creds['cloud-object-storage'].cos_hmac_keys.access_key_id;
  const cosHmacKeysSecret = args.secretAccessKey || args.__bx_creds['cloud-object-storage'].cos_hmac_keys.secret_access_key;

  const params = {};
  params.bucket = bucket;
  params.key = key;
  params.operation = operation;
  params.expires = expires;
  const config = {
    accessKeyId: cosHmacKeysId,
    secretAccessKey: cosHmacKeysSecret,
    endpoint,
  };
  COS.config.update(config);
  const cos = new COS.S3({ signatureVersion: 'v4' });
  return { cos, params };
}
