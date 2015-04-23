package controllers

//import models.{Servicer, Room}
import play.api._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global
import models.Servicer._

import scala.concurrent._

object Application extends Controller with MongoController {

  lazy val collappt = db("appointments")

  def index = Action.async {

    //val x:BSONDocument = Servicer(BSONObjectID.generate,"",List(),false)

    collappt.find(BSONDocument("_id" -> "count")).one[BSONDocument].map{
      case Some(doc) => doc.getAs[Int]("count").getOrElse(0)
      case None => 0
    }.map{
      count =>
        collappt.save(BSONDocument("_id" -> "count","count" -> (count + 1)))
        Ok(views.html.index("Your new application is ready.")(count))
    }
  }

}