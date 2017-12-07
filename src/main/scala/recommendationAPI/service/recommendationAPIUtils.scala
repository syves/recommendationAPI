import scalaz._, Scalaz._
import argonaut._, Argonaut._

object RecommendationUtils {

  case class AttrKey(value: String)
  case class AttrVal(value: String)
  case class Score(value: Int)
  case class Sku(value: String)
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

  implicit def AttrKeyEncodeJson: EncodeJson[AttrKey] =
    EncodeJson((a: AttrKey) => a.value.asJson)

  implicit def AttrValEncodeJson: EncodeJson[AttrVal] =
    EncodeJson((a: AttrVal) => a.value.asJson)

  def resToJson(res: Vector[(Sku, Score, Map[AttrKey, AttrVal])]): Json = {
    val vecJson = res.map{ tup3 =>
      val (sku, score, typedMap) = tup3
      Result(sku, score, typedMap)
    }
    vecJson.asJson
  }

  //Note: Perhaps if there were db class and instance of db. This function would be a method on it.
  //OO Refactor ideas: Then I would not need to pass the db around. However I only do later transformations conditionally,
  // based on the type of db being Some/None. This could be replaced if there was an empty db type?

  def decode(str: String): Either[String, Map[Sku, Map[AttrKey, AttrVal]]] = {
    Parse.decodeEither[Map[String, Map[String, String]]](str).map{skus =>
      //Note: transform Map to have typed values, because strings less explicit.
      //If this was a method it might be 2 seperate functions.
      skus.foldLeft(Map[Sku, Map[AttrKey, AttrVal]]()){ (z, pair) =>
        val (sku, attrs) = pair
        z + (Sku(sku) -> attrs.map{ case (k, v) =>  AttrKey(k) -> AttrVal(v)})
      }
    }
  }
  //This funtion is safe because it relies on the current shape of the data having prefixes
  // and cross collumn matches are not possible?

  //Note:
  //If the db exists then proceed to score db entries based on provided Sku.
  //and return the number or requested results.
  //else yeild error.

  //Note: FP Maybe I should have created a type alias for Vector[(sku, Score..)], OO: a class 'Item'
  //OO Refactor: Passing around tuples, and unpacking and naming them can be cumbersome.
  //If I used a class then the fileds would already be named and easy to access.
  def score(sku: Sku, skuDb: Map[Sku, Map[AttrKey, AttrVal]]): Either[String, Vector[(Sku, Score, Map[AttrKey, AttrVal])]] = {
    skuDb.get(sku) match {
      case Some(pastMap) =>
        //Note: get the attribute values for the past purchase for comparison. So that I can use zip in my sorting algorithm.
        val pastAttrVals =  pastMap.toList.sortBy(t => t._1.value).map(_._2)

        //Now I'm buiding up a collection with a more complex data type.
        // The first item in the tuple is a pair values I will sortby: Score &
        // matchString: that represents whether a match occured between the past purchased Item attribute
        //and the possible match we are interating over, by leveraging builtin ordering on strings.
        //Note: The item's map is passed around so that it can be accessed for printing later.
        val scored_ = skuDb.foldLeft( Vector[ ( (Int, String), (Sku, Score, Map[AttrKey, AttrVal]) ) ]() ) { (z, skuTup) =>
          //Note: since we are iterating over a map, the key, value pair is unpacked as a tuple.
          val (sku_, attrMap)  = skuTup
            //Note: If we find the past Purchased sku then we don't include it in mathches.
            if (sku_ == sku){
              z
            } else {
              //Note: get the attributes that we want to compare as a list, no zip on Maps,
              //default Maps are not ordered so after getting a tuple of k,v we sort on the keys, then collect the values.
              val attrVals = attrMap.toList.sortBy(t => t._1.value).map(_._2)
              //Note: create pairs for comparison, filter for matches gives a list of booleans.
              val matches = pastAttrVals.zip(attrVals).map { tup => tup._1 == tup._2 }
              //Note: the count is equal to score
              val score = matches.filter(x => x).length
              //Note: Hacky way to use builtin ordering to settle order when scores are equal.
              //If elem is true 'a' else 'b', seemed clearer than 01, and true false cannot be sorted by normal ordering.
              val matchString = matches.foldLeft(""){(z,a)=> z + (if (a) "a" else "b") }
              //Normal ordering word sort ascending so we negate the value.
              val comparator = (-score, matchString)

              z :+ ( comparator, (sku_, Score(score), attrMap) )
            }
            //sort by the comparators and the pass the rest of the sku info on.
        }.sortBy{ _._1 }

        //we only pass on the sku's info, not the comparator
        Right(scored_.map{ _._2 })
      case None => Left("The Sku provided was not found")
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
