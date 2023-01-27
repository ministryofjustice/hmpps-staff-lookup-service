plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.8.1"
  kotlin("plugin.spring") version "1.8.0"
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")

  implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.14")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.6.14")
  implementation("org.springdoc:springdoc-openapi-security:1.6.14")

  implementation("io.opentelemetry:opentelemetry-api:1.22.0")

  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:r2dbc-postgresql:0.9.3.RELEASE")
  runtimeOnly("org.postgresql:postgresql:42.5.1")

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
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
