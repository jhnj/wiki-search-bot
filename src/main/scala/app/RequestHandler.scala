package app

import fs2.{Strategy, Task}
import org.http4s.client._
import org.http4s.dsl._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.Uri
import org.http4s.circe._
import org.http4s.client.blaze.PooledHttp1Client

import scala.concurrent.ExecutionContext.Implicits.global

object RequestHandler {
  implicit val strategy: Strategy = Strategy.fromExecutionContext(global)

  case class Update(message: Message)
  case class Message(message_id: Option[Int],
                     date: Option[Int],
                     chat: Chat,
                     text: String)
  case class Chat(id: Int)
  case class SendMessage(chat_id: Int, text: String)

  def handleMessage(m: Message)(implicit config: Config): Task[Unit] = {
    MessageParser.handleText(m.text).map { task =>
      for {
        responseText <- task
        res <- respond(SendMessage(m.chat.id, responseText))
      } yield res
    }.getOrElse(Task(()))
  }

  def respond(m: SendMessage)(implicit config: Config): Task[Unit] = {
    val httpClient = PooledHttp1Client()

    case class User(name: String)
    Uri.fromString(config.telegramUrl + config.telegramToken).map { uri =>
      httpClient.expect[Unit](POST(uri, m.asJson))
    }.getOrElse(Task(()))
  }
}