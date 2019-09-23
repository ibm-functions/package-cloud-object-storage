# This action will read from Cloud Object Storage.  If the Cloud Object Storage
# service is not bound to this action or to the package containing this action,
# then you must provide the service information as argument input to this function.
# Cloud Functions actions accept a single parameter, which must be a JSON object.
#
# In this case, the args variable will look like:
#   {
#     "bucket": "your COS bucket name",
#     "key": "Name of the object to read"
#   }

import sys
import json
import os
import ibm_boto3
from ibm_botocore.client import Config, ClientError

def main(args):
  resultsGetParams = getParamsCOS(args)

  cos = resultsGetParams.get('cos')
  params = resultsGetParams.get('params')
  bucket = params.get('bucket')
  key = params.get('key')

  try:
    if not bucket or not key or not cos:
      raise ValueError("bucket name, key, and apikey are required for this operation.")
  except ValueError as e:
    print(e)
    raise

  try:
    object = cos.get_object(
    Bucket=bucket,
    Key=key,
  )
  except ClientError as e:
    print(e)
    raise e

  return {
  'bucket':bucket,
  'key':key,
  'body': str(object['Body'].read())
  }


def getParamsCOS(args):
  endpoint = args.get('endpoint','https://s3.us.cloud-object-storage.appdomain.cloud')
  if not (endpoint.startswith("https://") or endpoint.startswith("http://")) : endpoint = "https://" + endpoint
  api_key_id = args.get('apikey', args.get('apiKeyId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('apikey', os.environ.get('__OW_IAM_NAMESPACE_API_KEY') or ''))) 
  service_instance_id = args.get('resource_instance_id', args.get('serviceInstanceId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('resource_instance_id', '')))
  ibm_auth_endpoint = args.get('ibmAuthEndpoint', 'https://iam.cloud.ibm.com/identity/token')
  params = {}
  params['bucket'] = args.get('bucket')
  params['key'] = args.get('key')
  if not api_key_id:
    return {'cos': None, 'params':params}
  cos = ibm_boto3.client('s3',
    ibm_api_key_id=api_key_id,
    ibm_service_instance_id=service_instance_id,
    ibm_auth_endpoint=ibm_auth_endpoint,
    config=Config(signature_version='oauth'),
    endpoint_url=endpoint)
  return {'cos':cos, 'params':params}
