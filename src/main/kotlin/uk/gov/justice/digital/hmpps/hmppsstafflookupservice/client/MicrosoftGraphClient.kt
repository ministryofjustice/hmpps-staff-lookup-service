package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow

data class MicrosftADUser(
  val employeeId: String? = null
)

@Service
class MicrosoftGraphClient(
  @Qualifier("microsoftGraphApiWebClient") private val webClient: WebClient
) {
  suspend fun getUsersWithoutPagination(): Flow<MicrosftADUser> {
    return webClient
      .get()
      .uri("/v1.0/users/?\$select=givenName,surname,jobTitle,mail,userPrincipalName&\$top=5")
      .retrieve()
      .bodyToFlow()
  }
}
