import org.scalatest._

import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import argonaut._, Argonaut._

class recommendationAPISpec extends FlatSpec with Matchers {
  import RecommendationService._
  import RecommendationUtils._

  val input = """{
    "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
    "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
    "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"},
    "sku-4": {"att-a": "a1", "att-b": "b4", "att-c": "c3"},
    "sku-5": {"att-a": "a1", "att-b": "b5", "att-c": "c3"}
    }"""

  val skus = Parse.decodeOption[Map[String, Map[String, String]]](input)

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

  "scored" should "return a Vector of tuples" in {

    val actual = scored(pastPurchase, testMap)
    println(actual)
    /*
    Vector((Score(0),Sku(sku-7),Vector(AttrVal(att-a-15), AttrVal(att-b-10), AttrVal(att-c-7), AttrVal(att-d-7))),
    (Score(0),Sku(sku-4),Vector(AttrVal(att-a-9), AttrVal(att-b-14), AttrVal(att-c-7), AttrVal(att-e-4))),
    (Score(1),Sku(sku-9),Vector(AttrVal(att-a-4), AttrVal(att-b-10), AttrVal(att-c-7), AttrVal(att-d-1))),
    (Score(0),Sku(sku-1),Vector(AttrVal(att-a-7), AttrVal(att-b-3), AttrVal(att-c-10), AttrVal(att-d-10))),
    (Score(0),Sku(sku-11),Vector(AttrVal(att-a-2), AttrVal(att-b-15), AttrVal(att-c-9), AttrVal(att-d-4))),
    (Score(0),Sku(sku-5),Vector(AttrVal(att-a-8), AttrVal(att-b-7), AttrVal(att-c-10), AttrVal(att-d-4))),
    (Score(1),Sku(sku-10),Vector(AttrVal(att-a-10), AttrVal(att-b-3), AttrVal(att-c-7), AttrVal(att-d-2))),
    (Score(0),Sku(sku-2),Vector(AttrVal(att-a-9), AttrVal(att-b-7), AttrVal(att-c-12), AttrVal(att-d-4))),
    (Score(0),Sku(sku-8),Vector(AttrVal(att-a-14), AttrVal(att-b-1), AttrVal(att-c-2), AttrVal(att-d-9))),
    (Score(2),Sku(sku-12),Vector(AttrVal(att-a-10), AttrVal(att-b-12), AttrVal(att-c-14), AttrVal(att-d-1))),
    (Score(0),Sku(sku-6),Vector(AttrVal(att-a-6), AttrVal(att-b-2), AttrVal(att-c-13), AttrVal(att-d-6))))
    */

    //break results into parts with elements that have order and those that don't
    val expected = Vector(
      (Sku("sku-1"), Score(4), Vector(AttrVal("att-a-7"), AttrVal("att-b-3"), AttrVal("att-c-10"), AttrVal("att-d-10"))),
      (Sku("sku-5"), Score(1), Vector(AttrVal("att-a-8"), AttrVal("att-b-7"), AttrVal("att-c-10"), AttrVal("att-d-4"))),
      (Sku("sku-10"), Score(1), Vector(AttrVal("att-a-10"), AttrVal("att-b-3"), AttrVal("att-c-7"), AttrVal("att-d-2"))),
      (Sku("sku-11"), Score(0), Vector(AttrVal("att-a-2"), AttrVal("att-b-15"), AttrVal("att-c-9"), AttrVal("att-d-4"))),
      (Sku("sku-9"), Score(0), Vector(AttrVal("att-a-4"), AttrVal("att-b-10"), AttrVal("att-c-7"), AttrVal("att-d-1"))),
      (Sku("sku-6"), Score(0), Vector(AttrVal("att-a-6"), AttrVal("att-b-2"), AttrVal("att-c-13"), AttrVal("att-d-6"))),
      (Sku("sku-2"), Score(0), Vector(AttrVal("att-a-9"), AttrVal("att-b-7"), AttrVal("att-c-12"), AttrVal("att-d-4"))),
      (Sku("sku-4"), Score(0), Vector(AttrVal("att-a-9"), AttrVal("att-b-14"), AttrVal("att-c-7"), AttrVal("att-d-4"))),
      (Sku("sku-3"), Score(0), Vector(AttrVal("att-a-10"),AttrVal("att-b-6"),AttrVal("att-c-1"),AttrVal("att-d-1"))),
      (Sku("sku-12"), Score(0), Vector(AttrVal("att-a-10"), AttrVal("att-b-12"), AttrVal("att-c-14"), AttrVal("att-d-1"))),
      (Sku("sku-8"), Score(0), Vector(AttrVal("att-a-14"), AttrVal("att-b-1"), AttrVal("att-c-2"), AttrVal("att-d-9"))),
      (Sku("sku-7"), Score(0), Vector(AttrVal("att-a-15"), AttrVal("att-b-19"), AttrVal("att-c-7"), AttrVal("att-d-7"))))
    assert(actual == expected)
  }

}
