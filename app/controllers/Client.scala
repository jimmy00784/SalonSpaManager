package controllers

import scala.concurrent._
import play.api.mvc.{Controller, Action}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default._
/**
 * Created by karim on 4/26/15.
 */
object Client extends Controller with MongoController {

  lazy val collClient = db("clients")

  /**
   * Client
   */

  def index = Action.async {
    Future successful Ok
  }

  def get(id:String) = Action.async {
    Future successful Ok
  }

  def add = Action.async { implicit request =>
    Future successful Ok
  }

  def modify(id:String) = Action.async { implicit request =>
    Future successful Ok
  }

  def remove(id:String) = Action.async { implicit request =>
    Future successful Ok
  }

  /**
   * Contact Info
   */

  def addphone(id:String,phone:String) = Action.async {
    Future successful Ok
  }

  def delphone(id:String,phoneid:Int) = Action.async {
    Future successful Ok
  }

  def addemail(id:String,email:String) = Action.async {
    Future successful Ok
  }

  def delemail(id:String,emailid:Int) = Action.async {
    Future successful Ok
  }

  /**
   * Visit
   */

  def getvisits(id:String) = Action.async {
    Future successful Ok
  }

  def addvisit(id:String) = Action.async { implicit request =>
    Future successful Ok
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
