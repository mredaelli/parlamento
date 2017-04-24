
import cats.data.EitherT
import io.circe.Decoder
import org.http4s.{EntityDecoder, Request}
import Fields._
import Json.EncDec._
import doobieDecoders._
import io.circe.generic.auto._
import scribe.Logging

object Client extends Logging {

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

  /*
      logger.update {
      logger.copy(multiplier = 1.0)
    }
   */

  def getDdl[T <: SparqlRes](id: String)(implicit fields: HasFields[T], d: EntityDecoder[Seq[T]]) = {
    import doobieDecoders._

    val mt = implicitly[EntityDecoder[Sparql]].consumes.head
    logger.debug(fields)
    val query = completeQuery("Ddl", fields.fields.filterNot(_.equals("id")), ids = Set(id))
    logger.debug(query)
    val req: Request = Request(uri = queryURL(query) +? ("format", Seq(mt.renderString)) )
    logger.debug(req)
    val readAllDdl = client.expect[Seq[T]](req)(d)
    readAllDdl.unsafeAttemptRun()
  }

  def close(): Unit = client.shutdownNow()
}

