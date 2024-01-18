package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository
import java.util.stream.IntStream

@Service
class WarmUpDatabaseService(private val staffRepository: StaffRepository) {
  private val alphabet = IntStream.rangeClosed('a'.code, 'z'.code)
    .mapToObj { it.toChar().toString() }
    .toList()
  suspend fun warmUpStaff() {
    for (c in alphabet) {
      staffRepository.findFirst20ByEmailLikeIgnoreCaseOrderByEmailAsc(c)
    }
  }
}
