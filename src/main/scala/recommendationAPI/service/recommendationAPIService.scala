import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import argonaut._, Argonaut._
import argonaut.JsonObject._
import argonaut.Json._
import scala.io.Source._

object RecommendationService extends App {
  import RecommendationUtils._

  args match {

    case Array(sku, filePath, numRecords_) =>
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

          val numRecords = toInt(numRecords_).getOrElse(0)
          val resultNum: Int = math.min(numRecords, skus.length)
          val res = scored(Sku(sku), skus)

          res match {
            case Right(res) => val safeTake = res.take(resultNum)
                               println(s"Based on your past purchase you may like: ${resToJson(safeTake, skus)}")
                              //add a non zero exit code
            case Left(err) => println(res)
          }

        //add a non zero exit code
        case None => println("Invalid Json file.")
      }
    case _ => println("sbt run <sku> <filePath> <numRecords>")
  }

}
