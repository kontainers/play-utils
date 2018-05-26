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

import org.scalatestplus.play.PlaySpec

class UUIDImplicitsSpec extends PlaySpec {

  val uuidImplicits = new UUIDImplicits{}

  "UUIDImplicit.stringUUID" should {
    "convert strings" in {
      val uuid = UUID.randomUUID
      uuidImplicits.stringUUID(uuid.toString) mustEqual uuid
    }
  }

  "UUIDImplicit.unWrapOptionStringWrapOptionUUID" should {
    "handle None" in {
      uuidImplicits.unWrapOptionStringWrapOptionUUID(None) mustBe empty
    }
    "convert strings" in {
      val uuid = UUID.randomUUID
      uuidImplicits.unWrapOptionStringWrapOptionUUID(Some(uuid.toString)) mustEqual Some(uuid)
    }
  }

}
