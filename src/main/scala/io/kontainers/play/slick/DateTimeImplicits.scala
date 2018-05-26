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

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime, ZoneOffset}

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

trait DateTimeImplicits {
  this: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  implicit val localDateColumnType = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    d => Timestamp.from(d.toInstant(ZoneOffset.ofHours(0))),
    d => d.toLocalDateTime
  )

  implicit def unWrapOptionDateWrapOptionLocalDate(value: Option[Date]): Option[LocalDate] =
    value.map(dateToLocalDate(_))

  implicit def unWrapOptionTimestampWrapOptionLocalDateTime(value: Option[Timestamp]): Option[LocalDateTime] =
    value map { _.toLocalDateTime }

  implicit def dateToLocalDate(value: Date): LocalDate = value.toLocalDate
}
