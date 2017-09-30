package app

import fs2.{Stream, Task}
import org.http4s.HttpService
import org.http4s.dsl.{->, /, GET, Ok, Root}
import org.http4s.util.StreamApp
import org.http4s.server.blaze.BlazeBuilder
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.circe._
import org.http4s.client.blaze.PooledHttp1Client
import org.http4s.dsl._
import RequestHandler._

object Server extends StreamApp {

  implicit lazy val config: Config = Config.read

  override def stream(args: List[String]): Stream[Task, Nothing] =
    BlazeBuilder
      .bindHttp(9999, "0.0.0.0")
      .mountService(service)
      .serve



  val service = HttpService {
    case req @ POST -> Root / "bot" / config.telegramToken =>
      for {
        u <- req.as(jsonOf[Update])
        _ <- RequestHandler.handleMessage(u.message)
        resp <- Ok()
      } yield resp

    case req @ POST -> Root / "test" / token =>
      println(token)
      Ok()
  }
}
