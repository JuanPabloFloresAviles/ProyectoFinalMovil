package com.example.proyectofinalmovil.services.api

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiException(
    val statusCode: Int,
    val responseBody: String,
) : IllegalStateException("Error $statusCode al consultar la API")

class CineUabcsApiClient(
    private val config: CineUabcsApiConfig = CineUabcsApiConfig(),
) {
    fun get(path: String, bearerToken: String? = null): String {
        val connection = URL(config.endpoint(path)).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        if (!bearerToken.isNullOrBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
            if (connection.responseCode !in 200..299) {
                throw ApiException(connection.responseCode, body)
            }
            body
        } finally {
            connection.disconnect()
        }
    }

    fun postJson(path: String, jsonBody: String, bearerToken: String? = null): String =
        sendWithBody("POST", path, jsonBody, bearerToken)

    fun deleteJson(path: String, jsonBody: String, bearerToken: String? = null): String =
        sendWithBody("DELETE", path, jsonBody, bearerToken)

    private fun sendWithBody(
        method: String,
        path: String,
        jsonBody: String,
        bearerToken: String? = null,
    ): String {
        val connection = URL(config.endpoint(path)).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        if (!bearerToken.isNullOrBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        }

        connection.outputStream.use { output ->
            output.write(jsonBody.toByteArray(Charsets.UTF_8))
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
            if (connection.responseCode !in 200..299) {
                throw ApiException(connection.responseCode, body)
            }
            body
        } finally {
            connection.disconnect()
        }
    }
}
