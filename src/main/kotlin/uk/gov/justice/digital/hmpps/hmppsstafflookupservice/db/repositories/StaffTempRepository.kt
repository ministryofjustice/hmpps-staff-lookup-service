package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.StaffTemp

interface StaffTempRepository : CoroutineCrudRepository<StaffTemp, Long>
