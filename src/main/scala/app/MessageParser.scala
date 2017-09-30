package app

import cats.effect.IO
import search.Search

object MessageParser {
  val commands = Map(
    "search" -> Search.search
  )

  def handleText(text: String)(implicit config: Config): Option[IO[String]] = {
    val words = parseText(text)

    for {
      commandName <- words.headOption
      command <- commands.get(commandName)
    } yield command(words.tail, config)
  }

  // split at whitespace
  def parseText(text: String): Seq[String] = text.trim.split("\\s+")
}