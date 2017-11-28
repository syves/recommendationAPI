import org.scalatest._

class recommendationAPISpec extends FlatSpec with Matchers {
  import RecommendationService._
  import RecommendationUtils._

  "scored" should "return a skus ordered by matches then bt attribute weighted a-z" in {
    val testMap: Map[Sku, Map[AttrKey, AttrVal]] = Map(
      Sku("sku-1") -> Map(AttrKey("att-a") -> AttrVal("att-a-7"),AttrKey("att-b")->AttrVal("att-b-3"),AttrKey("att-c")->AttrVal("att-c-10"),AttrKey("att-d")->AttrVal("att-d-10")),
      Sku("sku-2") -> Map(AttrKey("att-a")->AttrVal("att-a-9"),AttrKey("att-b")->AttrVal("att-b-7"),AttrKey("att-c")->AttrVal("att-c-12"),AttrKey("att-d")->AttrVal("att-d-4")),
      Sku("sku-3") -> Map(AttrKey("att-a")->AttrVal("att-a-10"),AttrKey("att-b")->AttrVal("att-b-6"),AttrKey("att-c")->AttrVal("att-c-1"),AttrKey("att-d")->AttrVal("att-d-1")),
      Sku("sku-4") -> Map(AttrKey("att-a")->AttrVal("att-a-9"),AttrKey("att-b")->AttrVal("att-b-14"),AttrKey("att-c")->AttrVal("att-c-7"),AttrKey("att-d")->AttrVal("att-e-4")),
      Sku("sku-4") -> Map(AttrKey("att-a")->AttrVal("att-a-9"),AttrKey("att-b")->AttrVal("att-b-14"),AttrKey("att-c")->AttrVal("att-c-7"),AttrKey("att-d")->AttrVal("att-e-4")),
      Sku("sku-5") -> Map(AttrKey("att-a")->AttrVal("att-a-8"),AttrKey("att-b")->AttrVal("att-b-7"),AttrKey("att-c")->AttrVal("att-c-10"),AttrKey("att-d")->AttrVal("att-d-4")),
      Sku("sku-6") -> Map(AttrKey("att-a")->AttrVal("att-a-6"),AttrKey("att-b")->AttrVal("att-b-2"),AttrKey("att-c")->AttrVal("att-c-13"),AttrKey("att-d")->AttrVal("att-d-6")),
      Sku("sku-7") -> Map(AttrKey("att-a")->AttrVal("att-a-15"),AttrKey("att-b")->AttrVal("att-b-10"),AttrKey("att-c")->AttrVal("att-c-7"),AttrKey("att-d")->AttrVal("att-d-7")),
      Sku("sku-8") -> Map(AttrKey("att-a")->AttrVal("att-a-14"),AttrKey("att-b")->AttrVal("att-b-1"),AttrKey("att-c")->AttrVal("att-c-2"),AttrKey("att-d")->AttrVal("att-d-9")),
      Sku("sku-9") -> Map(AttrKey("att-a")->AttrVal("att-a-4"),AttrKey("att-b")->AttrVal("att-b-10"),AttrKey("att-c")->AttrVal("att-c-7"),AttrKey("att-d")->AttrVal("att-d-1")),
      Sku("sku-10") -> Map(AttrKey("att-a")->AttrVal("att-a-10"),AttrKey("att-b")->AttrVal("att-b-3"),AttrKey("att-c")->AttrVal("att-c-7"),AttrKey("att-d")->AttrVal("att-d-2")),
      Sku("sku-11") -> Map(AttrKey("att-a")->AttrVal("att-a-2"),AttrKey("att-b")->AttrVal("att-b-15"),AttrKey("att-c")->AttrVal("att-c-9"),AttrKey("att-d")->AttrVal("att-d-4")),
      Sku("sku-12") -> Map(AttrKey("att-a")->AttrVal("att-a-10"),AttrKey("att-b")->AttrVal("att-b-12"),AttrKey("att-c")->AttrVal("att-c-14"),AttrKey("att-d")->AttrVal("att-d-1")))

    val pastPurchase = Sku("sku-1")
    //??
    val actual = scored(pastPurchase, testMap).right.get.take(2)
    val expected = Vector(
      (Sku("sku-10"), Score(1), Vector(AttrVal("att-a-10"), AttrVal("att-b-3"), AttrVal("att-c-7"), AttrVal("att-d-2"))),
      (Sku("sku-5"), Score(1), Vector(AttrVal("att-a-8"), AttrVal("att-b-7"), AttrVal("att-c-10"), AttrVal("att-d-4"))))
    assert(actual == expected)
  }
  //other things to test? result read from whole file?
  /*
  Vector((Sku(sku-6276),Score(5),List(AttrVal(att-a-12), AttrVal(att-b-3), AttrVal(att-c-10), AttrVal(att-d-11), AttrVal(att-e-15), AttrVal(att-f-11), AttrVal(att-g-1), AttrVal(att-h-7), AttrVal(att-i-14), AttrVal(att-j-3))), (Sku(sku-10078),Score(5),List(AttrVal(att-a-5), AttrVal(att-b-3), AttrVal(att-c-10), AttrVal(att-d-8), AttrVal(att-e-15), AttrVal(att-f-2), AttrVal(att-g-2), AttrVal(att-h-7), AttrVal(att-i-14), AttrVal(att-j-1))), (Sku(sku-5349),Score(5),List(AttrVal(att-a-14), AttrVal(att-b-3), AttrVal(att-c-10), AttrVal(att-d-7), AttrVal(att-e-2), AttrVal(att-f-11), AttrVal(att-g-2), AttrVal(att-h-7), AttrVal(att-i-4), AttrVal(att-j-2))))
  */

}
