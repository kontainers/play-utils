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
package io.kontainers.play.slick

import java.util.UUID

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

trait UUIDImplicits {
  this: HasDatabaseConfig[JdbcProfile] =>

  implicit def unWrapOptionStringWrapOptionUUID(value: Option[String]): Option[UUID] =
    value match {
      case Some(idSt) => Some(UUID.fromString(idSt))
      case _ => None
    }

  implicit def stringUUID(value :String): UUID = UUID.fromString(value)
}
