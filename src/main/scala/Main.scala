
import scala.xml.Elem

object Decoders {
  import org.http4s.scalaxml._
  import org.http4s._
  implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }
}


object Main {

  import org.http4s.client.blaze._
  import org.http4s.Uri

  import Decoders._

  val senatoEp: Uri = Uri.uri("http://dati.senato.it") / "sparql" +? ("default-graph-uri", Seq("")) +? ("format", Seq("application/sparql-results+xml")) +? ("timeout", Seq("0")) +? ("debug", Seq("on"))

  def queryURL(queryStr: String) = senatoEp +? ("query", Seq(queryStr))

  def run(): Unit = {
    val client = PooledHttp1Client()

    val url: Uri = queryURL("PREFIX osr: <http://dati.senato.it/osr/> select distinct ?id where { ?id a osr:Ddl } limit 100")

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

import doobie.imports._

object DB {

  import doobie.util.transactor.DriverManagerTransactor

  val xa = DriverManagerTransactor[IOLite](
    "org.sqlite.JDBC", "jdbc:sqlite:sample.db", "", ""
  )
  def tr[T](cio: ConnectionIO[T]) = cio.transact(xa).unsafePerformIO

}

object App {


  def main(args: Array[String]): Unit = {
    val program2 : ConnectionIO[Int] = sql"select 42".query[Int].unique
    //val t = program2.transact(xa)
    DB.tr(sql"CREATE TABLE IF NOT EXISTS DDL (id STRING);".update.run)
    println(DB.tr(program2))
    Main.run()
  }
}