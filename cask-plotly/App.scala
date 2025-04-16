//> using scala 3.6.4
//> using dep com.lihaoyi::cask::0.10.2
//> using dep com.lihaoyi::scalatags::0.13.1
import scala.util.Random
import scalatags.Text
import scalatags.Text.all.*
import ujson.*

object App extends cask.MainRoutes {
  def generateRandomData(size: Int, maxValue: Int): Seq[Int] = {
    Seq.fill(size)(Random.nextInt(maxValue + 1))
  }

  def createPlotlyData(
      x: Seq[Int],
      y: Seq[Int],
      chartType: String,
      name: String
  ): Value = {
    Obj(
      "x" -> Arr(x.map(Num(_))*),
      "y" -> Arr(y.map(Num(_))*),
      "type" -> chartType,
      "name" -> name
    )
  }

  def createLayout(title: String): Value = {
    Obj("title" -> title)
  }

  case class Stats(average: Double, total: Int, max: Int, min: Int)

  def computeStats(values: Seq[Int]): Stats = {
    Stats(
      average = values.sum.toDouble / values.size,
      total = values.sum,
      max = values.max,
      min = values.min
    )
  }

  def buildStatsTable(stats: Stats): Text.TypedTag[String] = table(
    tr(th("Stats"), th("Value")),
    tr(td("Average"), td(f"${stats.average}%.2f")),
    tr(td("Total"), td(stats.total)),
    tr(td("Max"), td(stats.max)),
    tr(td("Min"), td(stats.min))
  )

  @cask.get("/")
  def index() = {
    val xValues = 0 to 23
    val yValues = generateRandomData(size = xValues.size, maxValue = 100)
    val dataJson = ujson.write(
      createPlotlyData(
        x = xValues,
        y = yValues,
        chartType = "bar",
        name = "random data"
      )
    )
    val layoutJson = ujson.write(createLayout("Random Data Chart"))
    val stats = computeStats(yValues)
    val statsTable = buildStatsTable(stats)

    html(
      head(
        script(src := "https://cdn.plot.ly/plotly-latest.min.js")
      ),
      body(
        h1("Report", style := "text-align: center"),
        div(id := "chartDiv"),
        script(raw(s"""
        const data = [$dataJson];
        const layout = $layoutJson;
        Plotly.newPlot('chartDiv', data, layout);
      """)),
        statsTable
      )
    )
  }

  initialize()
}
