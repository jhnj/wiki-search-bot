package search

import app.Config
import cats.effect.IO
import cats.implicits._


class Search(implicit config: Config) {
  val graphVector: Vector[Int] = GraphReader.graphStream.runLog.unsafeRunSync()
  val searchGraph = new SearchGraph(graphVector, graphVector.size)

  val search: (Seq[String], Config) => IO[String] =
    (seq, config) => {
      if (seq.length >= 2)
        searchGraph(seq.head, seq(1))(config)
          .map(formatResult)
      else
        IO { "Need 2 parameters" }
    }

  def searchGraph(from: String, to: String)(config: Config): IO[Option[List[String]]] = {
    val db = new DB(config)
    val offsets = for {
      f <- db.getOffset(from)
      t <- db.getOffset(to)
    } yield (f,t).bisequence
    val nodes = offsets.map { opt =>
      opt.map { case (f,t) =>
        searchGraph.bfs(f,t)
      }
    }
    nodes.flatMap { opt =>
      val list = opt.get
      db.getTitles(list)
    }
  }

  def formatResult(res: Option[List[String]]): String = {
    res.map(_.toString).getOrElse("Path not found")
  }
}


