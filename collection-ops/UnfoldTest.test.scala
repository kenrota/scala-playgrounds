//> using scala 3.6.4
//> using test.dep org.scalameta::munit::1.1.0
import java.time._

class UnfoldTest extends munit.FunSuite {
  case class Log(timestamp: ZonedDateTime, message: String)

  def zdt(str: String): ZonedDateTime = ZonedDateTime.parse(str)

  def truncateTo5Min(zdt: ZonedDateTime): ZonedDateTime = {
    val min = zdt.getMinute - (zdt.getMinute % 5)
    zdt.withMinute(min).withSecond(0).withNano(0)
  }

  val logs = List(
    Log(zdt("2025-04-01T00:00:12Z"), "A"),
    Log(zdt("2025-04-01T00:01:20Z"), "B"),
    Log(zdt("2025-04-01T00:03:00Z"), "C"),
    Log(zdt("2025-04-01T00:05:10Z"), "D"),
    Log(zdt("2025-04-01T00:06:30Z"), "E"),
    Log(zdt("2025-04-01T00:10:05Z"), "F")
  ).sortBy(_.timestamp)

  def splitInto5MinBuckets(
      logs: List[Log]
  ): List[(ZonedDateTime, List[Log])] = {
    LazyList
      .unfold(logs) { // `logs` is the initial state
        case Nil => None
        case remaining => // `remaining` is the current state
          val windowStart = truncateTo5Min(remaining.head.timestamp)
          val windowEnd = windowStart.plusMinutes(5)

          val (inWindow, rest) = remaining.span { log =>
            !log.timestamp
              .isBefore(windowStart) && log.timestamp.isBefore(windowEnd)
          }

          // `(windowStart, inWindow)` is the result for this iteration
          // `rest` is the state for the next iteration
          Some((windowStart, inWindow) -> rest)
      }
      .toList
  }

  test("splitInto5MinBuckets should group logs into 5-minute buckets") {
    assertEquals(
      splitInto5MinBuckets(logs),
      List(
        zdt("2025-04-01T00:00:00Z") -> List(
          Log(zdt("2025-04-01T00:00:12Z"), "A"),
          Log(zdt("2025-04-01T00:01:20Z"), "B"),
          Log(zdt("2025-04-01T00:03:00Z"), "C")
        ),
        zdt("2025-04-01T00:05:00Z") -> List(
          Log(zdt("2025-04-01T00:05:10Z"), "D"),
          Log(zdt("2025-04-01T00:06:30Z"), "E")
        ),
        zdt("2025-04-01T00:10:00Z") -> List(
          Log(zdt("2025-04-01T00:10:05Z"), "F")
        )
      )
    )
  }
}
