package controllers

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import play.api.Play.current
/**
 * Created by karim on 4/25/15.
 */

trait WithCollection {
  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]
  val coll: BSONCollection
}
