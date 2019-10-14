package models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime

object Attractions : IntIdTable() {
    val organizerId: Column<EntityID<Int>> = integer("organizerId").entityId()
    val title: Column<String> = text("title")
    val description: Column<String> = text("description")
    val participantsLimit: Column<Int> = integer("participantsLimit")
    val beginsAt: Column<DateTime> = datetime("beginsAt")
    val endsAt: Column<DateTime> = datetime("endsAt")
}

data class Attraction(
    val id: Int,
    val organizer: User,
    val title: String,
    val description: String,
    val participantsCurrent: Int,
    val participantsLimit: Int,
    val participantsList: Lazy<List<User>>,
    val beginsAt: DateTime,
    val endsAt: DateTime
) {
    fun toAttractionDTO(): AttractionDTO =
        AttractionDTO(
            id = this.id,
            organizer = this.organizer.login,
            title = this.title,
            description = this.description,
            participantsCurrent = this.participantsCurrent,
            participantsLimit = this.participantsLimit,
            beginsAt = this.beginsAt,
            endsAt = this.endsAt
        )
}

fun toAttraction(attractionRow: ResultRow, userRow: ResultRow, participantsCount: Int): Attraction =
    Attraction(
        id = attractionRow[Attractions.id].value,
        organizer = toUser(userRow),
        title = attractionRow[Attractions.title],
        description = attractionRow[Attractions.description],
        participantsCurrent = participantsCount,
        participantsLimit = attractionRow[Attractions.participantsLimit],
        participantsList = lazy {
            emptyList<User>()
            // TODO
            },
        beginsAt = attractionRow[Attractions.beginsAt],
        endsAt = attractionRow[Attractions.endsAt]
    )

data class AttractionDTO(
    val id: Int,
    val organizer: String,
    val title: String,
    val description: String,
    val participantsCurrent: Int,
    val participantsLimit: Int,
    val beginsAt: DateTime,
    val endsAt: DateTime
)

data class NewAttraction(
    val organizerId: Int,
    val title: String,
    val description: String,
    val participantsLimit: Int
)