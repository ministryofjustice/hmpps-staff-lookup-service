package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.controller.dto

data class StaffDetails constructor(
  val firstName: String,
  val lastName: String,
  val email: String,
  val jobTitle: String?
)
