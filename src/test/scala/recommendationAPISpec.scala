import org.scalatest._

import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import scala.util.parsing.json._

import recommendationService._
import RecommendationUtils._


class recommendationAPISpec extends FlatSpec with Matchers {


  val input = """{
    "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
    "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
    "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
    }"""

   val optionSkus: Option[Any] = JSON.parseFull(input)
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
    val map1 = Map("att-a" -> "a1", "att-b" -> "b1", "att-c" -> "c1")
    val map2 = Map(
                    Attribute("att-a") -> Description("a1"),
                    Attribute("att-b") -> Description("b1"),
                    Attribute("att-c") -> Description("c1"))
    val descr = "att-b-3"
		val actual1 = partedAttrs(map1)
    val expected1 = Vector("a"-> 1, "b" -> 1, "c" -> 1)
    assert(actual1 == expected1)
    val actual2 = partedAttrs(map1)
    val expected2 = Vector("a"-> 1, "b" -> 1, "c" -> 1)
    assert(actual2 == expected2)
	}

/*
  Example 1:
  {"sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"}} is more similar to
  {"sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"}} than to
  {"sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}}
  Example 2:
  {"sku-1": {"att-a": "a1", "att-b": "b1"}} is more similar to
  {"sku-2": {"att-a": "a1", "att-b": "b2"}} than to
  {"sku-3": {"att-a": "a2", "att-b": "b1"}}
*/
}
