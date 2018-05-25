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

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.{JdbcProfile, PositionedParameters, SQLActionBuilder, SetParameter}

trait QueryConcatImplicits {
  this: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  implicit class ConditionalQueryFilter[A,B,C[_]](q: Query[A,B,C]) {
    def filterIf(p: Boolean)(f: A => Rep[Boolean]): Query[A,B,C] =
      if (p) q.filter(f) else q
  }

  implicit class SQLActionBuilderConcat (a: SQLActionBuilder) {
    def concat (b: SQLActionBuilder): SQLActionBuilder = {
      SQLActionBuilder(a.queryParts ++ b.queryParts, new SetParameter[Unit] {
        def apply(p: Unit, pp: PositionedParameters): Unit = {
          a.unitPConv.apply(p, pp)
          b.unitPConv.apply(p, pp)
        }
      })
    }
  }
}
