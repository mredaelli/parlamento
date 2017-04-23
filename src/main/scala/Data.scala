import java.text.SimpleDateFormat
import java.util.Date

import io.circe._, io.circe.parser._
import io.circe.Decoder._
import io.circe.generic.auto._

import scala.util.Try

case class Ddl(id : String,
               statoDdl : String,
               ramo : String,
               dataPresentazione: Date,
               titolo : String,
               fase : String,
               descrIniziativa : String,
               presentatoTrasmesso : String,
               natura : String,
               idDdl: Int,
               dataStatoDdl: Date,
               numeroFase: Int,
               legislatura: Int,
               progressivoIter: Int,
               idFase: Int,
               numeroFaseCompatto : String,
               testoPresentato : String)
object Ddl {
  val fields = Seq("statoDdl", "ramo", "dataPresentazione", "titolo", "fase",
    "descrIniziativa",
    "presentatoTrasmesso",
    "natura",
    "idDdl",
    "dataStatoDdl",
    "numeroFase",
    "legislatura",
    "progressivoIter",
    "idFase",
    "numeroFaseCompatto",
    "testoPresentato")
}

case class Sparql(value: Json)

object EncDec {

  implicit val DateFormat : Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
    private val sdf = new SimpleDateFormat("yyyy-MM-dd")

    override def apply(a: Date): Json = Encoder.encodeString.apply(sdf.format(a))

    override def apply(c: HCursor): Result[Date] = Decoder.decodeString.emapTry(
      s => Try(sdf.parse(s))
    ).apply(c)
  }

  implicit val SparqlJSON: Decoder[Sparql] = new Decoder[Sparql] {
    override def apply(c: HCursor): Result[Sparql] = Decoder.decodeString.emap(
      s => parse(s) match {
        case Left(e) => Left(e.message)
        case Right(json) => Right(Sparql(json.hcursor.downField("results").downField("bindings").downArray.focus.get))
      }
    ).apply(c)
  }

  implicitly[Decoder[Ddl]]
}


/*implicit val DdlDec: Decoder[Ddl] =
    Decoder.forProduct17("id",
      "statoDdl", "ramo", "dataPresentazione", "titolo", "fase",
      "descrIniziativa",
      "presentatoTrasmesso",
      "natura",
      "idDdl",
      "dataStatoDdl",
      "numeroFase",
      "legislatura",
      "progressivoIter",
      "idFase",
      "numeroFaseCompatto",
      "testoPresentato")(Ddl.apply)*/