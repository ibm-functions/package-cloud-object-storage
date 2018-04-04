/**
 * This action will delete from Cloud Object Storage.  If the Cloud Object Storage
 * service is not bound to this action or to the package containing this action,
 * then you must provide the service information as argument input to this function.
 * @param Cloud Functions actions accept a single parameter, which must be a JSON object.
 *
 * In this case, the args variable will look like:
 *   {
 *     "Bucket": "your COS bucket name",
 *     "Key": "Name of the object to delete"
 *   }
 */
var CloudObjectStorage = require('ibm-cos-sdk');
function main(args) {
  let { cos, params } = getParamsCOS(args, CloudObjectStorage);
  return cos.deleteObject(params).promise();
}









function getParamsCOS(args, COS) {
  let Bucket = args.bucket || args.Bucket;
  let Key = args.key || args.Key;
  let endpoint = args.endpoint || 's3-api.us-geo.objectstorage.softlayer.net';
  let ibmAuthEndpoint = args.ibmAuthEndpoint || 'https://iam.ng.bluemix.net/oidc/token';
  let apiKeyId = args.apikey || args.apiKeyId || args.__bx_creds["cloud-object-storage"].apikey;
  let serviceInstanceId = args.resource_instance_id || args.serviceInstanceId || args.__bx_creds["cloud-object-storage"].resource_instance_id;

  var params = args;
  params.Bucket = Bucket;
  params.Key = Key;
  delete params.__bx_creds;

  cos = cos || new COS.S3({ endpoint, ibmAuthEndpoint, apiKeyId, serviceInstanceId });
  return { cos, params };
}
