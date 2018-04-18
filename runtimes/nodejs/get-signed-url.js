/**
 * This action will write to Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * @param Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "Bucket": "your COS bucket name",
 *     "Key": "Name of the object to write",
 *     "Body": "Body of the object to write"
 *   }
 */
const CloudObjectStorage = require('ibm-cos-sdk');

async function main(args) {
  const { cos, params } = getParamsCOS(args, CloudObjectStorage);
  const operation = params.Operation;
  delete params.Operation;

  let response;
  try {
    response = await cos.getSignedUrl(operation, params);
  } catch (err) {
    return Promise.reject(new Error({
      Bucket: params.Bucket,
      Key: params.Key,
      Error: err,
    }));
  }
  return {
    Bucket: params.Bucket,
    Key: params.Key,
    SignedUrl: response,
  };
}












function getParamsCOS(args, COS) {
  const Bucket = args.bucket || args.Bucket;
  const Key = args.key || args.Key;
  const Operation = args.operation || args.Operation;
  const endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  const cosHmacKeysId = args.__bx_creds['cloud-object-storage'].cos_hmac_keys.access_key_id;
  const cosHmacKeysSecret = args.__bx_creds['cloud-object-storage'].cos_hmac_keys.secret_access_key;

  const params = args;
  params.Bucket = Bucket;
  params.Key = Key;
  params.Operation = Operation;
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
