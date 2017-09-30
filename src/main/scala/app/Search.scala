package app

import fs2.{Strategy, Task}

import scala.concurrent.ExecutionContext.Implicits.global

object Search {
  implicit val strategy: Strategy = Strategy.fromExecutionContext(global)

  val search: Seq[String] => Task[String] =
    seq => {
      Task {
        if (seq.length == 2) "found" else "not"
      }
    }
}
