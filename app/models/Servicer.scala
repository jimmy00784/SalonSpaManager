package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import play.api.data.format.Formats._

/**
 * Created by karim on 4/22/15.
 */



case class Servicer (_id:String,
                name: String,
                services: List[String],
                active:Boolean)

/*case class Room
case class Stylist*/

object Servicer {

  import ImplicitConversions._
  import Common._

  val fldId = "_id"
  val fldName = "name"
  val fldServices = "services"
  val fldActive = "active"

  implicit val jsonFormat = Json.format[Servicer]
  implicit object ServicerReaderWriter extends BSONDocumentReader[Servicer] with  BSONDocumentWriter[Servicer]{
    def read(doc:BSONDocument) = Servicer(
      doc.getAs[String](fldId).get,
      doc.getAs[String](fldName).get,
      doc.getAs[List[String]](fldServices).get,
      doc.getAs[Boolean](fldActive).get
    )

    def write(servicer:Servicer) = BSONDocument(
      fldId -> servicer._id,
      fldName -> servicer.name,
      fldServices -> servicer.services,
      fldActive -> servicer.active
    )
  }

  val form = Form(
    mapping(
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldName -> nonEmptyText,
      //fldServices -> list(objectId),
      fldActive -> boolean
    )(
        (_id, name, /*services,*/ active) => Servicer(_id.getOrElse(BSONObjectID.generate.stringify),name,/*services*/List(),active)
      )(
        servicer => Some(Some(servicer._id),servicer.name,/*servicer.services,*/servicer.active)
      )
  )
}