package io.typish.governo

import scribe.Logging

object Main  extends Logging {
  //Logger.root.addHandler(LogHandler(level = Level.Debug, writer = ConsoleWriter))

  def main(args: Array[String]): Unit = {

    try {
      println(DB.init())

      /*val ddls = Client.allDdl().get
      println(ddls.size)*/
      //val cq = Client.completeQuery("Ddl", Ddl.fields, limit = Some(10), ids = Set("http://dati.senato.it/ddl/25597", "3"))
      import /*Fields._, */JsonUtils.EncDec._, circeDecoders._, io.circe.generic.auto._
      val res = Client.request[Ddl]//("http://dati.senato.it/ddl/25597")
      res.map( ddl => {
        logger.info(ddl.length)
        import doobie.imports._
        //val ddl = Ddl("id", "", "", new Date(), "", "", "", "", "", 1,new Date(), 1, 1, 1, 1, "", "" )
        DB.tr(DB.upsert(ddl))

        println(DB.tr(DB.qr[Ddl]("select * from Ddl").head.list))
      })


    } finally {
      Client.close()
    }

  }
}