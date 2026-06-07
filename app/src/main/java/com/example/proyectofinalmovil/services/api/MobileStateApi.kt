package com.example.proyectofinalmovil.services.api

import androidx.compose.ui.graphics.Color
import com.example.proyectofinalmovil.services.mock.MockChatMessage
import com.example.proyectofinalmovil.services.mock.MockConcessionPackage
import com.example.proyectofinalmovil.services.mock.MockMovieRecommendation
import com.example.proyectofinalmovil.services.mock.MockPaymentMethod
import com.example.proyectofinalmovil.services.mock.MockPurchaseConcessionItem
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockReview
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.MockTicketPackage
import com.example.proyectofinalmovil.services.mock.MockUserProfile
import com.example.proyectofinalmovil.services.state.AdminDashboardMetrics
import com.example.proyectofinalmovil.services.state.AdminReportItem
import com.example.proyectofinalmovil.services.state.AdminSalesRange
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class MobileUserSnapshot(
    val profile: MockUserProfile,
    val socialUsers: List<MockSocialUser>,
    val purchases: List<MockPurchase>,
    val reviews: List<MockReview>,
    val chatMessages: List<MockChatMessage>,
    val recommendations: List<MockMovieRecommendation>,
    val friendIds: List<String>,
    val incomingRequestIds: List<String>,
    val outgoingRequestIds: List<String>,
)

