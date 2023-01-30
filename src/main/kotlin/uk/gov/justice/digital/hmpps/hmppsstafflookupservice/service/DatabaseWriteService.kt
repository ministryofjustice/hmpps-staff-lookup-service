package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftADUser

@Service
class DatabaseWriteService {
  fun writeData(data: List<MicrosoftADUser>) {
    println("DATA SIZE: ${data.size}")
  }
}