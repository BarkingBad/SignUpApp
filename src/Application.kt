package com.signupapp

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.signupapp.controllers.authorizationController
import com.signupapp.services.AuthorizationService
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import com.signupapp.controllers.signUpController
import com.signupapp.models.SimpleJWT
import com.signupapp.services.SignUpService
import io.ktor.auth.jwt.jwt
import io.ktor.jackson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")

    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val authorizationService: AuthorizationService = AuthorizationService()
    val signUpService: SignUpService = SignUpService()

    routing {
        authorizationController(authorizationService, simpleJwt)
        signUpController(signUpService)
    }

}


