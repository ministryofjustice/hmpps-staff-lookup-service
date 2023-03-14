package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.BuildStatusRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.SINGLE_ITEM_ID
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class IndexingStatusService(
  val statusRepository: BuildStatusRepository,
) {
  suspend fun checkIndexingRequired(): Boolean {
    val lastSuccessfulBuildDateTime = statusRepository.findById(SINGLE_ITEM_ID)!!.lastSuccessfulBuildDateTime
    if (lastSuccessfulBuildDateTime != null && lastSuccessfulBuildDateTime.toLocalDate().isEqual(LocalDate.now())) {
      return false
    }
    return true
  }

  suspend fun indexingComplete(successfulBuild: Boolean) {
    val buildEntity = statusRepository.findById(SINGLE_ITEM_ID)
    if (successfulBuild) {
      buildEntity!!.lastSuccessfulBuildDateTime = LocalDateTime.now()
    } else {
      buildEntity!!.lastFailedBuildDateTime = LocalDateTime.now()
    }
    statusRepository.save(buildEntity)
  }
}
