import cats.data.EitherT
import io.circe.{Decoder, DecodingFailure}
import org.http4s._
import org.http4s.circe._
import JsonUtils.EncDec._
import cats.{Applicative, Unapply}
import fs2.Task
import io.circe.Decoder.Result
import org.http4s

object doobieDecoders {

  implicit def sparqlJSONdecoder: EntityDecoder[Sparql] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    jsonOf[Sparql].decode(msg.withContentType(Some(MediaType.`application/json`)), strict = true)
  }

  def fooU[FA](fa: FA)(implicit U: Unapply[Applicative, FA]): U.M[U.A] =
    U.subst(fa)

  implicit def sparqlJSONdecoderDdls[T<:SparqlRes](implicit imp: Decoder[T]): EntityDecoder[Seq[T]] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    import cats.implicits._


      val aa = msg.as[Sparql].map(sp => {
        println(sp)
        val ll: Seq[Result[T]] = sp.value.map(j => {
          println(imp.decodeJson(j))
          imp.decodeJson(j)
        })
        ll.foldRight( Either(io.circe.DecodingFailure, Seq[T]()) ) {
          case ( e, (ls, rs) ) => e.fold( l => (ls, rs), r => ( ls, r +: rs ) )
        }
      })
        //
   EitherT {
     aa
   }
  }

}


//import scala.xml.Elem
//import io.circe.generic.auto._

/*implicit def sparqlXMLdecoder: EntityDecoder[Elem] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+xml")) { msg =>
  xml.decode(msg.withContentType(Some(MediaType.`application/xml`)), strict = true)
}*/

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