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

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.defaultAwaitTimeout

import scala.concurrent.Await

class CronSpec extends PlaySpec with BeforeAndAfterAll with GuiceOneAppPerSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    val service = app.injector.instanceOf[CronService]
    Await.result(service.createSchema(), defaultAwaitTimeout.duration)
  }

  "CronDao" should {
    "handle insert" in {
      val service = app.injector.instanceOf[CronService]
      val date = LocalDate.now
      val dateTime = LocalDateTime.now
      val cron = Cron(id = None, uuid = UUID.randomUUID(), name = "name", expression = "0 0 0/1 1/1 * ? *", createdAt = date,
        lastTranAt = Some(dateTime), enabled = true)
      val insertedCount = Await.result(service.insert(cron), defaultAwaitTimeout.duration)
      insertedCount mustEqual 1
      val found = Await.result(service.findByName("name"), defaultAwaitTimeout.duration)
      found must not be empty
      found.get mustEqual cron.copy(id = found.get.id)
    }
  }

}
