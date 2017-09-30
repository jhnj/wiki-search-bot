package app

import pureconfig.loadConfigOrThrow

case class Config(botToken: String,
                  telegramUrl: String,
                  graph: String,
                  database: String,
                  port: Int,
                  ip: String)

object Config {
  def read: Config = loadConfigOrThrow[Config]
}