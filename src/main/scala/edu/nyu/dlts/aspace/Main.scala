package edu.nyu.dlts.aspace

import edu.nyu.dlts.aspace.AspaceClient.AspaceSupport
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.JsonDSL._

object Main extends App with AspaceSupport {

  val query: String = compact(render("query" ->
    ("field" -> "otherlevel") ~
      ("value" -> "website") ~
      ("jsonmodel_type" -> "field_query") ~
      ("negated" -> false) ~
      ("literal" -> true)))

  val initialResponse = search(2, 1, 10, query).get.body

  val lastPage = (initialResponse \ "last_page").extract[Int]
  val totalHits = (initialResponse \ "total_hits").extract[Int]

  (1 to lastPage).foreach { i =>
    val response = search(2,i,10,query).get
    println(s"page $i")
    val body = response.body
    val results = (body \ "results").extract[List[JValue]]
    results.foreach { result => println("\ttitle: " + (result \ "title").extract[String]) }
  }

}

