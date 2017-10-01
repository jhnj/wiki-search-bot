package app

import cats.effect.IO
import pureconfig.loadConfigOrThrow
import fs2.Stream

case class Config(botToken: String,
                  telegramUrl: String,
                  graph: String,
                  database: String,
                  port: Int,
                  ip: String)

object Config {
  def read: Stream[IO, Config] = Stream.eval(
    IO { loadConfigOrThrow[Config] }
  )
}