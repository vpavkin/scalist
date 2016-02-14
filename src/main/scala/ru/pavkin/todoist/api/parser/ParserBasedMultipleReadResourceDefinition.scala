package ru.pavkin.todoist.api.parser

import cats.Functor
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.utils.{Produce, NotContains, Flattener}
import shapeless.{::, HList, LUBConstraint}

class ParserBasedMultipleReadResourceDefinition[F[_], L[_], P[_], T <: HList, R <: HList, Req](requestFactory: Vector[String] Produce Req,
                                                                                               executor: RequestExecutor.Aux[Req, L, String],
                                                                                               flattener: Flattener[F, L, P],
                                                                                               parserProvider: ParserProvider[P, String])
                                                                                              (override implicit val lub: LUBConstraint[T, ReadResourceType],
                                                                                               override implicit val itr: IsResource.Aux[T, R],
                                                                                               override implicit val F: Functor[L])
  extends ParsedBasedRequestDefinition[F, L, P, String, Req] with MultipleReadResourceDefinition[F, T, R] {

  type Res = R

  val parser: MultipleResourcesParser.Aux[P, String, R] = parserProvider.parser[T, R]

  def load: L[String] = executor.execute(requestFactory.produce(itr.strings))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
  def parse(r: String): P[R] = parser.parse(r)

  def and[TT <: ReadResourceType](implicit NC: NotContains[T, TT],
                                  ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T, ITR.Repr :: R] =
    new ParserBasedMultipleReadResourceDefinition[F, L, P, TT :: T, ITR.Repr :: R, Req](
      requestFactory, executor, flattener, parserProvider
  )
}
