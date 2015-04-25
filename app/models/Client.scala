package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._
import play.api.data.format.Formats._

/**
 * Created by karim on 4/22/15.
 */
case class ProductQuantity(product:String,
                   quantity:Double)

object ProductQuantity {
  val fldProduct = "product"
  val fldQuantity = "quantity"

  implicit object ProductQuantityReaderWriter extends BSONDocumentReader[ProductQuantity] with BSONDocumentWriter[ProductQuantity]{
    def read(doc:BSONDocument) = ProductQuantity(
      doc.getAs[String](fldProduct).get,
      doc.getAs[Double](fldQuantity).get
    )
    def write(pq:ProductQuantity) = BSONDocument(
      fldProduct -> pq.product,
      fldQuantity -> pq.quantity
    )
  }

  val form = Form(
    mapping(
      fldProduct -> objectId,
      fldQuantity -> bigDecimal
    )(
        (product,quantity) => ProductQuantity(product,quantity.toDouble)
      )(
      pq => Some(pq.product,BigDecimal(pq.quantity))
      )
  )
}

case class VisitDetail(stylist:String,
                       service:String,
                       room:String,
                       products:List[ProductQuantity],
                       notes:String)

object VisitDetail {
  val fldStylist = "stylist"
  val fldService = "service"
  val fldRoom = "room"
  val fldProducts = "products"
  val fldNotes = "notes"

  implicit object VisitDetailReaderWriter extends BSONDocumentReader[VisitDetail] with BSONDocumentWriter[VisitDetail]{
    def read(doc:BSONDocument) = VisitDetail(
      doc.getAs[String](fldStylist).get,
      doc.getAs[String](fldService).get,
      doc.getAs[String](fldRoom).get,
      doc.getAs[List[ProductQuantity]](fldProducts).get,
      doc.getAs[String](fldNotes).get
    )
    def write(vdtl:VisitDetail) = BSONDocument(
      fldStylist -> vdtl.stylist,
      fldService -> vdtl.service,
      fldRoom -> vdtl.room,
      fldProducts -> vdtl.products,
      fldNotes -> vdtl.notes
    )
  }

  val form = Form(
    mapping(
      fldStylist -> objectId,
      fldService -> objectId,
      fldRoom -> objectId,
      fldProducts -> list(ProductQuantity.form.mapping),
      fldNotes -> text
    )(
        (stylist,service,room,products,notes) => VisitDetail(stylist,service,room,products,notes)
      )(
      vdtl => Some(vdtl.stylist,vdtl.service,vdtl.room,vdtl.products,vdtl.notes)
      )
  )
}

case class Visit (date:java.util.Date,
                  details:List[VisitDetail])

object Visit {
  val fldDate = "date"
  val fldDetails = "details"

  implicit object VisitReaderWriter extends BSONDocumentReader[Visit] with BSONDocumentWriter[Visit]{
    def read(doc:BSONDocument) = Visit(
      doc.getAs[java.util.Date](fldDate).get,
      doc.getAs[List[VisitDetail]](fldDetails).get
    )
    def write(visit:Visit) = BSONDocument(
      fldDate -> visit.date,
      fldDetails -> visit.details
    )
  }

  val form = Form(
    mapping(
      fldDate -> date,
      fldDetails -> list(VisitDetail.form.mapping)
    )(Visit.apply)(Visit.unapply)
  )
}

case class Client (_id:String,
                   firstname:String,
                   lastname:String,
                   phone:List[String],
                   email:List[String],
                   history:List[Visit])

object Client {
  val fldId = "_id"
  val fldFirstName = "firstname"
  val fldLastName = "lastname"
  val fldPhone = "phone"
  val fldEmail = "email"
  val fldHistory = "history"

  implicit object ClientReaderWriter extends BSONDocumentReader[Client] with BSONDocumentWriter[Client]{
    def read(doc: BSONDocument) = Client(
      doc.getAs[String](fldId).get,
      doc.getAs[String](fldFirstName).get,
      doc.getAs[String](fldLastName).get,
      doc.getAs[List[String]](fldPhone).get,
      doc.getAs[List[String]](fldEmail).get,
      doc.getAs[List[Visit]](fldHistory).get
    )
    def write(client:Client) = BSONDocument(
      fldId -> client._id,
      fldFirstName -> client.firstname,
      fldLastName -> client.lastname,
      fldPhone -> client.phone,
      fldEmail -> client.email,
      fldHistory -> client.history
    )
  }

  val form = Form(
    mapping(
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldFirstName -> text,
      fldLastName -> text,
      fldPhone -> list(text),
      fldEmail -> list(email),
      fldHistory -> list(Visit.form.mapping)
    )(
        (_id,firstname,lastname,phone,email,history) => Client(_id.getOrElse(BSONObjectID.generate.stringify),firstname,lastname,phone,email,history)
      )(
    client => Some(Some(client._id),client.firstname,client.lastname,client.phone,client.email,client.history)
      )
  )
}