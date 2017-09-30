package search

import scala.collection.mutable

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