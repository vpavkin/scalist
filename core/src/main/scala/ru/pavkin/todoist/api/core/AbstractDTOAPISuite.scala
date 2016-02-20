package ru.pavkin.todoist.api.core

import cats.{FlatMap, Monad}
import ru.pavkin.todoist.api.core.decoder.{MultipleResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.suite.APISuite
import shapeless.HList

trait AbstractDTOAPISuite[F[_], P[_], Base, Dto] extends APISuite[F, P, Base] {

  def dtoDecodingError[T](msg: String): P[T]

  implicit def dtoDecoder: SingleResponseDecoder.Aux[P, Base, Dto]

  protected def fromDtoDecoder[T](f: Dto => Option[T])
                                 (name: String)
                                 (implicit M: Monad[P]): SingleResponseDecoder.Aux[P, Dto, T] =
    SingleResponseDecoder.using[P, Dto, T] {
      dto => f(dto).map(M.pure).getOrElse(dtoDecodingError(s"No $name found in the response"))
    }

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, Dto, Projects]
  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, Dto, Labels]

  implicit def composeDecoders1[Out](implicit
                                     p2: SingleResponseDecoder.Aux[P, Dto, Out],
                                     F: FlatMap[P]): SingleResponseDecoder.Aux[P, Base, Out] =
    dtoDecoder.compose(p2)

  implicit def composeDecoders2[Out <: HList](implicit
                                              p2: MultipleResponseDecoder.Aux[P, Dto, Out],
                                              F: FlatMap[P]): MultipleResponseDecoder.Aux[P, Base, Out] =
    dtoDecoder.compose(p2)

}
