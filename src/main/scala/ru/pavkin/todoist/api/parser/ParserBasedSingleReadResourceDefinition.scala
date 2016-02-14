package ru.pavkin.todoist.api.parser

import cats.Functor
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.utils.{Produce, Flattener}
import shapeless.{::, <:!<, HNil}

class ParserBasedSingleReadResourceDefinition[F[_], L[_], P[_], T <: ReadResourceType, R, Req](requestFactory: Vector[String] Produce Req,
                                                                                               executor: RequestExecutor.Aux[Req, L, String],
                                                                                               flattener: Flattener[F, L, P],
                                                                                               parserProvider: ParserProvider[P, String])
                                                                                              (override implicit val itr: IsResource.Aux[T, R],
                                                                                               override implicit val F: Functor[L])
  extends ParsedBasedRequestDefinition[F, L, P, String, Req] with SingleReadResourceDefinition[F, T, R] {

  type Res = R

  private val parser: SingleResourceParser.Aux[P, String, R] = parserProvider.parser[T]

  def load: L[String] = executor.execute(requestFactory.produce(itr.strings))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
  def parse(r: String): P[R] = parser.parse(r)

  def and[TT <: ReadResourceType](implicit NEQ: <:!<[TT, T], ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T :: HNil, ITR.Repr :: R :: HNil] =
    new ParserBasedMultipleReadResourceDefinition[F, L, P, TT :: T :: HNil, ITR.Repr :: R :: HNil, Req](
      requestFactory, executor, flattener, parserProvider
    )
}
