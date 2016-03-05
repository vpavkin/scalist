package ru.pavkin.todoist.api.dispatch.circe

/**
  * Default asynchronous API client implementation.
  *
  * API effect is `Future[ Xor[DispatchAPI.Error, T] ]`
  *
  * Uses Dispatch HTTP client and Circe Json under the hood
  *
  * @see [[scala.concurrent.Future]]
  * @see [[cats.data.Xor]]
  * @see [[ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI.Error]]
  */
object default extends CirceModelAPISuite
