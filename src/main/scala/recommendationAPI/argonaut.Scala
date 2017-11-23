import scalaz._, Scalaz._
import argonaut._, Argonaut._
import scalaz.concurrent.{Task, Strategy}

object ParseJson extends {

	def eitherSkus(input: String) = Parse.decodeEither[SkuDb](input)
}

case class SkuDb(
          value: JsonObj[Sku]
        )

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
object upfrontFee {
	implicit def upfrontFeeCodecJson: CodecJson[upfrontFee] =
    casecodec1(upfrontFee.apply, upfrontFee.unapply)("value")
}
object Payback {
	implicit def PaybackCodecJson: CodecJson[Payback] =
    casecodec4(Payback.apply, Payback.unapply)("id", "date", "principal", "interestFee")
}
