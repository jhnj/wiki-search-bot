package app

import cats.effect.IO
import search.{Search, SearchGraph}

import scala.util.matching.Regex.Match

object MessageParser {
  trait Command
  case object SearchCommand extends Command

  def handleText(text: String, search: Search)(implicit config: Config): Option[IO[String]] = {
    parseCommand(text).map { case (command, tail) =>
      command match {
        case SearchCommand =>
          parseSearch(tail)
            .map(tuple => search.search(tuple._1, tuple._2)(config))
            .getOrElse(IO("Need 2 parameters for search"))
      }
    }
  }

  def parseCommand(text: String): Option[(Command, String)] = {
    val commandRegex = raw"/(\w+)[\\s| ]+(.*)".r//raw"/search"
    commandRegex.findPrefixMatchOf(text).flatMap {
      case regMatch: Match if regMatch.groupCount >= 2 =>
        val command = regMatch.group(1).toLowerCase match {
          case "search" => Some(SearchCommand)
          case _ => None
        }
        command.map((_, regMatch.group(2).trim))
      case _ => None
    }
  }

  def parseSearch(text: String): Option[(String,String)] = {
    val tl: String => String = _.trim.toLowerCase

    val searchRegex = raw"[\s ]*\(([^\)]+)\)[\s ]*\(([^\)]+)\).*".r
    searchRegex.findPrefixMatchOf(text).flatMap {
      case regMatch: Match if regMatch.groupCount >= 2 =>
        Some((
          tl(regMatch.group(1)),
          tl(regMatch.group(2))
        ))
      case _ => None
    }
  }
}