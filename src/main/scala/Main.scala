import java.util.Date

import doobie.imports._

object Main {
  import io.circe._, io.circe.parser._
  def main(args: Array[String]): Unit = {
val entries = parse("""{
  |	"head": {
  |		"link": [],
  |		"vars": ["id", "statoDdl", "ramo", "dataPresentazione", "titolo", "fase", "descrIniziativa", "presentatoTrasmesso", "natura", "idDdl", "dataStatoDdl", "numeroFase", "legislatura", "progressivoIter", "idFase", "numeroFaseCompatto", "testoPresentato"]
  |	},
  |	"results": {
  |		"distinct": false,
  |		"ordered": true,
  |		"bindings": [{
  |			"id": {
  |				"type": "uri",
  |				"value": "http://dati.senato.it/ddl/25597"
  |			},
  |			"statoDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "in corso di esame in commissione"
  |			},
  |			"ramo": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "S"
  |			},
  |			"dataPresentazione": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "2006-06-09"
  |			},
  |			"titolo": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "Norme per la valorizzazione e la salvaguardia dei prodotti agroalimentari \" tradizionali \" ai sensi dell' articolo 8, comma 2, del decreto legislativo 30 aprile 1998, n. 173"
  |			},
  |			"fase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "S.601"
  |			},
  |			"descrIniziativa": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "Sen.  DIVINA SERGIO"
  |			},
  |			"presentatoTrasmesso": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "presentato"
  |			},
  |			"natura": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "ordinaria"
  |			},
  |			"idDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "23295"
  |			},
  |			"dataStatoDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#date",
  |				"value": "2007-04-11"
  |			},
  |			"numeroFase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "601"
  |			},
  |			"legislatura": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "15"
  |			},
  |			"progressivoIter": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "1"
  |			},
  |			"idFase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "25597"
  |			},
  |			"numeroFaseCompatto": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "006010000100"
  |			},
  |			"testoPresentato": {
  |				"type": "uri",
  |				"value": "urn:nir:senato.repubblica:disegno.legge:15.legislatura;601"
  |			}
  |		},{
  |			"id": {
  |				"type": "uri",
  |				"value": "http://dati.senato.it/ddl/25597"
  |			},
  |			"statoDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "in corso di esame in commissione"
  |			},
  |			"ramo": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "S"
  |			},
  |			"dataPresentazione": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "2006-06-09"
  |			},
  |			"titolo": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "Norme per la valorizzazione e la salvaguardia dei prodotti agroalimentari \" tradizionali \" ai sensi dell' articolo 8, comma 2, del decreto legislativo 30 aprile 1998, n. 173"
  |			},
  |			"fase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "S.601"
  |			},
  |			"descrIniziativa": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "Sen.  DIVINA SERGIO"
  |			},
  |			"presentatoTrasmesso": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "presentato"
  |			},
  |			"natura": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "ordinaria"
  |			},
  |			"idDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "23295"
  |			},
  |			"dataStatoDdl": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#date",
  |				"value": "2007-04-11"
  |			},
  |			"numeroFase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "601"
  |			},
  |			"legislatura": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "15"
  |			},
  |			"progressivoIter": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "1"
  |			},
  |			"idFase": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#integer",
  |				"value": "25597"
  |			},
  |			"numeroFaseCompatto": {
  |				"type": "typed-literal",
  |				"datatype": "http://www.w3.org/2001/XMLSchema#string",
  |				"value": "006010000100"
  |			},
  |			"testoPresentato": {
  |				"type": "uri",
  |				"value": "urn:nir:senato.repubblica:disegno.legge:15.legislatura;601"
  |			}
  |		}]
  |	}
  |}""".stripMargin).getOrElse(Json.False).hcursor.downField("results").downField("bindings")
  //println(entries.get.)

    entries.values.get.foreach( v =>
      entries.downArray.fields.get.foreach( f => println(v.hcursor.downField(f).downField("value").focus) )
    )

    try {
      println(DB.init())

      /*val ddls = Client.allDdl().get
      println(ddls.size)*/
      println(Client.completeQuery("Ddl", Ddl.fields, limit = Some(10), ids = Set("http://dati.senato.it/ddl/25597", "3")))
      val ddl = Ddl("id", "", "", new Date(), "", "", "", "", "", 1,new Date(), 1, 1, 1, 1, "", "" )
      DB.tr(DB.upsert(ddl))

      println(DB.tr(DB.qr[Ddl]("select * from Ddl").list))


    } finally {
      Client.close()
    }

  }
}