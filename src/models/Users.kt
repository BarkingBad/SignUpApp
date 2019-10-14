package models

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow

object Users : IntIdTable() {
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
}

class User(
    val id: Int,
    val login: String,
    val password: String
)

fun toUser(row: ResultRow): User =
    User(
        id = row[Users.id].value,
        login = row[Users.login],
        password = row[Users.password]
    )
