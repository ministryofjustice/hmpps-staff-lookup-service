package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.DatabaseWriteService

class FullReindex : IntegrationTestBase() {

  @MockBean
  private lateinit var databaseWriteService: DatabaseWriteService

  @Test
  fun `must call microsoft for oauth token`() {
    singlePageGraphResponse()
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .isOk
    verifyMicrosoftOauthMockCall("someTenant")
  }

  @Test
  fun `submits the graph data for saving`() {
    multiplePageGraphResponse()
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .isOk
    verify(databaseWriteService, times(2)).writeData(any())
  }

  @Test
  fun `retries on error getting the graph data`() {
    erroredGraphResponseWithSuccessOnRetry()
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .isOk
  }

  @Test
  fun `fails eventually when error getting the graph data`() {
    erroredGraphResponse()
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }
}
