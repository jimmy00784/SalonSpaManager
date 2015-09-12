package controllers

import javax.inject.Inject

import models.VisitDetail
import play.api.Play._
import play.api.libs.json.Json
import reactivemongo.bson.{BSONArray, BSONDocument}

import scala.concurrent._
import play.api.mvc.{Controller, Action}
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents, ReactiveMongoModule, MongoController}
import reactivemongo.api.collections.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import reactivemongo.api.ReadPreference
/**
 * Created by karim on 4/26/15.
 */

trait ClientCollection extends WithCollection {
  lazy val coll = reactiveMongoApi.db("clients")
}

class Client extends Controller with ReactiveMongoComponents with ClientCollection {

  lazy val collClient = Client.coll
  val clientwohistory = BSONDocument() ++ (models.Client.fldHistory -> 0)

  /**
   * Client
   */

  def index = Action.async {
    collClient.find(BSONDocument(),clientwohistory).cursor[models.Client](ReadPreference.Primary).collect[List]().map{
      list =>
        Ok(Json.toJson(list))
    }
  }

  def get(id:String) = Action.async {

    collClient.find(BSONDocument("_id" -> id),BSONDocument("history" -> 0)).one[models.Client].map{
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
    ).cursor[models.Client](ReadPreference.Primary).collect[List]().map{
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
              BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
              BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
          BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
          BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
        BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
          BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
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
        BadRequest(Json.toJson(lasterror.errmsg.getOrElse("")))
    }
  }

  /**
   * Visit
   */

  def getvisits(id:String) = Action.async {
    for {
      services <- Service.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      stylists <- Stylist.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      rooms <- Room.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      optclient <- collClient.find(BSONDocument("_id" -> id)).one[models.Client].map {
        case Some(client) => {
          val newclienthistory = client.history.map{
            visit =>
              models.Visit(visit.date,
                visit.details.map{
                  detail =>
                    VisitDetail(stylists.foldLeft(""){(c,e) =>
                      if(e._1 == detail.stylist) {
                        e._2
                      } else {
                        c
                      }
                    },
                      services.foldLeft(""){(c,e) =>
                        if(e._1 == detail.service) {
                          e._2
                        } else {
                          c
                        }
                      },
                      rooms.foldLeft(""){(c,e) =>
                        if(e._1 == detail.room) {
                          e._2
                        } else {
                          c
                        }
                      }
                      ,List(),"")

                })
          }
          Some(models.Client(client._id,client.firstname,client.lastname,client.phone,client.email,newclienthistory))
        }
        case None => None
      }
    } yield {
      optclient match {
        case Some(client) => Ok(Json.toJson(client))
        case None => NotFound
      }
    }

  }

  def getvisit(id:String, visitid:Int) = Action.async {
    getvisitfut(id, visitid).map {
      case Some(client) => Ok(Json.toJson(client))
      case None => NotFound
    }
  }
  def getvisitfut(id:String, visitid:Int) = {

    for {
      services <- Service.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      stylists <- Stylist.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      rooms <- Room.coll.find(BSONDocument(),BSONDocument("name" -> 1)).cursor(ReadPreference.Primary).collect[List]().map{
        list =>
          list.map{
            item =>
              (item.getAs[String]("_id").getOrElse("") -> item.getAs[String]("name").getOrElse(""))
          }
      }
      optclient <- collClient.find(
        BSONDocument("_id" -> id,"history." + visitid.toString -> BSONDocument("$type" -> 3)),
        BSONDocument("firstname" ->1, "lastname" -> 1,"phone" -> 1,"email" -> 1, "history.$" -> 1)).one[models.Client].map {
        case Some(client) => {
          val newclienthistory = client.history.map{
            visit =>
              models.Visit(visit.date,
                visit.details.map{
                  detail =>
                    VisitDetail(stylists.foldLeft(""){(c,e) =>
                      if(e._1 == detail.stylist) {
                        e._2
                      } else {
                        c
                      }
                    },
                      services.foldLeft(""){(c,e) =>
                        if(e._1 == detail.service) {
                          e._2
                        } else {
                          c
                        }
                      },
                      rooms.foldLeft(""){(c,e) =>
                        if(e._1 == detail.room) {
                          e._2
                        } else {
                          c
                        }
                      }
                      ,List(),"")

                })
          }
          Some(models.Client(client._id,client.firstname,client.lastname,client.phone,client.email,newclienthistory))
        }
        case None => None
      }
    } yield {
      optclient
    }
  }



  def addvisit(id:String) = Action.async { implicit request =>
    models.Visit.form.bindFromRequest.fold(
      hasErrors => Future successful BadRequest(hasErrors.errorsAsJson),
      visit => {
        for {
          lasterror <- collClient.update(BSONDocument("_id" -> id), BSONDocument("$push" -> BSONDocument("history" -> visit)))
          optclient <- collClient.find(BSONDocument("_id" -> id)).one[models.Client] if lasterror.ok
          visit <- getvisitfut(id,optclient.get.history.length - 1) if optclient.isDefined
        } yield {
          visit match {
            case Some(client) => Ok(Json.toJson(client.history.head))
            case None => BadRequest
          }
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
   * Visit Details
   */

  def getvisitdetail(id:String,vid:Int,idx:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def addvisitdetail(id:String,vid:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def modvisitdetail(id:String,vid:Int,idx:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def delvisitdetail(id:String,vid:Int,idx:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  /**
   * Visit Products
   */

  def addproduct(id:String,visitid:Int,detailid:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def modproduct(id:String,visitid:Int,detailid:Int,productid:Int) = Action.async { implicit request =>
    Future successful Ok
  }

  def delproduct(id:String,visitid:Int,detailid:Int,productid:Int) = Action.async { implicit request =>
    Future successful Ok
  }
}

object Client extends ClientCollection