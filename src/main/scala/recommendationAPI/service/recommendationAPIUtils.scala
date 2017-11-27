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
  case class AttrKey(value: String)

  //This funtion is safe because it relies on the current shape of the data having prefixes
  // and cross collumn matches are not possible.

  //TODO return option of vector
  //TODO filter out sku
  //TODO return the weight
  def scored(sku: Sku, skuDb: Map[Sku, Map[AttrKey, AttrVal]]): Vector[(Sku, Score, List[AttrVal])] = {
    //TODO change to get or else
    val pastAttrMap = skuDb(sku)
    val pastAttrVals = pastAttrMap.toList.sortBy(t => t._1.value).map(_._2)

    val scored = skuDb.foldLeft(Vector[(Sku, Score, List[AttrVal])]()) { (z, skuTup) =>

      val (skuName, attrMap)  = skuTup
      if (skuName == sku){
        z
      } else {
        val attrVals = attrMap.toList.sortBy(t => t._1.value).map(_._2)
        val matches = pastAttrVals.zip(attrVals).map { tup => tup._1 == tup._2 }
        val score = matches.filter(x => x).length

        z :+ (skuName, Score(score), attrVals)
      }
    }

    scored.sortBy{ tup3 =>
      val (sku, score, attrVals) = tup3
      val matches = pastAttrVals.zip(attrVals).map { tup => tup._1 == tup._2 }
      //TOdo change sort to ascending
      (-score.value, matches.foldLeft("")((z,a)=> z + (if (a) "a" else "b")))
    }
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
