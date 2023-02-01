package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import kotlinx.coroutines.flow.collect
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftADUser
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.StaffTemp
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffTempRepository

@Service
class DatabaseWriteService(private val staffTempRepository: StaffTempRepository, @Value("\${email.domain}") private val emailDomain: String) {

  @Transactional
  suspend fun writeData(data: List<MicrosoftADUser>) {
    val staffToSave = data.filter {
      it.givenName != null && it.surname != null && it.getEmail().endsWith(emailDomain)
    }.map { StaffTemp(firstName = it.givenName!!, lastName = it.surname!!, jobTitle = it.jobTitle, email = it.getEmail()) }
    staffTempRepository.saveAll(staffToSave).collect()
  }
}
