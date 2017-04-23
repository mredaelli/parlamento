

import io.circe.Json

import scala.xml.Elem
import io.circe.generic.auto._
import io.circe._
import io.circe.syntax._

private object Decoders {

  import org.http4s.scalaxml._
  import org.http4s._
  import org.http4s.circe._
  import EncDec._

  implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }

  implicit def sparqlJSONdecoderDdl: EntityDecoder[Ddl] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    jsonOf[Ddl].decode(msg, strict = true)
  }
}


object Client {

  import org.http4s.client.blaze._
  import org.http4s.Uri

  import Decoders._
  private val client = PooledHttp1Client()

  private val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("format", Seq("application/sparql-results+xml")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

  private def queryURL(queryStr: String) = senatoEp +? ("query", Seq(queryStr))

  private val PREFIX = "PREFIX osr: <http://dati.senato.it/osr/>"

  def completeQuery(t: String, fields: Traversable[String], ids: Set[String] = Set.empty, limit: Option[Int] = None) : String = {
    val fields_ = (Seq("id") ++ fields).map(s => s"?$s").mkString(", ")
    val where = (fields.map(s => s"OPTIONAL { ?id osr:$s ?$s}") ++
     (if( ids.isEmpty )  Seq() else Seq("FILTER(?id IN (" + ids.map(id => s"<$id>").mkString(",") + "))"))
    ).mkString(" ")

    (Seq(PREFIX, "select distinct", fields_, s"where { ?id a osr:$t.", where , " }") ++
      (limit match {
        case Some(n) => Seq(s"LIMIT $n")
        case None => Seq()
      })
    ).mkString(" ")
  }


  def allDdlIDs(): Option[Seq[String]] = {

    val readAllDdl = client.expect[Elem](queryURL(
      """
        |PREFIX osr: <http://dati.senato.it/osr/>
        |select distinct ?id
        |where { ?id a osr:Ddl }
        |limit 100
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

  def getDdl(id: String) = {

    val readAllDdl = client.expect[Ddl](queryURL(completeQuery("Ddl", Ddl.fields)))

    readAllDdl.unsafeAttemptRun()
  }

  def close(): Unit = client.shutdownNow()
}

