package models

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import reactivemongo.bson.{BSONObjectID, BSONReader, BSONDateTime, BSONWriter}

/**
 * Created by karim on 4/22/15.
 */
object Common {
  val objectIdRegEx = """[a-fA-F0-9]{24}""".r
  val objectIdPattern = pattern(objectIdRegEx,"constraint.objectId","error.objectID")
  val objectId = text.verifying(objectIdPattern)

}

object ImplicitConversions {

  implicit object DateWriter extends BSONWriter[java.util.Date,BSONDateTime]{
    def write(date:java.util.Date) = BSONDateTime(date.getTime)
  }
  implicit object DateReader extends BSONReader[BSONDateTime,java.util.Date]{
    def read(date:BSONDateTime) = new java.util.Date(date.value)
  }
  implicit def StringToBSONObjectID(string:String):BSONObjectID = BSONObjectID(string)
  implicit def BSONObjectIDToString(bsonid:BSONObjectID):String = bsonid.stringify
  implicit def ListStringToListBSONObjectID(list:List[String]):List[BSONObjectID] = list.map(BSONObjectID(_))
  implicit def ListBSONObjectIDToListString(list:List[BSONObjectID]):List[String] = list.map(_.stringify)
  implicit def OptionStringToOptionBSONObjectID(opt:Option[String]):Option[BSONObjectID] = opt.map(BSONObjectID(_))
  implicit def OptionBSONObjectIDToOptionString(opt:Option[BSONObjectID]):Option[String] = opt.map(_.stringify)


}