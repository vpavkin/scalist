package ru.pavkin.todoist.api.core


object RequestExecutor {
  type Aux[Req, F[_], Res0] = RequestExecutor[Req, F] {type Res = Res0}
}

trait RequestExecutor[Req, F[_]] {
  type Res
  def execute(r: Req): F[Res]
}

