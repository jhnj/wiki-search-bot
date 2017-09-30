package app

import pureconfig.loadConfigOrThrow

case class Config(telegramToken: String,
                  telegramUrl: String)

object Config {
  def read: Config = loadConfigOrThrow[Config]
}