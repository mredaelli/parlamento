import cats.data.EitherT
import io.circe.Decoder

object doobieDecoders {
  import org.http4s._
  import org.http4s.circe._

  import Fields._, Json.EncDec._, doobieDecoders._, io.circe.generic.auto._
  //import scala.xml.Elem


  /*implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
    xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
  }*/

  implicit def sparqlJSONdecoder: EntityDecoder[Sparql] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    jsonOf[Sparql].decode(msg.withContentType(Some(MediaType.`application/json`)), strict = true)
  }

  import io.circe.generic.auto._

  /*
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
  }*/

  implicit def sparqlJSONdecoderDdls[T<:SparqlRes](implicit imp: Decoder[T]): EntityDecoder[Seq[T]] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    import cats.implicits._
    EitherT {
      msg.as[Sparql].map( sp => sp.value.flatMap( j => imp.decodeJson(j).toOption ).asRight[DecodeFailure])
    }
  }

}
