package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.TimeToLive
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody.json
import org.mockserver.model.MediaType.APPLICATION_JSON
import org.mockserver.verify.VerificationTimes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftADUser
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.UserResponse
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffTempRepository

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {

  private var microsoftOauthMock: ClientAndServer = startClientAndServer(9090)
  private var microsoftGraphMock: ClientAndServer = startClientAndServer(9091)

  @Autowired
  protected lateinit var objectMapper: ObjectMapper

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthHelper

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  protected lateinit var staffRepository: StaffRepository

  @Autowired
  protected lateinit var staffTempRepository: StaffTempRepository

  @BeforeEach
  fun removeAllData() = runBlocking {
    staffRepository.deleteAll()
    staffTempRepository.deleteAll()
  }

  @BeforeEach
  fun `setup microsoft oauth`() {
    setupMicrosoftOauth()
    microsoftGraphMock.reset()
  }

  @AfterAll
  fun tearDownServer() {
    microsoftOauthMock.stop()
    microsoftGraphMock.stop()
  }

  internal fun setupMicrosoftOauth() {
    val tenantFromConfig = "tenant-id"
    val response = response().withContentType(APPLICATION_JSON)
      .withBody(objectMapper.writeValueAsString(mapOf("access_token" to "ABCDE", "token_type" to "bearer")))
    microsoftOauthMock.`when`(request().withMethod("POST").withPath("/auth/$tenantFromConfig/oauth2/v2.0/token")).respond(response)
  }

  internal fun setAuthorisation(
    user: String = "ADMIN",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf()
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisation(user, roles, scopes)

  fun singlePageGraphResponse(
    usersResponse: List<MicrosoftADUser> = listOf(
      MicrosoftADUser("Abc", "Def", "SPO", "a.user@staff.com", "a.user@staff.com"),
      MicrosoftADUser("Ghi", "Jkl", null, null, "ABCDE"),
      MicrosoftADUser("No Surname", null, null, null, "ABCDE")
    )
  ) {
    val response = response().withContentType(APPLICATION_JSON)
      .withBody(objectMapper.writeValueAsString(UserResponse(null, usersResponse)))

    microsoftGraphMock.`when`(request().withPath("/v1.0/users/")).respond(response)
  }

  fun multiplePageGraphResponse() {
    val skipTokenToSecondPage = "ASKIPTOKEN"
    val firstResponseNextLink = "https://graph.microsoft.com/v1.0/users/?\$select=givenName%2csurname%2cjobTitle%2cmail%2cuserPrincipalName&\$top=5&\$skiptoken=$skipTokenToSecondPage"
    val firstResponseUsers = listOf(
      MicrosoftADUser("Abc", "Def", "SPO", "a.user@staff.com", "a.user@staff.com"),
      MicrosoftADUser(null, null, null, null, "ABCDE")
    )
    val firstResponse = response().withContentType(APPLICATION_JSON)
      .withBody(objectMapper.writeValueAsString(UserResponse(firstResponseNextLink, firstResponseUsers)))

    val secondResponseUsers = listOf(
      MicrosoftADUser("Mno", "Pqr", null, null, "XYZ")
    )
    val secondResponse = response().withContentType(APPLICATION_JSON)
      .withBody(objectMapper.writeValueAsString(UserResponse(null, secondResponseUsers)))

    microsoftGraphMock.`when`(
      request().withPath("/v1.0/users/"),
      Times.once(),
      TimeToLive.unlimited(),
      10
    ).respond(firstResponse)
    microsoftGraphMock.`when`(
      request().withPath("/v1.0/users/").withQueryStringParameter("\$skiptoken", skipTokenToSecondPage)
    ).respond(secondResponse)
  }

  fun erroredGraphResponseWithSuccessOnRetry() {
    val usersResponse = listOf(
      MicrosoftADUser("Abc", "Def", "SPO", "a.user@staff.com", "a.user@staff.com"),
    )
    val errorResponse = response().withStatusCode(500)
    val successfulResponse = response().withContentType(APPLICATION_JSON)
      .withBody(objectMapper.writeValueAsString(UserResponse(null, usersResponse)))

    microsoftGraphMock.`when`(
      request().withPath("/v1.0/users/"),
      Times.once(),
      TimeToLive.unlimited(),
      10
    ).respond(errorResponse)
    microsoftGraphMock.`when`(
      request().withPath("/v1.0/users/")
    ).respond(successfulResponse)
  }

  fun erroredGraphResponse() {
    val errorResponse = response().withStatusCode(500)

    microsoftGraphMock.`when`(
      request().withPath("/v1.0/users/")
    ).respond(errorResponse)
  }

  fun verifyMicrosoftOauthMockCall(tenantId: String) {
    val tenantFromConfig = "tenant-id"
    val encodedClientIdAndSecretFromConfig = "Y2xpZW50aWQ6Y2xpZW50c2VjcmV0"
    microsoftOauthMock.verify(
      request()
        .withPath("/auth/$tenantFromConfig/oauth2/v2.0/token")
        .withHeader("Authorization", "Basic $encodedClientIdAndSecretFromConfig")
        .withBody(
          json(
            """
            {
              scope: 'https://graph.microsoft.com/.default',
              grant_type: 'client_credentials'
            }
            """.trimIndent(),
            MatchType.STRICT
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }
}
