//> using scala 3.6.4
//> using test.dep org.scalameta::munit::1.1.0

class DropWhileTakeWhileTest extends munit.FunSuite {
  def extractFirstIncreasingSegment(data: List[Int]): List[Int] = {
    data
      .dropWhileWindow((prev, curr) => curr <= prev)
      .takeWhileWindow((prev, curr) => curr > prev)
  }

  extension (list: List[Int])
    def dropWhileWindow(cond: (Int, Int) => Boolean): List[Int] = {
      val remainFirst = list
        .sliding(2)
        .zipWithIndex
        .dropWhile {
          case (List(a, b), _) => cond(a, b)
          case _               => false
        }
        .nextOption()

      remainFirst match {
        case Some((_, idx)) => list.drop(idx)
        case None           => List.empty
      }
    }

    def takeWhileWindow(cond: (Int, Int) => Boolean): List[Int] = {
      val pairs = list.sliding(2).collect { case List(a, b) => (a, b) }.toList

      val taken = pairs.takeWhile { case (a, b) => cond(a, b) }
      if taken.isEmpty then List.empty
      else taken.map(_._1) :+ taken.last._2
    }

  val testFuncName = "extractFirstIncreasingSegment"

  test(s"$testFuncName should return extract the first increasing segment") {
    val data = List(10, 8, 7, 7, 8, 9, 13, 13, 11, 15)
    val expected = List(7, 8, 9, 13)
    assertEquals(extractFirstIncreasingSegment(data), expected)
  }

  test(
    f"$testFuncName should return an empty list when no increasing segment is found"
  ) {
    val data = List(10, 8, 7, 6, 3)
    val expected = List()
    assertEquals(extractFirstIncreasingSegment(data), expected)
  }

  test(
    s"$testFuncName should return entire list when all elements are increasing"
  ) {
    val data = List(7, 8, 9, 13, 14, 15)
    val expected = List(7, 8, 9, 13, 14, 15)
    assertEquals(extractFirstIncreasingSegment(data), expected)
  }
}
