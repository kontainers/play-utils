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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.defaultAwaitTimeout

import scala.concurrent.Await

/**
  * Test validates that the CronDAO works ok with DbImplicits
  */
class CronSpec extends PlaySpec with BeforeAndAfterAll with BeforeAndAfterEach with GuiceOneAppPerSuite {

  val service = app.injector.instanceOf[CronService]
  val expr = "0 0 0/1 1/1 * ? *"

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(service.createSchema(), defaultAwaitTimeout.duration)
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    Await.result(service.deleteAll(), defaultAwaitTimeout.duration)
  }

  "CronDao" should {
    "handle insert" in {
      val (insertedCount, cron) = insertCron()
      insertedCount mustEqual 1
      val found = Await.result(service.findByName("name"), defaultAwaitTimeout.duration)
      found must not be empty
      found.get mustEqual cron.copy(id = found.get.id)
    }
    "support filterIf" in {
      val (insertedCount, cron) = insertCron()
      insertedCount mustEqual 1

      val (insertedCount2, cron2) = insertCron("fake cron")
      insertedCount2 mustEqual 1

      val found = Await.result(service.findByExpression(Some(expr)), defaultAwaitTimeout.duration)
      found must have size 1
      found.head mustEqual cron.copy(id = found.head.id)

      val foundExpectEmpty = Await.result(service.findByExpression(Some("")), defaultAwaitTimeout.duration)
      foundExpectEmpty must have size 0

      val foundAll = Await.result(service.findByExpression(None), defaultAwaitTimeout.duration)
      foundAll must have size 2
      foundAll.head must not equal foundAll.tail
    }
  }

  private def insertCron(expression: String = expr): (Int, Cron) = {
    val date = LocalDate.now
    val dateTime = LocalDateTime.now
    val cron = Cron(id = None, uuid = UUID.randomUUID(), name = "name", expression = expression, createdAt = date,
      lastTranAt = Some(dateTime), enabled = true)
    (Await.result(service.insert(cron), defaultAwaitTimeout.duration), cron)
  }

}
