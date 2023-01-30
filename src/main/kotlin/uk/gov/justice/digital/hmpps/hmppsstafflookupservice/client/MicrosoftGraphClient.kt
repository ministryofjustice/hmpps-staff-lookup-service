package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

data class UserResponse(
  @JsonProperty("@odata.nextLink") val nextLink: String? = null,
  val value: List<MicrosoftADUser>,
)

data class MicrosoftADUser(
  val givenName: String,
  val surname: String,
  val jobTitle: String? = null,
  val mail: String? = null,
  val userPrincipalName: String,
)

@Service
class MicrosoftGraphClient(
  @Qualifier("microsoftGraphApiWebClient") private val webClient: WebClient
) {
  suspend fun getUsersPage(skipToken: String?): UserResponse {
    return webClient
      .get()
      .uri(
        "/v1.0/users/?\$select=givenName,surname,jobTitle,mail,userPrincipalName&\$top=5" +
          skipToken?.let { "&\$skiptoken=$skipToken" }
      )
      .retrieve()
      .awaitBody()
  }
}
