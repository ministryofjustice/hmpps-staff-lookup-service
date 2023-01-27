package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase

class FullReindex : IntegrationTestBase() {

  @Test
  fun `must call microsoft for oauth token`() {
    standardGraphResponse()
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .isOk
    verifyMicrosoftOauthMockCall("someTenant")
  }
}
