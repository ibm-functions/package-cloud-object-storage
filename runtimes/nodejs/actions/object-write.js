/**
 * This action will write to Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * Cloud Functions actions accept a single parameter, which must be a JSON object.
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
    key: params.key,
  };

  if (!params.bucket || !params.key || !params.body || !cos) {
    result.message = "bucket name, key, body, and apikey are required for this operation."
    return result
  }

  try {
    response = await cos.putObject({
      Bucket: params.bucket, Key: params.key, Body: params.body,
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
  var bxCredsApiKey = ""
  var bxCredsResourceInstanceId = ""

  if (args.__bx_creds && args.__bx_creds['cloud-object-storage']) {
    if (args.__bx_creds['cloud-object-storage'].apiKey) {
      bxCredsApiKey = args.__bx_creds['cloud-object-storage'].apikey
    }
    if (args.__bx_creds['cloud-object-storage'].resource_instance_id) {
      bxCredsResourceInstanceId = args.__bx_creds['cloud-object-storage'].resource_instance_id
    }
  }

  const { bucket, key } = args;
  let { body } = args;
  if (body.type === 'Buffer') {
    body = Buffer.from(body.data);
  }

  const endpoint = args.endpoint || 's3.us.cloud-object-storage.appdomain.cloud';
  const ibmAuthEndpoint = args.ibmAuthEndpoint || 'https://iam.cloud.ibm.com/identity/token';
  const apiKeyId = args.apikey || args.apiKeyId || bxCredsApiKey || process.env.__OW_IAM_NAMESPACE_API_KEY;
  const serviceInstanceId = args.resource_instance_id || args.serviceInstanceId || bxCredsResourceInstanceId;

  const params = {};
  params.bucket = bucket;
  params.key = key;
  params.body = body;

  if (!apiKeyId) {
    const cos = null
    return { cos, params }
  }

  const cos = new COS.S3({
    endpoint, ibmAuthEndpoint, apiKeyId, serviceInstanceId,
  });
  return { cos, params };
}
