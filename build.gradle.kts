plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.0"
  kotlin("plugin.spring") version "1.8.10"
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.2")

  implementation("io.opentelemetry:opentelemetry-api:1.24.0")

  runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:r2dbc-postgresql:1.0.0.RELEASE")
  runtimeOnly("org.postgresql:postgresql:42.5.3")

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

  testImplementation("io.jsonwebtoken:jjwt-impl:0.11.5")
  testImplementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

  testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
  testImplementation("org.mock-server:mockserver-netty:5.15.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "18"
    }
  }
}
