import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import argonaut._, Argonaut._
import argonaut.JsonObject._
import argonaut.Json._
import scala.io.Source._

object RecommendationService extends App {
  import RecommendationUtils._

  args match {

    case Array(sku, filePath, numRecords) =>
      val file = scala.io.Source.fromFile(filePath)
      val str = file.mkString
      file.close

      Parse.decodeOption[Map[String, Map[String, String]]](str) match {
        case Some(skus_) =>
          val skus = skus_.map{ tup =>
            val (sku, attrs) = tup
            Sku(sku) -> attrs.map{ map =>
              val (key, value) = map
              AttrKey(key) -> AttrVal(value)}
            }

          //validate numRecords ..regex
          val resultNum: Int = math.min(numRecords.toInt, skus.length)
          val safeTake = scored(Sku(sku), skus).take(resultNum)
          //Return Json?
          println(s"Based on your past purchase you may like: ${safeTake.toString}")
        //add a non zero exit code
        case None => println("Invalid Json file.")
      }
    //add a non zero exit code
    case _ => println("sbt run <sku> <filePath> <numRecords>")
  }

}
