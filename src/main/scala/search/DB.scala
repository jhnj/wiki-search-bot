package search

import app.Config
import doobie._
import doobie.implicits._
import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import doobie.util.transactor.Transactor.Aux

class DB(config: Config) {
  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC", s"jdbc:sqlite:${config.database}", "", ""
  )

  def getOffset(title: String): IO[Option[Int]] =
    sql"SELECT offset FROM pages WHERE title = $title"
      .query[Int]
      .option
      .transact(xa)

  def getTitle(offset: Int): IO[Option[String]] =
    sql"SELECT title FROM pages WHERE offset = $offset"
      .query[String]
      .option
      .transact(xa)


}
