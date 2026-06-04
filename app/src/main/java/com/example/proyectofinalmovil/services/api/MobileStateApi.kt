package com.example.proyectofinalmovil.services.api

import androidx.compose.ui.graphics.Color
import com.example.proyectofinalmovil.services.mock.MockChatMessage
import com.example.proyectofinalmovil.services.mock.MockMovieRecommendation
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockReview
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.MockUserProfile
import com.example.proyectofinalmovil.services.state.AdminDashboardMetrics
import org.json.JSONArray
import org.json.JSONObject

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

    fun getAdminMetrics(token: String): AdminDashboardMetrics {
        val json = JSONObject(client.get("mobile/admin/metrics", token))
        return AdminDashboardMetrics(
            averagePurchase = json.optInt("averagePurchase", 0),
            ticketSales = json.optInt("ticketSales", 0),
            concessionSales = json.optInt("concessionSales", 0),
            transactions = json.optInt("transactions", 0),
            preferredRoomFormat = json.optString("preferredRoomFormat", "Sin datos"),
            preferredRoomOccupancyPercent = json.optInt("preferredRoomOccupancyPercent", 0),
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
                    ),
                )
            }
        }
    }

    private fun parsePurchases(json: JSONArray): List<MockPurchase> {
        return buildList {
            for (index in 0 until json.length()) {
                val item = json.getJSONObject(index)
                val seats = item.optJSONArray("seats") ?: JSONArray()
                add(
                    MockPurchase(
                        folio = item.optString("folio", ""),
                        email = item.optString("email", ""),
                        movieId = item.optString("movieId", ""),
                        date = item.optString("date", ""),
                        time = item.optString("time", ""),
                        room = item.optString("room", ""),
                        seats = List(seats.length()) { seats.getString(it) },
                        status = item.optString("status", ""),
                        ticketTotal = item.optInt("ticketTotal", 0),
                        concessionsTotal = item.optInt("concessionsTotal", 0),
                    ),
                )
            }
        }
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
