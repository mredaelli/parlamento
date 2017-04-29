
import org.http4s.{EntityDecoder, Request}
import scribe.Logging
import org.http4s.client.blaze._
import org.http4s.Uri
import circeDecoders._

import scala.reflect.ClassTag

object Client extends Logging {

  private val client = PooledHttp1Client()

  private val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

  private def queryURL(queryStr: String) = senatoEp +? ("query", Seq(queryStr))

  private val PREFIX = "PREFIX osr: <http://dati.senato.it/osr/>"

  def completeQuery(t: String, fields: Traversable[String], ids: Traversable[String] = Set.empty, limit: Option[Int] = None) : String = {
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


  private def request[T <: SparqlRes](id: Traversable[String])
    (implicit ci: ClassInfo[T], d: EntityDecoder[Seq[T]]) : Either[Throwable, Seq[T]] = {
    val mt = implicitly[EntityDecoder[Sparql]].consumes.head
    val query = completeQuery(ci.name, ci.fields.filterNot(_.equals("id")), ids = id)
    logger.warn(query)
    val req: Request = Request(uri = queryURL(query) +? ("format", Seq(mt.renderString)) )
    val readAllDdl = client.expect[Seq[T]](req)(d)
    readAllDdl.unsafeAttemptRun()
  }

  def request[T <: SparqlRes](id: String)(implicit ci: ClassInfo[T], d: EntityDecoder[Seq[T]])
    : Either[Throwable, Seq[T]] = request[T](Seq(id))
  def request[T <: SparqlRes]()(implicit ci: ClassInfo[T], d: EntityDecoder[Seq[T]])
    : Either[Throwable, Seq[T]] = request[T](Seq())

  def close(): Unit = client.shutdownNow()
}

