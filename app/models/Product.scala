package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format._
import play.api.data.validation.Constraints._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._

/**
 * Created by karim on 4/22/15.
 */
case class Product (_id:BSONObjectID,
                    name:String,
                    active:Boolean)

object Product {

  val fldId = "_id"
  val fldName = "name"
  val fldActive = "active"

  implicit object ProductReaderWriter extends BSONDocumentReader[Product] with BSONDocumentWriter[Product]{
    def read(doc:BSONDocument) = Product(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[String](fldName).get,
      doc.getAs[Boolean](fldActive).get
    )
    def write(product:Product) = BSONDocument(
      fldId -> product._id,
      fldName -> product.name,
      fldActive -> product.active
    )
  }

  val form = Form (
    mapping(
      fldId -> objectId,
      fldName -> text,
      fldActive -> boolean
    )(
        (_id,name,active) => Product(_id,name,active)
      )(
        product => Some(product._id.stringify,product.name,product.active)
      )
  )
}


case class ProductPrice(product:BSONObjectID,
                        price:Double)
object ProductPrice {
  val fldProduct = "product"
  val fldPrice = "price"

  implicit object ProductPriceReaderWriter extends BSONDocumentReader[ProductPrice] with BSONDocumentWriter[ProductPrice]{
    def read(doc:BSONDocument) = ProductPrice(
      doc.getAs[BSONObjectID](fldProduct).get,
      doc.getAs[Double](fldPrice).get
    )
    def write(pp:ProductPrice) = BSONDocument(
      fldProduct -> pp.product,
      fldPrice -> pp.price
    )
  }

  val form = Form(
    mapping(
      fldProduct -> objectId,
      fldPrice -> bigDecimal
    )(
        (product,price)=>ProductPrice(product,price.toDouble)
      )(
        pp => Some(pp.product.stringify,BigDecimal(pp.price)
        )
      )
  )
}

case class ProductPriceList (_id:BSONObjectID,
                             effdate:java.util.Date,
                             products:List[ProductPrice],
                             active:Boolean)
object ProductPriceList {
  val fldId = "_id"
  val fldEffDate = "effdate"
  val fldProducts = "products"
  val fldActive = "active"

  implicit object ProductPriceListReaderWriter extends BSONDocumentReader[ProductPriceList] with BSONDocumentWriter[ProductPriceList]{
    def read(doc:BSONDocument) = ProductPriceList(
      doc.getAs[BSONObjectID](fldId).get,
      doc.getAs[java.util.Date](fldEffDate).get,
      doc.getAs[List[ProductPrice]](fldProducts).get,
      doc.getAs[Boolean](fldActive).get
    )
    def write(pp:ProductPriceList) = BSONDocument(
      fldId -> pp._id,
      fldEffDate -> pp.effdate,
      fldProducts -> pp.products,
      fldActive -> pp.active
    )
  }

  val form = Form(
    mapping(
      fldId -> objectId,
      fldEffDate -> date,
      fldProducts -> list(ProductPrice.form.mapping),
      fldActive -> boolean
    )(
        (_id,effdate,products,active)=>ProductPriceList(_id,effdate,products,active)
      )(
        ppl => Some(ppl._id.stringify,ppl.effdate,ppl.products,ppl.active)
      )
  )
}