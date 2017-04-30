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
      val res = Client.request[Ddl]()//(Some(200))//("http://dati.senato.it/ddl/25597")
      res match {
        case Right(ddls) =>
          logger.info(ddls.length)
          import doobie.imports._
          //val ddl = Ddl("id", "", "", new Date(), "", "", "", "", "", 1,new Date(), 1, 1, 1, 1, "", "" )
          DB.tr(DB.upsert(ddls))

          println(DB.tr(DB.qr[Ddl]("select * from Ddl").head.list))
        case Left(e) => logger.error(e)
      }
      /*res.map( ddl =>
      })*/


    } finally {
      Client.close()
    }

  }
}