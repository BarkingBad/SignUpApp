package com.signupapp.services

import com.signupapp.services.DbProvider.dbQuery
import models.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class SignUpService {
    suspend fun getAllAttractions(): List<Attraction> {
        return dbQuery {
                Attractions
                    .selectAll()
                    .map {
                        toAttraction(
                            it,
                            transaction {
                                Users
                                    .select { Users.id eq it[Attractions.organizerId] }
                                    .first()
                            },
                            transaction {
                                Reservations
                                    .select { Reservations.attractionId eq it[Attractions.id] }
                                    .count()
                            }
                        )
                    }
        }
    }

    suspend fun addNewAttraction(
        organizerId: Int,
        title: String,
        description: String,
        participantsLimit: Int
    ): Int {
        return dbQuery {
            Attractions.insertAndGetId {
                it[Attractions.organizerId] = EntityID(organizerId, Users)
                it[Attractions.title] = title
                it[Attractions.description] = description
                it[Attractions.participantsLimit] = participantsLimit
            }.value
        }
    }

    suspend fun singUpForAttraction(userId: Int, attractionId: Int): InsertStatement<Number> {

        return dbQuery {
            Reservations.insert {
                it[Reservations.userId] = EntityID(userId, Users)
                it[Reservations.attractionId] = EntityID(attractionId, Attractions)
                it[timeStamp] = DateTime.now()
            }
        }
    }

    suspend fun dropOutFromAttraction(userId: Int, attractionId: Int): Int {
        return dbQuery {
            Reservations.deleteWhere {
                (Reservations.userId eq userId) and (Reservations.attractionId eq attractionId)
            }
        }
    }
}