package models

import reactivemongo.bson.BSONObjectID

/**
 * Created by karim on 4/22/15.
 */
case class AppointmentRequest (service:BSONObjectID,
                        stylist:Option[BSONObjectID])

case class Appointment (_id:BSONObjectID,
                        date:java.util.Date,
                        client:BSONObjectID,
                        services:List[AppointmentRequest],
                        notes:String)
