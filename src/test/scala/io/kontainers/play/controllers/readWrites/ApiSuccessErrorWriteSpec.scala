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
package io.kontainers.play.controllers.readWrites

import io.kontainers.play.controllers.{ApiError, ApiSuccess}
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.libs.json.Json

class ApiSuccessErrorWriteSpec extends PlaySpec with ApiSuccessErrorWrite {

  "ApiSuccessErrorWrite" should {
    "write success" in {
      asJson(ApiSuccess()) mustEqual asJson(Status.OK, "success")
      asJson(ApiSuccess(Status.CREATED, "created")) mustEqual asJson(Status.CREATED, "created")
    }
    "write error" in {
      val msg = "unexpected"
      asJson(ApiError(Status.BAD_REQUEST, msg)) mustEqual asJson(Status.BAD_REQUEST, msg)
    }
  }

  def asJson(a: ApiSuccess): String = {
    Json.stringify(Json.toJson(a))
  }

  def asJson(a: ApiError): String = {
    Json.stringify(Json.toJson(a))
  }

  def asJson(code: Int, reason: String): String = {
    s"""{"reason":"$reason","status":$code}"""
  }
}
