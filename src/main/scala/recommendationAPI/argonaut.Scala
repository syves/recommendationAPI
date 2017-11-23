import scalaz._, Scalaz._
import argonaut._, Argonaut._
import scalaz.concurrent.{Task, Strategy}

object ParseJson extends {

	def eitherSkus(input: String) = Parse.decodeEither[SkuDb](input)
}

//how to create a flat json object of unknown length
//how to create a codec for an object that has distinc key names for each sku object?
//perhaps this is the wrong tool.
//perhaps reading in the data is the primary project?

case class SkuDb(
          //List[]
					principal: Int,
					upfrontFee: Value,
					upfrontCreditlineFee: Value,
					schedule: List[Payback])

case class upfrontFee(value: Value)
case class upfrontCreditlineFee(value: Value)
case class Value(value: Int)
case class Payback(id: Int, date: String, principal: Int, interestFee: Int)




object CashFlow {
	implicit def CashFlowCodecJson: CodecJson[CashFlow] =
    casecodec4(CashFlow.apply, CashFlow.unapply)(
    	"principal",
    	"upfrontFee",
    	"upfrontCreditlineFee",
    	"schedule")
}
object Value {
	implicit def valueCodecJson: CodecJson[Value] =
    casecodec1(Value.apply, Value.unapply)("value")
}
object upfrontCreditlineFee {
	implicit def upfrontCreditlineFeeCodecJson: CodecJson[upfrontCreditlineFee] =
    casecodec1(upfrontCreditlineFee.apply, upfrontCreditlineFee.unapply)("value")
}
object sku {
	implicit def skuCodecJson: CodecJson[sku] =
    casecodec1(upfrontFee.apply, upfrontFee.unapply)("value")
}
object skuDb {
	implicit def skuDbCodecJson: CodecJson[skuDb] =
    casecodec(Payback.apply, Payback.unapply)("id", "date", "principal", "interestFee")
}
