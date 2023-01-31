package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class StatusStore {
  private var isIndexBuildInProgress = AtomicBoolean(false)

  fun isBuildInProgress(): Boolean = isIndexBuildInProgress.get()

  fun checkAndSetInProgress() {
    if (!isIndexBuildInProgress.compareAndSet(false, true)) {
      throw RuntimeException("Build already in progress")
    }
  }

  fun setComplete() {
    isIndexBuildInProgress.compareAndSet(true, false)
  }
}