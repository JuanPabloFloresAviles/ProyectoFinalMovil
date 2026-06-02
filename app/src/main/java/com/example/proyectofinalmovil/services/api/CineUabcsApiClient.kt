package com.example.proyectofinalmovil.services.api

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CineUabcsApiClient(
    private val config: CineUabcsApiConfig = CineUabcsApiConfig(),
) {
    fun get(path: String): String {
        val connection = URL(config.endpoint(path)).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
            if (connection.responseCode !in 200..299) {
                error("Error ${connection.responseCode} al consultar ${config.endpoint(path)}: $body")
            }
            body
        } finally {
            connection.disconnect()
        }
    }

    fun postJson(path: String, jsonBody: String): String {
        val connection = URL(config.endpoint(path)).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

        connection.outputStream.use { output ->
            output.write(jsonBody.toByteArray(Charsets.UTF_8))
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
            if (connection.responseCode !in 200..299) {
                error("Error ${connection.responseCode} al enviar ${config.endpoint(path)}: $body")
            }
            body
        } finally {
            connection.disconnect()
        }
    }
}
