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

  let response;
  try {
    response = await cos.putObject(params).promise();
  } catch (err) {
    return Promise.reject({
      Bucket: params.Bucket,
      Key: params.Key,
      Error: err,
    });
  }
  return {
    Bucket: params.Bucket,
    Key: params.Key,
    body: response,
  };
}













function getParamsCOS(args, COS) {
  const Bucket = args.bucket || args.Bucket;
  const Key = args.key || args.Key;
  let Body = args.body || args.Body;
  if (Body.type === 'Buffer') {
    Body = Buffer.from(Body.data);
  }

  const operation = args.operation || 'getObject';
  const endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  const ibmAuthEndpoint = args.ibmAuthEndpoint || 'https://iam.ng.bluemix.net/oidc/token';
  const apiKeyId = args.apikey || args.apiKeyId || args.__bx_creds['cloud-object-storage'].apikey;
  const serviceInstanceId = args.resource_instance_id || args.serviceInstanceId || args.__bx_creds['cloud-object-storage'].resource_instance_id;

  const params = {};
  params.Bucket = Bucket;
  params.Key = Key;
  params.Body = Body;
  const cos = new COS.S3({
    endpoint, ibmAuthEndpoint, apiKeyId, serviceInstanceId,
  });
  return { cos, params };
}
