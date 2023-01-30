package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.search

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.Staff
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase

class SearchStaffByEmail : IntegrationTestBase() {

  @Test
  fun `must return staff by partial matched email`(): Unit = runBlocking {
    val first = staffRepository.save(Staff(firstName = "John", lastName = "Smith", jobTitle = "Probation Practitioner", email = "john.smith@staff.com"))
    val second = staffRepository.save(Staff(firstName = "Joanne", lastName = "Smith", email = "joanne.smith@staff.com"))

    webTestClient.get()
      .uri("/staff/search?email=sm")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.[?(@.email=='${first.email}')].firstName").isEqualTo(first.firstName)
      .jsonPath("$.[?(@.email=='${first.email}')].lastName").isEqualTo(first.lastName)
      .jsonPath("$.[?(@.email=='${first.email}')].jobTitle").isEqualTo(first.jobTitle)
      .jsonPath("$.[?(@.email=='${second.email}')].firstName").isEqualTo(second.firstName)
      .jsonPath("$.[?(@.email=='${second.email}')].lastName").isEqualTo(second.lastName)
  }

  @Test
  fun `must favour username over domain name`(): Unit = runBlocking {
    for (i in 0..21) {
      staffRepository.save(Staff(firstName = "a$i", lastName = "Smith", jobTitle = "Probation Practitioner", email = "a.smith$i@staff.com"))
    }

    val usernameStartsWithDomain = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@staff.com"))

    webTestClient.get()
      .uri("/staff/search?email=sta")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.[?(@.email=='${usernameStartsWithDomain.email}')].firstName").isEqualTo(usernameStartsWithDomain.firstName)
      .jsonPath("$.[?(@.email=='${usernameStartsWithDomain.email}')].lastName").isEqualTo(usernameStartsWithDomain.lastName)
      .jsonPath("$.[?(@.email=='${usernameStartsWithDomain.email}')].jobTitle").isEqualTo(usernameStartsWithDomain.jobTitle)
  }

  @Test
  fun `must return bad request when no email supplied`(): Unit = runBlocking {
    webTestClient.get()
      .uri("/staff/search")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isBadRequest
  }

  @Test
  fun `cannot search staff when no auth token supplied`() {
    webTestClient.get()
      .uri("/staff/search?email=sta")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }
}
