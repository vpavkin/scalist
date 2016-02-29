package ru.pavkin.todoist.api.suite

import cats.{FlatMap, Monad}
import ru.pavkin.todoist.api.core.decoder.{MultipleResponseDecoder, SingleResponseDecoder}
import shapeless.HList

trait AbstractDTOQueryAPISuite[F[_], P[_], Base, ResourceDTO]
  extends QueryAPISuite with AbstractDTOAPISuite[P] {

  def resourceDtoDecoder: SingleResponseDecoder[P, Base, ResourceDTO]

  protected def fromResourceDtoDecoder[T](f: ResourceDTO => Option[T])
                                         (name: String)
                                         (implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, T] =
    SingleResponseDecoder.using[P, ResourceDTO, T] {
      dto => f(dto).map(M.pure).getOrElse(dtoDecodingError(s"No $name found in the response"))
    }

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Projects]
  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Labels]
  implicit def dtoToTasks(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Tasks]
  implicit def dtoToNotes(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Notes]
  implicit def dtoToFilters(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Filters]
  implicit def dtoToReminders(implicit M: Monad[P]): SingleResponseDecoder[P, ResourceDTO, Reminders]

  implicit def composeDecoders1[Out](implicit
                                     p2: SingleResponseDecoder[P, ResourceDTO, Out],
                                     F: FlatMap[P]): SingleResponseDecoder[P, Base, Out] =
    resourceDtoDecoder.compose(p2)


  implicit def composeDecoders2[Out <: HList](implicit
                                              p2: MultipleResponseDecoder[P, ResourceDTO, Out],
                                              F: FlatMap[P]): MultipleResponseDecoder[P, Base, Out] =
    resourceDtoDecoder.compose(p2)

}
