package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.BuildStatus

@Repository
interface BuildStatusRepository : CoroutineCrudRepository<BuildStatus, Long>

const val SINGLE_ITEM_ID = 1L
