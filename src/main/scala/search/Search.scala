package search

import app.Config
import cats.effect.IO
import cats.implicits._
import cats._
import cats.data._


class Search(implicit config: Config) {
  val graphVector: Vector[Int] = GraphReader.graphStream.runLog.unsafeRunSync()
  val searchGraph = new SearchGraph(graphVector, graphVector.size)

  val search: (Seq[String], Config) => IO[String] =
    (seq, config) => {
      if (seq.length >= 2)
        searchGraph(seq.head, seq(1))(config)
      else
        IO { "Need 2 parameters" }
    }

  def searchGraph(from: String, to: String)(config: Config): IO[String] = {
    val db = new DB(config)
    val offsets: IO[Either[Seq[String], Seq[Int]]] = for {
      l <- List(from, to).traverse(getOffsetOrError(db))
    } yield sequenceEither(l)
    val nodes = offsets.map { either =>
      either.map { case List(f,t) =>
        searchGraph.bfs(f,t)
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


