package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.bson.{BSONDocument}
import reactivemongo.api.collections.default._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.modules.reactivemongo.json.BSONFormats._

/**
 * Created by karim on 4/25/15.
 */
object Service extends Controller with MongoController {

  lazy val collService = db("services")

  def findAll = collService.find(BSONDocument()).cursor[models.Service].collect[List]()

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
        collService.save(service).map{
          lasterror =>
            if(lasterror.ok)
              Ok(Json toJson service)
            else
              NotAcceptable(Json.toJson(lasterror.err.getOrElse("")))
        }
    )
  }

  def addproduct(id:String,product:String) = Action.async {
    for {
      lasterror <-collService.update(
        BSONDocument("_id" -> id),
        BSONDocument("$addToSet" -> BSONDocument("products" -> product)))
      optsvc <- collService.find(BSONDocument("_id" -> id)).one[models.Service] if lasterror.ok
      products <- Product.collProduct.find(
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
      products <- Product.collProduct.find(
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
              NotAcceptable(Json.toJson(lasterror.err.getOrElse("")))
        }
    )
  }

  def delete(id:String) = Action.async {
    collService.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          NotAcceptable(Json.toJson(lasterror.err.getOrElse("")))
    }
  }
}
