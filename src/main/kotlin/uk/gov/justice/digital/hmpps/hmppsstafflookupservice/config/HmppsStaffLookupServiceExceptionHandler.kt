package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.config

import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.MissingRequestValueException
import reactor.core.publisher.Mono

@RestControllerAdvice
class HmppsStaffLookupServiceExceptionHandler {
  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(e: Exception): Mono<ResponseEntity<ErrorResponse>> {
    log.info("Validation exception: {}", e.message)
    return Mono.just(
      ResponseEntity
        .status(BAD_REQUEST)
        .body(
          ErrorResponse(
            status = BAD_REQUEST,
            userMessage = "Validation failure: ${e.message}",
            developerMessage = e.message,
          ),
        ),
    )
  }

  @ExceptionHandler(MissingRequestValueException::class)
  fun handleMissingRequestValueException(e: Exception): Mono<ResponseEntity<ErrorResponse>> {
    log.info("Missing Request Value exception: {}", e.message)
    return Mono.just(
      ResponseEntity
        .status(BAD_REQUEST)
        .body(
          ErrorResponse(
            status = BAD_REQUEST,
            userMessage = "Missing Request Value failure: ${e.message}",
            developerMessage = e.message,
          ),
        ),
    )
  }

  @ExceptionHandler(java.lang.Exception::class)
  fun handleException(e: java.lang.Exception): Mono<ResponseEntity<ErrorResponse>> {
    log.error("Unexpected exception", e)
    return Mono.just(
      ResponseEntity
        .status(INTERNAL_SERVER_ERROR)
        .body(
          ErrorResponse(
            status = INTERNAL_SERVER_ERROR,
            userMessage = "Unexpected error: ${e.message}",
            developerMessage = e.message,
          ),
        ),
    )
  }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse?>? {
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(
        ErrorResponse(
          status = HttpStatus.FORBIDDEN,
          userMessage = "Access is denied",
          developerMessage = e.message,
        ),
      )
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

data class ErrorResponse(
  val status: Int,
  val errorCode: Int? = null,
  val userMessage: String? = null,
  val developerMessage: String? = null,
  val moreInfo: String? = null,
) {
  constructor(
    status: HttpStatus,
    errorCode: Int? = null,
    userMessage: String? = null,
    developerMessage: String? = null,
    moreInfo: String? = null,
  ) :
    this(status.value(), errorCode, userMessage, developerMessage, moreInfo)
}
