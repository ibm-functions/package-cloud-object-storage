/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package packages


import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.junit.JUnitRunner
import common.{TestHelpers, Wsk, WskProps, WskTestHelpers}
import common.TestUtils.RunResult
import common.rest.WskRest
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import spray.json._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


@RunWith(classOf[JUnitRunner])
class CloudObjectStorageTests extends TestHelpers
  with WskTestHelpers
  with BeforeAndAfterAll {

  implicit val wskprops = WskProps()
  val wsk = new Wsk()
  val wskRest: common.rest.WskRest = new WskRest
  val allowedActionDuration = 120 seconds

  // get creds for COS instance
  val creds = TestUtils.getVCAPcredentials("cloud-object-storage");
  val apikey = creds.get("apikey")
  var resource_instance_id = creds.get("resource_instance_id")
  val __bx_creds = JsObject(
    "cloud-object-storage" -> JsObject(
      "apikey" -> JsString(apikey),
      "resource_instance_id" -> JsString(resource_instance_id)))
  val bucket="ibm-functions-devops-testing"

  // statuses for deployWeb
  val successStatus =
    """"status":"success""""

  val deployTestRepo = "https://github.com/ibm-functions/package-cloud-object-storage"
  val actionWrite = "object-write"
  val actionRead = "object-read"
  val actionDelete = "object-delete"
  val actionGetSignedUrl = "client-get-signed-url"
  val actionBucketCorsGet = "bucket-cors-get"
  val actionBucketCorsPut = "bucket-cors-put"
  val actionBucketCorsDelete = "bucket-cors-delete"
  val packageName = "cloud-object-storage"
  val deployAction = "/whisk.system/deployWeb/wskdeploy"
  val deployActionURL = s"https://${wskprops.apihost}/api/v1/web${deployAction}.http"

  //set parameters for deploy tests
  val node8RuntimePath = "runtimes/nodejs"
  val nodejsfolder = "../runtimes/nodejs";
  val nodejskind = "nodejs:8"
  val pythonRuntimePath = "runtimes/python"
  val pythonfolder = "../runtimes/python";
  val pythonkind = "python-jessie:3"

  behavior of "Cloudant Object Storage Package"

  // test to create the nodejs 8 Cloud Object Storage package from github url.  Will use preinstalled folder.
  it should "create the nodejs 8 Cloud Object Storage package from github url" in {

    // create unique asset names
    val nodejsActionWrite = packageName + "/" + actionWrite
    val nodejsActionRead = packageName + "/" + actionRead
    val nodejsActionDelete = packageName + "/" + actionDelete
    val nodejsActionGetSignedUrl = packageName + "/" + actionGetSignedUrl
    val nodejsActionBucketCorsGet = packageName + "/" + actionBucketCorsGet
    val nodejsActionBucketCorsPut = packageName + "/" + actionBucketCorsPut
    val nodejsActionBucketCorsDelete = packageName + "/" + actionBucketCorsDelete

    makePostCallWithExpectedResult(JsObject(
      "gitUrl" -> JsString(deployTestRepo),
      "manifestPath" -> JsString(node8RuntimePath),
      "wskApiHost" -> JsString(wskprops.apihost),
      "wskAuth" -> JsString(wskprops.authKey)
    ), successStatus, 200);

    // ensure actions exist and are of expected kind
    val testActionWrite = wsk.action.get(nodejsActionWrite)
    verifyAction(testActionWrite, nodejsActionWrite, JsString(nodejskind))

    val testActionRead = wsk.action.get(nodejsActionRead)
    verifyAction(testActionRead, nodejsActionRead, JsString(nodejskind))

    val testActionDelete = wsk.action.get(nodejsActionDelete)
    verifyAction(testActionDelete, nodejsActionDelete, JsString(nodejskind))

    val testActionGetSignedUrl = wsk.action.get(nodejsActionGetSignedUrl)
    verifyAction(testActionGetSignedUrl, nodejsActionGetSignedUrl, JsString(nodejskind))

    val testActionBucketCorsGet = wsk.action.get(nodejsActionBucketCorsGet)
    verifyAction(testActionBucketCorsGet, nodejsActionBucketCorsGet, JsString(nodejskind))

    val testActionBucketCorsPut = wsk.action.get(nodejsActionBucketCorsPut)
    verifyAction(testActionBucketCorsPut, nodejsActionBucketCorsPut, JsString(nodejskind))

    val testActionBucketCorsDelete = wsk.action.get(nodejsActionBucketCorsDelete)
    verifyAction(testActionBucketCorsDelete, nodejsActionBucketCorsDelete, JsString(nodejskind))
    // clean up after test
    wsk.action.delete(nodejsActionWrite)
    wsk.action.delete(nodejsActionRead)
    wsk.action.delete(nodejsActionDelete)
    wsk.action.delete(nodejsActionGetSignedUrl)
    wsk.action.delete(nodejsActionBucketCorsGet)
    wsk.action.delete(nodejsActionBucketCorsPut)
    wsk.action.delete(nodejsActionBucketCorsDelete)
    wsk.pkg.delete(packageName)
  }

  // test to create the python Cloud Object Storage package from github url.  Will use preinstalled folder.
  it should "create the python Cloud Object Storage package from github url" in {

    // create unique asset names
    val pythonActionWrite = packageName + "/" + actionWrite
    val pythonActionRead = packageName + "/" + actionRead
    val pythonActionDelete = packageName + "/" + actionDelete
    val pythonActionGetSignedUrl = packageName + "/" + actionGetSignedUrl
    val pythonActionBucketCorsGet = packageName + "/" + actionBucketCorsGet
    val pythonActionBucketCorsPut = packageName + "/" + actionBucketCorsPut
    val pythonActionBucketCorsDelete = packageName + "/" + actionBucketCorsDelete

    makePostCallWithExpectedResult(JsObject(
      "gitUrl" -> JsString(deployTestRepo),
      "manifestPath" -> JsString(pythonRuntimePath),
      "wskApiHost" -> JsString(wskprops.apihost),
      "wskAuth" -> JsString(wskprops.authKey)
    ), successStatus, 200);

    // ensure actions exist and are of expected kind
    val testActionWrite = wsk.action.get(pythonActionWrite)
    verifyAction(testActionWrite, pythonActionWrite, JsString(pythonkind))

    val testActionRead = wsk.action.get(pythonActionRead)
    verifyAction(testActionRead, pythonActionRead, JsString(pythonkind))

    val testActionDelete = wsk.action.get(pythonActionDelete)
    verifyAction(testActionDelete, pythonActionDelete, JsString(pythonkind))

    val testActionGetSignedUrl = wsk.action.get(pythonActionGetSignedUrl)
    verifyAction(testActionGetSignedUrl, pythonActionGetSignedUrl, JsString(pythonkind))

    val testActionBucketCorsGet = wsk.action.get(pythonActionBucketCorsGet)
    verifyAction(testActionBucketCorsGet, pythonActionBucketCorsGet, JsString(pythonkind))

    val testActionBucketCorsPut = wsk.action.get(pythonActionBucketCorsPut)
    verifyAction(testActionBucketCorsPut, pythonActionBucketCorsPut, JsString(pythonkind))

    val testActionBucketCorsDelete = wsk.action.get(pythonActionBucketCorsDelete)
    verifyAction(testActionBucketCorsDelete, pythonActionBucketCorsDelete, JsString(pythonkind))

    // clean up after test
    wsk.action.delete(pythonActionWrite)
    wsk.action.delete(pythonActionRead)
    wsk.action.delete(pythonActionDelete)
    wsk.action.delete(pythonActionGetSignedUrl)
    wsk.action.delete(pythonActionBucketCorsGet)
    wsk.action.delete(pythonActionBucketCorsPut)
    wsk.action.delete(pythonActionBucketCorsDelete)
    wsk.pkg.delete(packageName)
  }

  // test individual package actions
  it should "Test deleting an object in Cloud Object Storage Bucket on IBM Cloud" in withAssetCleaner(wskprops) {
  (wp, assetHelper) =>
    val timestamp: String = System.currentTimeMillis.toString
    val name = "deleteObjectNode" + timestamp
    val file = Some(new File(nodejsfolder, "bucket-cors-delete.js").toString())
    assetHelper.withCleaner(wsk.action, name) { (action, _) =>
      action.create(
        name,
        file,
        kind=Some(nodejskind),
        parameters = Map("__bx_creds" => __bx_creds, "Bucket" => bucket, "Key" => "filename")
      )
    }

    withActivation(wsk.activation, wsk.action.invoke(name, params)) {
      _.response.result.get.toString should include(s"bucket: $bucket")
    }
  }

  // test individual package actions
  it should "Test reading an object in Cloud Object Storage Bucket on IBM Cloud" in withAssetCleaner(wskprops) {
  (wp, assetHelper) =>
    val timestamp: String = System.currentTimeMillis.toString
    val name = "getObjectNode" + timestamp
    val file = Some(new File(nodejsfolder, "bucket-cors-delete.js").toString())
    assetHelper.withCleaner(wsk.action, name) { (action, _) =>
      action.create(
        name,
        file,
        kind=Some(nodejskind),
        parameters = Map("__bx_creds" => __bx_creds, "Bucket" => bucket.toJson, "Key" => "filename".toJson)
      )
    }

    withActivation(wsk.activation, wsk.action.invoke(name, params)) {
      _.response.result.get.toString should include(s"bucket: $bucket")
    }
  }

  private def makePostCallWithExpectedResult(params: JsObject, expectedResult: String, expectedCode: Int) = {
    val response = RestAssured.given()
      .contentType("application/json\r\n")
      .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
      .body(params.toString())
      .post(deployActionURL)
    assert(response.statusCode() == expectedCode)
    response.body.asString should include(expectedResult)
    response.body.asString.parseJson.asJsObject.getFields("activationId") should have length 1
  }

  private def verifyAction(action: RunResult, name: String, kindValue: JsString): Unit = {
    val stdout = action.stdout
    assert(stdout.startsWith(s"ok: got action $name\n"))
    wsk.parseJsonString(stdout).fields("exec").asJsObject.fields("kind") shouldBe kindValue
  }
}
