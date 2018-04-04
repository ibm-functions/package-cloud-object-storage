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

  // statuses for deployWeb
  val successStatus =
    """"status":"success""""

  val deployTestRepo = "https://github.com/ibm-functions/template-cloud-object-storage"
  val actionWrite = "object-write"
  val actionRead = "object-read"
  val actionDelete = "object-delete"
  val packageName = "myPackage"
  val deployAction = "/whisk.system/deployWeb/wskdeploy"
  val deployActionURL = s"https://${wskprops.apihost}/api/v1/web${deployAction}.http"

  //set parameters for deploy tests
  val node8RuntimePath = "runtimes/nodejs"
  val nodejs8folder = "../runtimes/nodejs";
  val nodejs8kind = "nodejs:8"
  val node6RuntimePath = "runtimes/nodejs-6"
  val nodejs6folder = "../runtimes/nodejs-6";
  val nodejs6kind = "nodejs:6"
  val pythonRuntimePath = "runtimes/python"
  val pythonfolder = "../runtimes/python";
  val pythonkind = "python-jessie:3"

  behavior of "Cloudant Object Storage Template"

  // test to create the nodejs 8 Cloud Object Storage template from github url.  Will use preinstalled folder.
  it should "create the nodejs 8 Cloud Object Storage template from github url" in {

    // create unique asset names
    val timestamp: String = System.currentTimeMillis.toString
    val nodejs8Package = packageName + timestamp
    val nodejs8ActionWrite = nodejs8Package + "/" + actionWrite
    val nodejs8ActionRead = nodejs8Package + "/" + actionRead
    val nodejs8ActionDelete = nodejs8Package + "/" + actionDelete

    makePostCallWithExpectedResult(JsObject(
      "gitUrl" -> JsString(deployTestRepo),
      "manifestPath" -> JsString(node8RuntimePath),
      "envData" -> JsObject(
        "PACKAGE_NAME" -> JsString(nodejs8Package)
      ),
      "wskApiHost" -> JsString(wskprops.apihost),
      "wskAuth" -> JsString(wskprops.authKey)
    ), successStatus, 200);

    withActivation(wsk.activation, wsk.action.invoke(nodejs8ActionWrite)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }
    withActivation(wsk.activation, wsk.action.invoke(nodejs8ActionRead)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }
    withActivation(wsk.activation, wsk.action.invoke(nodejs8ActionDelete)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }

    val testActionWrite = wsk.action.get(nodejs8ActionWrite)
    verifyAction(testActionWrite, nodejs8ActionWrite, JsString(nodejs8kind))

    val testActionRead = wsk.action.get(nodejs8ActionRead)
    verifyAction(testActionRead, nodejs8ActionRead, JsString(nodejs8kind))

    val testActionDelete = wsk.action.get(nodejs8ActionDelete)
    verifyAction(testActionDelete, nodejs8ActionDelete, JsString(nodejs8kind))

    // clean up after test
    wsk.action.delete(nodejs8ActionWrite)
    wsk.action.delete(nodejs8ActionRead)
    wsk.action.delete(nodejs8ActionDelete)
    wsk.pkg.delete(nodejs8Package)
  }

  // test to create the nodejs 6 Cloud Object Storage template from github url.  Will use preinstalled folder.
  it should "create the nodejs 6 Cloud Object Storage template from github url" in {

    // create unique asset names
    val timestamp: String = System.currentTimeMillis.toString
    val nodejs6Package = packageName + timestamp
    val nodejs6ActionWrite = nodejs6Package + "/" + actionWrite
    val nodejs6ActionRead = nodejs6Package + "/" + actionRead
    val nodejs6ActionDelete = nodejs6Package + "/" + actionDelete

    makePostCallWithExpectedResult(JsObject(
      "gitUrl" -> JsString(deployTestRepo),
      "manifestPath" -> JsString(node6RuntimePath),
      "envData" -> JsObject(
        "PACKAGE_NAME" -> JsString(nodejs6Package)
      ),
      "wskApiHost" -> JsString(wskprops.apihost),
      "wskAuth" -> JsString(wskprops.authKey)
    ), successStatus, 200);

    withActivation(wsk.activation, wsk.action.invoke(nodejs6ActionWrite)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }
    withActivation(wsk.activation, wsk.action.invoke(nodejs6ActionRead)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }
    withActivation(wsk.activation, wsk.action.invoke(nodejs6ActionDelete)) {
      _.response.result.get.toString should include("Cannot read property 'cloud-object-storage' of undefined")
    }

    val testActionWrite = wsk.action.get(nodejs6ActionWrite)
    verifyAction(testActionWrite, nodejs6ActionWrite, JsString(nodejs6kind))

    val testActionRead = wsk.action.get(nodejs6ActionRead)
    verifyAction(testActionRead, nodejs6ActionRead, JsString(nodejs6kind))

    val testActionDelete = wsk.action.get(nodejs6ActionDelete)
    verifyAction(testActionDelete, nodejs6ActionDelete, JsString(nodejs6kind))

    // clean up after test
    wsk.action.delete(nodejs6ActionWrite)
    wsk.action.delete(nodejs6ActionRead)
    wsk.action.delete(nodejs6ActionDelete)
    wsk.pkg.delete(nodejs6Package)
  }

  // test to create the python Cloud Object Storage template from github url.  Will use preinstalled folder.
  it should "create the python Cloud Object Storage template from github url" in {

    // create unique asset names
    val timestamp: String = System.currentTimeMillis.toString
    val pythonPackage = packageName + timestamp
    val pythonActionWrite = pythonPackage + "/" + actionWrite
    val pythonActionRead = pythonPackage + "/" + actionRead
    val pythonActionDelete = pythonPackage + "/" + actionDelete

    makePostCallWithExpectedResult(JsObject(
      "gitUrl" -> JsString(deployTestRepo),
      "manifestPath" -> JsString(pythonRuntimePath),
      "envData" -> JsObject(
        "PACKAGE_NAME" -> JsString(pythonPackage)
      ),
      "wskApiHost" -> JsString(wskprops.apihost),
      "wskAuth" -> JsString(wskprops.authKey)
    ), successStatus, 200);

    withActivation(wsk.activation, wsk.action.invoke(pythonActionWrite)) {
      _.response.result.get.toString should include("The action did not return a dictionary.")
    }
    withActivation(wsk.activation, wsk.action.invoke(pythonActionRead)) {
      _.response.result.get.toString should include("The action did not return a dictionary.")
    }
    withActivation(wsk.activation, wsk.action.invoke(pythonActionDelete)) {
      _.response.result.get.toString should include("The action did not return a dictionary.")
    }

    val testActionWrite = wsk.action.get(pythonActionWrite)
    verifyAction(testActionWrite, pythonActionWrite, JsString(pythonkind))

    val testActionRead = wsk.action.get(pythonActionRead)
    verifyAction(testActionRead, pythonActionRead, JsString(pythonkind))

    val testActionDelete = wsk.action.get(pythonActionDelete)
    verifyAction(testActionDelete, pythonActionDelete, JsString(pythonkind))

    // clean up after test
    wsk.action.delete(pythonActionWrite)
    wsk.action.delete(pythonActionRead)
    wsk.action.delete(pythonActionDelete)
    wsk.pkg.delete(pythonPackage)
  }

  //TODO:
  //Test individual actions -> how can we test object-delete, read, write with COS credentials for open?

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
