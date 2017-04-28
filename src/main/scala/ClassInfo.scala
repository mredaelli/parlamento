import shapeless.{::, Generic, HList, HNil, HasCoproductGeneric, HasProductGeneric, LabelledGeneric, Lazy, Witness}
import shapeless.labelled.FieldType

import scala.reflect.ClassTag

// credit to http://limansky.me/posts/2017-02-02-generating-sql-queries-with-shapeless.html

trait ClassInfo[A] {
  val fields: List[String]
  val name: String
}

trait ClassInfoLowPriority {
  implicit def primitiveFieldLister[K <: Symbol, H, T <: HList](implicit
                                                                witness: Witness.Aux[K],
                                                                tLister: ClassInfo[T]
                                                               ): ClassInfo[FieldType[K, H] :: T] = new ClassInfo[FieldType[K, H] ::T] {
    override val fields: List[String] = witness.value.name :: tLister.fields
    override val name: String = tLister.name
  }
}

object ClassInfo extends  ClassInfoLowPriority {

  implicit val hnilLister: ClassInfo[HNil] = new ClassInfo[HNil] {
    override val fields = Nil
    override val name: String = ""
  }

  implicit def genericLister[A, R](implicit
                                   gen: LabelledGeneric.Aux[A, R],
                                   w: ClassTag[A], // Toto: without reflection?
                                  ww: LabelledGeneric[A],
                                   lister: Lazy[ClassInfo[R]]
                                  ): ClassInfo[A] = new ClassInfo[A] {
    override val fields: List[String] = lister.value.fields
    override val name: String = w.toString()
  }

  implicit def hconsLister[K, H, T <: HList](implicit
                                             hLister: Lazy[ClassInfo[H]],
                                             tLister: ClassInfo[T]
                                            ): ClassInfo[FieldType[K, H] :: T] = new ClassInfo[FieldType[K, H] :: T] {
    override val fields: List[String] = hLister.value.fields ++ tLister.fields
    override val name: String = tLister.name
  }

  /*override implicit def primitiveFieldLister[K <: Symbol, H, T <: HList](implicit
                                                                         witness: Witness.Aux[K],
                                                                         tLister: ClassInfo[T]
                                                               ): ClassInfo[FieldType[K, H] :: T] = new ClassInfo[FieldType[K, H] ::T] {
    override val fields: List[String] = witness.value.name :: tLister.fields
    override val name: String = tLister.name
  }*/

}

