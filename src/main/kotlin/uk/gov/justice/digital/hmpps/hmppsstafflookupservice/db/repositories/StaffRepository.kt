package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.Staff

interface StaffRepository : CoroutineCrudRepository<Staff, Long>
