package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service.WarmUpDatabaseService

class WarmUpDatabaseServiceTests : IntegrationTestBase() {

  @MockBean
  override lateinit var staffRepository: StaffRepository

  @Autowired
  protected lateinit var warmUpDatabaseService: WarmUpDatabaseService

  private val expectedCharacters = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

  @Test
  fun `must query database with all lowercase alphabet characters`(): Unit = runBlocking {
    warmUpDatabaseService.warmUpStaff()
    for (character in expectedCharacters) {
      Mockito.verify(staffRepository).findFirst20ByEmailLikeOrderByEmailAsc(character)
    }
  }
}
