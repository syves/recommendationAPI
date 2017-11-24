import scalaz._, Scalaz._
import argonaut._, Argonaut._
import scalaz.concurrent.{Task, Strategy}

object ParseJson extends {

	def eitherSkus(input: String) = Parse.decodeEither[SkuDb](input)
}
val input = """{
	"sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"},
	"sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"},
	"sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}
}"""

//how to create a flat json object of unknown length
//how to create a codec for an object that has distinc key names for each sku object?
//perhaps this is the wrong tool.
//perhaps reading in the data is the primary project?
 val people = input.decodeOption[JsonObj[sku]].getOrElse(Nil)


    Json(jsonString -> Json(jsonString-> JsonString))
