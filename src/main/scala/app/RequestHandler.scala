package app

import cats.effect.IO
import org.http4s.client._
import org.http4s.dsl._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.Uri
import org.http4s.circe._
import org.http4s.client.blaze.PooledHttp1Client
import search.Search


object RequestHandler {

  case class Update(message: Message)

  case class Message(message_id: Option[Int],
                     date: Option[Int],
                     chat: Chat,
                     text: String)

  case class Chat(id: Int)

  case class SendMessage(chat_id: Int, text: String)

  def handleMessage(m: Message, search: Search)(implicit config: Config): IO[Unit] = {
    new MessageParser(search).handleText(m.text).map { task =>
      for {
        responseText <- task
        res <- respond(SendMessage(m.chat.id, responseText))
      } yield res
    }.getOrElse(IO.unit)
  }

  def respond(send: SendMessage)(implicit config: Config): IO[Unit] = {
    val httpClient = PooledHttp1Client[IO]()

    case class User(name: String)
    Uri.fromString(config.telegramUrl + config.botToken + "/sendMessage").map { uri =>
      httpClient.expect[Unit](POST(uri, send.asJson))
    }.getOrElse(IO(()))
  }
}