
import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import scala.util.parsing.json._

import RecommendationService._
import scala.util.Random._

object RecommendationUtils extends App {

  case class Attribute(value: String)
  case class Description(value: String)
  case class Sku(value: String)
  case class SkuMap(name: Sku, attrs: Map[Attribute, Description])

  val input = """{
  "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
  "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
  "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
  }"""

//maybe I should use argonouat it may be able to give me an Option[Map[String, String]]
//some of map[string, string] or None
 //val optionSkus: Option[Any] = JSON.parseFull(input)

 def randomTargetSku: String = {
   val rand = new scala.util.Random()
   val randInt = math.abs(rand.nextInt(10000))
   val prefix = "sku-" + randInt.toString
   prefix
 }

 //map containing one record.
 def chooseRandomTarget(sku: String, skuDb: Map[String, Map[String, String]]): Map[String, Map[String, String]] = {
   //make me a new map with one item
   skuDb.get(sku) match {
     //wrap strings in types.
     case Some(res) => Map(sku -> res.flatMap{ case (attr, desc) => Map(attr -> desc) })
     //Is there a case where this will run indefinitely? perhaps the data is different form example.
     case None      => chooseRandomTarget(randomTargetSku, skuDb)
   }
 }

//1.Calculate similarity of articles identified by sku based on their (attributes values).
 //2. The number of (matching attributes) is the most important metric for defining similarity.
 //2.bIn case of a draw, attributes with name higher in alphabet (a is higher than z) is weighted with heavier weight.
//Q: is the final weight a total number or a weight for each attribute?


//could return a db of elements with a score, then after sort by score.
//this seems inefficent. but more composable.
//TODO find the right term for model
  case class Score(value: Int)
  def trueMatch(model: Vector[(String, Int)], suggested: Vector[(String, Int)]): Score = {
    Score((model intersect suggested).length)
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

  //make return type a type with impicit ordering?
  def score(jsonRes: Option[Map[String, Map[String,String]]]): Vector[(Score, Sku, Vector[(String, Int)])] = {
    jsonRes match {
      case Some(skuDb) =>
        //select the first element in the db representation as the item we will match for.
        //how could this be more composable? It might as well be efficient since it is random.
        var scored: Vector[ (Score, Sku, Vector[ (String, Int) ]) ] = Vector()
        val skuTup = skuDb.head
        val partedMod: Vector[(String, Int)] = partedAttrs(skuTup)
        //TODO replace with fold? while loop is fastest?

        //we do not return the model Sku itself as a match.
        //this relies on the model coming from the db. which it likely would.
        //TODO maybe spliting the description is unncessesary and we only sort by the attr value?
        skuDb.tail.foreach { skuTup =>
          val sku = Sku(skuTup._1)
          val partedSug = partedAttrs(skuTup)
          val score = trueMatch(partedMod, partedSug) //Score
          scored = scored :+ (score, sku, partedSug )
        }
        //no implicit ordering on a tuple3? move the Tuple3 sorting to a function?
        scored.sortWith{ case (a, b) =>
          val (aScore, aSku, aDescrTup) = a; val (bScore, bSku, bDescrTup) = b
          val (aChar, aNum) = bDescrTup(0); val (bChar, bNum) = bDescrTup(0)
            //does this cover all the cases?
            if (aScore == 0 && bScore == 0) {
              if (aChar != bChar) {
              //no exact matches, but char Matchers
                aChar < bChar
              }else {
                println("sorting by num")
                aNum < bNum
              }
            } else {
              aScore.value > bScore.value
            }
        }
      case None => Vector()
      }
    }
    //remove the model skew from the sorted results
    //TODO sort look for context.
    //TODO take 10

//original Map does not have type wrappers
//TODO type alias for return type?
//TODO do we even need the Int? from example it seems so, but from the readme desc.

  def partedAttrs(skuTup: (String, Map[String, String])): Vector[(String, Int)] = {
    val (skuName, attrMap) = skuTup
    attrMap
    //TODO replace two steps with a fold?
      .toSeq
      .map{ pair => val (attr, descr) = pair; partitionDescription(attr, descr)}
      //is sorting here inefficient?
      .toVector.sorted
    }
//this is only reusable if the data takes this shape. where something in the Attribut name matches the decription.
  def partitionDescription(attr: String, descr: String): (String, Int) = {
    val prefixLen = attr.length
    val alpha: String = descr.slice(prefixLen -1, prefixLen)
    val beta: Int = descr.slice(6,descr.length).toInt
    (alpha, beta)
  }
  println(score(optionMap))
}
