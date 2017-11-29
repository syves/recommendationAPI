import scalaz._, Scalaz._
import scala.io.Source._

object RecommendationService extends App {
  import RecommendationUtils._

  args match {
    case Array(sku, filePath, numRecords_) =>
      val file = scala.io.Source.fromFile(filePath)
      val str = file.mkString
      file.close
      val numRecords = toInt(numRecords_).getOrElse(0)

      decode(str).flatMap{ db => score(Sku(sku), db)} match {
        case Left(err)   => println(err)
                            sys.exit(1)
        case Right(skus) => println(resToJson(skus.take(numRecords)))
      }

    case _ => println("sbt 'run <sku> <filePath> <numRecords>'")
              sys.exit(1)
  }
}
