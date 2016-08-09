package com.example

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing._


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    } ~
      get {
        respondWithMediaType(MediaTypes.`application/json`) {
          path("book" / IntNumber) { bookId =>
            complete {
              val book: Book = Book(bookId, "DefaultName")
              book.toJson.prettyPrint
            }
          }
        }
      } ~
      // http://localhost:8080/book/add?bookId=1&name=book
      get {
        path("book" / "add") {
          parameters('bookId, 'name) { (bookId, name) =>
            val book = Book(bookId.toInt, name)
            println(s"Book was created! \n book Id: ${book.bookId} \n book name: ${book.title}")
            complete {
              StatusCodes.OK
            }
          }
        }
      } ~
      // curl -X POST  -H "Content-Type: application/json" --data '{ "bookId" : '9', "title" : "John" }' localhost:8080/Json
      post {
        path("Json") {
          post {
            entity(as[Book]) { book =>
              println("BookId: " + book.bookId)
              println("name: " + book.title)
              complete(StatusCodes.OK)
            }
          }
        }
      }

}