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

import scala.collection.JavaConverters._

class ConfigUtilSpec extends PlaySpec {

  val simplePropName = "kontainers.core.sample"
  val listPropName = "kontainers.core.listSample"

  "ConfigUtil" should {
    "have default config with expected property" in {
      ConfigUtil.getProperty(simplePropName, None) mustEqual "sample-prop"
      val cfg = ConfigFactory.parseString(ConfigUtil.getDefaultsConfigAsText)
      cfg.getString(simplePropName) mustEqual "sample-prop"
    }
    "apply override to property" in {
      val overrideProps = s"$simplePropName=overridden"
      ConfigUtil.getProperty(simplePropName, Some(overrideProps)) mustEqual "overridden"
    }
    "apply override to getConfigAsText" in {
      val overrideProps = s"$simplePropName=overridden"
      val cfg = ConfigFactory.parseString(ConfigUtil.getConfigAsText(overrideProps))
      cfg.getString(simplePropName) mustEqual "overridden"
    }
    "update config with new property" in {
      val originalProps = "test.control1=v1\ntest.control2=v2"
      val updateProps = "test.control2=vx"
      val newConfig = ConfigUtil.updateConfig(originalProps, updateProps)
      val cfg = ConfigFactory.parseString(newConfig)
      cfg.getString("test.control1") mustEqual "v1"
      cfg.getString("test.control2") mustEqual "vx"
      // shoudln't include default properties from kontainers-defaults.conf
      intercept[ConfigException.Missing] {
        cfg.getString("kontainers.core.sample")
      }
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
      intercept[ConfigException.Missing] {
        cfg.getString("kontainers.core.sample")
      }
    }
  }

  "ConfigUtil propertyList" should {
    "have default config with expected property" in {
      ConfigUtil.getPropertyList(listPropName, None) mustEqual List("abc", "123")
      val cfg = ConfigFactory.parseString(ConfigUtil.getDefaultsConfigAsText)
      cfg.getStringList(listPropName) mustEqual List("abc", "123").asJava
    }
    "apply override to property" in {
      val overrideProps = s"""$listPropName=["xyz"]"""
      ConfigUtil.getPropertyList(listPropName, Some(overrideProps)) mustEqual List("xyz")
    }
    "apply override to getConfigAsText" in {
      val overrideProps = s"""$listPropName=["xyz"]"""
      val cfg = ConfigFactory.parseString(ConfigUtil.getConfigAsText(overrideProps))
      cfg.getStringList(listPropName) mustEqual List("xyz").asJava
    }
    "update config with new property" in {
      val originalProps =
        """test {
          |  control1=["v1"]
          |  control2=["v2"]
          |}""".stripMargin
      val updateProps =
        """test {
          |  control2=["vx"]
          |}""".stripMargin
      val newConfig = ConfigUtil.updateConfig(originalProps, updateProps)
      val cfg = ConfigFactory.parseString(newConfig)
      cfg.getStringList("test.control1") mustEqual List("v1").asJava
      cfg.getStringList("test.control2") mustEqual List("vx").asJava
    }
    "remove property from config" in {
      val originalProps =
        """test {
          |  control1=["v1"]
          |  control2=["v2"]
          |}""".stripMargin
      val removeProp = "test.control2"
      val newConfig = ConfigUtil.deleteConfig(originalProps, removeProp)
      val cfg = ConfigFactory.parseString(newConfig)
      cfg.getStringList("test.control1") mustEqual List("v1").asJava
      intercept[ConfigException.Missing] {
        cfg.getStringList("test.control2")
      }
    }
  }
  "ConfigUtil getSubPropertyNames" should {
    "handle nested properties from default config" in {
      ConfigUtil.getSubPropertyNames("kontainers.core.nested", None) mustEqual Set("config1.prop", "config2.prop")
    }
    "handle extra nested properties in override" in {
      val overrides = "kontainers.core.nested.configx=vx\nkontainers.core.nested.configy.arg=varg"
      ConfigUtil.getSubPropertyNames("kontainers.core.nested", Some(overrides)) mustEqual
        Set("config1.prop", "config2.prop", "configx", "configy.arg")
    }
  }
  "ConfigUtil renderOptions" should {
    "omit origin comments" in {
      ConfigUtil.renderOptions.getOriginComments mustEqual false
      ConfigUtil.renderOptions.getComments mustEqual false
    }
  }
}