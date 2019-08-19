# This action will get a signed url based on the cloud object storage bucket, key,
# and specified operation.  If the Cloud Object Storage service is not bound to this
# action or to the package containing this action, then you must provide the
# service information as argument input to this function.
# Cloud Functions actions accept a single parameter, which must be a JSON object.
# In this case, the args variable will look like:
#   {
#     "bucket": "your COS bucket name",
#     "key": "Name of the object to be written",
#     "operation":"putObject, getObject, or deleteObject"
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
  object = cos.generate_presigned_url(
    ExpiresIn=params['expires'],
    ClientMethod=params['operation'],
    Params={
        'Bucket': params['bucket'],
        'Key': params['key'],
    },
  )
  return {
    'bucket':bucket,
    'key':key,
    'body': str(object)
  }







def getParamsCOS(args):
  operation = args.get('operation').lower();
  if '_' not in operation:
    index = operation.find('object')
    operation = operation[:index] + '_' + operation[index:]
  expires = args.get('expires', 60 * 15)
  endpoint = args.get('endpoint','https://s3.us.cloud-object-storage.appdomain.cloud')
  access_key_id=args.get('access_key_id', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('cos_hmac_keys', {}).get('access_key_id', ''))
  secret_access_key = args.get('secret_access_key', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('cos_hmac_keys', {}).get('secret_access_key', ''))
  api_key_id = args.get('apikey', args.get('apiKeyId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('apikey', '')))
  service_instance_id = args.get('resource_instance_id', args.get('serviceInstanceId', args.get('__bx_creds', {}).get('cloud-object-storage', {}).get('resource_instance_id', '')))
  ibm_auth_endpoint = args.get('ibmAuthEndpoint', 'https://iam.cloud.ibm.com/identity/token')
  cos = ibm_boto3.client('s3',
    aws_access_key_id=access_key_id,
    aws_secret_access_key=secret_access_key,
    region_name='us-standard',
    ibm_auth_endpoint=ibm_auth_endpoint,
    config=Config(signature_version='s3v4'),
    endpoint_url=endpoint)
  params = {}
  params['bucket'] = args['bucket']
  params['key'] = args['key']
  params['operation'] = operation
  params['expires'] = expires
  return {'cos':cos, 'params':params}
