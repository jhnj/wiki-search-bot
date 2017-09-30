package search

import java.nio.ByteBuffer
import java.nio.file.Paths

import app.Config
import cats.effect.IO
import fs2.{Chunk, Pipe, Pull, Stream}
import fs2.io.file.readAll

class GraphReader {
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
