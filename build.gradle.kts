plugins {
  id("com.gorylenko.gradle-git-properties") version "2.5.3"
  id("org.owasp.dependencycheck") version "12.1.3"
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "9.0.0"
  kotlin("plugin.spring") version "2.2.10"
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencyCheck {
  nvd.datafeedUrl = "file:///opt/vulnz/cache"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")

  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.12")

  implementation("io.opentelemetry:opentelemetry-api:1.54.0")

  runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.flywaydb:flyway-core")
  implementation("org.postgresql:postgresql:42.7.7")
  runtimeOnly("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
  runtimeOnly("org.postgresql:postgresql:42.7.7")
  runtimeOnly("org.flywaydb:flyway-database-postgresql:11.12.0")

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

  testImplementation("io.jsonwebtoken:jjwt-impl:0.13.0")
  testImplementation("io.jsonwebtoken:jjwt-jackson:0.13.0")

  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
  testImplementation("org.mock-server:mockserver-netty:5.15.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
  }
}

ktlint {
  version.set("1.5.0")
}

tasks.test {
  maxParallelForks = 1
}
