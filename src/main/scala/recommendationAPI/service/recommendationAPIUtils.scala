import scalaz._, Scalaz._
import argonaut._, Argonaut._

object RecommendationUtils {

  case class AttrKey(value: String)
  case class AttrVal(value: String)
  case class Score(value: Int)
  case class Sku(value: String)
  case class Error(value: String)
  case class Result(sku: Sku, score: Score, attrs: Map[AttrKey, AttrVal])

  implicit def ResultEncodeJson: EncodeJson[Result] =
    EncodeJson((r: Result) =>
      ("sku" := r.sku ) ->:
      ("score" := r.score.value) ->:
      ("attributes", r.attrs.map { case (k, v) => k.value -> v.value }.asJson) ->:
      jEmptyObject
    )
  implicit def SkuEncodeJson: EncodeJson[Sku] =
    EncodeJson((s: Sku) => s.value.asJson)

  //TODO remove or fix usesage
  implicit def AttrKeyEncodeJson: EncodeJson[AttrKey] =
    EncodeJson((a: AttrKey) => a.value.asJson)

  implicit def AttrValEncodeJson: EncodeJson[AttrVal] =
    EncodeJson((a: AttrVal) => a.value.asJson)

  def resToJson(res: Vector[(Sku, Score, List[AttrVal])], skuDb: Map[Sku, Map[AttrKey, AttrVal]]): Json = {
    val vecJson = res.map{ tup3 =>
      val (sku, score, _) = tup3
      val typedMap = skuDb(sku)
      Result(sku, score, typedMap)
    }
    vecJson.asJson
  }
  //This funtion is safe because it relies on the current shape of the data having prefixes
  // and cross collumn matches are not possible.
  def scored(sku: Sku, skuDb: Map[Sku, Map[AttrKey, AttrVal]]): Either[Error, Vector[(Sku, Score, List[AttrVal])]] = {
    skuDb.get(sku) match {
      case Some(pastMap) =>
        val pastAttrVals =  pastMap.toList.sortBy(t => t._1.value).map(_._2)

        val scored_ = skuDb.foldLeft(Vector[(Sku, Score, List[AttrVal])]()) { (z, skuTup) =>
          val (skuName, attrMap)  = skuTup
            if (skuName == sku){
              z
            } else {
              val attrVals = attrMap.toList.sortBy(t => t._1.value).map(_._2)
              val matches = pastAttrVals.zip(attrVals).map { tup => tup._1 == tup._2 }
              val score = matches.filter(x => x).length
              z :+ (skuName, Score(score), attrVals)
            }
        }.sortBy{ tup3 =>
          val (sku, score, attrVals) = tup3
          val matches = pastAttrVals.zip(attrVals).map { tup => tup._1 == tup._2 }
          (-score.value, matches.foldLeft("")((z,a)=> z + (if (a) "a" else "b")))
        }
        Right(scored_)
      case None => Left(Error("The Sku provided was not found"))
    }
  }

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

}
