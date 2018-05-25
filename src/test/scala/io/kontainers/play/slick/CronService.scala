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

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class CronService @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, cronDao: CronDao)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  def createSchema(): Future[Unit] = db.run(cronDao.createSchema())

  def insert(cron: Cron): Future[Int] = db.run(cronDao.insert(cron))

  def findByName(name: String): Future[Option[Cron]] = db.run(cronDao.findByName(name))
}
