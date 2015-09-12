package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents, ReactiveMongoModule, MongoController}
import play.api.libs.json._
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import models.Product._

/**
 * Created by karim on 4/23/15.
 */

trait ProductCollection extends WithCollection {
  lazy val coll = reactiveMongoApi.db("products")
}

class Product extends Controller with ReactiveMongoComponents with ProductCollection {

  lazy val collProduct = Product.coll

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
              BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
        }
    )
  }

  def modify(id:String) = Action.async { implicit request =>
    models.Product.form.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      product =>
        collProduct.update(BSONDocument("_id" -> BSONObjectID(id)), product).map{
          lasterror =>
            if(lasterror.ok)
              Accepted
            else
              NotAcceptable(Json.toJson(lasterror.errmsg.getOrElse("")))
        }
    )
  }

  def delete(id:String) = Action.async {
    collProduct.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          NotAcceptable(Json.toJson(lasterror.errmsg.getOrElse("")))
    }
  }
}

object Product extends ProductCollection