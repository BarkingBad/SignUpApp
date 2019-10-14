package com.signupapp.services

import com.signupapp.services.DbProvider.dbQuery
import models.Users
import models.toUser
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select


class AuthorizationService {
    suspend fun isLoginTaken(loginToCompare: String): Boolean {
        return dbQuery {
            Users.select { Users.login eq loginToCompare }.count() != 0
        }
    }

    suspend fun addNewUser(newLogin: String, newPassword: String): Int {
        if (isLoginTaken(newLogin)) {
            throw IllegalArgumentException()
        }
        return dbQuery {
            Users.insertAndGetId {
                it[login] = newLogin
                it[password] = newPassword
            }.value
        }
    }

    suspend fun isPasswordCorrect(loginToCompare: String, passwordToCompare: String): Boolean {
        val user = dbQuery {
            Users.select { Users.login eq loginToCompare }.map { toUser(it) }
        }
        return user.first().password == passwordToCompare
    }
}

