package models

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}

class User(
    val id: Int,
    val login: String,
    val password: String
)
