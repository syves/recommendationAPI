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

  val testMap: Map[String, Map[String, String]] = Map(
    "sku-1" -> Map("att-a" -> "att-a-7","att-b"->"att-b-3","att-c"->"att-c-10","att-d"->"att-d-10"),
    "sku-2" -> Map("att-a"->"att-a-9","att-b"->"att-b-7","att-c"->"att-c-12","att-d"->"att-d-4"),
    "sku-3" -> Map("att-a"->"att-a-10","att-b"->"att-b-6","att-c"->"att-c-1","att-d"->"att-d-1"),
    //"sku-4" -> Map("att-a"->"att-a-9","att-b"->"att-b-14","att-c"->"att-c-7","att-d"->"att-e-4"),
    "sku-4" -> Map("att-a"->"att-a-9","att-b"->"att-b-14","att-c"->"att-c-7","att-e"->"att-e-4"),
    "sku-5" -> Map("att-a"->"att-a-8","att-b"->"att-b-7","att-c"->"att-c-10","att-d"->"att-d-4"),
    "sku-6" -> Map("att-a"->"att-a-6","att-b"->"att-b-2","att-c"->"att-c-13","att-d"->"att-d-6"),
    "sku-7" -> Map("att-a"->"att-a-15","att-b"->"att-b-10","att-c"->"att-c-7","att-d"->"att-d-7"),
    "sku-8" -> Map("att-a"->"att-a-14","att-b"->"att-b-1","att-c"->"att-c-2","att-d"->"att-d-9"),
    "sku-9" -> Map("att-a"->"att-a-4","att-b"->"att-b-10","att-c"->"att-c-7","att-d"->"att-d-1"),
    "sku-10" -> Map("att-a"->"att-a-10","att-b"->"att-b-3","att-c"->"att-c-7","att-d"->"att-d-2"),
    "sku-11" -> Map("att-a"->"att-a-2","att-b"->"att-b-15","att-c"->"att-c-9","att-d"->"att-d-4"),
    "sku-12" -> Map("att-a"->"att-a-10","att-b"->"att-b-12","att-c"->"att-c-14","att-d"->"att-d-1"))

  val optionMap = Some(testMap)

  "trueMatch" should "return a Vector of tuples" in {
    val model = Vector(AttrVal("a1"), AttrVal("b1"),AttrVal("c1"))
    val target = Vector(AttrVal("b3"), AttrVal("c1"), AttrVal("d1"))
    val actual = trueMatch(model, target).value
    val expected = 1
    assert(actual == expected)
  }

  "scored" should "return a Vector of tuples; does not contain head, sorted by trueMatch, atttribute value starting Char, them attr value number" in {

    val actual = scored(optionMap)
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
    val expected = Vector(
                          //(Score(4),Sku("sku-3"),Vector(("a",10), ("b",6), ("c",1), ("d",1))),
                          (Score(2),Sku("sku-12"),Vector(AttrVal("att-a-10"), AttrVal("att-b-12"), AttrVal("att-c-14"), AttrVal("att-d-1"))),
                          (Score(1),Sku("sku-9"),Vector(AttrVal("att-a-4"), AttrVal("att-b-10"), AttrVal("att-c-7"), AttrVal("att-d-1"))),
                          (Score(1),Sku("sku-10"),Vector(AttrVal("att-a-10"), AttrVal("att-b-3"), AttrVal("att-c-7"), AttrVal("att-d-2"))),
                          //a2 should come before a3
                          (Score(0),Sku("sku-11"),Vector(AttrVal("att-a-2"), AttrVal("att-b-15"), AttrVal("att-c-9"), AttrVal("att-d-4"))),
                          (Score(0),Sku("sku-6"),Vector(AttrVal("att-a-6"), AttrVal("att-b-2"), AttrVal("att-c-13"), AttrVal("att-d-6"))),
                          (Score(0),Sku("sku-1"),Vector(AttrVal("att-a-7"), AttrVal("att-b-3"), AttrVal("att-c-10"), AttrVal("att-d-10"))),
                          (Score(0),Sku("sku-5"),Vector(AttrVal("att-a-8"), AttrVal("att-b-7"), AttrVal("att-c-10"), AttrVal("att-d-4"))),
                          (Score(0),Sku("sku-2"),Vector(AttrVal("att-a-9"), AttrVal("att-b-7"), AttrVal("att-c-12"), AttrVal("att-d-4"))),
                          (Score(0),Sku("sku-4"),Vector(AttrVal("att-a-9"), AttrVal("att-b-14"), AttrVal("att-c-7"), AttrVal("att-e-4"))),
                          (Score(0),Sku("sku-8"),Vector(AttrVal("att-a-14"), AttrVal("att-b-1"), AttrVal("att-c-2"), AttrVal("att-d-9"))),
                          (Score(0),Sku("sku-7"),Vector(AttrVal("att-a-15"), AttrVal("att-b-19"), AttrVal("att-c-7"), AttrVal("att-d-7")))
                        )
    assert(actual == expected)
  }
  /*
  Vector((Score(2),Sku(sku-12),Vector(AttrVal(att-a-10), AttrVal(att-b-12), AttrVal(att-c-14), AttrVal(att-d-1))),
  (Score(1),Sku(sku-9),Vector(AttrVal(att-a-4), AttrVal(att-b-10), AttrVal(att-c-7), AttrVal(att-d-1))),
  (Score(1),Sku(sku-10),Vector(AttrVal(att-a-10), AttrVal(att-b-3), AttrVal(att-c-7), AttrVal(att-d-2))),
  (Score(0),Sku(sku-7),Vector(AttrVal(att-a-15), AttrVal(att-b-10), AttrVal(att-c-7), AttrVal(att-d-7))),
  (Score(0),Sku(sku-4),Vector(AttrVal(att-a-9), AttrVal(att-b-14), AttrVal(att-c-7), AttrVal(att-e-4))),
  (Score(0),Sku(sku-1),Vector(AttrVal(att-a-7), AttrVal(att-b-3), AttrVal(att-c-10), AttrVal(att-d-10))),
  (Score(0),Sku(sku-11),Vector(AttrVal(att-a-2), AttrVal(att-b-15), AttrVal(att-c-9), AttrVal(att-d-4))),
  (Score(0),Sku(sku-5),Vector(AttrVal(att-a-8), AttrVal(att-b-7), AttrVal(att-c-10), AttrVal(att-d-4))),
  (Score(0),Sku(sku-2),Vector(AttrVal(att-a-9), AttrVal(att-b-7), AttrVal(att-c-12), AttrVal(att-d-4))),
  (Score(0),Sku(sku-8),Vector(AttrVal(att-a-14), AttrVal(att-b-1), AttrVal(att-c-2), AttrVal(att-d-9))),
  (Score(0),Sku(sku-6),Vector(AttrVal(att-a-6), AttrVal(att-b-2), AttrVal(att-c-13), AttrVal(att-d-6))))
  */

  "getRecommendations" should "returns n suggestions" in {
    val scord = scored(optionMap)
    val numRecords = 5
    val actual = getRecommendations(scord, numRecords)
    val expected = Vector(
                          (Score(2),Sku("sku-12"),Vector(AttrVal("att-a-10"), AttrVal("att-b-12"), AttrVal("att-c-14"), AttrVal("att-d-1"))),
                          (Score(1),Sku("sku-9"),Vector(AttrVal("att-a-4"), AttrVal("att-b-10"), AttrVal("att-c-7"), AttrVal("att-d-1"))),
                          (Score(1),Sku("sku-10"),Vector(AttrVal("att-a-10"), AttrVal("att-b-3"), AttrVal("att-c-7"), AttrVal("att-d-2"))),
                          (Score(0),Sku("sku-11"),Vector(AttrVal("att-a-2"), AttrVal("att-b-15"), AttrVal("att-c-9"), AttrVal("att-d-4"))),
                          (Score(0),Sku("sku-6"),Vector(AttrVal("att-a-6"), AttrVal("att-b-2"), AttrVal("att-c-13"), AttrVal("att-d-6"))))
    assert(actual == expected)
  }
}
