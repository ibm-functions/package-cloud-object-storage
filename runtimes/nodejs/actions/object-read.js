/**
 * This action will read from Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "bucket": "your COS bucket name",
 *     "key": "Name of the object to read"
 *   }
 */
const CloudObjectStorage = require('ibm-cos-sdk');


async function main(args) {
  const { cos, params } = getParamsCOS(args, CloudObjectStorage);
  let response;
  const result = params;

  if (!params.bucket || !params.key || !cos) {
    result.message = "bucket name, key, and apikey are required for this operation."
    return result
  }

  try {
    response = await cos.getObject({ Bucket: params.bucket, Key: params.key }).promise();
  } catch (err) {
    console.log(err);
    result.message = err.message;
    throw result;
  }
  result.body = response.Body.toString();
  return result;
}












function getParamsCOS(args, COS) {
  var bxCredsApiKey = ""
  var bxCredsResourceInstanceId = ""

  if (args.__bx_creds && args.__bx_creds['cloud-object-storage']) {
    if (args.__bx_creds['cloud-object-storage'].apiKey) {
      // bxCredsApiKey = args.__bx_creds['cloud-object-storage'].apikey
      bxCredsApiKey = ""
    }
    if (args.__bx_creds['cloud-object-storage'].resource_instance_id) {
      bxCredsResourceInstanceId = args.__bx_creds['cloud-object-storage'].resource_instance_id
    }
  }

  const { bucket, key } = args;
  const endpoint = args.endpoint || 's3.us.cloud-object-storage.appdomain.cloud';
  const ibmAuthEndpoint = args.ibmAuthEndpoint || 'https://iam.cloud.ibm.com/identity/token';
  const apiKeyId = args.apikey || args.apiKeyId || bxCredsApiKey || process.env.__OW_IAM_NAMESPACE_API_KEY;
  const serviceInstanceId = args.resource_instance_id || args.serviceInstanceId || bxCredsResourceInstanceId;

  const params = {};
  params.bucket = bucket;
  params.key = key;

  if (!apiKeyId) {
    const cos = null
    return { cos, params}
  }

  const cos = new COS.S3({
    endpoint, ibmAuthEndpoint, apiKeyId, serviceInstanceId,
  });
  return { cos, params };
}
