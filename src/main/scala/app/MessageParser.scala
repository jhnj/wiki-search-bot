package app

import cats.effect.IO

object MessageParser {
  val commands = Map(
    "search" -> Search.search
  )

  def handleText(text: String): Option[IO[String]] = {
    val words = parseText(text)

    for {
      commandName <- words.headOption
      command <- commands.get(commandName)
    } yield command(words.tail)
  }

  // split at whitespace
  def parseText(text: String): Seq[String] = text.trim.split("\\s+")
}