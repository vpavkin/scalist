package ru.pavkin.todoist.api.dispatch.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.RawRequest

import scala.collection.JavaConversions._

class DispatchAuthorizedRequestFactorySpec extends FunSuite with Checkers {

  test("DispatchAuthorizedRequestFactory creates valid todoist requests") {
    check((token: String, request: RawRequest) => {
      val factory = DispatchAuthorizedRequestFactory(token)
      val req = factory.produce(request).toRequest
      req.getQueryParams.keys.zip(req.getQueryParams.values).toMap.mapValues(_.mkString) ==
        request.mapValues(l => s"""[${l.mkString(",")}]""") ++ Map(
          "token" -> token,
          "seq_no" -> "0"
        ) &&
        req.getMethod == "POST"
    })
  }

}

