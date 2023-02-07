package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository

class WarmUpDatabaseServiceTests {

  private val staffRepository = Mockito.mock(StaffRepository::class.java)

  private val warmUpDatabaseService = WarmUpDatabaseService(staffRepository)

  private val expectedCharacters = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
  @Test
  fun `must query database with all lowercase alphabet characters`(): Unit = runBlocking {
    warmUpDatabaseService.warmUpStaff()
    for (character in expectedCharacters) {
      Mockito.verify(staffRepository).findFirst20ByEmailLikeOrderByEmailAsc(character)
    }
  }
}
