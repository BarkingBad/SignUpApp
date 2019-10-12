package models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

object Attractions : IntIdTable() {
    val organizerId: Column<EntityID<Int>> = integer("organizerId").entityId()
    val title: Column<String> = text("title")
    val description: Column<String> = text("description")
    val participantsLimit: Column<Int> = integer("participantsLimit")
}

data class Attraction(
    val id: Int,
    val organizer: User,
    val title: String,
    val description: String,
    val participantsCurrent: Int,
    val participantsLimit: Int,
    val participantsList: Lazy<List<User>>
)

data class AttractionDTO(
    val id: Int,
    val organizer: String,
    val title: String,
    val description: String,
    val participantsCurrent: Int,
    val participantsLimit: Int
)

data class NewAttraction(
    val organizerId: Int,
    val title: String,
    val description: String,
    val participantsLimit: Int
)