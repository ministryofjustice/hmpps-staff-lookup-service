package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities

import org.springframework.data.relational.core.mapping.Table

@Table("staff_temp")
data class StaffTemp(
  override val id: Long? = null,
  override val firstName: String,
  override val lastName: String? = null,
  override val jobTitle: String? = null,
  override val email: String
) : BaseStaff(id, firstName, lastName, jobTitle, email)
