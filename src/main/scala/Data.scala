import java.util.Date

trait Transparent

case class URLString(url: String) extends Transparent

case class Ref[T](id: String) extends Transparent

sealed trait SparqlRes

case class Natura(name: String) extends SparqlRes

case class Ddl(id : URLString,
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

case class Classificazione(id: String,
                          livello: String,
                          subject: String) extends SparqlRes
//case class Subject(id: String)

case class Iniziativa(id: URLString,
                      label: String,
                      tipoIniziativa: String,
                      presentatore: String,
                      primoFirmatario: Int,
                      senatore: Ref[Senatore]) extends SparqlRes

case class Senatore(id: URLString,
                   label: String,
                   firstName: String,
                   gender: String,
                   lastName: String,
                   depiction: URLString,
                   cittaNascita: String,
                   provinciaNascita: String,
                   nazioneNascita: String,
                   dataNascita: Date) extends SparqlRes
                    //interviene: ...,
                    //afferisce: ...,
                    //mandato: ...

case class Documento() extends SparqlRes

case class Procedura(id: String,
                    titolo: String,
                    legislatura: Int,
                    tipo: String,
                    relativoA: Ref[Documento]) // ?? sottoclasse
  extends SparqlRes

case class Seduta(id: String,
                 dataSeduta: Date,
                 tipoSeduta: String,
                 legislatura: Int,
                 numeroSeduta: Int) extends SparqlRes

case class Intervento(id: String,
                     label: String,
                     oggetto: Ref[Procedura], // ?? sottoclasse
                     seduta: Ref[Seduta],
                     senatore: Ref[Senatore]) extends SparqlRes



