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
package io.kontainers.config

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatestplus.play.PlaySpec

class ConfigUtilSpec extends PlaySpec {

  val propName = "kontainers.core.sample"

  "ConfigUtil" should {
    "have default config with expected property" in {
      ConfigUtil.getProperty(propName, None) mustEqual "sample-prop"
      val cfg = ConfigFactory.parseString(ConfigUtil.getDefaultsConfigAsText)
      cfg.getString(propName) mustEqual "sample-prop"
    }
    "apply override to property" in {
      val overrideProps = s"$propName=overridden"
      ConfigUtil.getProperty(propName, Some(overrideProps)) mustEqual "overridden"
    }
    "apply override to getConfigAsText" in {
      val overrideProps = s"$propName=overridden"
      val cfg = ConfigFactory.parseString(ConfigUtil.getConfigAsText(overrideProps))
      cfg.getString(propName) mustEqual "overridden"
    }
    "update config with new property" in {
      val originalProps = "test.control1=v1\ntest.control2=v2"
      val updateProps = "test.control2=vx"
      val newConfig = ConfigUtil.updateConfig(originalProps, updateProps)
      val cfg = ConfigFactory.parseString(newConfig)
      cfg.getString("test.control1") mustEqual "v1"
      cfg.getString("test.control2") mustEqual "vx"
    }
    "remove property from config" in {
      val originalProps = "test.control1=v1\ntest.control2=v2"
      val removeProp = "test.control2"
      val newConfig = ConfigUtil.deleteConfig(originalProps, removeProp)
      val cfg = ConfigFactory.parseString(newConfig)
      cfg.getString("test.control1") mustEqual "v1"
      intercept[ConfigException.Missing] {
        cfg.getString("test.control2")
      }
    }
  }
}
