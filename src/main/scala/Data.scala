import java.util.Date

sealed trait SparqlRes

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
               testoPresentato : String) extends SparqlRes


trait HasFields[T] {
  def fields: Traversable[String]
}

object Fields {

  implicit def DdlHasFields(implicit fl: FieldLister[Ddl]) = new HasFields[Ddl] {
    override val fields: List[String] = fl.list
  }
}

