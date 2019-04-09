package edu.nyu.dlts.aspace

import edu.nyu.dlts.aspace.AspaceClient.AspaceSupport
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.json4s.JsonAST.{JArray, JString, JValue}
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable.ListMap
import scala.io.Source

case class AO(title: String, level: String)

object Main extends App with AspaceSupport {

  val json = ("query" ->
    ("field" -> "otherlevel") ~
      ("value" -> "website") ~
      ("jsonmodel_type" -> "field_query") ~
      ("negated" -> false) ~
      ("literal" -> true))

  val builder = new URIBuilder(env + "/repositories/2/search")
  builder.setParameter("page", "1")
  builder.setParameter("page_size", "20")
  builder.setParameter("aq", compact(render(json)))
  val httpGet = new HttpGet(builder.build())
  httpGet.addHeader(header, token.get)
  val response = get(httpGet)
  val results = (response.get.json \ "results").extract[List[JValue]]

  println("\"uri\"\ttitle\"\t\"level\"")
  results.foreach { r =>
    val uri = (r \ "uri").extract[String]
    val title = (r \ "title").extract[String]
    val level = (r \ "level").extract[String]

    println("\"" + uri + "\"\t\"" + title + "\"\t\"" + level + "\"")

  }

}

