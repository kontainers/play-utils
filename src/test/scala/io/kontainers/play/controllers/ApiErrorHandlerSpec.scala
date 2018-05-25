/*
 * =========================================================================================
 * Copyright © 2018 Kontainers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */
package io.kontainers.play.controllers

import io.kontainers.play.controllers.readWrites.ApiSuccessErrorWrite
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsResultException, Json}
import play.api.mvc.RequestHeader
import play.api.test.Helpers._

class ApiErrorHandlerSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with ApiSuccessErrorWrite {

  val errorHandler = app.injector.instanceOf[ApiErrorHandler]
  val rh = mock[RequestHeader]

  "ApiErrorHandler" should {
    "map NotFoundException" in {
      val msg = "not found"
      val resultFuture = errorHandler.onServerError(rh, NotFoundException(msg))
      val text = contentAsString(resultFuture)
      text mustEqual exceptionAsJson(Status.NOT_FOUND, msg)
    }
    "map ApiJsonException" in {
      val msg = "api-json"
      val resultFuture = errorHandler.onServerError(rh, ApiJsonException(msg))
      val text = contentAsString(resultFuture)
      text mustEqual exceptionAsJson(Status.BAD_REQUEST, msg)
    }
    "map JsResultException" in {
      val msg = "JsResultException(errors:List())"
      val resultFuture = errorHandler.onServerError(rh, JsResultException(Seq.empty))
      val text = contentAsString(resultFuture)
      text mustEqual exceptionAsJson(Status.BAD_REQUEST, msg)
    }
    "map RuntimeException" in {
      val msg = "runtime"
      val resultFuture = errorHandler.onServerError(rh, new RuntimeException(msg))
      val text = contentAsString(resultFuture)
      text mustEqual exceptionAsJson(Status.INTERNAL_SERVER_ERROR, msg)
    }
    "map onBadRequest" in {
      val msg = "bad request"
      val resultFuture = errorHandler.onBadRequest(rh, msg)
      val text = contentAsString(resultFuture)
      text mustEqual exceptionAsJson(Status.BAD_REQUEST, msg)
    }
  }

  def exceptionAsJson(code: Int, msg: String): String = {
    Json.stringify(Json.toJson(ApiError(code, msg)))
  }
}
