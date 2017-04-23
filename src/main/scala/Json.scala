import java.text.SimpleDateFormat
import java.util.Date

import cats.data.Kleisli
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.util.Try

case class Sparql(value: Seq[Json])

object Json {

  object EncDec {


    private def parseSparqlJson(json: Json): Either[String, Seq[Json]] = {
      import cats.implicits._
      val bindings_ = json.hcursor.downField("results").downField("bindings").success
      bindings_ match {
        case Some(bindings) =>
          val funcs = bindings.downArray.fields.get.map(f =>
            Kleisli {
              (v: Json) => v.hcursor.downField(f).withFocus(j => j.withObject(_ => v.hcursor.downField(f).downField("value").focus.get)).top
            }
          ) reduce ((a, b) => a compose b)
          //val all = funcs
          Right(bindings.values.get.flatMap(funcs.apply))
        case None => Left(s"Couldn't navigate to results/bindings from $json")
      }
    }

    implicit val DateFormat : Encoder[Date] with Decoder[Date] = new Encoder[Date] with Decoder[Date] {
      private val sdf = new SimpleDateFormat("yyyy-MM-dd")

      override def apply(a: Date): Json = Encoder.encodeString.apply(sdf.format(a))

      override def apply(c: HCursor): Result[Date] = Decoder.decodeString.emapTry(
        s => Try(sdf.parse(s))
      ).apply(c)
    }

    implicit val SparqlJSON: Decoder[Sparql] = (c: HCursor) => Decoder.decodeJson.emap(
      s =>{
        parseSparqlJson(s) match {
          case Left(e) => Left(e)
          case Right(seq) => Right(Sparql(seq))
        }}
    ).apply(c)
  }

}
