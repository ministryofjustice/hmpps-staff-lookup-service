package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class IndexingStatusService(
  val statusStore: StatusStore
) {
  fun checkIndexingRequired(): Boolean {
    try {
      val lastSuccessfulBuild = statusStore.lastSuccessfulBuildDate
      if (lastSuccessfulBuild != null && lastSuccessfulBuild.toLocalDate().isEqual(LocalDate.now())) {
        return false
      }
      statusStore.checkAndSetInProgress()
      return true
    } finally {
    }
  }

  fun indexingComplete(successfulBuild: Boolean) {
    statusStore.setComplete()
  }
}
