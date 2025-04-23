//> using dep "com.google.cloud.functions:functions-framework-api:1.1.4"

package example

import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}

class HelloFunction extends HttpFunction {
  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    // Log the request method and URL
    val method = request.getMethod
    val url = request.getUri
    println(s"Received $method request for $url")

    // Response to the HTTP request
    response.setContentType("text/plain")
    val writer = response.getWriter
    writer.write("Hello!")
    writer.flush()
  }
}
