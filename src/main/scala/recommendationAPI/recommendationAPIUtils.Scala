
import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import scala.util.parsing.json._

import RecommendationService._
import math.Random._

object RecommendationUtils extends App {

case class Attribute(value: String)
case class Descprition(value: String)
case class Sku(value: String)
case class SkuMap(name: Sku, attrs: Map[Attribute, Descprition])

val input = """{
  "sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
  "sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
  "sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
  }"""

//some of map[string, string] or None
 val optionSkus: Option[Any] = JSON.parseFull(input)

def randomTargetSku: Sku =
  val rand = new scala.util.Random()
  val randInt = math.abs(rand.nextInt(10000))
  val prefix = "sku-" + rantInt.toString


//map containing one record.
def chooseRandomTarget(sku: Sku, skuDb: Map[String, Map[String, String]]): Map[Sku, Map[Attribute, Descprition]] = {
  //make me a new map with one item
  skuDb.get(sku) match {
    //wrap strings in types.
    case Some(res) => Map(Sku(sku) -> res.map{ case (attr, desc) => Attribute(attr); Description(desc)})
    //Is there a case where this will run indefinitely? perhaps the data is different form example.
    case None      => chooseRandomTarget(randomTargetSku, skuDb)
  }
}

//Q: filter map for results that are not a match at all? but what if there are no good matches?
//def isSameCategory desc.startsWith(attr)
//Map[String, Map[String, String]]
/*
  def sortByWeight(target: Map[Sku, Map[Attribute, Descprition]], jsonRes: Option[Any]): ? = {

    val partedTarg = partedAttrs(target)

    jsonRes match {
      Some(skuDb) =>
        skuDb.toSeq.sortWith { skuMap =>

          //get target attrs sort elements
          val(skuName, attrMap) = skuMap;

          val partedAttributes = attrMap.map{ case (attr, descr) =>
            val AttrMap = skuMap(skuName)
            partedAttrs(targAttrMap)
          }

         //somehow we compare the values in partedTarget with each sku in JsonRes

       }
      //is this the way to handle it?
     None => None
   }
}
*/

//original Map does not have type wrappers
def partedAttrs(map: Map[Any, Any]): Vector[(String, Int)] =
  targAttrMap
    //TODO replace two steps with a fold?
    .toSeq
    .map( case (attr, descr) => partitionDescription(attr, descr))
    .toVector

//this is only reusable if the data takes this shape. where something in the Attribut name matches the decription.
def partitionDescription(attr: String, descr: String): (String, Int) = {
  val prefixLen = attr.length
  val alpha: String = desc.slice(prefixLen -1, prefixLen)
  val beta: Int = desc.slice(6,desc.length).toInt
  (alpha, beta)
}

/*


for some sku num, which is a key in a dictionary representing a database. retrieve the key safely.
If the key exists
	find the first 10 values of keys that are most similar to the input key.
	or find the best matches of all the possible matches.
	*speed ? divide the db into sections? and search in parallel for good matches? then
	sort good matches?

	what is a weight and how to show it,

	def weight
	def match
*/
