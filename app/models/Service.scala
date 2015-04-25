package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.data.format._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._
import play.api.data.format.Formats._
import play.modules.reactivemongo.json.BSONFormats._
/**
 * Created by karim on 4/22/15.
 */
case class Service (_id:String,
                    name:String,
                    desc:String,
                    duration: Int,
                    products: List[String],
                    active:Boolean)

object Service{
  val fldId = "_id"
  val fldName = "name"
  val fldDesc = "desc"
  val fldDuration = "duration"
  val fldProducts = "products"
  val fldActive = "active"

  implicit val jsonFormat = Json.format[Service]

  implicit object ServiceReaderWriter extends BSONDocumentReader[Service] with BSONDocumentWriter[Service]{
    def read(doc:BSONDocument) = Service(
      doc.getAs[String](fldId).get,
      doc.getAs[String](fldName).get,
      doc.getAs[String](fldDesc).get,
      doc.getAs[Int](fldDuration).get,
      doc.getAs[List[String]](fldProducts).get,
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
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldName -> text,
      fldDesc -> text,
      fldDuration -> number,
      //fldProducts -> list(text.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId"))),
      fldActive -> boolean
    )(
        (_id,name,desc,duration,/*products,*/active) => Service(_id.getOrElse(BSONObjectID.generate.stringify),name,desc,duration,List(),active)
      )(
        service => Some(Some(service._id),service.name,service.desc,service.duration,/*service.products.map(_.stringify),*/service.active)
      )
  )
}

case class ServicePrice(service:String,
                        price:Double)

object ServicePrice{
  val fldService = "service"
  val fldPrice = "price"

  implicit object ServicePriceReaderWriter extends BSONDocumentReader[ServicePrice] with BSONDocumentWriter[ServicePrice]{
    def read(doc:BSONDocument) = ServicePrice(
      doc.getAs[String](fldService).get,
      doc.getAs[Double](fldPrice).get
    )
    def write(sp:ServicePrice) = BSONDocument(
      fldService -> sp.service,
      fldPrice -> sp.price
    )
  }

  val form = Form(
    mapping(
      fldService ->  objectId,
      fldPrice -> bigDecimal
    )((service,price) => ServicePrice(service,price.toDouble))(sp => Some(sp.service,BigDecimal(sp.price)))
  )
}

case class ServicePriceList (_id:String,
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
      doc.getAs[String](fldId).get,
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
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldEffDate -> date,
      fldServices -> list(ServicePrice.form.mapping),
      fldActive -> boolean
    )(
      (_id,effdate,services,active) => ServicePriceList(_id.getOrElse(BSONObjectID.generate.stringify),effdate,services,active)
    )(
      spl => Some(Some(spl._id),spl.effdate,spl.services,spl.active)
    )
  )
}