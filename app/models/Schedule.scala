package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import ImplicitConversions._
import Common._
/**
 * Created by karim on 4/22/15.
 */
case class Schedule (_id:String,
                     stylist:String,
                     from:java.util.Date,
                     to:java.util.Date)

object Schedule {
  val fldId = "_id"
  val fldStylist = "stylist"
  val fldFrom = "from"
  val fldTo = "to"

  implicit object ScheduleReader extends BSONDocumentReader[Schedule]{
    def read(doc:BSONDocument) = Schedule(
      doc.getAs[String](fldId).get,
      doc.getAs[String](fldStylist).get,
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
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldStylist -> objectId,
      fldFrom -> date,
      fldTo -> date
    )(
        (_id,stylist,from,to) => Schedule(_id.getOrElse(BSONObjectID.generate.stringify) ,stylist,from,to)
      )(
        schedule => Some(Some(schedule._id),schedule.stylist,schedule.from,schedule.to)
      )
  )
}