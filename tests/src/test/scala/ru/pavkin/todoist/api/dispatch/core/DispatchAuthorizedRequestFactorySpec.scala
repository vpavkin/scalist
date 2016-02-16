package ru.pavkin.todoist.api.dispatch.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

import scala.collection.JavaConversions._

class DispatchAuthorizedRequestFactorySpec extends FunSuite with Checkers {

  test("DispatchAuthorizedRequestFactory creates valid todoist requests") {
    check((token: String, request: Vector[String]) => {
      val factory = DispatchAuthorizedRequestFactory(token)
      val req = factory.produce(request).toRequest
      req.getQueryParams.keys.zip(req.getQueryParams.values).toMap.mapValues(_.mkString) == Map(
        "token" -> token,
        "seq_no" -> "0",
        "resource_types" -> s"""[${request.map("\"" + _ + "\"").mkString(",")}]"""
      ) &&
        req.getMethod == "POST"
    })
  }

}

