package app

import pureconfig.loadConfigOrThrow

case class Config(telegramToken: String,
                  telegramUrl: String,
                  graph: String,
                  database: String)

object Config {
  def read: Config = loadConfigOrThrow[Config]
}