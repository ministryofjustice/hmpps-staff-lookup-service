package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities

data class Staff(
  override val id: Long? = null,
  override val firstName: String,
  override val lastName: String? = null,
  override val jobTitle: String? = null,
  override val email: String
) : BaseStaff(id, firstName, lastName, jobTitle, email)
