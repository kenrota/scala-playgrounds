//> using scala 3.6.4
//> using test.dep org.scalameta::munit::1.1.0

class UnfoldTest extends munit.FunSuite {
  case class Log(timestamp: Long)

  val logs = List(
    Log(0),
    Log(60),
    Log(120),
    Log(310),
    Log(320),
    Log(330),
    Log(700)
  ).sortBy(_.timestamp)

  def splitIntoBatches(logs: List[Log], windowSize: Long): List[List[Log]] = {
    LazyList
      .unfold(logs) { // `logs` is the initial state
        case Nil => None
        case xs => // `xs` is the current state
          val start = xs.head.timestamp
          val (batch, rest) = xs.span(_.timestamp < start + windowSize)

          // `batch` is the result of this iteration
          // `rest` is the state for the next iteration
          Some((batch, rest))
      }
      .toList
  }

  test("split logs into 3 batches of 5 minutes") {
    assertEquals(
      splitIntoBatches(logs, 300L).toList,
      List(
        List(
          Log(0),
          Log(60),
          Log(120)
        ),
        List(
          Log(310),
          Log(320),
          Log(330)
        ),
        List(
          Log(700)
        )
      )
    )
  }

  test("split logs into 2 batches of 6 minutes") {
    assertEquals(
      splitIntoBatches(logs, 360L).toList,
      List(
        List(
          Log(0),
          Log(60),
          Log(120),
          Log(310),
          Log(320),
          Log(330)
        ),
        List(
          Log(700)
        )
      )
    )
  }
}
