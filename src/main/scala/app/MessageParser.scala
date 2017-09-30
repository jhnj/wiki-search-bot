package app

import fs2.Task

object MessageParser {
  val commands = Map(
    "search" -> Search.search
  )

  def handleText(text: String): Option[Task[String]] = {
    val words = parseText(text)

    for {
      commandName <- words.headOption
      command <- commands.get(commandName)
    } yield command(words.tail)
  }

  // split at whitespace
  def parseText(text: String): Seq[String] = text.trim.split("\\s+")
}