
import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import scala.util.parsing.json._
import argonaut._, Argonaut._
//import scala.math.Ordering.Implicits._

import RecommendationService._
import scala.util.Random._

object RecommendationUtils extends App {

  case class Sku(value: String)
  case class Score(value: Int)
  case class AttrVal(value: String) //extends Ordered[AttrVal]

  val input = """{
  "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
  "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
  "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
  }"""

//TODO: maybe I should use argonouat it may be able to give me an Option[Map[String, String]]
//some of map[string, string] or None
 //val optionSkus: Option[Any] = JSON.parseFull(input)
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

 def trueMatch(model: Vector[AttrVal], suggested: Vector[AttrVal]): Score = Score((model intersect suggested).length)

 def mapToAttrs(m: Map[String, String]): Vector[AttrVal] = m.toVector.sorted.map(tup => AttrVal(tup._2))

 def score(jsonRes: Option[Map[String, Map[String,String]]]): Vector[(Score, Sku, Vector[AttrVal])] = {
   jsonRes match {
     case Some(skuDb) =>
        var scored: Vector[ (Score, Sku, Vector[AttrVal]) ] = Vector()
        //how could this be more composable?
        //The directions did not include the source of the item to be matched.
        // I've chosen to take the first item from the db representation.
        val pastPurchase = skuDb.head
        val (pastSku, pastAttrMap) = pastPurchase
        val purchasedAttrs = mapToAttrs(pastAttrMap)

        //TODO replace with fold? while loop is fastest?
        skuDb.tail.foreach { skuTup =>
          val (skuName, attrMap)  = skuTup
          val attrs = mapToAttrs(attrMap)
          val score = trueMatch(purchasedAttrs, attrs)
          scored = scored :+ (score, Sku(skuName), attrs)
        }

        scored.sortWith{ case (a, b) =>
          val (aScore, aSku, aVals) = a; val (bScore, bSku, bVals) = b
            if (aScore.value > 0 && bScore.value > 0 )
              aScore.value > bScore.value
            else
              aVals.zip(bVals).forall { case (a, b) => a.value < b.value }
            }
      case None => Vector()
      }
    }

    //TODO take 10 and convert to the right shape

  println(score(optionMap))
}
