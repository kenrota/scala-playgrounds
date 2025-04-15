//> using scala 3.6.4
//> using test.dep "org.scalameta::munit::1.1.0"

class WithDefaultValueTest extends munit.FunSuite {
  val rawSales: List[(String, Int)] = List(
    ("Books", 1200),
    ("Electronics", 5000),
    ("Books", 800),
    ("Toys", 2000)
  )

  def aggregateSales(sales: List[(String, Int)]): Map[String, Int] = {
    sales
      // group by category
      .groupBy(_._1)
      // sum the sales for each category
      .map { case (cat, entries) =>
        cat -> entries.map(_._2).sum
      }
      // return a map with default value of 0 for missing categories
      .withDefaultValue(0)
  }

  test("aggregateSales should return correct sum for existing categories") {
    val result = aggregateSales(rawSales)

    assertEquals(result("Books"), 2000)
    assertEquals(result("Electronics"), 5000)
    assertEquals(result("Toys"), 2000)
  }

  test("aggregateSales should return default value for missing categories") {
    val result = aggregateSales(rawSales)

    assertEquals(result("Clothing"), 0)
    assertEquals(result("Games"), 0)
  }
}
