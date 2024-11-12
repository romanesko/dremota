package dremota.plugins

import dremota.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    install(Authentication) {

        bearer("auth-bearer") {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                AuthService.getUserByToken(tokenCredential.token)
            }

        }
    }
}
