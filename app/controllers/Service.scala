package controllers

import javax.inject.Inject

import play.api.Play._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents, ReactiveMongoModule, MongoController}
import reactivemongo.api.ReadPreference
import reactivemongo.bson.{BSONDocument}
import reactivemongo.api.collections.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.modules.reactivemongo.json.BSONFormats._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

/**
 * Created by karim on 4/25/15.
 */

trait ServiceCollection extends WithCollection {
  lazy val coll = reactiveMongoApi.db("services")
}

class Service extends Controller with ReactiveMongoComponents with ServiceCollection{

  lazy val collService = Service.coll

  def findAll = collService.find(BSONDocument()).cursor[models.Service](ReadPreference.Primary).collect[List]()

  def index = Action.async {
    findAll.map {
      services => Ok(Json.toJson(services))
    }
  }

  def get(id:String) = Action.async {
    collService.find(BSONDocument("_id" -> id)).one[models.Service].map{
      case Some(service) => Ok(Json.toJson(service))
      case None => NotFound
    }
  }

  def add = Action.async { implicit request =>
    models.Service.form.bindFromRequest.fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      service =>
        collService.insert(service).map{
          lasterror =>
            if(lasterror.ok)
              Ok(Json toJson service)
            else
              NotAcceptable(Json.toJson(lasterror.errmsg.getOrElse("")))
        }
    )
  }

  def addproduct(id:String,product:String) = Action.async {
    for {
      lasterror <-collService.update(
        BSONDocument("_id" -> id),
        BSONDocument("$addToSet" -> BSONDocument("products" -> product)))
      optsvc <- collService.find(BSONDocument("_id" -> id)).one[models.Service] if lasterror.ok
      products <- Product.coll.find(
        BSONDocument("_id" ->
          BSONDocument("$in" -> optsvc.get.products))
      ).cursor[models.Product].collect[List]() if optsvc.isDefined
    } yield {
        Accepted(Json.toJson(products))
    }
  }

  def delproduct(id:String,product:String) = Action.async {
    for {
      lasterror <-collService.update(
        BSONDocument("_id" -> id),
        BSONDocument("$pull" -> BSONDocument("products" -> product)))
      optsvc <- collService.find(BSONDocument("_id" -> id)).one[models.Service] if lasterror.ok
      products <- Product.coll.find(
        BSONDocument("_id" ->
          BSONDocument("$in" -> optsvc.get.products))
      ).cursor[models.Product].collect[List]() if optsvc.isDefined
    } yield {
      Accepted(Json.toJson(products))
    }
  }

  def modify(id:String) = Action.async { implicit request =>
    models.Service.form.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      service =>
        collService.update(
          BSONDocument("_id" -> id),
          BSONDocument("$set" ->
            BSONDocument(
              "name" -> service.name,
              "desc" -> service.desc,
              "duration" -> service.duration,
              "active" -> service.active
            )
          )
        ).map{
          lasterror =>
            if(lasterror.ok)
              Accepted
            else
              NotAcceptable(Json.toJson(lasterror.errmsg.getOrElse("")))
        }
    )
  }

  def delete(id:String) = Action.async {
    collService.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          NotAcceptable(Json.toJson(lasterror.errmsg.getOrElse("")))
    }
  }
}

object Service extends ServiceCollection