import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import argonaut._, Argonaut._
//import argonaut.EncodeJson._
import argonaut.JsonObject._
import argonaut.Json._

object RecommendationUtils {

  case class Sku(value: String)
  case class Score(value: Int)
  case class AttrVal(value: String)

  def trueMatch(model: Vector[AttrVal], suggested: Vector[AttrVal]): Score = Score((model intersect suggested).length)

  def mapToAttrs(m: Map[String, String]): Vector[AttrVal] = m.toVector.sorted.map(tup => AttrVal(tup._2))

  def scored(jsonRes: Option[Map[String, Map[String, String]]]): Vector[(Score, Sku, Vector[AttrVal])] = {
    jsonRes match {
      case Some(skuDb) =>
        var scored: Vector[ (Score, Sku, Vector[AttrVal]) ] = Vector()
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
            if (aScore.value == 0 && bScore.value == 0 )
              aVals.zip(bVals).forall { case (x, y) => x.value < y.value }
            else
              aScore.value > bScore.value
            }
      case None => Vector()
      }
    }

  def getRecommendations(scored: Vector[(Score, Sku, Vector[AttrVal])], numRec: Int): Vector[(Score, Sku, Vector[AttrVal])] = {
    if (scored.length >= numRec) scored.take(numRec) else scored
  }
/*
  def :+(fj: (JsonString, Json), obj: Json): JsonObject = {
    obj +
  }

  //extending json object ??
  trait myJson extends sealed abstract class JsonObject {
    def +(f: JsonField, j: Json): JsonObject = {
      if (fieldsMap.contains(f)) {
        copy(fieldsMap = fieldsMap.updated(f, j))
      } else {
        copy(fieldsMap = fieldsMap.updated(f, j), orderedFields = orderedFields :+ f)
    }
  }

    def :+(fj: (JsonString, Json)): JsonObject = {
      this.+(fj._1, fj._2)
    }
  }

  //write a codec for a result?
  def showRec(rawRecs: Vector[(Score, Sku, Vector[AttrVal])]): Json = {
    var arr: Json = jArray(Nil)
    rawRecs.foreach { tup3 =>
      val (score, sku, map) = tup3
      //val (score, sku, attrs) = tup3
      //val jmapBuider = skus.flatMap(map => map(sku)).foldLeft(Json()) { (pair, z) =>
        //val (k,v) = pair
        //jString(k)-> jString(v)
      //}
      arr :+(("skuName" -> jString(sku.value), "Weight" -> jNumber(score.value)))
      //Json(jString(sku) -> jmapBuider, "Weight" -> jString(score))
    }
    arr
  }
*/
}
