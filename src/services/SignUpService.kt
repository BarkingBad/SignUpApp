package com.signupapp.services

import com.signupapp.services.DbProvider.dbQuery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                    .innerJoin(Users, { Attractions.organizerId }, { Users.id } )
                    .join(Reservations, JoinType.LEFT, additionalConstraint = {Attractions.id eq Reservations.attractionId})
                    .slice(
                        Attractions.id,
                        Users.id,
                        Users.login,
                        Users.password,
                        Attractions.title,
                        Attractions.description,
                        Attractions.participantsLimit,
                        Attractions.beginsAt,
                        Attractions.endsAt,
                        Reservations.userId.count()
                    )
                    .selectAll()
                    .groupBy(
                        Attractions.id,
                        Users.id,
                        Users.login,
                        Users.password,
                        Attractions.title,
                        Attractions.description,
                        Attractions.participantsLimit,
                        Attractions.beginsAt,
                        Attractions.endsAt
                    )
                    .map {
                        toAttraction(
                            it
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

    fun getParticipants(attractionId: Int): List<String> {
        return transaction {
            Reservations
                .innerJoin(Users, { Reservations.userId }, { Users.id } )
                .select { Reservations.attractionId eq attractionId }
                .map {
                    it[Users.login]
                }

        }
    }

    fun toAttraction(row: ResultRow): Attraction =
        Attraction(
            id = row[Attractions.id].value,
            organizer = User(row[Users.id].value, row[Users.login], row[Users.password]),
            title = row[Attractions.title],
            description = row[Attractions.description],
            participantsCurrent = row[Reservations.userId.count()],
            participantsLimit = row[Attractions.participantsLimit],
            participantsList = getParticipants(row[Attractions.id].value),
            beginsAt = row[Attractions.beginsAt],
            endsAt = row[Attractions.endsAt]
        )
}