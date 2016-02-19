package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

trait PlainAPISuite[F[_], P[_], A] extends APISuite[F, P, A] {

  type Projects = A @@ tags.Projects
  type Labels = A @@ tags.Labels

}
