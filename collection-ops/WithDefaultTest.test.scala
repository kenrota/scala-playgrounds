//> using scala 3.6.4
//> using test.dep "org.scalameta::munit::1.1.0"

class WithDefaultTest extends munit.FunSuite {
  val userNames: Map[String, String] = Map(
    "user1" -> "Alice",
    "user2" -> "Bob"
  ).withDefault(id => s"Guest-$id") // Provide a fallback name for unknown users

  val logs: List[(String, String)] = List(
    // userId, action
    ("user1", "login"),
    ("user2", "purchase"),
    ("user42", "login"), // Non-existing user
    ("user99", "logout") // Non-existing user
  )

  def enrichLogsWithUserNames(
      logs: List[(String, String)]
  ): List[(String, String)] = {
    logs.map { case (userId, action) =>
      val name = userNames(userId)
      (name, action)
    }
  }

  test("enrichLogsWithUserNames should replace userId with userName") {
    val result = enrichLogsWithUserNames(logs)

    assertEquals(
      result,
      List(
        ("Alice", "login"),
        ("Bob", "purchase"),
        ("Guest-user42", "login"),
        ("Guest-user99", "logout")
      )
    )
  }
}
