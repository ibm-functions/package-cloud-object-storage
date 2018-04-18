/**
 * This action will write to Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * @param Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "bucket": "your COS bucket name",
 *     "key": "Name of the object to be written",
 *   }
 */
const CloudObjectStorage = require('ibm-cos-sdk');

async function main(args) {
  const { cos, params } = getParamsCOS(args, CloudObjectStorage);
  const operation = params.Operation;
  delete params.Operation;

  let response;
  const result = {
    bucket: params.Bucket,
    key: params.Key,
  };

  try {
    response = await cos.getSignedUrl(operation, params);
  } catch (err) {
    console.log(err)
    result.message = err.message
    throw result;
  }
  result.url = response;
  return result;
}











function getParamsCOS(args, COS) {
  const bucket = args.bucket || args.Bucket;
  const key = args.key || args.Key;
  const operation = args.operation || args.Operation;
  const endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  const cosHmacKeysId = args.__bx_creds['cloud-object-storage'].cos_hmac_keys.access_key_id;
  const cosHmacKeysSecret = args.__bx_creds['cloud-object-storage'].cos_hmac_keys.secret_access_key;

  const params = args;
  params.Bucket = bucket;
  params.Key = key;
  params.Operation = operation;
  delete params.__bx_creds;
  const config = {
    accessKeyId: cosHmacKeysId,
    secretAccessKey: cosHmacKeysSecret,
    endpoint,
  };
  COS.config.update(config);
  const cos = new COS.S3();
  return { cos, params };
}