package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._

/**
 * Created by karim on 4/22/15.
 */
case class AppointmentRequest (service:BSONObjectID,
                        stylist:Option[BSONObjectID])

object AppointmentRequest{
  val fldService = "service"
  val fldStylist = "stylist"

  implicit object AppointmentRequestReaderWriter extends BSONDocumentReader[AppointmentRequest] with BSONDocumentWriter[AppointmentRequest]{
    def read(doc:BSONDocument) = AppointmentRequest(
      doc.getAs[BSONObjectID](fldService).get,
      doc.getAs[BSONObjectID](fldStylist)
    )
    def write(ar:AppointmentRequest) = {
      val doc = BSONDocument(fldService -> ar.service)
      ar.stylist match {
        case Some(stylist) => doc.++(fldStylist -> stylist)
        case None => doc
      }
    }
  }

  val form = Form(
    mapping(
      fldService -> objectId,
      fldService -> optional(objectId)
    )(
        (service,stylist) => AppointmentRequest(service,stylist)
      )(
      ar => Some(ar.service.stringify,ar.stylist.map(_.stringify))
      )
  )
}

case class Appointment (_id:BSONObjectID,
                        date:java.util.Date,
                        client:BSONObjectID,
                        services:List[AppointmentRequest],
                        walkin:Boolean,
                        notes:String)

object Appointment{
  val fldId = "_id"
  val fldDate = "date"
  val fldClient = "client"
  val fldServices = "services"
  val fldWalkin = "walkin"
  val fldNotes = "notes"


  val form = Form(
    mapping(
      fldId -> objectId,
      fldDate -> date,
      fldClient -> objectId,
      fldServices -> list(AppointmentRequest.form.mapping),
      fldWalkin -> boolean,
      fldNotes -> text
    )(
        (_id,date,client,services,walkin,notes) => Appointment(_id,date,client,services,walkin,notes)
      )(
      appt => Some(appt._id.stringify,appt.date,appt.client.stringify,appt.services,appt.walkin,appt.notes)
      )
  )
}