import scala.xml.{Elem}

object aaa {
  import org.http4s.scalaxml._
  import org.http4s._
  implicit def videoDec: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }
}


object Main {

  import org.http4s.client.blaze._
  import org.http4s.Uri

  import aaa._

  val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("format", Seq("application/sparql-results+xml")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

  def run(): Unit = {
    val client = PooledHttp1Client()

    val url: Uri = senatoEp +? ("query", Seq("PREFIX osr: <http://dati.senato.it/osr/> select distinct ?id where { ?id a osr:Ddl } limit 100"))

    val task = client.expect[Elem](url)

    val either: Either[Throwable, Elem] = task.unsafeAttemptRun()
    either match {
      case Right(body) => {
        val all = (body \\ "result" \ "binding" \ "uri") map (n => n.text)
      }
      case Left(e) => e.printStackTrace()
    }

    client.shutdownNow()
  }
}

object App {
  def main(args: Array[String]): Unit = {
    Main.run()
  }
}