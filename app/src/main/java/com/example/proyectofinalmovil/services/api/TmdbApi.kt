package com.example.proyectofinalmovil.services.api

import com.example.proyectofinalmovil.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3"
private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

data class TmdbMovieCandidate(
    val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseYear: String,
    val voteAverage: Double,
    val adult: Boolean,
)

data class TmdbMovieDetail(
    val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseYear: String,
    val voteAverage: Double,
    val adult: Boolean,
    val runtimeMinutes: Int,
)

class TmdbApi(
    private val apiKey: String = BuildConfig.TMDB_API_KEY,
) {
    fun isConfigured(): Boolean = apiKey.isNotBlank()

    fun getNowPlaying(): List<TmdbMovieCandidate> {
        if (!isConfigured()) return emptyList()
        val nowPlaying = getMovieList("/movie/now_playing")
        val upcoming = getMovieList("/movie/upcoming")
        return (nowPlaying + upcoming)
            .distinctBy { it.tmdbId }
            .take(20)
    }

    fun searchMovies(query: String): List<TmdbMovieCandidate> {
        if (!isConfigured() || query.isBlank()) return getNowPlaying()
        return getMovieList("/search/movie", mapOf("query" to query.trim())).take(20)
    }

    fun getMovieDetail(tmdbId: Int): TmdbMovieDetail {
        val json = JSONObject(get("/movie/$tmdbId"))
        return TmdbMovieDetail(
            tmdbId = json.getInt("id"),
            title = json.optString("title", "Película"),
            overview = json.optString("overview", "").ifBlank { "Sinopsis disponible próximamente." },
            posterUrl = posterUrl(json.optString("poster_path", "")),
            releaseYear = releaseYear(json.optString("release_date", "")),
            voteAverage = json.optDouble("vote_average", 0.0),
            adult = json.optBoolean("adult", false),
            runtimeMinutes = json.optInt("runtime", 120).takeIf { it > 0 } ?: 120,
        )
    }

    private fun getMovieList(path: String, params: Map<String, String> = emptyMap()): List<TmdbMovieCandidate> {
        val results = JSONObject(get(path, params)).optJSONArray("results") ?: JSONArray()
        return buildList {
            for (index in 0 until results.length()) {
                val item = results.getJSONObject(index)
                add(
                    TmdbMovieCandidate(
                        tmdbId = item.getInt("id"),
                        title = item.optString("title", "Película"),
                        overview = item.optString("overview", "").ifBlank {
                            "Sinopsis disponible próximamente."
                        },
                        posterUrl = posterUrl(item.optString("poster_path", "")),
                        releaseYear = releaseYear(item.optString("release_date", "")),
                        voteAverage = item.optDouble("vote_average", 0.0),
                        adult = item.optBoolean("adult", false),
                    ),
                )
            }
        }
    }

    private fun get(path: String, params: Map<String, String> = emptyMap()): String {
        val query = buildMap {
            put("api_key", apiKey)
            put("language", "es-MX")
            putAll(params)
        }.entries.joinToString("&") { (key, value) ->
            "${key.encodeUrl()}=${value.encodeUrl()}"
        }
        val connection = URL("$TMDB_BASE_URL$path?$query").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
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

private fun posterUrl(path: String?): String? {
    val safePath = path?.takeIf { it.isNotBlank() && it != "null" } ?: return null
    return "$TMDB_IMAGE_BASE_URL$safePath"
}

private fun releaseYear(date: String): String {
    return date.takeIf { it.length >= 4 }?.take(4) ?: "2026"
}

private fun String.encodeUrl(): String = URLEncoder.encode(this, Charsets.UTF_8.name())
