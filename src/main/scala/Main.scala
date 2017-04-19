import doobie.imports._

object App {

  def main(args: Array[String]): Unit = {

    val program2 : ConnectionIO[Int] = sql"select 42".query[Int].unique
    DB.tr(sql"CREATE TABLE IF NOT EXISTS DDL (id STRING);".update.run)
    println(DB.tr(program2))

    println(Client.allDdl())

    Client.close()
  }
}