package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.StatusStore
import java.time.LocalDateTime

class ReindexStatusChecks : IntegrationTestBase() {

  @Autowired
  private lateinit var statusStore: StatusStore

  @Test
  fun `does not perform a re-index when there has been a successful build today`() {
    lastSuccessfulBuild(0)
    doFullReindex(true)
    verifyMicrosoftGraphCallTimes(0)
  }

  @Test
  fun `performs a re-index if there has not been a successful build today`() {
    lastSuccessfulBuild(1)
    singlePageGraphResponse()
    doFullReindex(true)
    verifyMicrosoftGraphCallTimes(1)
  }

  @Test
  fun `performs a re-index if checks bypassed and there has been a successful build today`() {
    lastSuccessfulBuild(0)
    singlePageGraphResponse()
    doFullReindex(false)
    verifyMicrosoftGraphCallTimes(1)
  }

  private fun lastSuccessfulBuild(daysAgo: Long) {
    statusStore.lastSuccessfulBuildDate = LocalDateTime.now().minusDays(daysAgo)
  }

  private fun doFullReindex(checkBuildRequired: Boolean) {
    webTestClient.post()
      .uri("/admin/refresh-staffs?checkBuildRequired=$checkBuildRequired")
      .exchange()
      .expectStatus()
      .isOk
    await untilCallTo { statusStore.isBuildInProgress() } matches { it == false }
  }
}
