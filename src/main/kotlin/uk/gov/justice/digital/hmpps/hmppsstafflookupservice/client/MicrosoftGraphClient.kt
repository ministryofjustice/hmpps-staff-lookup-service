package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClientRequest
import java.time.Duration

data class UserResponse(
  @JsonProperty("@odata.nextLink") val nextLink: String? = null,
  val value: List<MicrosoftADUser>,
)

data class MicrosoftADUser(
  val displayName: String?,
  val givenName: String?,
  val surname: String?,
  val jobTitle: String?,
  val mail: String?,
  val userPrincipalName: String,
) {
  fun getEmail() = (mail ?: userPrincipalName).lowercase()

  fun getFirstName() = givenName ?: displayName
}

@Service
class MicrosoftGraphClient(
  @Qualifier("microsoftGraphApiWebClient") private val webClient: WebClient,
  @Value("\${microsoft.graph.batch-size:100}") private val batchSize: Int,
) {
  suspend fun getUsersPage(skipToken: String?): UserResponse = webClient
    .get()
    .uri(
      "/v1.0/users/?\$select=displayName,givenName,surname,jobTitle,mail,userPrincipalName&\$top=$batchSize" +
        (skipToken?.let { "&\$skiptoken=$skipToken" } ?: ""),
    )
    .httpRequest {
      val reactorRequest: HttpClientRequest = it.getNativeRequest()
      reactorRequest.responseTimeout(Duration.ofSeconds(10))
    }
    .retrieve()
    .bodyToMono(UserResponse::class.java)
    .retry(3)
    .awaitSingle()
}
