package app

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import fs2._
import org.http4s.{EntityDecoder, HttpService}
import org.http4s.dsl.{->, /, GET, Ok, Root}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.circe._
import org.http4s.dsl._
import RequestHandler._
import cats.effect.IO
import search.Search

object Server extends {
  implicit val decoder: EntityDecoder[IO, Update] = jsonOf[IO, Update]

  def server(config: Config, search: Search): Stream[IO, Nothing] = {
      val route: HttpService[IO] = HttpService[IO] {
        case req@POST -> Root / "wikibot" / config.`botToken` =>
          for {
            u <- req.as[Update]
            _ <- RequestHandler.handleMessage(u.message, search)(config)
            resp <- Ok()
          } yield resp
      }

      BlazeBuilder[IO]
        .bindHttp(config.port, config.ip)
        .mountService(route, "/")
        .serve
    }

  def main(args: Array[String]): Unit = {
    (for {
      config <- Config.read
      search <- Search(config)
      serv <- server(config, search).run
    } yield serv).unsafeRunSync()
  }
}
