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
import javax.inject.{Inject, Provider, Singleton}
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.{JsResultException, Json}
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent.Future

@Singleton
class ApiErrorHandler @Inject()(env: Environment,
                                config: Configuration,
                                sourceMapper: OptionalSourceMapper,
                                router: Provider[Router]
                                )
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with ApiSuccessErrorWrite {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      exception match {
        case _: NotFoundException => {
          Logger.warn("Not Found", exception)
          NotFound(Json.toJson(ApiError(http.Status.NOT_FOUND, exception.getMessage)))
        }
        case _ @ (_: ApiJsonException | _: JsResultException) => {
          Logger.error("Bad Request", exception)
          BadRequest(Json.toJson(ApiError(http.Status.BAD_REQUEST, exception.getMessage)))
        }
        case _ => {
          Logger.error("Server error", exception)
          InternalServerError(Json.toJson(ApiError(http.Status.INTERNAL_SERVER_ERROR, exception.getMessage)))
        }
      }
    )
  }

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    Logger.error(s"Bad Request $message")
    Future.successful(BadRequest(Json.toJson(ApiError(http.Status.BAD_REQUEST, message))))
  }
}
