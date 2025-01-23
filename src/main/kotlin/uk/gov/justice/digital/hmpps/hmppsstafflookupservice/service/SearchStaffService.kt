package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.controller.dto.StaffDetails
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository

@Service
class SearchStaffService(private val staffRepository: StaffRepository) {
  suspend fun searchStaff(email: String): Flow<StaffDetails> = staffRepository.findFirst20ByEmailLikeIgnoreCaseOrderByEmailAsc("%$email%")
    .map { staffEntity -> StaffDetails(staffEntity.firstName, staffEntity.lastName, staffEntity.email, staffEntity.jobTitle) }
}
