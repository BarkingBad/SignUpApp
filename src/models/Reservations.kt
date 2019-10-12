package models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Reservations : Table() {
    val userId: Column<EntityID<Int>> = integer("userId").primaryKey(0).entityId()
    val attractionId: Column<EntityID<Int>> = integer("attractionId").primaryKey(1).entityId()
    val timeStamp: Column<DateTime> = datetime("timeStamp")
}

data class Reservation(
    val user: User,
    val attraction: Attraction,
    val timestamp: DateTime
)

data class NewReservation(
    val userId: Int,
    val attractionId: Int
)


