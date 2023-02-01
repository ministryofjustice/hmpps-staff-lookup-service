package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.indexing

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.client.MicrosoftADUser
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.Staff
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase

class SaveOnIndex : IntegrationTestBase() {

  @Test
  fun `must save into staff temp table`(): Unit = runBlocking {
    val microsoftADUser = MicrosoftADUser(
      "Abc", "Def", "SPO", "a.mail@staff.com", "a.user@staff.com"
    )

    singlePageGraphResponse(listOf(microsoftADUser))

    val staffTemp = refreshStaffReturnFirstSaved()

    Assertions.assertEquals(microsoftADUser.givenName, staffTemp.firstName)
    Assertions.assertEquals(microsoftADUser.surname, staffTemp.lastName)
    Assertions.assertEquals(microsoftADUser.jobTitle, staffTemp.jobTitle)
    Assertions.assertEquals(microsoftADUser.mail, staffTemp.email)
  }

  @Test
  fun `must fallback on user principal name when mail doesn't exist`(): Unit = runBlocking {
    val microsoftADUser = MicrosoftADUser(
      "Abc", "Def", "SPO", null, "a.user@staff.com"
    )

    singlePageGraphResponse(listOf(microsoftADUser))

    val staffTemp = refreshStaffReturnFirstSaved()
    Assertions.assertEquals(microsoftADUser.userPrincipalName, staffTemp.email)
  }

  @Test
  fun `must not store when no name`(): Unit = runBlocking {
    val microsoftADUser = MicrosoftADUser(
      "Abc", "Def", "SPO", "a.mail@staff.com", "a.user@staff.com"
    )
    val noNameUser = MicrosoftADUser(
      null, null, "SPO", "b.mail@staff.com", "b.user@staff.com"
    )
    singlePageGraphResponse(listOf(microsoftADUser, noNameUser))

    val staffTemp = refreshStaffReturnFirstSaved()
    Assertions.assertEquals(microsoftADUser.mail, staffTemp.email)
  }

  @Test
  fun `must store email in lower case`(): Unit = runBlocking {
    val microsoftADUser = MicrosoftADUser(
      "Abc", "Def", "SPO", "A.MAIL@STAFF.COM", "a.user@staff.com"
    )

    singlePageGraphResponse(listOf(microsoftADUser))

    val staffTemp = refreshStaffReturnFirstSaved()
    Assertions.assertEquals(microsoftADUser.mail!!.lowercase(), staffTemp.email)
  }

  @Test
  fun `must not store when email ends in different domain`(): Unit = runBlocking {
    val microsoftADUser = MicrosoftADUser(
      "Abc", "Def", "SPO", "a.mail@staff.com", "a.user@staff.com"
    )
    val differentDomain = MicrosoftADUser(
      "Abc", "Def", "SPO", "a.mail@different.domain.com", "a.user@different.domain.com"
    )
    singlePageGraphResponse(listOf(differentDomain, microsoftADUser))

    val staffTemp = refreshStaffReturnFirstSaved()
    Assertions.assertEquals(microsoftADUser.mail, staffTemp.email)
  }

  private suspend fun refreshStaffReturnFirstSaved(): Staff {
    webTestClient.post()
      .uri("/admin/refresh-staffs")
      .exchange()
      .expectStatus()
      .isOk
    await.until {
      runBlocking {
        staffRepository.count() == 1L
      }
    }

    val staffTemp = staffRepository.findAll().first()
    return staffTemp
  }
}
