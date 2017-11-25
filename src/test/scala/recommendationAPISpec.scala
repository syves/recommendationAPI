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
    val map1 = Map("att-b" -> "att-b-1", "att-c" -> "att-c-1", "att-a" -> "att-a-1")
    val descr = "att-b-3"
		val actual1 = partedAttrs(map1)
    val expected1 = Vector("a"-> 1, "b"-> 1,"c"->1)
    assert(actual1 == expected1)
	}

  "trueMatch" should "return a Vector of tuples" in {
    val map1 = Map("att-b" -> "att-b-1", "att-c" -> "att-c-1", "att-a" -> "att-a-1")
    val map2 = Map("att-b" -> "att-b-3", "att-c" -> "att-c-1", "att-d" -> "att-d-1")
    val actual = trueMatch(map1, map2)
    val expected = 1
    assert(actual == expected)
  }

  val testMap: Map[String, Map[String, String]] = Map(
    "sku-1" -> Map("att-a" -> "att-a-7","att-b"->"att-b-3","att-c"->"att-c-10","att-d"->"att-d-10"),
    "sku-2" -> Map("att-a"->"att-a-9","att-b"->"att-b-7","att-c"->"att-c-12","att-d"->"att-d-4"),
    "sku-3" -> Map("att-a"->"att-a-10","att-b"->"att-b-6","att-c"->"att-c-1","att-d"->"att-d-1"),
    "sku-4" -> Map("att-a"->"att-a-9","att-b"->"att-b-14","att-c"->"att-c-7","att-d"->"att-d-4"),
    "sku-5" -> Map("att-a"->"att-a-8","att-b"->"att-b-7","att-c"->"att-c-10","att-d"->"att-d-4"),
    "sku-6" -> Map("att-a"->"att-a-6","att-b"->"att-b-2","att-c"->"att-c-13","att-d"->"att-d-6"),
    "sku-7" -> Map("att-a"->"att-a-15","att-b"->"att-b-10","att-c"->"att-c-7","att-d"->"att-d-7"),
    "sku-8" -> Map("att-a"->"att-a-14","att-b"->"att-b-1","att-c"->"att-c-2","att-d"->"att-d-9"),
    "sku-9" -> Map("att-a"->"att-a-4","att-b"->"att-b-10","att-c"->"att-c-7","att-d"->"att-d-1"),
    "sku-10" -> Map("att-a"->"att-a-10","att-b"->"att-b-3","att-c"->"att-c-7","att-d"->"att-d-2"),
    "sku-11" -> Map("att-a"->"att-a-2","att-b"->"att-b-15","att-c"->"att-c-9","att-d"->"att-d-4"),
    "sku-12" -> Map("att-a"->"att-a-10","att-b"->"att-b-12","att-c"->"att-c-14","att-d"->"att-d-1"))

val optionMap = Some(testMap)

}
