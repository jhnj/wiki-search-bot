package app

import app.MessageParser.SearchCommand
import org.scalatest.{FlatSpec, Matchers}
import search.Search

class MessageParserSpec extends FlatSpec with Matchers {
  "parseText" should "parse '/search' commands correctly" in {
    val text = "/search rest  "
    MessageParser.parseCommand(text) should be (Some((SearchCommand, "rest")))
  }

  it should "return handle invalid commands" in {
    val text = "/nonexisting rest  "
    MessageParser.parseCommand(text) should be (None)
  }

  "parseSearch" should "parse 2 parameters" in {
    val text = "[first param]  \n [second]"
    MessageParser.parseSearch(text) should be (Some("first param", "second"))
  }

  it should "handle too few params" in {
    val text = "[first]   "
    MessageParser.parseSearch(text) should be (None)
  }

  it should "handle invalid input" in {
    val text = "[first] error [second]  "
    MessageParser.parseSearch(text) should be (None)
  }

  "handleText" should "parse command" in {
    implicit val config: Config = Config("","","","",1,"")
    val text = "/search something"
    MessageParser.handleText(text, new Search(Array(0))(config)) shouldBe defined
  }

  "handleText" should "parse invalid command" in {
    implicit val config: Config = Config("","","","",1,"")
    val text = "/nonexisting something"
    MessageParser.handleText(text, new Search(Array(0))(config)) shouldBe None
  }
}
