package ru.pavkin.todoist.api.core.model

import java.text.SimpleDateFormat
import java.util.{TimeZone, Calendar, Date}

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class TodoistDateSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  private val dateFormatter = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z")
  dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))

  test("TodoistDate.format holds the format") {
    forAll(arbitrary[Date]) { (d: Date) =>
      TodoistDate.format(d) shouldEqual dateFormatter.format(d)
    }
  }

  test("TodoistDate.parse parses it's own results") {
    forAll(Gen.choose(new Date().getTime - 1000000L, new Date().getTime + 1000000L)) { (l: Long) =>
      TodoistDate.parse(TodoistDate.format(new Date(l))).map(_.getTime()) shouldEqual
        Some((l / 1000) * 1000)
    }
  }

  test("TodoistDate.parse parses some realworld dates") {
    TodoistDate.parse("Fri 26 Feb 2016 10:39:51 +0000").map(_.getTime) shouldEqual Some(1456483191000L)
    TodoistDate.format(new Date(1456483191000L)) shouldBe "Fri 26 Feb 2016 10:39:51 +0000"
  }
}

