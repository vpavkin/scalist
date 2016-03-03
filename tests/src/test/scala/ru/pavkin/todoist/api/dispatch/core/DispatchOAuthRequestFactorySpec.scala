package ru.pavkin.todoist.api.dispatch.core

import com.ning.http.client.FluentStringsMap
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.RawRequest

import scala.collection.JavaConversions._

class DispatchOAuthRequestFactorySpec extends FunSuite with Checkers {

  val factory = new DispatchOAuthRequestFactory

  test("DispatchOAuthRequestFactory creates valid todoist requests") {
    check((request: RawRequest) => {
      val req = factory.produce(request).toRequest
      val params = Option(req.getQueryParams).getOrElse(new FluentStringsMap())
      params.keys.zip(params.values).toMap.mapValues(_.mkString) ==
        request.mapValues(_.mkString) &&
        req.getMethod == "POST"
    })
  }

}

