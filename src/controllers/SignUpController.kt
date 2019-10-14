package com.signupapp.controllers

import com.signupapp.services.SignUpService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import models.NewAttraction
import models.NewReservation

fun Route.signUpController(signUpService: SignUpService) {
    authenticate {
        route("/signup") {
            get {
                call.respond(HttpStatusCode.OK, signUpService.getAllAttractions().map { it.toAttractionDTO() })
            }
            post("/new") {
                val post = call.receive<NewAttraction>()
                if(signUpService.addNewAttraction(post.organizerId, post.title, post.description, post.participantsLimit) > 0) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error during creation of attraction")
                }
            }
            post {
                val post = call.receive<NewReservation>()
                try {
                    signUpService.singUpForAttraction(post.userId, post.attractionId)
                    call.respond(HttpStatusCode.Created)
                } catch(e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error during signing up for attraction")
                }
            }
            delete {
                val delete = call.receive<NewReservation>()
                try {
                    signUpService.dropOutFromAttraction(delete.userId, delete.attractionId)
                    call.respond(HttpStatusCode.OK)
                } catch(e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error during dropping out from attraction")
                }
            }
        }
    }
}