package models

import reactivemongo.bson.BSONObjectID

/**
 * Created by karim on 4/22/15.
 */
case class ProductQuantity(product:BSONObjectID,
                   quantity:Double)

case class VisitDetail(stylist:BSONObjectID,
                       service:BSONObjectID,
                       room:BSONObjectID,
                       products:List[ProductQuantity],
                       notes:String)

case class Visit (date:java.util.Date,
                  details:List[VisitDetail])

case class Client (_id:BSONObjectID,
                   firstname:String,
                   lastname:String,
                   phone:List[String],
                   email:List[String],
                   history:List[Visit])
