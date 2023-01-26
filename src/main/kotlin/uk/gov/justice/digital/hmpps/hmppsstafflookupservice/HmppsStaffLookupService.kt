package uk.gov.justice.digital.hmpps.hmppsstafflookupservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
class HmppsStaffLookupService

fun main(args: Array<String>) {
  runApplication<HmppsStaffLookupService>(*args)
}
