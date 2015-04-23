package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import ImplicitConversions._
import Common._
/**
 * Created by karim on 4/22/15.
 */
case class Schedule (_id:BSONObjectID,
                     stylist:BSONObjectID,
                     from:java.util.Date,
                     to:java.util.Date)

object Schedule {
  val fldId = "_id"
  val fldStylist = "stylist"
  val fldFrom = "from"
  val fldTo = "to"

  implicit object ScheduleReader extends BSONDocumentReader[Schedule]{
    def read(doc:BSONDocument) = Schedule(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[BSONObjectID](fldStylist).get,
      doc.getAs[java.util.Date](fldFrom).get,
      doc.getAs[java.util.Date](fldTo).get
    )
  }
  implicit object ScheduleWriter extends BSONDocumentWriter[Schedule]{
    def write(schedule:Schedule) = BSONDocument(
      fldId -> schedule._id,
      fldStylist -> schedule.stylist,
      fldFrom -> schedule.from,
      fldTo -> schedule.to
    )
  }

  val form = Form(
    mapping(
      fldId -> text.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId")),
      fldStylist -> text.verifying(pattern(objectIdRegEx,"constraint.objectId","error.objectId")),
      fldFrom -> date,
      fldTo -> date
    )(
        (_id,stylist,from,to) => Schedule(_id,stylist,from,to)
      )(
        schedule => Some(schedule._id.stringify,schedule.stylist.stringify,schedule.from,schedule.to)
      )
  )
}