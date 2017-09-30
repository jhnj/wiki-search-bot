package search

import java.nio.ByteBuffer
import java.nio.file.Paths

import app.Config
import cats.effect.IO
import fs2.io.file.readAll
import fs2.{Chunk, Pipe, Pull, Stream}

import scala.collection.mutable

object Search {
  val search: (Seq[String], Config) => IO[String] =
    (seq, config) => {
      val db = new DB(config)
      db.getOffset("August").map(o => s"Found: $o")
    }
}


