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
                    products: List[BSONObjectID],
                    active:Boolean)

object Service{
  val fldId = "_id"
  val fldName = "name"
  val fldDesc = "desc"
  val fldDuration = "duration"
  val fldProducts = "products"
  val fldActive = "active"

  implicit object ServiceReaderWriter extends BSONDocumentReader[Service] with BSONDocumentWriter[Service]{
    def read(doc:BSONDocument) = Service(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[String](fldName).get,
      doc.getAs[String](fldDesc).get,
      doc.getAs[Int](fldDuration).get,
      doc.getAs[List[BSONObjectID]](fldProducts).get,
      doc.getAs[Boolean](fldActive).get
    )
    def write(service:Service) = BSONDocument(
      fldId -> service._id,
      fldName -> service.name,
      fldDesc -> service.desc,
      fldDuration -> service.duration,
      fldProducts -> service.products,
      fldActive -> service.active
    )
  }

  val form = Form(
    mapping(
      fldId -> text.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId")),
      fldName -> text,
      fldDesc -> text,
      fldDuration -> number,
      fldProducts -> list(text.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId"))),
      fldActive -> boolean
    )(
        (_id,name,desc,duration,products,active) => Service(_id,name,desc,duration,products,active)
      )(
        service => Some(service._id.stringify,service.name,service.desc,service.duration,service.products.map(_.stringify),service.active)
      )
  )
}

case class ServicePrice(service:BSONObjectID,
                        price:Double)

object ServicePrice{
  val fldService = "service"
  val fldPrice = "price"

  implicit object ServicePriceReaderWriter extends BSONDocumentReader[ServicePrice] with BSONDocumentWriter[ServicePrice]{
    def read(doc:BSONDocument) = ServicePrice(
      doc.getAs[BSONObjectID](fldService).get,
      doc.getAs[Double](fldPrice).get
    )
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

  implicit object ServicePriceListReaderWriter extends BSONDocumentReader[ServicePriceList] with BSONDocumentWriter[ServicePriceList]{
    def read(doc:BSONDocument) = ServicePriceList(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[java.util.Date](fldEffDate).get,
      doc.getAs[List[ServicePrice]](fldServices).get,
      doc.getAs[Boolean](fldActive).get
    )
    def write(spl:ServicePriceList) = BSONDocument(
      fldId -> spl._id,
      fldEffDate -> spl.effdate,
      fldServices -> spl.services,
      fldActive -> spl.active
    )
  }

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