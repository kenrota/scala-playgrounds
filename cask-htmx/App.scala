//> using dep com.lihaoyi::cask::0.10.2
//> using dep com.lihaoyi::scalatags::0.13.1
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.util.Random
import scalatags.Text
import scalatags.Text.all.*

object App extends cask.MainRoutes {
  @cask.get("/")
  def index() = {
    html(
      head(
        script(src := "https://unpkg.com/htmx.org@1.9.12"),
        meta(charset := "utf-8")
      ),
      body(
        h1("Hello htmx"),
        h2("Polling"),
        div(
          attr("hx-get") := "/random-percentage",
          attr("hx-trigger") := "load, every 5s"
        ),
        h2("Click Event"),
        button(
          attr("hx-get") := "/new-item",
          attr("hx-trigger") := "click[shiftKey]",
          attr("hx-confirm") := "ok?",
          attr("hx-target") := "#logs",
          attr("hx-swap") := "afterend",
          "Shift + Click"
        ),
        ul(
          id := "logs",
          "History"
        ),
        h2("Toggle Color"),
        rect()
      )
    )
  }

  @cask.get("/random-percentage")
  def randomPercentage() = {
    s"${Random.between(0, 101)}%"
  }

  @cask.get("/new-item")
  def newItem() = {
    val now = ZonedDateTime.now()
    val isoDateTime = now.format(DateTimeFormatter.ISO_DATE_TIME)
    li(isoDateTime)
  }

  private var rectColor = "black"
  private def rect() = {
    div(
      id := "rect",
      style := s"width: 100px; height: 100px; background-color: $rectColor;",
      attr("hx-get") := "/toggle-color",
      attr("hx-swap") := "outerHTML"
    )
  }

  @cask.get("/toggle-color")
  def toggleColor() = {
    val newColor = if (rectColor == "black") "gray" else "black"
    rectColor = newColor
    rect()
  }

  initialize()
}
