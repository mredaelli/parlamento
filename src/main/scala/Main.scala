
object Main {


      def main(args: Array[String]): Unit = {
    try {
      //println(DB.init())

      /*val ddls = Client.allDdl().get
      println(ddls.size)*/
      //val cq = Client.completeQuery("Ddl", Ddl.fields, limit = Some(10), ids = Set("http://dati.senato.it/ddl/25597", "3"))
      val res = Client.getDdl("http://dati.senato.it/ddl/25597")
      println(res)

      /*val ddl = Ddl("id", "", "", new Date(), "", "", "", "", "", 1,new Date(), 1, 1, 1, 1, "", "" )
      DB.tr(DB.upsert(ddl))

      println(DB.tr(DB.qr[Ddl]("select * from Ddl").list))*/


    } finally {
      Client.close()
    }

  }
}