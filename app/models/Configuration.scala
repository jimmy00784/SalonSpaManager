package models

import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson.BSONObjectID
import Common._
import ImplicitConversions._

/**
 * Created by karim on 4/22/15.
 */

case class HoursOfOperation(_id:Int,//Sunday - Saturday: 1 - 7
                            open:java.util.Date,
                            close:java.util.Date)

object HoursOfOperation {
  val fldDayOfWeek = "_id"
  val fldOpen = "open"
  val fldClose = "close"

  val form = Form(
    mapping(
      fldDayOfWeek -> number(1,7,true),
      fldOpen -> date,
      fldClose -> date
    )(HoursOfOperation.apply)(HoursOfOperation.unapply)
  )
}

case class OperationOverride(_id:String, //BSONObjectID.generate
                             date:java.util.Date,
                             open:java.util.Date,
                             close:java.util.Date,
                             desc:String)

object OperationOverride {
  val fldId = "_id"
  val fldDate = "date"
  val fldOpen = "open"
  val fldClose = "close"
  val fldDesc = "desc"

  val form = Form(
    mapping(
      fldId -> objectId,
      fldDate -> date,
      fldOpen -> date,
      fldClose -> date,
      fldDesc -> text
    )(OperationOverride.apply)(OperationOverride.unapply)
  )
}

case class CloseSchedule(_id:String, //BSONObjectID.generate
                         from:java.util.Date,
                         to:java.util.Date,
                         desc:String)

object CloseSchedule{
  val fldId = "_id"
  val fldFrom = "from"
  val fldTo = "to"
  val fldDesc = "desc"

  val form = Form(
    mapping(
      fldId -> objectId,
      fldFrom -> date,
      fldTo -> date,
      fldDesc -> text
    )(CloseSchedule.apply)(CloseSchedule.unapply)
  )
}