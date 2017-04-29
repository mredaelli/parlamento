package io.typish.governo

import java.sql.Date

import doobie.free.connection.ConnectionIO
import doobie.imports._
import doobie.util.transactor
import fs2.Stream
import cats.implicits._
import doobie.util.transactor.DriverManagerTransactor
import scribe.Logging

import scala.reflect.runtime.universe._

object DB extends Logging {

  val xa: transactor.Transactor[IOLite] = DriverManagerTransactor[IOLite](
    "org.sqlite.JDBC", "jdbc:sqlite:sample.db", "", ""
  )

  implicit val UrlStringMeta: Meta[URLString] =
    Meta[String].nxmap(URLString, _.url)

  implicit def RefMeta[T >: Null : Meta : TypeTag]: Meta[Ref[T]] =
    Meta[String].nxmap(s => Ref[T](s), _.id)


  def init(): Int = {
    println("initialize db")

    def drop[T](implicit ci: ClassInfo[T]) = (fr"drop table if exists " ++ Fragment.const(ci.name)).update.run

    val naturaC = sql"""CREATE TABLE Natura (
                            Nome STRING,
                            PRIMARY KEY (Nome)
                          );""".update.run

    val naturaF = sql"""INSERT INTO Natura (Name) VALUES ('ordinaria'),
                                ('di conversione di decreto-legge'),
                                ('di approvazione di bilancio'),
                                ('costituzionale');""".update.run

    val DdlC = sql"""CREATE TABLE IF NOT EXISTS Ddl (
                             id STRING not null,
                             statoDdl STRING,
                             ramo STRING,
                             dataPresentazione DATE,
                             titolo STRING,
                             fase STRING,
                             descrIniziativa STRING,
                             presentatoTrasmesso STRING,
                             natura STRING,
                             idDdl INTEGER,
                             dataStatoDdl DATE,
                             numeroFase INTEGER,
                             legislatura INTEGER,
                             progressivoIter INTEGER,
                             idFase INTEGER,
                             numeroFaseCompatto STRING,
                             testoPresentato STRING,
                             primary key (id),
                             FOREIGN KEY(natura) REFERENCES Natura(Nome)
                           );""".update.run

    tr( drop[Natura] *> drop[Ddl] *> naturaC *> DdlC )
  }

  def tr[T](cio: ConnectionIO[T]): T = cio.transact(xa).unsafePerformIO

  def qr[T: Composite](q: String): Stream[ConnectionIO, T] = HC.process[T](q, ().pure[PreparedStatementIO], 512)

  def upsert(ddl: Ddl): ConnectionIO[Int] = {
    Update[Ddl]("""
      replace into Ddl
      (id , statoDdl, ramo , dataPresentazione , titolo , fase , descrIniziativa , presentatoTrasmesso , natura , idDdl
      , dataStatoDdl , numeroFase , legislatura , progressivoIter , idFase , numeroFaseCompatto , testoPresentato)
      values
      (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")
    .run(ddl)
  }

  def upsert[T](t: T)(implicit ci: ClassInfo[T], co: Composite[T]): ConnectionIO[Int] = {
    val table = ci.name
    val fields = ci.fields.mkString("(", ",", ")")
    val qms = ci.fields.map(_ => "?").mkString("(", ",", ")")
    val sql = s"replace into $table $fields values $qms"
    logger.warn(sql)
    Update[T](sql).run(t)
  }

  def upsert[T](t: Traversable[T])(implicit ci: ClassInfo[T], co: Composite[T]): ConnectionIO[Int] = {
    t.map( o => upsert(o) ).reduce((a, b) => a *> b)
  }
}