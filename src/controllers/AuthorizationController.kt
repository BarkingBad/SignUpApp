package com.signupapp.controllers

import com.signupapp.models.Credentials
import com.signupapp.models.SimpleJWT
import com.signupapp.services.AuthorizationService
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.lang.IllegalArgumentException


fun Route.authorizationController(authorizationService: AuthorizationService, simpleJWT: SimpleJWT) {
    route("/login") {
        get {

        }
        post {
            val post = call.receive<Credentials>()
            println(post.login)
            println(post.password)
            if (authorizationService.isPasswordCorrect(post.login, post.password)) {
                call.respond(mapOf("token" to simpleJWT.sign(post.login), "userId" to 1)) //TODO Change user id that is sent
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Invalid credentials")
            }
        }
    }

    post("/register") {
        val post = call.receive<Credentials>()
        try {
            authorizationService.addNewUser(post.login, post.password)
            call.respond(HttpStatusCode.Created)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.InternalServerError, "Login is already taken")
        }
    }
}



