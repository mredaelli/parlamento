import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import shapeless.labelled.FieldType

// credit to http://limansky.me/posts/2017-02-02-generating-sql-queries-with-shapeless.html


trait FieldLister[A] {
  val list: List[String]
}

/*

trait FieldListerLowPriority {
  implicit def primitiveFieldLister[K <: Symbol, H, T <: HList](implicit
                                                                witness: Witness.Aux[K],
                                                                tLister: FieldLister[T]
                                                               ): FieldLister[FieldType[K, H] :: T] = new FieldLister[FieldType[K, H] ::T] {
    override val list = witness.value.name :: tLister.list
  }
}

*/

object FieldLister {

  implicit val hnilLister: FieldLister[HNil] = new FieldLister[HNil] {
    override val list = Nil
  }

  implicit def hconsLister[K, H, T <: HList](implicit
                                             hLister: Lazy[FieldLister[H]],
                                             tLister: FieldLister[T]
                                            ): FieldLister[FieldType[K, H] :: T] = new FieldLister[FieldType[K, H] :: T] {
    override val list: List[String] = hLister.value.list ++ tLister.list
  }

  implicit def genericLister[A, R](implicit
                          gen: LabelledGeneric.Aux[A, R],
                          lister: Lazy[FieldLister[R]]
                         ): FieldLister[A] = new FieldLister[A] {
    override val list: List[String] = lister.value.list
  }

  implicit def primitiveFieldLister[K <: Symbol, H, T <: HList](implicit
                                                                witness: Witness.Aux[K],
                                                                tLister: FieldLister[T]
                                                               ): FieldLister[FieldType[K, H] :: T] = new FieldLister[FieldType[K, H] ::T] {
    override val list: List[String] = witness.value.name :: tLister.list
  }

}