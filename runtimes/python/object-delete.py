# This action will delete from Cloud Object Storage.  If the Cloud Object Storage
# service is not bound to this action or to the package containing this action,
# then you must provide the service information as argument input to this function.
# Cloud Functions actions accept a single parameter, which must be a JSON object.
#
# In this case, the args variable will look like:
#   {
#     "bucket": "your COS bucket name",
#     "key": "Name of the object to delete"
#   }

import sys
import json
import ibm_boto3
from ibm_botocore.client import Config

def main(args):
  resultsGetParams = getParamsCOS(args)
  cos = resultsGetParams['cos']
  params = resultsGetParams['params']
  bucket = params['bucket']
  key = params['key']
  try:
    object = cos.delete_object(
    Bucket=bucket,
    Key=key,
    )
  except ClientError as e:
    print(e)
    raise e

  return {
    'bucket':bucket,
    'key':key,
    'body': str(object)
    }


def getParamsCOS(args):
  endpoint = args.get('endpoint','https://s3-api.us-geo.objectstorage.softlayer.net')
  api_key_id = args.get('apikey', args.get('apiKeyId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('apikey', '')))
  service_instance_id = args.get('resource_instance_id', args.get('serviceInstanceId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('resource_instance_id', '')))
  ibm_auth_endpoint = args.get('ibmAuthEndpoint', 'https://iam.ng.bluemix.net/oidc/token')
  cos = ibm_boto3.client('s3',
    ibm_api_key_id=api_key_id,
    ibm_service_instance_id=service_instance_id,
    ibm_auth_endpoint=ibm_auth_endpoint,
    config=Config(signature_version='oauth'),
    endpoint_url=endpoint)
  params = {}
  params['bucket'] = args['bucket']
  params['key'] = args['key']
  return {'cos':cos, 'params':params}
