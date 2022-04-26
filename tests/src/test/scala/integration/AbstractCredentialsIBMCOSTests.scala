/*
 * Copyright 2017 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration

import common._
import spray.json._
import spray.json.DefaultJsonProtocol._
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration.DurationInt
import packages.CloudObjectStoragePackage
import org.apache.openwhisk.utils.retry

abstract class AbstractCredentialsIBMCOSTests
    extends CloudObjectStoragePackage
    with WskTestHelpers
    with BeforeAndAfterAll {

  var defaultKind = Some("nodejs:12")
  val creds = TestUtils.getCredentials("cloud-object-storage")
  val apikey = creds.get("apikey").getAsString()
  var resource_instance_id = creds.get("resource_instance_id").getAsString()
  val access_key_id = creds
    .get("cos_hmac_keys")
    .getAsJsonObject()
    .get("access_key_id")
    .getAsString()
  val secret_access_key = creds
    .get("cos_hmac_keys")
    .getAsJsonObject()
    .get("secret_access_key")
    .getAsString()
  val __bx_creds = JsObject(
    "cloud-object-storage" -> JsObject(
      "cos_hmac_keys" -> JsObject(
        "access_key_id" -> JsString(access_key_id),
        "secret_access_key" -> JsString(secret_access_key)
      ),
      "apikey" -> JsString(apikey),
      "resource_instance_id" -> JsString(resource_instance_id)
    ))
  val testText: String = "This is just a test"
  val testKey: String = "cos-package-test-data.txt"
  val testKeyImmutable: String = "cos-package-test-data-permanant.txt"
  val testBucket: String = "ibm-functions-cos-package-testing"

  def getRuntime(): String
  def deployRuntime()
  def deleteRuntime()

  it should "Deploy " + getRuntime + " COS Package and Actions" in {
    deployRuntime
  }

  it should "Test should create, read, delete Object to Cloud Object Storage bucket without CORS" in {
    // Create a test file in our Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke("cloud-object-storage/object-write",
                        Map(
                          "key" -> JsString(testKey),
                          "bucket" -> JsString(testBucket),
                          "body" -> JsString(testText),
                          "__bx_creds" -> __bx_creds
                        ))
    ) { activation =>
      activation.response.success should be(true)
      activation.response.result.get.toString should include(
        s"""bucket":"$testBucket""""
      )
    }

    // Read back the test file we just put in the Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke("cloud-object-storage/object-read",
                        Map(
                          "key" -> JsString(testKey),
                          "bucket" -> JsString(testBucket),
                          "__bx_creds" -> __bx_creds
                        ))
    ) { activation =>
      activation.response.success should be(true)
      activation.response.result.get.toString should include(testKey)
    }

    // Delete the test file from the Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke("cloud-object-storage/object-delete",
                        Map(
                          "key" -> JsString(testKey),
                          "bucket" -> JsString(testBucket),
                          "__bx_creds" -> __bx_creds
                        ))
    ) { activation =>
      activation.response.success should be(true)
      activation.response.result.get.toString should include(
        s"""bucket":"$testBucket""""
      )
    }

    // Assert the file was deleted from the Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke("cloud-object-storage/object-read",
                        Map(
                          "key" -> JsString(testKey),
                          "bucket" -> JsString(testBucket),
                          "__bx_creds" -> __bx_creds
                        ))
    ) { activation =>
      activation.response.success should be(false)
      activation.response.result.get.toString should include("error")
    }
  }

  it should "Test should create, read, delete CORS configuration to Cloud Object Storage bucket" in {
    // Create a test file in our Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke(
        "cloud-object-storage/bucket-cors-put",
        Map(
          "bucket" -> JsString(testBucket),
          "corsConfig" -> JsObject("CORSRules" -> JsArray(JsObject(
            "AllowedHeaders" -> JsArray(JsString("*")),
            "AllowedMethods" -> JsArray(JsString("PUT"),
                                        JsString("GET"),
                                        JsString("DELETE")),
            "AllowedOrigins" -> JsArray(JsString("*"))
          ))),
          "__bx_creds" -> __bx_creds
        )
      )
    ) { activation =>
      activation.response.success should be(true)
      activation.response.result.get.toString should include(
        s"""bucket":"$testBucket"""")
    }

    // Read back the test file we just put in the Bucket
    retry(
      {
        withActivation(
          wsk.activation,
          wsk.action.invoke("cloud-object-storage/bucket-cors-get",
                            Map(
                              "bucket" -> JsString(testBucket),
                              "__bx_creds" -> __bx_creds
                            ))
        ) { activation =>
          activation.response.success should be(true)
          activation.response.result.get.toString should include("CORSRules")
        }
      },
      10,
      Some(6.seconds)
    )

    // Delete the test file from our Bucket
    withActivation(
      wsk.activation,
      wsk.action.invoke("cloud-object-storage/bucket-cors-delete",
                        Map(
                          "bucket" -> JsString(testBucket),
                          "__bx_creds" -> __bx_creds
                        ))
    ) { activation =>
      activation.response.success should be(true)
      activation.response.result.get.toString should include(
        s"""bucket":"$testBucket""""
      )

    }

    // Assert the file was deleted from the Bucket
    retry(
      {
        withActivation(
          wsk.activation,
          wsk.action.invoke("cloud-object-storage/bucket-cors-get",
                            Map(
                              "bucket" -> JsString(testBucket),
                              "__bx_creds" -> __bx_creds
                            ))
        ) { activation =>
          activation.response.success should be(false)
          activation.response.result.get.toString should include("error")
        }
      },
      10,
      Some(6.seconds)
    )
  }

  it should "Test should get signed URL to fetch file and use that URL to fetch the file" in {
    withActivation(
      wsk.activation,
      wsk.action.invoke(
        "cloud-object-storage/client-get-signed-url",
        Map(
          "bucket" -> JsString(testBucket),
          "key" -> JsString(testKeyImmutable),
          "operation" -> JsString("getObject"),
          "__bx_creds" -> __bx_creds
        )
      )
    ) { activation =>
      activation.response.success should be(true)
      // var signedUrl = activation.response.result.get.fields
      var signedUrl =
        activation.response.result.get.fields
          .get("body")
          .get
          .convertTo[String]
      var signedUrlReturn = scala.io.Source.fromURL(signedUrl).mkString
      signedUrlReturn should include("test file")
    }
  }

  it should "Delete " + getRuntime + " COS Package and Actions" in {
    deleteRuntime
  }
}
