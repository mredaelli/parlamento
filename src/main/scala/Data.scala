import java.util.Date

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