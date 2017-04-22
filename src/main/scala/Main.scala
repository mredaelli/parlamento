import java.util.Date

import doobie.imports._

object Main {

  def main(args: Array[String]): Unit = {

    try {
      println(DB.init())

      /*val ddls = Client.allDdl().get
      println(ddls.size)*/

      val ddl = Ddl("id", "", "", new Date(), "", "", "", "", "", 1,new Date(), 1, 1, 1, 1, "", "" )
      DB.tr(DB.upsert(ddl))

      println(DB.tr(DB.qr[Ddl]("select * from Ddl").list))


    } finally {
      Client.close()
    }

  }
}