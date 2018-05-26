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
import java.time.{LocalDate, LocalDateTime, Month}

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class DateTimeImplicitsSpec extends PlaySpec with GuiceOneAppPerSuite {

  val dt = app.injector.instanceOf[CronDao]

  "DateTimeImplicits.dateToLocalDate" should {
    "convert sql dates" in {
      dt.dateToLocalDate(new Date(0)) mustEqual LocalDate.of(1970, Month.JANUARY, 1)
    }
  }
  "DateTimeImplicits.unWrapOptionDateWrapOptionLocalDate" should {
    "handle None" in {
      dt.unWrapOptionDateWrapOptionLocalDate(None) mustBe empty
    }
    "handle sql dates" in {
      dt.unWrapOptionDateWrapOptionLocalDate(Some(new Date(0))) mustBe
        Some(LocalDate.of(1970, Month.JANUARY, 1))
    }
  }
  "DateTimeImplicits.unWrapOptionTimestampWrapOptionLocalDateTime" should {
    "handle None" in {
      dt.unWrapOptionTimestampWrapOptionLocalDateTime(None) mustBe empty
    }
    "handle sql timestamps" in {
      dt.unWrapOptionTimestampWrapOptionLocalDateTime(Some(new Timestamp(0))) mustBe
        Some(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0))
    }
  }
  "DateTimeImplicits.localDateColumnType" should {
    "handle LocalDate" in {
      dt.localDateColumnType.valueToSQLLiteral(LocalDate.of(1970, Month.JANUARY, 1)) mustEqual "{d '1970-01-01'}"
    }
  }
  "DateTimeImplicits.localDateTimeColumnType" should {
    "handle LocalDateTime" in {
      val dateTime = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0)
      dt.localDateTimeColumnType.valueToSQLLiteral(dateTime) mustEqual "{ts '1970-01-01 00:00:00.0'}"
    }
  }
}
