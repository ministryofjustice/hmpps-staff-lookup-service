package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.integration

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.Staff
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.entities.StaffTemp
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffRepository
import uk.gov.justice.digital.hmpps.hmppsstafflookupservice.db.repositories.StaffTempRepository

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
abstract class IntegrationTestBase {

  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  protected lateinit var staffRepository: StaffRepository

  @Autowired
  protected lateinit var staffTempRepository: StaffTempRepository

  @BeforeEach
  fun removeAllData() = runBlocking {
    staffRepository.save(Staff(firstName = "processed", lastName = "staff", email = "processed@staff.com"))
    staffTempRepository.save(StaffTemp(firstName = "temp", lastName = "staff", email = "temp@staff.com"))

    staffRepository.deleteAll()
    staffTempRepository.deleteAll()
  }
}
