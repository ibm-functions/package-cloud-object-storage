# This action will get CORS Configuration for a Cloud Object Storage bucket.
# If the Cloud Object Storage service is not bound to this action or to the package
# containing this action, then you must provide the service information as argument
# input to this function.
# Cloud Functions actions accept a single parameter, which must be a JSON object.
#
# In this case, the args variable will look like:
#   {
#     "bucket": "your COS bucket name",
#   }

import sys
import json
import os
import ibm_boto3
from ibm_botocore.client import Config, ClientError

def main(args):
  resultsGetParams = getParamsCOS(args)
  cos = resultsGetParams.get('cos')
  bucket = resultsGetParams.get('params').get('bucket')

  if not bucket or not cos:
    return {
      'bucket':bucket,
      'key':key,
      'message':"bucket name and apikey are required for this operation."
    } 

  try:
    object = cos.get_bucket_cors(
    Bucket=bucket,
    )
  except ClientError as e:
    print(e)
    raise e

  return {
    'bucket':bucket,
    'body': str(object)
    }


def getParamsCOS(args):
  endpoint = args.get('endpoint','https://s3.us.cloud-object-storage.appdomain.cloud')
  api_key_id = args.get('apikey', args.get('apiKeyId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('apikey', os.environ.get('__OW_IAM_NAMESPACE_API_KEY') or ''))) 
  service_instance_id = args.get('resource_instance_id', args.get('serviceInstanceId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('resource_instance_id', '')))
  ibm_auth_endpoint = args.get('ibmAuthEndpoint', 'https://iam.cloud.ibm.com/identity/token')
  cos = ibm_boto3.client('s3',
    ibm_api_key_id=api_key_id,
    ibm_service_instance_id=service_instance_id,
    ibm_auth_endpoint=ibm_auth_endpoint,
    config=Config(signature_version='oauth'),
    endpoint_url=endpoint)
  params = {}
  params['bucket'] = args.get('bucket')
  if not api_key_id:
    return {'cos': null, 'params':params}
  return {'cos':cos, 'params':params}
