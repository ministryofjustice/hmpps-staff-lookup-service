package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.IndexingService

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class IndexingController(
  private val indexingService: IndexingService,
) {

  @Operation(summary = "Re-index data from Microsoft Graph")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @Hidden
  @PostMapping("/admin/refresh-staffs")
  suspend fun fullReindex(@RequestParam(defaultValue = "true") checkBuildRequired: Boolean) {
    indexingService.indexAll(checkBuildRequired)
  }
}
