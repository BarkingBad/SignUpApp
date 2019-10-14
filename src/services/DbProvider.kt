package com.signupapp.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction


object DbProvider {
    val db by lazy {
        val dbtmp = Database.connect(
            "jdbc:postgresql://localhost:5432/chat", driver = "org.postgresql.Driver",
            user = "postgres", password = "123"
        )
        transaction {
            create(Users)
            create(Attractions)
            create(Reservations)
        }
        dbtmp
    }

    suspend fun <T> dbQuery(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            transaction(db) {
                block()
            }
        }
    }
}