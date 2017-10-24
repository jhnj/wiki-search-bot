package search

import app.Config
import cats.effect.IO
import cats.implicits._
import cats._
import cats.data._
import fs2.Stream


class Search(graphArray: Array[Int])(config: Config) {
  def search(from: String, to: String)(config: Config): IO[String] = {
    val db = new DB(config)
    val offsets: IO[Either[Seq[String], Seq[Int]]] = for {
      l <- List(from, to).traverse(getOffsetOrError(db))
    } yield sequenceEither(l)
    val pageCounta: IO[Option[Int]] = db.getPageCount()

    val nodes: IO[Either[Seq[String], List[Int]]] = for {
      pageCount <- db.getPageCount()
      either <- offsets
    } yield {
      pageCount.toRight(Seq()).flatMap { pc =>
        val searchGraph = new SearchGraph(graphArray, pc + 10000)
        either.map { case List(f,t) =>
          searchGraph.bfs(f,t)
        }
      }
    }

    nodes.flatMap {
        case Right(ofs) if ofs.length >= 2 =>
          db.getTitles(ofs).map(formatResult)
        case Left(notFound) => IO {
          if (notFound.length == 2)
            s"Could not find titles: ${notFound.head} and ${notFound(1)}"
          else if (notFound.length == 1)
            s"Could not find title: ${notFound.head}"
          else "Something went wrong"
      }
    }
  }

  def getOffsetOrError(db: DB)(title: String): IO[Either[String, Int]] = {
    db.getOffset(title).map {
      case Some(offset) => Right(offset)
      case None => Left(title)
    }
  }

  def formatResult(res: Option[List[String]]): String = {
    res.map { list =>
      s"Shortest path between ${list.head} and ${list.last}:\n" +
      list.mkString(" -> ")
    } getOrElse "Path not found"
  }

  def sequenceEither[A,B](x: Seq[Either[A, B]]): Either[Seq[A], Seq[B]] = x partition {_.isLeft} match {
    case (Seq(), r) => Right(r map {_.right.get})
    case (l, _) => Left(l map {_.left.get})
  }
}

object Search {
  def apply(config: Config): IO[Search] = {
    for {
      graphVector <- GraphReader.readGraphArray(config.graph)
      search <- IO {
        new Search(graphVector)(config)
      }
    } yield search
  }
}


