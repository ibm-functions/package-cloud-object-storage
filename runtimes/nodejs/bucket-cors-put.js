/**
 * This action will write to Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * @param Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "bucket": "your COS bucket name",
 *     "key": "Name of the object to write",
 *     "body": "Body of the object to write"
 *   }
 */
const CloudObjectStorage = require('ibm-cos-sdk');

async function main(args) {
  const { cos, params } = getParamsCOS(args, CloudObjectStorage);

  let response;
  const result = {
    bucket: params.bucket,
  };
  try {
    response = await cos.putBucketCors({
      Bucket: params.bucket,
      CORSConfiguration: params.corsConfig,
    }).promise();
  } catch (err) {
    console.log(err);
    result.message = err.message;
    throw result;
  }
  result.body = response;
  return result;
}














function getParamsCOS(args, COS) {
  const { bucket, corsConfig } = args;
  const endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  const ibmAuthEndpoint = args.ibmAuthEndpoint || 'https://iam.ng.bluemix.net/oidc/token';
  const apiKeyId = args.apikey || args.apiKeyId || args.__bx_creds['cloud-object-storage'].apikey;
  const serviceInstanceId = args.resource_instance_id || args.serviceInstanceId || args.__bx_creds['cloud-object-storage'].resource_instance_id;

  const params = {};
  params.bucket = bucket;
  params.corsConfig = corsConfig;
  const cos = new COS.S3({
    endpoint, ibmAuthEndpoint, apiKeyId, serviceInstanceId,
  });
  return { cos, params };
}
