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

import org.scalatest.BeforeAndAfterAll
import common.{TestHelpers, Wsk, WskActorSystem, WskProps, WskTestHelpers}
import common.rest.WskRestOperations
import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import spray.json._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class CloudObjectStoragePackage
    extends TestHelpers
    with WskTestHelpers
    with WskActorSystem
    with BeforeAndAfterAll {

  implicit val wskprops = WskProps()
  val wsk = new Wsk()
  val wskRest: common.rest.WskRestOperations = new WskRestOperations
  val allowedActionDuration = 120 seconds

  // statuses for deployWeb
  val successStatus =
    """"status": "success""""

  val deployTestRepo =
    "https://github.com/ibm-functions/package-cloud-object-storage"
  val packageName = "cloud-object-storage"
  val deployAction = "/whisk.system/deployWeb/wskdeploy"
  val deployActionURL =
    s"https://${wskprops.apihost}/api/v1/web${deployAction}.http"

  //set parameters for deploy tests
  val nodejsRuntimePath = "runtimes/nodejs"
  val nodejsfolder = "../runtimes/nodejs";
  val nodejskind = "nodejs:10"
  val pythonRuntimePath = "runtimes/python"
  val pythonfolder = "../runtimes/python";
  val pythonkind = "python:3.7"

  //action definitions
  val actionWrite = packageName + "/object-write"
  val actionRead = packageName + "/object-read"
  val actionDelete = packageName + "/object-delete"
  val actionGetSignedUrl = packageName + "/client-get-signed-url"
  val actionBucketCorsGet = packageName + "/bucket-cors-get"
  val actionBucketCorsPut = packageName + "/bucket-cors-put"
  val actionBucketCorsDelete = packageName + "/bucket-cors-delete"

  behavior of "Cloud Object Storage Package"

  def deployNodeJS = {
    makePostCallWithExpectedResult(
      JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(nodejsRuntimePath),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ),
      successStatus,
      200
    );
  }

  def deleteNodeJS = {
    // create unique asset names
    wsk.action.delete(actionWrite)
    wsk.action.delete(actionRead)
    wsk.action.delete(actionDelete)
    wsk.action.delete(actionGetSignedUrl)
    wsk.action.delete(actionBucketCorsGet)
    wsk.action.delete(actionBucketCorsPut)
    wsk.action.delete(actionBucketCorsDelete)
    wsk.pkg.delete(packageName)
  }

  def deployPython = {
    makePostCallWithExpectedResult(
      JsObject(
        "gitUrl" -> JsString(deployTestRepo),
        "manifestPath" -> JsString(pythonRuntimePath),
        "wskApiHost" -> JsString(wskprops.apihost),
        "wskAuth" -> JsString(wskprops.authKey)
      ),
      successStatus,
      200
    );
  }

  def deletePython = {
    wsk.action.delete(actionWrite)
    wsk.action.delete(actionRead)
    wsk.action.delete(actionDelete)
    wsk.action.delete(actionGetSignedUrl)
    wsk.action.delete(actionBucketCorsGet)
    wsk.action.delete(actionBucketCorsPut)
    wsk.action.delete(actionBucketCorsDelete)
    wsk.pkg.delete(packageName)
  }

  private def makePostCallWithExpectedResult(params: JsObject,
                                             expectedResult: String,
                                             expectedCode: Int) = {
    val response = RestAssured
      .given()
      .contentType("application/json\r\n")
      .config(
        RestAssured
          .config()
          .sslConfig(new SSLConfig().relaxedHTTPSValidation()))
      .body(params.toString())
      .post(deployActionURL)
    assert(response.statusCode() == expectedCode)
    response.body.asString should include(expectedResult)
    response.body.asString.parseJson.asJsObject
      .getFields("activationId") should have length 1
  }
}
