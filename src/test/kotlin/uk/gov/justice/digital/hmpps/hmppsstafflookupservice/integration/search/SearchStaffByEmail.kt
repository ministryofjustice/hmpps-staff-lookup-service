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
    val noSurname = staffRepository.save(Staff(firstName = "TeamSmilesShared", email = "teamsmilesshared@staff.com"))

    webTestClient.get()
      .uri("/staff/search?email=sm")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.[?(@.email=='${first.email}')].firstName").isEqualTo(first.firstName)
      .jsonPath("$.[?(@.email=='${first.email}')].lastName").isEqualTo(first.lastName)
      .jsonPath("$.[?(@.email=='${first.email}')].fullName").isEqualTo("${first.firstName} ${first.lastName}")
      .jsonPath("$.[?(@.email=='${first.email}')].jobTitle").isEqualTo(first.jobTitle)
      .jsonPath("$.[?(@.email=='${second.email}')].firstName").isEqualTo(second.firstName)
      .jsonPath("$.[?(@.email=='${second.email}')].lastName").isEqualTo(second.lastName)
      .jsonPath("$.[?(@.email=='${noSurname.email}')].fullName").isEqualTo("${noSurname.firstName}")
  }

  @Test
  fun `must fail if not authorized`(): Unit = runBlocking {
    webTestClient.get()
      .uri("/staff/search?email=sm")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `must accept lower and upper case query`(): Unit = runBlocking {
    staffRepository.save(Staff(firstName = "Andrew", lastName = "Smith", jobTitle = "Probation Practitioner", email = "andrew.smith@staff.com"))

    val usernameStartsWithDomain = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@staff.com"))

    webTestClient.get()
      .uri("/staff/search?email=StAc")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(1)
      .jsonPath("$.[0].firstName").isEqualTo(usernameStartsWithDomain.firstName)
      .jsonPath("$.[0].lastName").isEqualTo(usernameStartsWithDomain.lastName)
      .jsonPath("$.[0].jobTitle").isEqualTo(usernameStartsWithDomain.jobTitle)
  }

  @Test
  fun `must match full email address`(): Unit = runBlocking {
    staffRepository.save(Staff(firstName = "Andrew", lastName = "Smith", jobTitle = "Probation Practitioner", email = "andrew.smith@staff.com"))

    val usernameStartsWithDomain = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@staff.com"))
    val otherUserName = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@oldstaff.com"))

    webTestClient.get()
      .uri("/staff/search?email=Stacie.smith@stAff.co")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(1)
      .jsonPath("$.[0].firstName").isEqualTo(usernameStartsWithDomain.firstName)
      .jsonPath("$.[0].lastName").isEqualTo(usernameStartsWithDomain.lastName)
      .jsonPath("$.[0].jobTitle").isEqualTo(usernameStartsWithDomain.jobTitle)
  }

  @Test
  fun `must match partial email address with @`(): Unit = runBlocking {
    staffRepository.save(Staff(firstName = "Andrew", lastName = "Smith", jobTitle = "Probation Practitioner", email = "andrew.smith@staff.com"))

    val usernameStartsWithDomain = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@staff.com"))
    val otherUserName = staffRepository.save(Staff(firstName = "Stacie", lastName = "Smith", jobTitle = "Probation Practitioner", email = "stacie.smith@oldstaff.com"))

    webTestClient.get()
      .uri("/staff/search?email=acie.smith@stAff")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isOk
      .expectBody()
      .jsonPath("$.length()").isEqualTo(1)
      .jsonPath("$.[0].firstName").isEqualTo(usernameStartsWithDomain.firstName)
      .jsonPath("$.[0].lastName").isEqualTo(usernameStartsWithDomain.lastName)
      .jsonPath("$.[0].jobTitle").isEqualTo(usernameStartsWithDomain.jobTitle)
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
