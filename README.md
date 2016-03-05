# Scalist
### Todoist API client for Scala

[![Build Status](https://img.shields.io/travis/vpavkin/scalist/master.svg)](https://travis-ci.org/vpavkin/scalist) 
[![Coverage status](https://img.shields.io/codecov/c/github/vpavkin/scalist/master.svg)](https://codecov.io/github/vpavkin/scalist?branch=master)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/vpavkin/scalist/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/ru.pavkin/scalist-core_2.11.svg)](https://github.com/vpavkin/scalist)
[![Join the chat at https://gitter.im/vpavkin/scalist](https://badges.gitter.im/vpavkin/scalist.svg)](https://gitter.im/vpavkin/scalist?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Scalist is a client library for [Todoist API](https://developer.todoist.com/), written in Scala. It is built on top of [Cats](https://github.com/typelevel/cats/) and [Shapeless](https://github.com/milessabin/shapeless) with a special attention to type safety. You don't need deep knowledge of Shapeless or Cats to use this library.

Scalist works on Scala 2.11 with Java 7/8.

**Warning:**
Project is at early stages now. Most major features are implemented, but there can be performance problems. Also, a great work should be done on clearing out what types should be made private/public.


1. [Getting started](#getting-started)
  1. [Setup](#setup)
  2. [Calling the API](#calling-the-api)
    1. [Queries](#queries)
    2. [Commands](#commands)
    3. [Request execution](#request-execution)
    4. [Response handling](#response-handling)
    5. [Result type](#result-type)
2. [Design](#design)
  1. [Modules](#modules)
  2. [API dependencies](#api-dependencies)
  3. [Type safety](#type-safety)
3. [Documentation](#documentation)
4. [Supported resources and commands](#supported-resources-and-commands)
5. [Contributing](#contributing)


## Getting started

### Setup
Currently, there's only one API implementation, based on [Dispatch HTTP](https://github.com/dispatch/reboot) and [Circe JSON](https://github.com/travisbrown/circe) libraries. To get it, include this in your `build.sbt`:

```scala
libraryDependencies += "ru.vpavkin" %% "scalist-dispatch-circe" % "0.2.0"
```

Next, import the API toolkit where you need it:

```scala
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.dispatch.circe.default._
// Most of the times you would like to have syntax import alongside.
import ru.pavkin.todoist.api.dispatch.circe.default.syntax._
```

Use your token to get an authorized API wrapper:

```scala
// here you'll need to have an implicit ExecutionContext in scope
val api = todoist.withToken("<your_token_here>")
```

This is everything you need to start calling Todoist API.

### Calling the API

#### Queries

Build a single resource query:

```scala
val projectsReq = api.get[Projects]
```

Build a multiple resource query:

```scala
val multipleReq1 = api.get[Projects]
   .and[Labels]
   .and[Tasks]

// or this way (using shapeless HList)
val multipleReq2 = api.getAll[Projects :: Labels :: Tasks :: HNil]

// or this to load all resources
val multipleReq3 = api.getAll[All]
```

Scalist request builder tracks the list of requested resources, so that you can't, for instance, require same resource twice. These calls won't compile:

```scala
val invalidReq1 = api.get[Projects].and[Labels].and[Projects]
val invalidReq2 = api.getAll[Projects :: Projects :: HNil]
val invalidReq3 = api.getAll[All].and[Projects]
```

#### Commands

Build a single command request:

```scala
val addProject = api.perform(AddProject("Learn scalist", Some(ProjectColor.color18)))
```

Build a typesafe multiple command request (multiple ways).
Notice the usage of `projectId` tagger to mark raw `UUID` as a project id. 
All entity ids have tagged types for additional compile time safety.

```scala
import java.util.UUID

val existingProject: UUID = UUID.randomUUID() // and id of some existing project

// note the existingProject.projectId conversion: we have to tag the raw UUID as a ProjectId
val addStuff1 = api.perform(AddProject("Learn Scalist"))
                   .and(AddProject("Try Scalist"))
                   .and(AddTask("Add Scalist to my project", existingProject.projectId))

// or this way (using shapeless HList with some syntactic sugar to avoid HNils)
val addStuff2 = api.performAll(AddProject("Learn Scalist") :+
                               AddProject("Try Scalist") :+
                               AddTask("Add Scalist to my project", existingProject.projectId))
```

Command chains (using `temp_id` parameter):
```scala
// single dependant command
val addProjectWithTask = api.performAll(
    AddProject("Project").andForIt(projId => AddTask("Task1", projId)) 
    // or more concise: AddProject("Project").andForIt(AddTask("Task1", _)) 
)

// multiple dependant commands
val addProjectWithTasks = api.performAll(
    AddProject("Project").andForItAll(projId => 
      AddTask("Task1", projId) :+
      AddTask("Task2", projId) :+
      AddTask("Task3", projId)
    ) 
)
```

Note, that tagged ids help to avoid misuse here: for instance, you won't be able to create `AddTask` command with a temp id of a label:
```scala
// labelId and taskId have differently tagged types
val invalidCommand = AddLabel("Label").andForIt(AddTask("Task1", _)) 
```

#### Request execution

Everything we created in [Queries](#queries) and [Commands](#commands) sections examples are just request definitions: no requests were actually executed yet.

Given a request definition we can send the request by just calling `execute` method on the request definition instance:

```scala
projectsRequest.execute
```

#### Response handling

Every API call result has type `Future[Xor[Error, Result]]`, where:

- `Xor` is an alternative implementation of `Either` from Cats library (see [more](http://typelevel.org/cats/tut/xor.html])). It's the only public dependency on Cats or Shapeless you'll have to use.
- `Error` can be either:
 - `HTTPError`, that means nonsuccessful HTTP code returned from Todoist API
 - `DecodingError`, that means some inconsistency between Scalist and the Todoist API. If you got that, please [file an issue](https://github.com/vpavkin/scalist/issues/new).
- `Result` is typesafe representation of API response, based on what you requested.

With this in mind, we can handle the result this way:

```scala
import cats.data.Xor // we need this for Xor pattern matching

api.get[Projects].and[Labels].execute.foreach{
  case Xor.Left(error) => println(s"Error: $error"
  case Xor.Right(result) => 
    println(s"Projects: ${result.projects}")
    println(s"Labels: ${result.labels}")
}
```

Of course, you are good to go here with all well known combinators for `Future` and `Xor`, as well as with `XorT` monad transformer.

#### Result type.

The API *effect* is always the same:
```scala
type Effect[Result] = Future[Xor[Error, Result]]
```

`Result` type depends on what you requested. Take a look at following examples:

Single resource request returns just a `List` of entity instances:
```scala
api.get[Projects] // List[Project]
```

Multiple resources request returns an `HList` of requested resources collections:
```scala 
api.get[Projects]
   .and[Labels]   // List[Labels] :: List[Projects] :: HNil  
```

You won't need to work with `HList`s - there're typesafe helpers, defined for such results:
```scala 
api.get[Projects]
   .and[Labels]
   
// later in response handler:
result.projects // List[Project]
result.labels // List[Label]
result.filters // won't compile as we didn't request filters
```

Single command returns `CommandResult` object for simple commands and `TempIdCommandResult` for commads with `temp_id` parameter (e.g. adding new resources):
```scala
api.perform(UpdateTask(1, "NewText")) // CommandResult
api.perform(AddProject("Project")) // TempIdCommandResult
```
Without going into details both `CommandResult` and `TempIdCommandResult` are success/failure containers. `TempIdCommandResult` also holds the real id, assigned to the created resource.

All command sequences, defined either with `perform().and().and()` or `performAll()`, return `HList` of corresponding results.

Again, you don't have to deal with `HList`s explicitly:

- `resultFor(N)` helper is typesafe, you get results with corresponding type being calculated at compile time:
```scala
api.perform(AddProject("project"))
   .and(UpdateTask(1, "task"))

// later
result.resultFor(_0) // TempIdCommandResult of AddProject command
result.resultFor(_1) // CommandResult of UpdateTask 
result.resultFor(_2) // won't compile, only 2 commands were sent
```
- Runtime typed `resultFor(uuid: UUID)` allows to get the result of a command with particular `uuid`.

Note, that it gives much less compile time safety: method calls always compile and the return type is always `Option[TodoistCommandResult]`, where `TodoistCommandResult` is a super-trait for any command result.

```scala
val addProject = AddProject("project")
api.perform(addProject)
   .and(UpdateTask(1, "task"))

// later
// Option[TodoistCommandResult], resolves to Some(TempIdCommandResult)
result.resultFor(addProject.uuid) 

result.resultFor(UUID.randomUUID) // returns None at runtime
```

## Design

Scalist is designed with three correlated requirements:

1. Expressive, readable and concise DSL.
2. Strong type safety with minimum ways for a library user to shoot himself in a foot.
3. You don't have to know the libraries project depends on (Shapeless, Cats, Circe, etc.) to use Scalist.

Most design decisions are guided by those requirements. That's why, for instance, performance is not and won't be a concern in the first versions of Scalist. Moreover, project domain doesn't set any strict requirements for performance.


### Modules

While currently Scalist has only one end-user module `scalist-dispatch-circe`, it's built from several internal modules for easy extension:

1. `scalist-core`: contains model and DTO classes as well as the DSL abstractions. Depends on `cats-core` and `shapeless`.
2. `scalist-dispatch`: general machinery for HTTP requests based on Dispatch HTTP client.
3. `scalist-circe`: Circe based implementations of core abstractions for request/response serialization. Depends on `circe-core`, `circe-generic` and `circe-parser`.
4. `scalist-dispatch-circe`: Complete API implementation, using `scalist-dispatch` and `scalist-circe`.

Modules for other libraries like `play-json` are in future plans.

### API dependencies

You don't need to know Cats or Shapeless to use Scalist. However, some of their APIs are intentionally exposed to Scalist user:

1. `cats.data.Xor` is a part of API effect. There's no need in reinventing the wheel. Standard `Either` lacks some useful features, so Scalist uses high-quality `Xor` tool for typesafe error handling.
2. `shapeless.HList`s are construction material for multi-command or multi-query requests, and they also hold the results of such requests. You don't need to directly call any of it's methods, because Scalist supplies convenient syntax wrappers for `HList`. However, returning raw `HList`s allows experienced developers to use all shapeless power in their advance.

### Type safety

Some type level tricks, that were used within the Scalist DSL will be described here.

*TBD.*

## Documentation

Full API documentation is under development.
For now, please, check the [Getting started](#getting-started) guide or [file an issue](https://github.com/vpavkin/scalist/issues/new) with a question.

Also, all methods that can be used by library user are documented in the source.
Full scaladocs are located [here](http://vpavkin.github.io/scalist/api/#package).

Model classes are good to study right in the [source](https://github.com/vpavkin/scalist/tree/master/core/src/main/scala/ru/pavkin/todoist/api/core/model).

## Supported resources and commands:

Currently supported resources:

- Project
- Label
- Filter
- Task
- Note
- Reminder
- User

Full list of commands, currently supported by Scalist:

- AddAbsoluteTimeBasedReminder
- AddFilter
- AddLabel
- AddLocationBasedReminder
- AddNote
- AddProject
- AddRelativeTimeBasedReminder
- AddTask
- AddTaskToInbox
- ArchiveProjects
- CloseTask
- DeleteFilter
- DeleteLabel
- DeleteNote
- DeleteProjects
- DeleteReminder
- DeleteTasks
- MoveTasks
- UnarchiveProjects
- UncompleteTasks
- UpdateFilter
- UpdateLabel
- UpdateNote
- UpdateProject
- UpdateTask

## Contributing

Any contribution is welcome! :) If you want to, please, don't hesitate to:
- File and issue.
- Send a pull request.
- Ask or leave feedback on [gitter channel](https://gitter.im/vpavkin/scalist).

Full contribution guide is *TBD.*
