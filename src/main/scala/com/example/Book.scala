package com.example
import spray.json.DefaultJsonProtocol._



case class Book(bookId : Int, title: String)
object Book {
  implicit val bookJson = jsonFormat2(Book.apply)
}


