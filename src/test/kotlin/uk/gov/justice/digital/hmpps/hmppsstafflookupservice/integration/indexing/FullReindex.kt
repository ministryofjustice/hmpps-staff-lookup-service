package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import com.microsoft.applicationinsights.TelemetryClient
import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.StatusStore
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.telemetry.TelemetryEventType

class FullReindex : IntegrationTestBase() {

  @MockBean
  private lateinit var telemetryClient: TelemetryClient

  @Autowired
  private lateinit var statusStore: StatusStore

  @Test
  fun `must call microsoft for oauth token`() {
    singlePageGraphResponse()
    doFullReindex()
    verifyMicrosoftOauthMockCall("someTenant")
  }

  @Test
  fun `telemetry call made on success`() {
    singlePageGraphResponse()
    doFullReindex()
    verify(telemetryClient).trackEvent(eq(TelemetryEventType.INDEX_BUILD_COMPLETE.eventName), any(), anyOrNull())
  }

  @Test
  fun `submits the graph data for saving`(): Unit = runBlocking {
    multiplePageGraphResponse()
    doFullReindex()
    Assertions.assertEquals(2L, staffTempRepository.count())
  }

  @Test
  fun `retries on error getting the graph data`(): Unit = runBlocking {
    erroredGraphResponseWithSuccessOnRetry()
    doFullReindex()
    Assertions.assertEquals(1L, staffTempRepository.count())
  }

  @Test
  fun `fails eventually when error getting the graph data`(): Unit = runBlocking {
    erroredGraphResponse()
    doFullReindex()
    Assertions.assertEquals(0, staffTempRepository.count())
  }

  @Test
  fun `telemetry calls made on failure`() {
    erroredGraphResponse()
    doFullReindex()
    verify(telemetryClient).trackException(any<Exception>())
    verify(telemetryClient).trackEvent(eq(TelemetryEventType.INDEX_BUILD_COMPLETE.eventName), any(), anyOrNull())
  }

  private fun doFullReindex() {
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .headers(setAuthorisation(roles = listOf("ROLE_TEST")))
      .exchange()
      .expectStatus()
      .isOk
    await untilCallTo { statusStore.isBuildInProgress() } matches { it == false }
  }
}
