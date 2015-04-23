package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._
/**
 * Created by karim on 4/22/15.
 */
case class Service (_id:BSONObjectID,
                    name:String,
                    desc:String,
                    duration: Int,
                    products: List[Product],
                    active:Boolean)

case class ServicePrice(service:BSONObjectID,
                        price:Double)

object ServicePrice{
  val fldService = "service"
  val fldPrice = "price"

  implicit object ServicePriceReader extends BSONDocumentReader[ServicePrice]{
    def read(doc:BSONDocument) = ServicePrice(
      doc.getAs[BSONObjectID](fldService).get,
      doc.getAs[Double](fldPrice).get
    )
  }

  implicit object ServicePriceWriter extends BSONDocumentWriter[ServicePrice]{
    def write(sp:ServicePrice) = BSONDocument(
      fldService -> sp.service,
      fldPrice -> sp.price
    )
  }

  val form = Form(
    mapping(
      fldService -> text.verifying(
        pattern(objectIdRegEx,"constraint.objectId","error.objectId")
      ),
      fldPrice -> bigDecimal
    )((service,price) => ServicePrice(service,price.toDouble))(sp => Some(sp.service.stringify.stringify,BigDecimal(sp.price)))
  )
}

case class ServicePriceList (_id:BSONObjectID,
                      effdate:java.util.Date,
                      services:List[ServicePrice],
                      active:Boolean)

object ServicePriceList{
  val fldId = "_id"
  val fldEffDate = "effdate"
  val fldServices = "services"
  val fldActive = "active"


  val form = Form (
    mapping(
      fldId -> nonEmptyText.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId")),
      fldEffDate -> date,
      fldServices -> list(ServicePrice.form.mapping),
      fldActive -> boolean
    )(
      (_id,effdate,services,active) => ServicePriceList(_id,effdate,services,active)
    )(
      spl => Some(spl._id.stringify,spl.effdate,spl.services,spl.active)
    )
  )
}