package app

import cats.effect.IO
import pureconfig.loadConfigOrThrow

case class Config(botToken: String,
                  telegramUrl: String,
                  graph: String,
                  database: String,
                  port: Int,
                  ip: String)

object Config {
  def read: IO[Config] = IO { loadConfigOrThrow[Config] }
}