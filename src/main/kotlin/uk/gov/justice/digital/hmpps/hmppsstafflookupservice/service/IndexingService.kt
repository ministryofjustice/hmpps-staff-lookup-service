package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftGraphClient

@Service
class IndexingService(
  val microsoftGraphClient: MicrosoftGraphClient,
  val databaseWriteService: DatabaseWriteService
) {
  suspend fun indexAll(): String {
    var exampleData = microsoftGraphClient.getUsersPage(null)
    databaseWriteService.writeData(exampleData.value)
    while (exampleData.nextLink != null) {
      val skipToken = exampleData.nextLink!!.substringAfter("skiptoken=")
      exampleData = microsoftGraphClient.getUsersPage(skipToken)
      databaseWriteService.writeData(exampleData.value)
    }
    return "DONE"
  }
}
