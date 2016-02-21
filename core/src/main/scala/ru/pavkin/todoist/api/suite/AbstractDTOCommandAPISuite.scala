package ru.pavkin.todoist.api.suite

import cats.{FlatMap, Monad}
import ru.pavkin.todoist.api.core.decoder._
import ru.pavkin.todoist.api.core.dto.{RawCommandWithTempId, RawCommand}
import shapeless.HList

trait AbstractDTOCommandAPISuite[F[_], P[_], Base, CommandResultDTO]
  extends CommandAPISuite with AbstractDTOAPISuite[P] {

  implicit def commandDtoDecoder: SingleResponseDecoder.Aux[P, Base, CommandResultDTO]

  implicit def dtoToRawCommand1[A]
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, RawCommand[A], CommandResultDTO, CommandResult]

  implicit def dtoToRawCommand2[A](implicit M: Monad[P])
  : SingleCommandResponseDecoder.Aux[P, RawCommandWithTempId[A], CommandResultDTO, TempIdCommandResult]

  protected def fromCommandResultDtoDecoder[C, R]
  (f: (C, CommandResultDTO) => Option[R])
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, C, CommandResultDTO, R] =
    SingleCommandResponseDecoder.using[P, C, CommandResultDTO, R] {
      (c, dto) => f(c, dto).map(M.pure).getOrElse(dtoDecodingError(s"Couldn't find result for command $c"))
    }

  implicit def composeCommandDecoders1[C, Out]
  (implicit
   p2: SingleCommandResponseDecoder.Aux[P, C, CommandResultDTO, Out],
   F: FlatMap[P]): SingleCommandResponseDecoder.Aux[P, C, Base, Out] =
    commandDtoDecoder.compose(p2)

  implicit def composeCommandDecoders2[C <: HList, Out <: HList]
  (implicit
   p2: MultipleCommandResponseDecoder.Aux[P, C, CommandResultDTO, Out],
   F: FlatMap[P]): MultipleCommandResponseDecoder.Aux[P, C, Base, Out] =
    commandDtoDecoder.compose(p2)

}
