package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.config

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationInsightsConfiguration {
  @Bean
  fun telemetryClient(): TelemetryClient = TelemetryClient()
}
