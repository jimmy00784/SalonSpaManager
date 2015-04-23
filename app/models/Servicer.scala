package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

/**
 * Created by karim on 4/22/15.
 */



case class Servicer (_id:BSONObjectID,
                name: String,
                services: List[BSONObjectID],
                active:Boolean)

/*case class Room(override val _id:BSONObjectID,
                override val name: String,
                override val services: List[BSONObjectID],
                override val active:Boolean) extends Servicer(_id,name,services,active)

case class Stylist(override val _id:BSONObjectID,
                override val name: String,
                override val services: List[BSONObjectID],
                override val active:Boolean) extends Servicer(_id,name,services,active)*/

object Servicer {

  import ImplicitConversions._
  import Common._

  val fldId = "_id"
  val fldName = "name"
  val fldServices = "services"
  val fldActive = "active"

  implicit object ServicerReaderWriter extends BSONDocumentReader[Servicer] with  BSONDocumentWriter[Servicer]{
    def read(doc:BSONDocument) = Servicer(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[String](fldName).get,
      doc.getAs[List[BSONObjectID]](fldServices).get,
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
      fldId -> text.verifying(pattern(
        objectIdRegEx,
        "constraint.objectId",
        "error.objectId"
      )),
      fldName -> nonEmptyText,
      fldServices -> list(text.verifying(pattern(
        Common.objectIdRegEx,
        "constraint.objectId",
        "error.objectId"
      ))),
      fldActive -> boolean
    )(
        (_id, name, services, active) => Servicer(_id,name,services,active)
      )(
        servicer => Some(servicer._id.stringify,servicer.name,servicer.services.map(_.stringify),servicer.active)
      )
  )
}