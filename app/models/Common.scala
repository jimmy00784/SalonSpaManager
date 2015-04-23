package models

import reactivemongo.bson.{BSONObjectID, BSONReader, BSONDateTime, BSONWriter}

/**
 * Created by karim on 4/22/15.
 */
object Common {
  val objectIdRegEx = """[a-fA-F0-9]{24}""".r
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
}