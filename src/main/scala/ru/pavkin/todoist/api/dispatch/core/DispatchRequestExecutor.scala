package ru.pavkin.todoist.api.dispatch.core

import dispatch.Req
import ru.pavkin.todoist.api.core.RequestExecutor

trait DispatchRequestExecutor[F[_], Res0] extends RequestExecutor[Req, F] {
  type Res = Res0
}
