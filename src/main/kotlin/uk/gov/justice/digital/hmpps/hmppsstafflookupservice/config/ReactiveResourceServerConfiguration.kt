package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class ReactiveResourceServerConfiguration {

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
      .csrf { it.disable() } // crst not needed an rest api
      .authorizeExchange {
        it.pathMatchers(
          "/webjars/**", "/favicon.ico", "/csrf",
          "/health/**", "/info", "/h2-console/**",
          "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
          "/admin/**",
        ).permitAll()
          .anyExchange().authenticated()
      }
      .oauth2ResourceServer {
        it.jwt { jwtCustomizer ->
          jwtCustomizer.jwtAuthenticationConverter(ReactiveAuthAwareTokenConverter())
        }
      }.build()
  }
}
