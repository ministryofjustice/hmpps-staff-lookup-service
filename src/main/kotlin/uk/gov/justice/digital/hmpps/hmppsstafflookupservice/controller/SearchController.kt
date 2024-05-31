package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.controller.dto.StaffDetails
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.SearchStaffService

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class SearchController(
  private val searchStaffService: SearchStaffService,
) {

  @Operation(summary = "Search Staff by partial match email")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_MANAGE_A_WORKFORCE_ALLOCATE') or hasRole('ROLE_WORKLOAD_READ')")
  @GetMapping("/staff/search")
  suspend fun searchStaff(@RequestParam(required = true) email: String): Flow<StaffDetails> = searchStaffService.searchStaff(email)
}
