package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftGraphClient

@Service
class IndexingService(
  val microsoftGraphClient: MicrosoftGraphClient,
  val databaseWriteService: DatabaseWriteService
) {
  suspend fun indexAll(): String {
    var graphResponse = microsoftGraphClient.getUsersPage(null)
    databaseWriteService.writeData(graphResponse.value)
    while (graphResponse.nextLink != null) {
      val skipToken = graphResponse.nextLink!!.substringAfter("skiptoken=")
      graphResponse = microsoftGraphClient.getUsersPage(skipToken)
      databaseWriteService.writeData(graphResponse.value)
    }
    return "DONE"
  }
}
