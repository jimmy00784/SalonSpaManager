package controllers

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController

/**
 * Created by karim on 4/23/15.
 */
object Product extends Controller with MongoController {

  def index = Action {
    Ok
  }

  def get(id:String) = Action {
    Ok
  }

  def add = Action {
    Ok
  }

  def modify(id:String) = Action {
    Ok
  }

  def delete(id:String) = Action {
    Ok
  }
}
