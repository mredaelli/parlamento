import doobie.imports._
import doobie.util.transactor

object DB {

  import doobie.util.transactor.DriverManagerTransactor

  val xa: transactor.Transactor[IOLite] = DriverManagerTransactor[IOLite](
    "org.sqlite.JDBC", "jdbc:sqlite:sample.db", "", ""
  )

  def tr[T](cio: ConnectionIO[T]): T = cio.transact(xa).unsafePerformIO
  println("initialize db")
  val initializeDB = tr(
    sql"""
      drop table Ddl;

      CREATE TABLE Natura (
        Nome STRING.
        PRIMARY KEY (Nome)
      );

      INSERT INTO Natura (Name) VALUES ('ordinaria'),
        ('di conversione di decreto-legge'),
        ('di approvazione di bilancio'),
        ('costituzionale');

      CREATE TABLE IF NOT EXISTS Ddl (
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
      );
    """.update.run)

}