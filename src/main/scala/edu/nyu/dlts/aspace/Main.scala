package edu.nyu.dlts.aspace

import java.io.{File, FileWriter}

import edu.nyu.dlts.aspace.AspaceClient.AspaceSupport
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.json4s.JsonAST.{JArray, JString, JValue}
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.JsonDSL._


case class AO(title: String, level: String)

object Main extends App with AspaceSupport {

  val f = new File("test.tsv")
  val writer = new FileWriter(f)
  writer.write(s"uri\ttitle\tresource_uri\tscope_note\n")
  writer.flush()

  val json = "query" ->
    ("field" -> "otherlevel") ~
      ("value" -> "website") ~
      ("jsonmodel_type" -> "field_query") ~
      ("negated" -> false) ~
      ("literal" -> true)

  val builder = new URIBuilder(env + "/repositories/2/search")
  builder.setParameter("page", "1")
  builder.setParameter("page_size", "20")
  builder.setParameter("aq", compact(render(json)))
  val httpGet = new HttpGet(builder.build())
  httpGet.addHeader(header, token.get)
  val response = get(httpGet)
  val results = (response.get.json \ "results").extract[List[JValue]]


  results.foreach { result =>
    val j = parse((result \ "json").extract[String])
    val uri = (result \ "uri").extract[String]
    val title = (result \ "title").extract[String]
    val resource = (result \ "resource").extract[String]
    val notes = (j \ "notes").extract[JValue]
    val scope = ((notes(0) \"subnotes")(0) \"content").extract[String]
    writer.write(s"t$uri\t$title\t$resource\t$scope\n")
    writer.flush()

  }

  writer.flush()
  writer.close

}