class MobileStateApi(
    private val client: CineUabcsApiClient = CineUabcsApiClient(),
) {
    fun getUserState(token: String): MobileUserSnapshot {
        return parseUserState(JSONObject(client.get("mobile/user/state", token)))
    }

    fun getAdminMetrics(token: String, range: AdminSalesRange = AdminSalesRange.LAST_7_DAYS): AdminDashboardMetrics {
        val json = JSONObject(client.get("mobile/admin/metrics?rango=${range.apiValue}", token))
        return AdminDashboardMetrics(
            averagePurchase = json.optInt("averagePurchase", 0),
            ticketSales = json.optInt("ticketSales", 0),
            concessionSales = json.optInt("concessionSales", 0),
            transactions = json.optInt("transactions", 0),
            preferredRoomFormat = json.optString("preferredRoomFormat", "Sin datos"),
            preferredRoomOccupancyPercent = json.optInt("preferredRoomOccupancyPercent", 0),
            ticketsSold = json.optInt("ticketsSold", 0),
            roomOccupancyPercent = json.optInt("roomOccupancyPercent", 0),
            lowStockCount = json.optInt("lowStockCount", 0),
            topMovies = parseReportItems(json.optJSONArray("topMovies") ?: JSONArray()),
            topProducts = parseReportItems(json.optJSONArray("topProducts") ?: JSONArray()),
            salesSeries = parseReportItems(json.optJSONArray("salesSeries") ?: JSONArray()),
        )
    }

    fun checkout(
        token: String?,
        payload: MobileCheckoutPayload,
    ): MockPurchase {
        val body = JSONObject()
            .put("nombreComprador", payload.nombreComprador)
            .put("correoComprador", payload.correoComprador)
            .put("telefonoComprador", payload.telefonoComprador ?: "")
            .put("esInvitado", payload.esInvitado)
            .putNullable("funcionId", payload.funcionId)
            .put("seats", JSONArray(payload.seats))
            .put(
                "dulceria",
                JSONArray(
                    payload.dulceria.map { item ->
                        JSONObject()
                            .putNullable("productoId", item.productoId)
                            .putNullable("comboId", item.comboId)
                            .put("cantidad", item.cantidad)
                            .put("precioUnitario", item.precioUnitario)
                    },
                ),
            )
            .put(
                "pago",
                JSONObject()
                    .put("metodo", "tarjeta")
                    .put("last4", payload.paymentLast4)
                    .put("cvv", payload.cvv),
            )

        val json = JSONObject(client.postJson("mobile/compras", body.toString(), token))
        return parseCheckoutPurchase(json)
    }

    fun listPaymentMethods(token: String): List<MockPaymentMethod> {
        val json = JSONObject(client.get("mobile/perfil/metodos-pago", token))
        return parsePaymentMethods(json.optJSONArray("metodos") ?: JSONArray())
    }

    fun savePaymentMethod(
        token: String,
        titularTarjeta: String,
        numeroTarjeta: String,
        vencimientoTarjeta: String,
    ): List<MockPaymentMethod> {
        val body = JSONObject()
            .put("titularTarjeta", titularTarjeta)
            .put("numeroTarjeta", numeroTarjeta)
            .put("vencimientoTarjeta", vencimientoTarjeta)
        val json = JSONObject(client.postJson("mobile/perfil/metodos-pago", body.toString(), token))
        return parsePaymentMethods(json.optJSONArray("metodos") ?: JSONArray())
    }

    fun deletePaymentMethod(token: String, id: String): List<MockPaymentMethod> {
        val body = JSONObject().put("id", id.toIntOrNull() ?: 0)
        val json = JSONObject(client.deleteJson("mobile/perfil/metodos-pago", body.toString(), token))
        return parsePaymentMethods(json.optJSONArray("metodos") ?: JSONArray())
    }

    private fun parsePaymentMethods(json: JSONArray): List<MockPaymentMethod> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockPaymentMethod(
                        id = item.optString("id", ""),
                        last4 = item.optString("last4", ""),
                        holderName = item.optString("holderName", ""),
                        expiry = item.optString("expiry", ""),
                        isDefault = index == 0,
                    ),
                )
            }
        }
    }

    fun separarBoletos(
        token: String,
        folio: String,
        seats: List<String>,
    ): MockTicketPackage {
        val body = JSONObject()
            .put("folio", folio)
            .put("seats", JSONArray(seats))
        val json = JSONObject(client.postJson("mobile/compras/separar", body.toString(), token))
        val seatsJson = json.optJSONArray("seats") ?: JSONArray()
        return MockTicketPackage(
            id = json.optString("id", ""),
            label = json.optString("label", "Boletos separados"),
            seats = List(seatsJson.length()) { seatsJson.getString(it) },
            qrCode = json.optString("qrCode", ""),
        )
    }

    private fun parseUserState(json: JSONObject): MobileUserSnapshot {
        val profile = json.getJSONObject("profile")
        return MobileUserSnapshot(
            profile = MockUserProfile(
                name = profile.optString("name", ""),
                email = profile.optString("email", ""),
                phone = profile.optString("phone", ""),
                studentId = profile.optString("studentId", ""),
                favoriteGenre = profile.optString("favoriteGenre", "Cartelera"),
                memberSince = profile.optString("memberSince", ""),
                initials = profile.optString("initials", ""),
            ),
            socialUsers = parseSocialUsers(json.optJSONArray("socialUsers") ?: JSONArray()),
            purchases = parsePurchases(json.optJSONArray("purchases") ?: JSONArray()),
            reviews = parseReviews(json.optJSONArray("reviews") ?: JSONArray()),
            chatMessages = parseChatMessages(json.optJSONArray("chatMessages") ?: JSONArray()),
            recommendations = parseRecommendations(json.optJSONArray("recommendations") ?: JSONArray()),
            friendIds = parseStringList(json.optJSONArray("friendIds") ?: JSONArray()),
            incomingRequestIds = parseStringList(json.optJSONArray("incomingRequestIds") ?: JSONArray()),
            outgoingRequestIds = parseStringList(json.optJSONArray("outgoingRequestIds") ?: JSONArray()),
        )
    }

    private fun parseSocialUsers(json: JSONArray): List<MockSocialUser> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                val palette = avatarPalette(index)
                add(
                    MockSocialUser(
                        id = item.getString("id"),
                        name = item.optString("name", "Usuario"),
                        initials = item.optString("initials", "CU"),
                        career = item.optString("career", "Comunidad UABCS"),
                        favoriteGenre = item.optString("favoriteGenre", "Cartelera"),
                        bio = item.optString("bio", "Usuario de Cine UABCS"),
                        avatarStart = palette.first,
                        avatarEnd = palette.second,
                        isOnline = item.optBoolean("isOnline", false),
                        friendCode = item.optString("friendCode", ""),
                    ),
                )
            }
        }
    }

    private fun parsePurchases(json: JSONArray): List<MockPurchase> {
        return buildList {
            for (index in 0 until json.length()) {
                add(parseCheckoutPurchase(json.getJSONObject(index)))
            }
        }
    }

    private fun parseCheckoutPurchase(item: JSONObject): MockPurchase {
        val seats = item.optJSONArray("seats") ?: JSONArray()
        val concessionItems = parseConcessionItems(item.optJSONArray("concessionItems") ?: JSONArray())
        return MockPurchase(
            folio = item.optString("folio", ""),
            email = item.optString("email", ""),
            movieId = item.optString("movieId", ""),
            date = normalizePurchaseDate(item.opt("date")),
            time = normalizePurchaseTime(item.opt("time")),
            room = item.optString("room", ""),
            seats = List(seats.length()) { seats.getString(it) },
            status = item.optString("status", ""),
            ticketTotal = item.optInt("ticketTotal", 0),
            concessionsTotal = item.optInt("concessionsTotal", 0),
            qrCode = item.optString("qrCode", item.optString("folio", "")),
            qrExpiresAtMillis = if (item.has("qrExpiresAtMillis")) {
                item.optLong("qrExpiresAtMillis")
            } else {
                null
            },
            paymentMethodLabel = item.optString("paymentMethodLabel", "Tarjeta digital"),
            guestPurchase = item.optBoolean("guestPurchase", false),
            concessionItems = concessionItems,
            ticketPackages = parseTicketPackages(item.optJSONArray("ticketPackages") ?: JSONArray()),
            concessionPackages = parseConcessionPackages(
                item.optJSONArray("concessionPackages") ?: JSONArray(),
            ),
        )
    }

    private fun parseReviews(json: JSONArray): List<MockReview> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockReview(
                        id = item.getString("id"),
                        movieId = item.optString("movieId", ""),
                        author = item.optString("author", "Usuario"),
                        rating = item.optInt("rating", 0),
                        date = item.optString("date", ""),
                        comment = item.optString("comment", ""),
                        isMine = item.optBoolean("isMine", false),
                    ),
                )
            }
        }
    }

    private fun parseChatMessages(json: JSONArray): List<MockChatMessage> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockChatMessage(
                        friendId = item.optString("friendId", ""),
                        sender = item.optString("sender", "Usuario"),
                        text = item.optString("text", ""),
                        time = item.optString("time", ""),
                        isMine = item.optBoolean("isMine", false),
                    ),
                )
            }
        }
    }

    private fun parseRecommendations(json: JSONArray): List<MockMovieRecommendation> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockMovieRecommendation(
                        id = item.getString("id"),
                        friendId = item.optString("friendId", ""),
                        movieId = item.optString("movieId", ""),
                        note = item.optString("note", ""),
                        date = item.optString("date", ""),
                        isMine = item.optBoolean("isMine", false),
                    ),
                )
            }
        }
    }

    private fun parseStringList(json: JSONArray): List<String> {
        return List(json.length()) { index -> json.getString(index) }
    }

    private fun parseReportItems(json: JSONArray): List<AdminReportItem> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    AdminReportItem(
                        label = item.optString("label", ""),
                        value = item.optInt("value", 0),
                        detail = item.optString("detail", ""),
                        secondaryValue = if (item.has("secondaryValue")) item.optInt("secondaryValue", 0) else null,
                    ),
                )
            }
        }
    }

    private fun parseConcessionItems(json: JSONArray): List<MockPurchaseConcessionItem> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockPurchaseConcessionItem(
                        id = item.optString("id", ""),
                        name = item.optString("name", item.optString("nombre", "Producto")),
                        quantity = item.optInt("quantity", item.optInt("cantidad", 0)),
                        type = item.optString("type", "producto"),
                    ),
                )
            }
        }
    }

    private fun parseTicketPackages(json: JSONArray): List<MockTicketPackage> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                val seats = item.optJSONArray("seats") ?: JSONArray()
                add(
                    MockTicketPackage(
                        id = item.optString("id", ""),
                        label = item.optString("label", "Boletos"),
                        seats = List(seats.length()) { seats.getString(it) },
                        qrCode = item.optString("qrCode", ""),
                    ),
                )
            }
        }
    }

    private fun parseConcessionPackages(json: JSONArray): List<MockConcessionPackage> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                add(
                    MockConcessionPackage(
                        id = item.optString("id", ""),
                        label = item.optString("label", "Dulcería"),
                        items = parseConcessionItems(item.optJSONArray("items") ?: JSONArray()),
                        qrCode = item.optString("qrCode", ""),
                    ),
                )
            }
        }
    }
}

