package controllers

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.api.libs.json._
import reactivemongo.api.collections.default._
import reactivemongo.bson.BSONDocument
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import models.Product._

/**
 * Created by karim on 4/23/15.
 */
object Product extends Controller with MongoController {

  lazy val collProduct = db("products")

  def findAll = collProduct.find(BSONDocument()).cursor[models.Product].collect[List]()

  def index = Action.async {
    findAll.map{
      products => Ok(Json.toJson(products))
    }
  }

  def get(id:String) = Action.async { implicit request =>
    collProduct.find(BSONDocument("_id" -> id)).one[models.Product].map{
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound
    }
  }

  def add = Action.async { implicit request =>
    models.Product.form.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      product =>
        collProduct.insert(product).map{
          lasterror =>
            if(lasterror.ok)
              Accepted(Json.toJson(product))
            else
              BadRequest(Json.toJson(lasterror.err.getOrElse("")))
        }
    )
  }

  def modify(id:String) = Action.async { implicit request =>
    models.Product.form.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      product =>
        collProduct.save(product).map{
          lasterror =>
            if(lasterror.ok)
              Accepted
            else
              NotAcceptable(Json.toJson(lasterror.err.getOrElse("")))
        }
    )
  }

  def delete(id:String) = Action.async {
    collProduct.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          NotAcceptable(Json.toJson(lasterror.err.getOrElse("")))
    }
  }
}
