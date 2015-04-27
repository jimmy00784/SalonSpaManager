package controllers

import play.api.mvc.{Controller, Action}
import play.modules.reactivemongo.MongoController
import play.api.libs.json._
import reactivemongo.api.collections.default._
import reactivemongo.bson.BSONDocument
import scala.concurrent.{Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by karim on 4/25/15.
 */
class Servicer(val collection:String) extends Controller with MongoController{
  lazy val coll = db(collection)

  def index = Action.async {
    coll.find(BSONDocument()).cursor[models.Servicer].collect[List]().map{
      servicers => Ok(Json.toJson(servicers))
    }
  }

  def get(id:String) = Action.async {
    coll.find(BSONDocument("_id" -> id)).one[models.Servicer].map{
      case Some(servicer) => Ok(Json.toJson(servicer))
      case None => NotFound
    }
  }

  def getbyservice(svcid:String) = Action.async {
    coll.find(BSONDocument("services" -> svcid)).cursor[models.Servicer].collect[List]().map{
      servicers => Ok(Json.toJson(servicers))
    }
  }

  def add = Action.async { implicit request =>
    models.Servicer.form.bindFromRequest().fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      servicer => coll.save(servicer).map{
        lasterror =>
          if(lasterror.ok)
            Accepted(Json.toJson(servicer))
          else
            BadRequest(Json.toJson(lasterror.err.getOrElse("")))
      }
    )
  }

  def modify(id:String) = Action.async { implicit request =>
    models.Servicer.form.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      servicer =>
        coll.update(
          BSONDocument("_id" -> id),
          BSONDocument("$set" ->
            BSONDocument(
              "name" -> servicer.name,
              "active" -> servicer.active
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
    coll.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  def addservice(id:String,service:String) = Action.async {
    for {
      lasterror <-coll.update(
        BSONDocument("_id" -> id),
        BSONDocument("$addToSet" -> BSONDocument("services" -> service)))
      optsvcr <- coll.find(BSONDocument("_id" -> id)).one[models.Servicer] if lasterror.ok
      services <- Service.collService.find(
        BSONDocument("_id" ->
          BSONDocument("$in" -> optsvcr.get.services))
      ).cursor[models.Service].collect[List]() if optsvcr.isDefined
    } yield {
      Accepted(Json.toJson(services))
    }
  }

  def delservice(id:String,service:String) = Action.async {
    for {
      lasterror <-coll.update(
        BSONDocument("_id" -> id),
        BSONDocument("$pull" -> BSONDocument("services" -> service)))
      optsvcr <- coll.find(BSONDocument("_id" -> id)).one[models.Servicer] if lasterror.ok
      services <- Service.collService.find(
        BSONDocument("_id" ->
          BSONDocument("$in" -> optsvcr.get.services))
      ).cursor[models.Service].collect[List]() if optsvcr.isDefined
    } yield {
      Accepted(Json.toJson(services))
    }
  }
}

object Servicer {
  def apply(collection: String) = new Servicer(collection)
}

object Room extends Servicer("rooms")
object Stylist extends Servicer("stylists")