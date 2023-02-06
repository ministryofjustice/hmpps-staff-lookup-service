package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.BuildStatus
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.BuildStatusRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.SINGLE_ITEM_ID
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import java.time.LocalDateTime

class ReindexStatusChecks : IntegrationTestBase() {

  @Autowired
  private lateinit var buildStatusRepository: BuildStatusRepository

  @Test
  fun `does not perform a re-index when there has been a successful build today`() {
    lastSuccessfulBuild(0)
    requestReindex()
    verifyMicrosoftGraphCallTimes(0)
  }

  @Test
  fun `performs a re-index if there has not been a successful build today`() {
    lastSuccessfulBuild(1)
    singlePageGraphResponse()
    doReindex(true)
    verifyMicrosoftGraphCallTimes(1)
  }

  @Test
  fun `performs a re-index if checks bypassed and there has been a successful build today`() {
    lastSuccessfulBuild(0)
    singlePageGraphResponse()
    doReindex(false)
    verifyMicrosoftGraphCallTimes(1)
  }

  @Test
  fun `updates the failed build time on failure`() {
    erroredGraphResponse()
    doFailedReindex()
  }

  private fun lastSuccessfulBuild(daysAgo: Long) {
    val lastSuccessfulBuildDate = LocalDateTime.now().minusDays(daysAgo)
    runBlocking {
      buildStatusRepository.save(
        BuildStatus(SINGLE_ITEM_ID, null, lastSuccessfulBuildDate)
      )
    }
  }

  private fun requestReindex() {
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .exchange()
      .expectStatus()
      .isOk
  }

  private fun doReindex(checkBuildRequired: Boolean) {
    webTestClient.post()
      .uri("/admin/refresh-staffs?checkBuildRequired=$checkBuildRequired")
      .exchange()
      .expectStatus()
      .isOk
    await untilCallTo {
      runBlocking {
        buildStatusRepository.findById(SINGLE_ITEM_ID)
      }
    } matches { it!!.lastFailedBuildDateTime == null && it.lastSuccessfulBuildDateTime != null }
  }

  private fun doFailedReindex() {
    runBlocking {
      buildStatusRepository.save(
        BuildStatus(SINGLE_ITEM_ID, null, null)
      )
    }
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .exchange()
      .expectStatus()
      .isOk
    await untilCallTo {
      runBlocking {
        buildStatusRepository.findById(SINGLE_ITEM_ID)
      }
    } matches { it!!.lastFailedBuildDateTime != null && it.lastSuccessfulBuildDateTime == null }
  }
}
