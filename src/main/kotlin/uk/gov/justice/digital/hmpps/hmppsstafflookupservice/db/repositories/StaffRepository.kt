package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.Staff

interface StaffRepository : CoroutineCrudRepository<Staff, Long> {
  suspend fun findFirst20ByEmailLikeOrderByEmailAsc(email: String): Flow<Staff>
}
