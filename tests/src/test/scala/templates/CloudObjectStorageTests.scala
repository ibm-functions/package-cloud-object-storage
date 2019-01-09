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
import common.{WskTestHelpers}
import common.TestUtils.RunResult
import spray.json._

@RunWith(classOf[JUnitRunner])
class CloudObjectStorageTests
    extends CloudObjectStoragePackage
    with WskTestHelpers
    with BeforeAndAfterAll {

  behavior of "Cloud Object Storage Package"

  // test to create the nodejs 8 Cloud Object Storage package from github url.  Will use preinstalled folder.
  it should "create the nodejs 8 Cloud Object Storage package from github url" in {
    deployNodeJS
    // create unique asset names

    // ensure actions exist and are of expected kind
    val testActionWrite =
      wsk.action.get(actionWrite)
    verifyAction(testActionWrite, actionWrite, JsString(nodejskind))

    val testActionRead = wsk.action.get(actionRead)
    verifyAction(testActionRead, actionRead, JsString(nodejskind))

    val testActionDelete = wsk.action.get(actionDelete)
    verifyAction(testActionDelete, actionDelete, JsString(nodejskind))

    val testActionGetSignedUrl = wsk.action.get(actionGetSignedUrl)
    verifyAction(testActionGetSignedUrl,
                 actionGetSignedUrl,
                 JsString(nodejskind))

    val testActionBucketCorsGet = wsk.action.get(actionBucketCorsGet)
    verifyAction(testActionBucketCorsGet,
                 actionBucketCorsGet,
                 JsString(nodejskind))

    val testActionBucketCorsPut = wsk.action.get(actionBucketCorsPut)
    verifyAction(testActionBucketCorsPut,
                 actionBucketCorsPut,
                 JsString(nodejskind))

    val testActionBucketCorsDelete =
      wsk.action.get(actionBucketCorsDelete)
    verifyAction(testActionBucketCorsDelete,
                 actionBucketCorsDelete,
                 JsString(nodejskind))
    // clean up after test
    deleteNodeJS
  }

  // test to create the python Cloud Object Storage package from github url.  Will use preinstalled folder.
  it should "create the python Cloud Object Storage package from github url" in {
    deployPython

    // ensure actions exist and are of expected kind
    val testActionWrite = wsk.action.get(actionWrite)
    verifyAction(testActionWrite, actionWrite, JsString(pythonkind))

    val testActionRead = wsk.action.get(actionRead)
    verifyAction(testActionRead, actionRead, JsString(pythonkind))

    val testActionDelete = wsk.action.get(actionDelete)
    verifyAction(testActionDelete, actionDelete, JsString(pythonkind))

    val testActionGetSignedUrl = wsk.action.get(actionGetSignedUrl)
    verifyAction(testActionGetSignedUrl,
                 actionGetSignedUrl,
                 JsString(pythonkind))

    val testActionBucketCorsGet = wsk.action.get(actionBucketCorsGet)
    verifyAction(testActionBucketCorsGet,
                 actionBucketCorsGet,
                 JsString(pythonkind))

    val testActionBucketCorsPut = wsk.action.get(actionBucketCorsPut)
    verifyAction(testActionBucketCorsPut,
                 actionBucketCorsPut,
                 JsString(pythonkind))

    val testActionBucketCorsDelete =
      wsk.action.get(actionBucketCorsDelete)
    verifyAction(testActionBucketCorsDelete,
                 actionBucketCorsDelete,
                 JsString(pythonkind))

    // clean up after test
    deletePython
  }

  private def verifyAction(action: RunResult,
                           name: String,
                           kindValue: JsString): Unit = {
    val stdout = action.stdout
    assert(stdout.startsWith(s"ok: got action $name\n"))
    wsk
      .parseJsonString(stdout)
      .fields("exec")
      .asJsObject
      .fields("kind") shouldBe kindValue
  }
}
