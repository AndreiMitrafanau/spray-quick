package com.example

import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.http._
import spray.testkit.Specs2RouteTest
import spray.json._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system
  
  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET, POST"
      }
    }
    // My tests
    "return Json Book object for GET request" in {
      Get("/book/1") ~> myRoute ~> check {
        responseAs[String] === Book(1, "DefaultName").toJson.prettyPrint
      }
    }

    "return OK for GET request" in {
      Get("/book/add?bookId=1&name=book") ~> myRoute ~> check {
        status === OK
      }
    }

    "return OK for Post request" in {
      Post("/Json", HttpEntity(MediaTypes.`application/json`,
        """{ "bookId" : 9, "title" : "John" }""")) ~> myRoute ~> check {
        status === OK
      }
    }
  }

}
