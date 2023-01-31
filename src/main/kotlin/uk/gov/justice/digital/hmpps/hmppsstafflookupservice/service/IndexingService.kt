package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import com.microsoft.applicationinsights.TelemetryClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftGraphClient
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.telemetry.TelemetryEventType

@Service
class IndexingService(
  val statusStore: StatusStore,
  val telemetryClient: TelemetryClient,
  val microsoftGraphClient: MicrosoftGraphClient,
  val databaseWriteService: DatabaseWriteService
) {
  fun indexAll() {
    executeAsynchronously {
      var graphResponse = microsoftGraphClient.getUsersPage(null)
      databaseWriteService.writeData(graphResponse.value)
      while (graphResponse.nextLink != null) {
        val skipToken = graphResponse.nextLink!!.substringAfter("skiptoken=")
        graphResponse = microsoftGraphClient.getUsersPage(skipToken)
        databaseWriteService.writeData(graphResponse.value)
      }
    }
    return
  }

  private fun executeAsynchronously(indexingBlock: suspend () -> Unit) {
    statusStore.checkAndSetInProgress()
    CoroutineScope(Dispatchers.IO).launch {
      var successfulBuild = false
      val stopWatch = StopWatch()
      stopWatch.start()
      try {
        indexingBlock.invoke()
        successfulBuild = true
      } catch (e: Exception) {
        telemetryClient.trackException(e)
      } finally {
        stopWatch.stop()
        statusStore.setComplete()
        telemetryClient.trackEvent(
          TelemetryEventType.INDEX_BUILD_COMPLETE.eventName,
          mapOf(
            "success" to successfulBuild.toString(),
            "timeTaken" to stopWatch.totalTimeSeconds.toString(),
          ),
          null
        )
      }
    }
  }
}
