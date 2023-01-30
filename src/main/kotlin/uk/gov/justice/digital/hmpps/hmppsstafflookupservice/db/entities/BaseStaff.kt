package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities

import org.springframework.data.annotation.Id

open class BaseStaff(
  @Id open val id: Long? = null,
  open val firstName: String,
  open val lastName: String,
  open val jobTitle: String? = null,
  open val email: String
)
