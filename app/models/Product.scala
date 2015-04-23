package models

import reactivemongo.bson.BSONObjectID

/**
 * Created by karim on 4/22/15.
 */
case class Product (_id:BSONObjectID,
                    name:String)

case class ProductPrice(product:BSONObjectID,
                        price:Double)

case class ProductPriceList (_id:BSONObjectID,
                             effdate:java.util.Date,
                             products:List[ProductPrice],
                             active:Boolean)
