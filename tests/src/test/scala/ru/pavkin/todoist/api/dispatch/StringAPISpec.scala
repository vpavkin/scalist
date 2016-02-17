package ru.pavkin.todoist.api.dispatch

import cats.Id
import scala.concurrent.ExecutionContext.Implicits.global
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor

class StringAPISpec
  extends FutureBasedAPISuiteSpec[DispatchStringRequestExecutor.Result, Id, String]("Dispatch String API")
    with StringAPI
