package ru.pavkin.todoist.api.suite

import cats.{FlatMap, Monad}
import ru.pavkin.todoist.api.core.decoder.{MultipleResponseDecoder, SingleResponseDecoder}
import shapeless.HList

trait AbstractDTOQueryAPISuite[F[_], P[_], Base, ResourceDTO]
  extends QueryAPISuite with AbstractDTOAPISuite[P] {

  implicit def resourceDtoDecoder: SingleResponseDecoder.Aux[P, Base, ResourceDTO]

  protected def fromResourceDtoDecoder[T](f: ResourceDTO => Option[T])
                                         (name: String)
                                         (implicit M: Monad[P]): SingleResponseDecoder.Aux[P, ResourceDTO, T] =
    SingleResponseDecoder.using[P, ResourceDTO, T] {
      dto => f(dto).map(M.pure).getOrElse(dtoDecodingError(s"No $name found in the response"))
    }

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, ResourceDTO, Projects]
  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, ResourceDTO, Labels]

  implicit def composeDecoders1[Out](implicit
                                     p2: SingleResponseDecoder.Aux[P, ResourceDTO, Out],
                                     F: FlatMap[P]): SingleResponseDecoder.Aux[P, Base, Out] =
    resourceDtoDecoder.compose(p2)

  implicit def composeDecoders2[Out <: HList](implicit
                                              p2: MultipleResponseDecoder.Aux[P, ResourceDTO, Out],
                                              F: FlatMap[P]): MultipleResponseDecoder.Aux[P, Base, Out] =
    resourceDtoDecoder.compose(p2)

}
