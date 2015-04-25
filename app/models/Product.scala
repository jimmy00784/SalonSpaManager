package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.format._
import play.api.data.validation.Constraints._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader, BSONObjectID}
import Common._
import ImplicitConversions._

/**
 * Created by karim on 4/22/15.
 */
case class Product (_id:String,
                    name:String,
                    active:Boolean)

object Product {

  val fldId = "_id"
  val fldName = "name"
  val fldActive = "active"

  implicit val jsonFormat = Json.format[Product]

  implicit object ProductReaderWriter extends BSONDocumentReader[Product] with BSONDocumentWriter[Product] {
    def read(doc:BSONDocument) = Product(
      doc.getAs[String](fldId).get,
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
      fldId -> optional(of[String] verifying objectIdPattern),
      fldName -> text,
      fldActive -> boolean
    )(
        (_id,name,active) => Product(_id.getOrElse(BSONObjectID.generate.stringify),name,active)
      )(
        product => Some(Some(product._id),product.name,product.active)
      )
  )
}


case class ProductPrice(product:String,
                        price:Double)
object ProductPrice {
  val fldProduct = "product"
  val fldPrice = "price"

  implicit object ProductPriceReaderWriter extends BSONDocumentReader[ProductPrice] with BSONDocumentWriter[ProductPrice]{
    def read(doc:BSONDocument) = ProductPrice(
      doc.getAs[String](fldProduct).get,
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
        pp => Some(pp.product,BigDecimal(pp.price)
        )
      )
  )
}

case class ProductPriceList (_id:String,
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
      doc.getAs[String](fldId).get,
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
      fldId -> optional(of[String].verifying(objectIdPattern)),
      fldEffDate -> date,
      fldProducts -> list(ProductPrice.form.mapping),
      fldActive -> boolean
    )(
        (_id,effdate,products,active)=>ProductPriceList(_id.getOrElse(BSONObjectID.generate.stringify),effdate,products,active)
      )(
        ppl => Some(Some(ppl._id),ppl.effdate,ppl.products,ppl.active)
      )
  )
}