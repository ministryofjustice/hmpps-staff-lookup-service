package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.config

import org.flywaydb.core.internal.database.postgresql.PostgreSQLConfigurationExtension
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfiguration {

  @Bean
  fun flywayConfigurationCustomizer(): FlywayConfigurationCustomizer {
    return FlywayConfigurationCustomizer { configuration ->
      val configurationExtension = configuration.pluginRegister.getPlugin(PostgreSQLConfigurationExtension::class.java)
      configurationExtension.isTransactionalLock = false
    }
  }
}
