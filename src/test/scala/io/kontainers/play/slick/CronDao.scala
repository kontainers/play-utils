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

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{GetResult, JdbcProfile}

case class Cron(id: Option[Long], uuid: UUID, name: String, expression: String, createdAt: LocalDate,
                lastTranAt: Option[LocalDateTime], enabled: Boolean)

class CronDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DbImplicits {
  import profile.api._

  private val CronQuery = TableQuery[CronTable]

  implicit val cronResult: GetResult[Cron] = GetResult(r => Cron(r.nextLongOption(), r.nextString(),
    r.nextString(), r.nextString(), r.nextDate(), r.nextTimestampOption(), r.nextBoolean()))

  def insert(cron: Cron): DBIOAction[Int, NoStream, Effect.All] = CronQuery.insertOrUpdate(cron)
  def deleteAll(): DBIOAction[Int, NoStream, Effect.All] = CronQuery.delete

  def findByName(name: String): DBIOAction[Option[Cron], NoStream, Effect.Read] = CronQuery.filter(_.name === name).result.headOption

  // artificial test of filterIf; if expr input is None, findByExpression selects all
  def findByExpression(expr: Option[String]): DBIOAction[Seq[Cron], NoStream, Effect.Read] =
    CronQuery.filterIf(expr.isDefined)(_.expression === expr.get).result

  // artificial test of concat; if expr input is None, findByExpression selects all
  def findByExpressionConcatVersion(expr: Option[String]): DBIOAction[Seq[Cron], NoStream, Effect.Read] = {
    val selectBase = sql"""select * from "cron" where "id" is not null"""
    val select = expr match {
      case Some(e) => selectBase.concat(sql""" and "expression" = $e""")
      case _ => selectBase
    }
    select.as[Cron]
  }

  def createSchema(): DBIOAction[Unit, NoStream, Effect.All] = CronQuery.schema.create

  // scalastyle:off
  private class CronTable (tag: Tag) extends Table[Cron](tag, "cron") {
    def id= column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def uuid = column[UUID]("uuid")
    def name = column[String]("name")
    def expression = column[String]("expression")
    def createdAt = column[LocalDate]("created_at")
    def lastTranAt = column[Option[LocalDateTime]]("last_tran_at")
    def enabled = column[Boolean]("enabled")
    def * = (id, uuid, name, expression, createdAt, lastTranAt, enabled) <> (Cron.tupled, Cron.unapply _)
  }
}
