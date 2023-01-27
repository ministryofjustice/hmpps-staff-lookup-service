package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftGraphClient

@Service
class IndexingService(
  val microsoftGraphClient: MicrosoftGraphClient
) {
  suspend fun indexAll(): String {
    val exampleData = microsoftGraphClient.getUsersWithoutPagination()
    exampleData.collect {
      println("DATA: ${it}")
    }
    return "DONE"
  }
}