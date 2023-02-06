package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class BuildStatus(
  @Id val id: Long? = null,
  var lastFailedBuildDateTime: LocalDateTime? = null,
  var lastSuccessfulBuildDateTime: LocalDateTime? = null,
)
