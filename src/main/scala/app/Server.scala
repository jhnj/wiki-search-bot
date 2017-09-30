package app

import fs2.Stream
import org.http4s.{EntityDecoder, HttpService}
import org.http4s.dsl.{->, /, GET, Ok, Root}
import org.http4s.util.{StreamApp, _}
import org.http4s.server.blaze.BlazeBuilder
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.circe._
import org.http4s.dsl._
import RequestHandler._
import cats.effect.IO
import search.Search

object Server extends StreamApp[IO] {

  implicit lazy val config: Config = Config.read
  implicit val decoder: EntityDecoder[IO, Update] = jsonOf[IO, Update]
  val search = new Search


  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, Nothing] = {
    val port: Int = config.port
    val ip = config.ip
    BlazeBuilder[IO]
      .bindHttp(port, ip)
      .mountService(route, "/")
      .serve
  }



  val route: HttpService[IO] = HttpService[IO] {
    case req @ POST -> Root / "wikibot" / config.`botToken` =>
      for {
        u <- req.as[Update]
        _ <- RequestHandler.handleMessage(u.message, search)
        resp <- Ok()
      } yield resp
  }
}
