package app

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import fs2._
import org.http4s.{EntityDecoder, HttpService}
import org.http4s.dsl.{->, /, GET, Ok, Root}
import org.http4s.util.{StreamApp, _}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.circe._
import org.http4s.dsl._
import RequestHandler._
import cats.effect.IO
import search.Search

object Server extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val decoder: EntityDecoder[IO, Update] = jsonOf[IO, Update]

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, Nothing] = {

    Config.read
      .flatMap(config =>
        Stream(config).zipWith(
          Stream.eval(Search(config))
        )((_,_))
      )
      .observeAsync(Int.MaxValue)(server)
      .drain


  }

  def server[A]: Sink[IO, (Config, Search)] = cs => {
    cs.flatMap { case (config, search) =>
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
  }
}
