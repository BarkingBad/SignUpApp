package com.signupapp.controllers

import com.signupapp.services.SignUpService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import models.NewAttraction
import models.NewReservation
import models.Reservation

fun Route.signUpController(signUpService: SignUpService) {
    authenticate {
        route("/signup") {
            get {
                call.respond(HttpStatusCode.Accepted, signUpService.getAllAttractions())
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
        }
    }
}