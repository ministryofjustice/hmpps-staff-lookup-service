package uk.gov.justice.digital.hmpps.hmppsstafflookupservice.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.ConnectionFactoryUtils
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service

@Service
class SwapStaffTablesService(private val databaseClient: DatabaseClient) {

  suspend fun swapTables() {
    val connection = ConnectionFactoryUtils.getConnection(databaseClient.connectionFactory).awaitSingle()
    ScriptUtils.executeSqlScript(connection, ClassPathResource("swap-staff-tables.sql")).awaitSingle()
  }
}
