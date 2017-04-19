import scala.xml.Elem

private object Decoders {
  import org.http4s.scalaxml._
  import org.http4s._
  implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }
}


object Client {

  import org.http4s.client.blaze._
  import org.http4s.Uri

  import Decoders._
  private val client = PooledHttp1Client()

  private val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("format", Seq("application/sparql-results+xml")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

  private def queryURL(queryStr: String) = senatoEp +? ("query", Seq(queryStr))



  def allDdl(): Option[Seq[String]] = {

    val readAllDdl = client.expect[Elem](queryURL(
      """
        |PREFIX osr: <http://dati.senato.it/osr/>
        |select distinct ?id
        |where { ?id a osr:Ddl }
        |""".stripMargin))

    readAllDdl.unsafeAttemptRun() match {
      case Right(body) =>
        Some((body \\ "result" \ "binding" \ "uri") map (n => n.text))
      case Left(e) => {
        e.printStackTrace()
        None
      }
    }
  }

  def close(): Unit = client.shutdownNow()
}

