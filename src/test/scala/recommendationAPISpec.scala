import org.scalatest._

import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import scala.util.parsing.json._

class recommendationAPISpec extends FlatSpec with Matchers {
  import RecommendationService._
  import RecommendationUtils._

  val input = """{
    "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
    "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
    "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
    }"""

   //val optionSkus: Option[Any] = JSON.parseFull(input)
   //Some(Map(sku-1 -> Map(att-b -> att-b-3, att-i -> att-i-5, att-d -> att-d-10,
   // att-a -> att-a-7, att-e -> att-e-15, att-j -> att-j-14, att-g -> att-g-2,
   //att-c -> att-c-10, att-f -> att-f-11, att-h -> att-h-7)))

   //how to test random untilities?

	"partitionDescription" should "return a tup of String Int" in {
    val attr = "att-b"
    val descr = "att-b-3"
		val actual = partitionDescription(attr, descr)
    val expected = ("b", 3)
    assert(actual == expected)
	}

  "partedAttrs" should "return a Vector of tuples" in {
    val map1 = ("sku-1" -> Map("att-b" -> "att-b-1", "att-c" -> "att-c-1", "att-a" -> "att-a-1"))
		val actual = partedAttrs(map1)
    val expected = Vector("a"-> 1, "b"-> 1,"c"->1)
    assert(actual == expected)
	}

  "trueMatch" should "return a Vector of tuples" in {
    val map1 = Vector("a"-> 1, "b"-> 1,"c"->1)
    val map2 = Vector(("b" -> 3), ("c"->1), ("d"->1))
    val actual = trueMatch(map1, map2).value
    val expected = 1
    assert(actual == expected)
  }



  "score" should "return a Vector of tuples; does not contain head, sorted by trueMatch, atttribute value starting Char, them attr value number" in {
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

    val actual = score(optionMap)
    println(actual)
    /*
    Vector((Score(2),Sku(sku-12),Vector((a,10), (b,12), (c,14), (d,1))),
    (Score(1),Sku(sku-9),Vector((a,4), (b,10), (c,7), (d,1))),
    (Score(1),Sku(sku-10),Vector((a,10), (b,3), (c,7), (d,2))),
    (Score(0),Sku(sku-7),Vector((a,15), (b,10), (c,7), (d,7))),
    (Score(0),Sku(sku-4),Vector((a,9), (b,14), (c,7), (d,4))),
    (Score(0),Sku(sku-1),Vector((a,7), (b,3), (c,10), (d,10))),
    (Score(0),Sku(sku-11),Vector((a,2), (b,15), (c,9), (d,4))),
    (Score(0),Sku(sku-5),Vector((a,8), (b,7), (c,10), (d,4))),
    (Score(0),Sku(sku-2),Vector((a,9), (b,7), (c,12), (d,4))),
    (Score(0),Sku(sku-8),Vector((a,14), (b,1), (c,2), (d,9))),
    (Score(0),Sku(sku-6),Vector((a,6), (b,2), (c,13), (d,6))))*/

    val expected = Vector(
                          //(Score(4),Sku("sku-3"),Vector(("a",10), ("b",6), ("c",1), ("d",1))),
                          (Score(2),Sku("sku-12"),Vector(("a",10), ("b",12), ("c",14), ("d",1))),
                          (Score(1),Sku("sku-9"),Vector(("a",4), ("b",10), ("c",7), ("d",1))),
                          (Score(1),Sku("sku-10"),Vector(("a",10), ("b",3), ("c",7), ("d",2))),
                          //a2 should come before a3
                          (Score(0),Sku("sku-11"),Vector(("a",2), ("b",15), ("c",9), ("d",4))),
                          (Score(0),Sku("sku-6"),Vector(("a",6), ("b",2), ("c",13), ("d",6))),
                          (Score(0),Sku("sku-1"),Vector(("a",7), ("b",3), ("c",10), ("d",10))),    
                          (Score(0),Sku("sku-5"),Vector(("a",8), ("b",7), ("c",10), ("d",4))),
                          (Score(0),Sku("sku-2"),Vector(("a",9), ("b",7), ("c",12), ("d",4))),
                          (Score(0),Sku("sku-8"),Vector(("a",14), ("b",1), ("c",2), ("d",9))),
                          (Score(0),Sku("sku-7"),Vector(("a",15), ("b",10), ("c",7), ("d",7))),

                          //e should come after d
                          (Score(0),Sku("sku-4"),Vector(("a",9), ("b",14), ("c",7), ("e",4)))
                        )
    assert(actual == expected)
  }

}
