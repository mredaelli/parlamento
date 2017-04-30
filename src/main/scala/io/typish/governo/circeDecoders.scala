package io.typish.governo

import java.text.SimpleDateFormat
import java.util.Date

import cats.implicits._
import cats.data._
import io.circe.{Decoder, DecodingFailure}
import org.http4s._
import org.http4s.circe._
import JsonUtils.EncDec._
import fs2.Task
import io.circe.Decoder.Result

object circeDecoders {

  implicit def sparqlJSONdecoder: EntityDecoder[Sparql] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>
    jsonOf[Sparql].decode(msg.withContentType(Some(MediaType.`application/json`)), strict = true)
  }

  implicit val decodeUrlString: Decoder[URLString] = Decoder.decodeString.map { str =>
    URLString(str)
  }

  implicit val decodeDate: Decoder[Date] = Decoder.decodeString.emap { str =>
    val dsf = new SimpleDateFormat("yyyy-MM-dd")
    Either.catchNonFatal(dsf.parse(str)).leftMap(t => t.getMessage)
  }

  implicit def decodeRef[T](implicit r: Decoder[T]): Decoder[Ref[T]] = Decoder.decodeString.map { str =>
    Ref(str)
  }

  implicit def sparqlJSONdecoderDdls[T<:SparqlRes](implicit imp: Decoder[T]): EntityDecoder[Seq[T]] = EntityDecoder.decodeBy(MediaType.fromKey("application", "sparql-results+json")) { msg =>

      val decodeRes: Task[Either[DecodeFailure, Seq[T]]] = msg.as[Sparql].map(sp => {
        val allRes: Seq[Result[T]] =  sp.value.map(imp.decodeJson)
        val oneRes = allRes.foldLeft(Seq[T]().asRight[DecodingFailure]) { (acc, n) =>
          acc match {
            case Left(err) => n match {
              case Left(nerr) => DecodingFailure(err.message + nerr.message, err.history ++ nerr.history).asLeft[Seq[T]]
              case _ => acc
            }
            case Right(ok) => n match {
              case Left(nerr) => DecodingFailure(nerr.message, nerr.history).asLeft[Seq[T]]
              case Right(nok) => (nok +: ok).asRight[DecodingFailure]
            }
          }
        }
        oneRes match {
          case Right(r) => r.asRight[DecodeFailure]
          case Left(l) => GenericDecodeFailure(l.getMessage(), httpVersion => Response(Status.NotAcceptable, httpVersion).withBody(""))
            .asLeft[Seq[T]]
        }
      })
   EitherT {
     decodeRes
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