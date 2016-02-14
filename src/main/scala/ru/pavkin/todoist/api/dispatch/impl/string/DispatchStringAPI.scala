package ru.pavkin.todoist.api.dispatch.impl.string

import dispatch.Req
import ru.pavkin.todoist.api.core.{AuthorizedRequestFactory, PlainAPI, RequestExecutor}

class DispatchStringAPI(override val requestFactory: AuthorizedRequestFactory[Vector[String], Req],
                        override val executor: RequestExecutor.Aux[Req, DispatchStringRequestExecutor.Result, String])
  extends PlainAPI[DispatchStringRequestExecutor.Result, Req, String]
