package com.example.proyectofinalmovil.services.api

import org.json.JSONObject

data class AuthSession(
    val userId: Int,
    val email: String,
    val name: String,
    val role: String,
    val clientId: Int?,
    val token: String,
    val expiration: String,
)

class AuthApi(
    private val client: CineUabcsApiClient = CineUabcsApiClient(),
) {
    fun login(email: String, password: String): AuthSession {
        val request = JSONObject()
            .put("correo", email.trim())
            .put("password", password)

        val response = try {
            JSONObject(
                client.postJson(
                    path = "mobile/auth/login",
                    jsonBody = request.toString(),
                ),
            )
        } catch (error: ApiException) {
            val message = runCatching {
                JSONObject(error.responseBody).getString("error")
            }.getOrDefault("No se pudo iniciar sesión")
            throw AuthException(message)
        }
        val user = response.getJSONObject("user")

        return AuthSession(
            userId = user.getInt("id"),
            email = user.getString("correo"),
            name = user.getString("nombre"),
            role = user.getString("rol"),
            clientId = if (user.isNull("clienteId")) null else user.getInt("clienteId"),
            token = response.getString("token"),
            expiration = response.getString("expiracion"),
        )
    }

    fun registrar(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String?,
        correo: String,
        contrasena: String,
    ): AuthSession {
        val cuerpo = JSONObject()
            .put("nombre", nombre.trim())
            .put("apellidoPaterno", apellidoPaterno.trim())
            .put("correo", correo.trim())
            .put("password", contrasena)
        if (!apellidoMaterno.isNullOrBlank()) {
            cuerpo.put("apellidoMaterno", apellidoMaterno.trim())
        }

        try {
            client.postJson(
                path = "api/auth/register",
                jsonBody = cuerpo.toString(),
            )
        } catch (error: ApiException) {
            val mensaje = runCatching {
                JSONObject(error.responseBody).getString("error")
            }.getOrDefault("No se pudo crear la cuenta")
            throw AuthException(mensaje)
        }

        return login(correo, contrasena)
    }
}

class AuthException(message: String) : IllegalStateException(message)
