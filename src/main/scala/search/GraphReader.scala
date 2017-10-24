package search

import java.io.FileInputStream
import java.nio.channels.FileChannel
import java.nio.{ByteBuffer, ByteOrder}

import cats.effect.IO

object GraphReader {
  def readGraphArray(path: String): IO[Array[Int]] = IO {
    val stream = new FileInputStream(path)
    try {
      val inChannel = stream.getChannel
      println(inChannel.size())
      val buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size)
      val result = new Array[Int]((inChannel.size / 4).toInt)
      buffer.order(ByteOrder.BIG_ENDIAN)
      val intBuffer = buffer.asIntBuffer
      intBuffer.get(result)
      result
    } finally if (stream != null) stream.close()
  }
}
