package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import com.microsoft.applicationinsights.TelemetryClient
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.DatabaseWriteService
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.StatusStore
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.telemetry.TelemetryEventType
import java.lang.Exception

class FullReindex : IntegrationTestBase() {

  @MockBean
  private lateinit var databaseWriteService: DatabaseWriteService
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
  fun `submits the graph data for saving`() {
    multiplePageGraphResponse()
    doFullReindex()
    verify(databaseWriteService, times(2)).writeData(any())
  }

  @Test
  fun `retries on error getting the graph data`() {
    erroredGraphResponseWithSuccessOnRetry()
    doFullReindex()
    verify(databaseWriteService, times(1)).writeData(any())
  }

  @Test
  fun `fails eventually when error getting the graph data`() {
    erroredGraphResponse()
    doFullReindex()
    verify(databaseWriteService, times(0)).writeData(any())
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
