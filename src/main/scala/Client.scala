

import cats.data.EitherT

import io.circe.Decoder
import org.http4s.{EntityDecoder, Request}

//import scala.xml.Elem

private object Decoders {

  import org.http4s._
  import org.http4s.circe._
  import EncDec._

  /*implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }*/

  implicit def sparqlJSONdecoder: EntityDecoder[Sparql] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    jsonOf[Sparql].decode(msg.withContentType(Some(MediaType.`application/json`)), strict = true)
  }

  implicit def sparqlJSONdecoderDdl: EntityDecoder[Ddl] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    val imp = implicitly[Decoder[Ddl]]
    import cats.implicits._
    EitherT {
      msg.as[Sparql].map( sp => imp.decodeJson(sp.value.head).toOption.get.asRight[DecodeFailure])
    }
  }

  implicit def sparqlJSONdecoderDdls: EntityDecoder[Seq[Ddl]] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    val imp = implicitly[Decoder[Ddl]]
    import cats.implicits._
    EitherT {
      msg.as[Sparql].map( sp => sp.value.flatMap( j => imp.decodeJson(j).toOption ).asRight[DecodeFailure])
    }
  }
}


object Client {

  import org.http4s.client.blaze._
  import org.http4s.Uri


  private val client = PooledHttp1Client()

  private val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

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

  /*
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
  */
  def getDdl(id: String): Either[Throwable, Seq[Ddl]] = {
    import Decoders._
    val mt = implicitly[EntityDecoder[Sparql]].consumes.head
    val req: Request = Request(uri = queryURL(completeQuery("Ddl", Ddl.fields, ids = Set(id))) +? ("format", Seq(mt.renderString)) )
    println(req)
    val readAllDdl = client.expect[Seq[Ddl]](req)
    readAllDdl.unsafeAttemptRun()
  }

  def close(): Unit = client.shutdownNow()
}

