package app

import cats.effect.IO
import search.{Search, SearchGraph}

class MessageParser(search: Search)(implicit config: Config) {
  val commands = Map(
    "search" -> search.search
  )

  def handleText(text: String)(implicit config: Config): Option[IO[String]] = {
    val words = parseText(text)

    for {
      commandName <- words.headOption
      command <- commands.get(commandName)
    } yield command(words.tail, config)
  }

  // split at | surrounded by whitespace or 2+ whitespaces
  def parseText(text: String): Seq[String] = text.trim.split("\\s*\\|\\s*|\\s\\s+")
}