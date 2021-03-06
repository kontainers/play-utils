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

import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}

import scala.collection.JavaConverters._

object ConfigUtil {
  private lazy val defaultsConfig = ConfigFactory.parseResourcesAnySyntax("kontainers-defaults.conf")

  def getProperty(name: String, overrides: Option[String]): String = overrides match {
    case Some(o) => getConfigWithDefaults(o).getString(name)
    case _ => defaultsConfig.getString(name)
  }

  def getPropertyList(name: String, overrides: Option[String]): List[String] = overrides match {
    case Some(o) => getConfigWithDefaults(o).getStringList(name).asScala.toList
    case _ => defaultsConfig.getStringList(name).asScala.toList
  }

  def getSubPropertyNames(name: String, overrides: Option[String]): Set[String] = {
    val parentCfg = overrides match {
      case Some(o) => getConfigWithDefaults(o)
      case _ => defaultsConfig
    }
    parentCfg.getConfig(name).entrySet().asScala.map(_.getKey).toSet
  }

  def getDefaultsConfigAsText: String = configAsText(defaultsConfig)

  def getConfigAsText(overrides: String): String = configAsText(getConfigWithDefaults(overrides))

  def updateConfig(configText: String, updatedText: String): String = {
    val newConfig = getConfig(updatedText).withFallback(getConfig(configText))
    configAsText(newConfig)
  }

  def deleteConfig(configText: String, propertyToRemove: String): String = {
    val newConfig = getConfig(configText).withoutPath(propertyToRemove)
    configAsText(newConfig)
  }

  private[config] def renderOptions =
    ConfigRenderOptions.defaults().setComments(false).setOriginComments(false)

  private def configAsText(cfg: Config): String = cfg.root().render(renderOptions)

  private def getConfigWithDefaults(overrides: String): Config = {
    getConfig(overrides).withFallback(defaultsConfig)
  }

  private def getConfig(overrides: String): Config = {
    ConfigFactory.parseString(overrides)
  }
}