data class MobileCheckoutPayload(
    val nombreComprador: String,
    val correoComprador: String,
    val telefonoComprador: String?,
    val esInvitado: Boolean,
    val funcionId: Int?,
    val seats: List<String>,
    val dulceria: List<MobileCheckoutConcessionItem>,
    val paymentLast4: String,
    val cvv: String,
)

data class MobileCheckoutConcessionItem(
    val productoId: Int?,
    val comboId: Int?,
    val cantidad: Int,
    val precioUnitario: Int,
)

private val mobilePurchaseZone: ZoneId = ZoneId.of("America/Mazatlan")
private val mobilePurchaseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
private val mobilePurchaseTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun normalizePurchaseDate(value: Any?): String {
    val raw = value?.toString().orEmpty()
    if (raw.isBlank()) return ""
    return runCatching {
        Instant.parse(raw)
            .atZone(mobilePurchaseZone)
            .format(mobilePurchaseDateFormatter)
    }.getOrElse { raw }
}

private fun normalizePurchaseTime(value: Any?): String {
    val raw = value?.toString().orEmpty()
    if (raw.isBlank()) return ""
    return runCatching {
        Instant.parse(raw)
            .atZone(mobilePurchaseZone)
            .format(mobilePurchaseTimeFormatter)
    }.getOrElse { raw }
}

private fun JSONObject.putNullable(name: String, value: Int?): JSONObject {
    return if (value == null) put(name, JSONObject.NULL) else put(name, value)
}

private fun avatarPalette(index: Int): Pair<Color, Color> {
    val palettes = listOf(
        Color(0xFFD8454A) to Color(0xFF781820),
        Color(0xFF3A6A8C) to Color(0xFF1A3A5A),
        Color(0xFF4AB07A) to Color(0xFF1F5A3D),
        Color(0xFFFFA84A) to Color(0xFFC06A12),
        Color(0xFF8C5AD8) to Color(0xFF3F236F),
    )
    return palettes[index % palettes.size]
}
