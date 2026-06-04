package com.example.proyectofinalmovil.services.api

import androidx.compose.ui.graphics.Color
import com.example.proyectofinalmovil.services.mock.MockConcessionItem
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.state.AdminConcessionCombo
import org.json.JSONArray
import org.json.JSONObject

data class CatalogSnapshot(
    val movies: List<MockMovie>,
    val showtimesByMovieId: Map<String, List<MockShowtime>>,
    val concessions: List<MockConcessionItem>,
    val combos: List<AdminConcessionCombo>,
    val rooms: List<AdminRoomOption>,
)

data class AdminRoomOption(
    val id: String,
    val name: String,
    val capacity: Int,
)

class CatalogApi(
    private val client: CineUabcsApiClient = CineUabcsApiClient(),
) {
    fun getCatalog(): CatalogSnapshot {
        return parseCatalog(JSONObject(client.get("mobile/catalog")))
    }

    fun saveMovie(
        token: String,
        id: String?,
        title: String,
        synopsis: String,
        classification: String,
        durationMinutes: Int,
    ): CatalogSnapshot {
        val body = JSONObject()
            .putNullable("id", id)
            .put("titulo", title)
            .put("sinopsis", synopsis)
            .put("clasificacion", classification)
            .put("duracionMin", durationMinutes)
        return parseCatalog(JSONObject(client.postJson("mobile/admin/peliculas", body.toString(), token)))
    }

    fun saveShowtime(
        token: String,
        id: String?,
        movieId: String,
        roomId: String,
        startsAt: String,
        price: Int,
    ): CatalogSnapshot {
        val body = JSONObject()
            .putNullable("id", id)
            .put("peliculaId", movieId)
            .put("salaId", roomId)
            .put("fechaHora", startsAt)
            .put("precioBase", price)
        return parseCatalog(JSONObject(client.postJson("mobile/admin/funciones", body.toString(), token)))
    }

    fun saveProduct(
        token: String,
        id: String?,
        name: String,
        category: String,
        price: Int,
        stock: Int,
    ): CatalogSnapshot {
        val body = JSONObject()
            .put("type", "producto")
            .putNullable("id", id)
            .put("nombre", name)
            .put("categoria", category)
            .put("precio", price)
            .put("stock", stock)
        return parseCatalog(JSONObject(client.postJson("mobile/admin/dulceria", body.toString(), token)))
    }

    fun saveCombo(
        token: String,
        id: String?,
        name: String,
        price: Int,
        productIds: List<String>,
    ): CatalogSnapshot {
        val body = JSONObject()
            .put("type", "combo")
            .putNullable("id", id)
            .put("nombre", name)
            .put("precio", price)
            .put("productoIds", JSONArray(productIds))
        return parseCatalog(JSONObject(client.postJson("mobile/admin/dulceria", body.toString(), token)))
    }

    private fun parseCatalog(json: JSONObject): CatalogSnapshot {
        val moviesJson = json.getJSONArray("peliculas")
        val movies = mutableListOf<MockMovie>()
        val showtimesByMovieId = mutableMapOf<String, List<MockShowtime>>()

        for (index in 0 until moviesJson.length()) {
            val movieJson = moviesJson.getJSONObject(index)
            val movieId = movieJson.getString("id")
            val functionsJson = movieJson.optJSONArray("funciones") ?: JSONArray()
            movies.add(
                MockMovie(
                    id = movieId,
                    title = movieJson.getString("titulo"),
                    genre = movieJson.optString("genero", "Cartelera"),
                    classification = movieJson.optString("clasificacion", "A"),
                    duration = "${movieJson.optInt("duracionMin", 90)} min",
                    rating = movieJson.optString("rating", "4.0"),
                    year = movieJson.optString("anio", "2026"),
                    accentStart = Color(0xFF1E5AA8),
                    accentEnd = Color(0xFF102A43),
                    isFeatured = movieJson.optBoolean("destacada", index == 0),
                    isNew = movieJson.optBoolean("estreno", true),
                    synopsis = movieJson.optString("sinopsis", ""),
                ),
            )
            showtimesByMovieId[movieId] = parseShowtimes(functionsJson)
        }

        return CatalogSnapshot(
            movies = movies,
            showtimesByMovieId = showtimesByMovieId,
            concessions = parseProducts(json.optJSONArray("productos") ?: JSONArray()),
            combos = parseCombos(json.optJSONArray("combos") ?: JSONArray()),
            rooms = parseRooms(json.optJSONArray("salas") ?: JSONArray()),
        )
    }

    private fun parseShowtimes(json: JSONArray): List<MockShowtime> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockShowtime(
                        time = item.optString("fechaHora").takeIf { it.isNotBlank() }?.substring(11, 16)
                            ?: "18:00",
                        room = item.optString("sala", "Sala"),
                        roomType = item.optString("tipoSala", "Tradicional"),
                        format = item.optString("formato", "2D · Dob."),
                        price = item.optInt("precioBase", 0),
                        availableSeats = item.optInt("butacasDisponibles", 0),
                        id = item.optString("id"),
                        movieId = item.optString("peliculaId"),
                        roomId = item.optString("salaId"),
                        startsAt = item.optString("fechaHora"),
                    ),
                )
            }
        }
    }

    private fun parseProducts(json: JSONArray): List<MockConcessionItem> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockConcessionItem(
                        id = item.getString("id"),
                        name = item.getString("nombre"),
                        description = item.optString("descripcion", item.optString("categoria", "Dulcería")),
                        cost = 0,
                        price = item.optInt("precio", 0),
                        stock = item.optInt("stock", 0),
                    ),
                )
            }
        }
    }

    private fun parseCombos(json: JSONArray): List<AdminConcessionCombo> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                val ids = item.optJSONArray("productoIds") ?: JSONArray()
                add(
                    AdminConcessionCombo(
                        id = item.getString("id"),
                        name = item.getString("nombre"),
                        description = item.optString("descripcion", "Combo"),
                        price = item.optInt("precio", 0),
                        productIds = List(ids.length()) { ids.getString(it) },
                    ),
                )
            }
        }
    }

    private fun parseRooms(json: JSONArray): List<AdminRoomOption> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    AdminRoomOption(
                        id = item.getString("id"),
                        name = item.getString("nombre"),
                        capacity = item.optInt("capacidad", 0),
                    ),
                )
            }
        }
    }
}

private fun JSONObject.putNullable(name: String, value: String?): JSONObject {
    return if (value.isNullOrBlank()) put(name, JSONObject.NULL) else put(name, value)
}
