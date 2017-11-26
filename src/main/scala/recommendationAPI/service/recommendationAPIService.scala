import scalaz.concurrent.{Task, Strategy}
import scalaz._, Scalaz._
import argonaut._, Argonaut._
import argonaut.JsonObject._
import argonaut.Json._
import scala.io.Source._

object RecommendationService extends App {
  import RecommendationUtils._

  args.length match {
    case 0 => println("Please supply input path to data file.")
    case 1 => println("Please supply the number of results requested.")
    case _ => val filePath = args(0)

              val openContents = scala.io.Source.fromFile(filePath).mkString

              val skus = Parse.decodeOption[Map[String, Map[String, String]]](openContents)
              
              val numRecords = args(1).toInt

              val result = getRecommendations(scored(skus), numRecords)
              //TODO pass the pastPurchased here
              //Return Json?
              println(s"Based on your past purchase you may like: ${result.toString}")
  }

}
