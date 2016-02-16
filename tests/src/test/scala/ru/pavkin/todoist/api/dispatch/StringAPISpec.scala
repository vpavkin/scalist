package ru.pavkin.todoist.api.dispatch

import cats._
import ru.pavkin.todoist.api.core.APISuiteSpec
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor

class StringAPISpec
  extends APISuiteSpec[DispatchStringRequestExecutor.Result, Id, String]("Dispatch String API")
    with StringAPI
