package controllers

import play.api.libs.json.Json
import reactivemongo.bson.{BSONArray, BSONDocument}

import scala.concurrent._
import play.api.mvc.{Controller, Action}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by karim on 4/26/15.
 */
object Client extends Controller with MongoController {

  lazy val collClient = db("clients")
  val clientwohistory = BSONDocument() ++ (models.Client.fldHistory -> 0)

  /**
   * Client
   */

  def index = Action.async {
    collClient.find(BSONDocument(),clientwohistory).cursor[models.Client].collect[List]().map{
      list =>
        Ok(Json.toJson(list))
    }
  }

  def get(id:String) = Action.async {
    collClient.find(BSONDocument("_id" -> id)).one[models.Client].map{
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def getbyname(name:String) = Action.async {
    val search = BSONDocument("$regex" -> name)
    collClient.find(
      BSONDocument(
        "$or" ->
          BSONArray(
            List(
              BSONDocument("firstname" -> search),
              BSONDocument( "lastname" -> search)
            )
          )
      ),clientwohistory
    ).cursor[models.Client].collect[List]().map{
      list =>
        Ok(Json.toJson(list))
    }
  }

  def add = Action.async { implicit request =>
    models.Client.form.bindFromRequest.fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      client => {
        collClient.insert(client).map{
          lasterror =>
            if(lasterror.ok)
              Accepted
            else
              BadRequest(Json.toJson(lasterror.err.getOrElse("")))
        }
      }
    )
  }

  def modify(id:String) = Action.async { implicit request =>
    models.Client.form.bindFromRequest.fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      client => {
        collClient.update(
          BSONDocument("_id" -> id),
          BSONDocument("$set" ->
            BSONDocument(
              "firstname" -> client.firstname,
              "lastname" -> client.lastname))).map{
          lasterror =>
            if(lasterror.ok)
              Accepted
            else
              BadRequest(Json.toJson(lasterror.err.getOrElse("")))
        }
      }
    )
  }

  def remove(id:String) = Action.async { implicit request =>
    collClient.remove(BSONDocument("_id" -> id)).map{
      lasterror =>
        if(lasterror.ok)
          Accepted
        else
          BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  /**
   * Contact Info
   */

  def addphone(id:String,phone:String) = Action.async {
    collClient.update(
      BSONDocument("_id" -> id),
      BSONDocument("$addToSet" ->
        BSONDocument("phone" -> phone))).map {
      lasterror =>
        if (lasterror.ok)
          Accepted
        else
          BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  def delphone(id:String,phoneid:Int) = Action.async {
    for {
      unset <- collClient.update(
        BSONDocument("_id" -> id),
        BSONDocument("$unset" ->
          BSONDocument("phone." + phoneid.toString -> 1)))
      lasterror <- collClient.update(
        BSONDocument("_id" -> id),
        BSONDocument("$pull" ->
          BSONDocument("phone" -> BSONDocument("$type" -> 10)))) if unset.ok
    } yield {
      if(lasterror.ok)
        Accepted
      else
        BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  def addemail(id:String,email:String) = Action.async {
    collClient.update(
      BSONDocument("_id" -> id),
      BSONDocument("$addToSet" ->
        BSONDocument("email" -> email))).map {
      lasterror =>
        if (lasterror.ok)
          Accepted
        else
          BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  def delemail(id:String,emailid:Int) = Action.async {
    for {
      unset <- collClient.update(
        BSONDocument("_id" -> id),
        BSONDocument("$unset" ->
          BSONDocument("email." + emailid.toString -> 1)))
      lasterror <- collClient.update(
        BSONDocument("_id" -> id),
        BSONDocument("$pull" ->
          BSONDocument("email" -> BSONDocument("$type" -> 10)))) if unset.ok
    } yield {
      if(lasterror.ok)
        Accepted
      else
        BadRequest(Json.toJson(lasterror.err.getOrElse("")))
    }
  }

  /**
   * Visit
   */

  def getvisits(id:String) = Action.async {
    collClient.find(BSONDocument("_id" -> id)).one[models.Client].map{
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def getvisit(id:String, visitid:Int) = Action.async {
    collClient.find(BSONDocument("_id" -> id,"history." + visitid.toString -> 1)).one[models.Client].map{
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }

  def addvisit(id:String) = Action.async { implicit request =>
    models.Visit.form.bindFromRequest.fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      visit => {
        collClient.update(BSONDocument("_id" -> id),BSONDocument("$push" -> BSONDocument("history" -> visit))).map{
          lasterror =>
            if (lasterror.ok)
              Accepted
            else
              BadRequest(Json.toJson(lasterror.err.getOrElse("")))
        }
      }
    )
  }

  def modvisit(id:String,visitid:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def delvisit(id:String,visitid:Int) = Action.async {
    Future successful Ok
  }

  /**
   * Visit Products
   */

  def addproduct(id:String,visitid:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def modproduct(id:String,visitid:Int,product:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def delproduct(id:String,visitid:Int,product:Int) = Action.async { implicit request =>
    Future successful Ok
  }
}
