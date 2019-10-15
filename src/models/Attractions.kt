package models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
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
    val participantsList: List<String>,
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
            beginsAt = this.beginsAt.toString(),
            endsAt = this.endsAt.toString()
        )
}

data class AttractionDTO(
    val id: Int,
    val organizer: String,
    val title: String,
    val description: String,
    val participantsCurrent: Int,
    val participantsLimit: Int,
    val beginsAt: String,
    val endsAt: String
)

data class NewAttraction(
    val organizerId: Int,
    val title: String,
    val description: String,
    val participantsLimit: Int
)