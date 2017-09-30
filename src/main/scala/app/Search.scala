package app

import java.nio.ByteBuffer
import java.nio.file.Paths

import cats.effect.IO
import fs2.io.file.readAll
import fs2.{Chunk, Pipe, Pull, Stream}

import scala.collection.mutable

object Search {
  val search: Seq[String] => IO[String] =
    seq => {
      IO {
        if (seq.length == 2) "found" else "not"
      }
    }

  def graphStream(config: Config): Stream[IO, Int] =
    readAll[IO](Paths.get(config.graph), 4096)
      .through(getInts)

  def getInts[F[_]]: Pipe[F,Byte,Int] = {
    def getInt(buffer: Vector[Byte], chunk: Chunk[Byte]): (Vector[Byte], Chunk[Int]) = {
      @annotation.tailrec
      def loop(buffer: Vector[Byte], output: Vector[Int]): (Vector[Byte], Chunk[Int]) = {
        if (buffer.length >= 4) {
          val (head, tail) = buffer.splitAt(4)
          val int = ByteBuffer.wrap(head.toArray).getInt
          loop(tail, output :+ int)
        } else {
          (buffer, Chunk.indexedSeq(output))
        }
      }

      loop(buffer ++ chunk.toVector, Vector.empty)
    }

    def go(bytes: Vector[Byte], s: Stream[F,Byte]): Pull[F, Int, None.type] = {
      s.pull.unconsChunk.flatMap {
        case Some((head, tail)) =>
          val (newBytes, ints) = getInt(bytes, head)
          Pull.output(ints) >> go(newBytes, tail)
        case None => Pull.pure(None)
      }
    }

    in => go(Vector.empty, in).stream
  }
}

class SearchGraph(graph: Vector[Int], size: Int) {
  val prev: Array[Int] = Array.fill(size)(-1)

  def links(offset: Int): Vector[Int] = {
    val numLinks = graph(offset)
    graph.slice(offset + 2, offset + numLinks + 2)
  }

  def bfs(start: Int, stop: Int): List[Int] = {
    val q = new mutable.Queue[Int]

    q.enqueue(start)

    var found = false
    while (q.nonEmpty && !found) {
      val node = q.dequeue
      if (node == stop)
        found = true
      else {
        links(node).foreach(n => {
          if (getPrev(n) < 0) {
            setPrev(n,node)
            q.enqueue(n)
          }
        })
      }
    }

    @annotation.tailrec
    def getPath(node: Int, list: List[Int] = List()): List[Int] = {
      if (node == start)
        node +: list
      else
        getPath(getPrev(node), node +: list)
    }

    getPath(stop)
  }

  def getPrev(i: Int): Int = {
    prev(graph(i + 1))
  }

  def setPrev(i: Int, value: Int): Unit = {
    prev(graph(i + 1)) = value
  }
}
