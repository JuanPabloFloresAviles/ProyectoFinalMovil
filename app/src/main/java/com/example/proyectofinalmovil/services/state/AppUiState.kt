package com.example.proyectofinalmovil.services.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyectofinalmovil.services.mock.MockChatMessage
import com.example.proyectofinalmovil.services.mock.MockConcessionItem
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.MockMovieRecommendation
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockReview
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.MockUserProfile
import com.example.proyectofinalmovil.services.mock.generosFiltro
import com.example.proyectofinalmovil.services.mock.mockChatMessages
import com.example.proyectofinalmovil.services.mock.mockConcessions
import com.example.proyectofinalmovil.services.mock.mockIncomingRequestIds
import com.example.proyectofinalmovil.services.mock.mockInitialFriendIds
import com.example.proyectofinalmovil.services.mock.mockMovies
import com.example.proyectofinalmovil.services.mock.mockOutgoingRequestIds
import com.example.proyectofinalmovil.services.mock.mockPurchases
import com.example.proyectofinalmovil.services.mock.mockRecommendations
import com.example.proyectofinalmovil.services.mock.mockReviews
import com.example.proyectofinalmovil.services.mock.mockShowtimesByMovieId
import com.example.proyectofinalmovil.services.mock.mockSocialUsers
import com.example.proyectofinalmovil.services.mock.mockSynopsis
import com.example.proyectofinalmovil.services.mock.mockCast
import com.example.proyectofinalmovil.services.mock.mockUserProfile

private const val DEFAULT_PURCHASE_EMAIL = "invitado@cineuabcs.mx"

@Stable
class AppUiState {
    val movies: List<MockMovie> = mockMovies
    val showtimesByMovieId: Map<String, List<MockShowtime>> = mockShowtimesByMovieId
    val concessions: List<MockConcessionItem> = mockConcessions
    val userProfile: MockUserProfile = mockUserProfile
    val socialUsers: List<MockSocialUser> = mockSocialUsers
    val genres: List<String> = generosFiltro

    val purchases = mutableStateListOf<MockPurchase>().apply { addAll(mockPurchases) }
    val reviews = mutableStateListOf<MockReview>().apply { addAll(mockReviews) }
    val chatMessages = mutableStateListOf<MockChatMessage>().apply { addAll(mockChatMessages) }
    val recommendations = mutableStateListOf<MockMovieRecommendation>().apply { addAll(mockRecommendations) }

    val friendIds = mutableStateListOf<String>().apply { addAll(mockInitialFriendIds) }
    val incomingRequestIds = mutableStateListOf<String>().apply { addAll(mockIncomingRequestIds) }
    val outgoingRequestIds = mutableStateListOf<String>().apply { addAll(mockOutgoingRequestIds) }

    var selectedMovieId by mutableStateOf(defaultFeaturedMovieId())
    var selectedShowtimeId by mutableStateOf(defaultShowtimeId())
    var selectedChatFriendId by mutableStateOf(mockInitialFriendIds.first())
    var selectedRecommendationFriendId by mutableStateOf(mockInitialFriendIds.first())
    var activePurchaseFolio by mutableStateOf(initialActivePurchase()?.folio ?: mockPurchases.first().folio)

    val selectedSeatIds = mutableStateListOf<String>()
    val concessionQuantities = mutableStateMapOf<String, Int>()

    fun selectMovie(movieId: String) {
        selectedMovieId = movieId
        selectedShowtimeId = showtimesFor(movieId).firstOrNull()?.let { showtimeId(movieId, it) }
            ?: ""
        clearCheckoutDraft()
    }

    fun selectShowtime(showtimeId: String) {
        selectedShowtimeId = showtimeId
        clearCheckoutSeatsAndConcessions()
    }

    fun toggleSeat(seatId: String) {
        if (seatId in selectedSeatIds) {
            selectedSeatIds.remove(seatId)
        } else {
            selectedSeatIds.add(seatId)
        }
    }

    fun setConcessionQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            concessionQuantities.remove(itemId)
        } else {
            concessionQuantities[itemId] = quantity
        }
    }

    fun currentMovie(): MockMovie = movieById(selectedMovieId)

    fun currentShowtime(): MockShowtime = showtimeForCurrentSelection()

    fun selectedConcessionItems(): List<Pair<MockConcessionItem, Int>> {
        return concessions.mapNotNull { item ->
            val quantity = concessionQuantities[item.id] ?: 0
            if (quantity > 0) item to quantity else null
        }
    }

    fun ticketTotal(): Int = selectedSeatIds.size * currentShowtime().price

    fun concessionTotal(): Int = selectedConcessionItems().sumOf { (item, quantity) -> item.price * quantity }

    fun totalToPay(): Int = ticketTotal() + concessionTotal()

    fun checkoutSeatLabels(): List<String> = selectedSeatIds.sorted()

    fun confirmCheckout(): MockPurchase {
        val purchase = MockPurchase(
            folio = generatePurchaseFolio(),
            email = DEFAULT_PURCHASE_EMAIL,
            movieId = selectedMovieId,
            date = "Hoy",
            time = currentShowtime().time,
            room = currentShowtime().room,
            seats = checkoutSeatLabels(),
            status = "Activa",
            ticketTotal = ticketTotal(),
            concessionsTotal = concessionTotal(),
        )
        purchases.add(0, purchase)
        activePurchaseFolio = purchase.folio
        return purchase
    }

    fun activePurchase(): MockPurchase = purchases.firstOrNull { it.folio == activePurchaseFolio }
        ?: purchases.first()

    fun recoverPurchase(folio: String, email: String): MockPurchase? {
        return purchases.firstOrNull {
            it.folio.equals(folio.trim(), ignoreCase = true) &&
                it.email.equals(email.trim(), ignoreCase = true)
        }
    }

    fun movieForPurchase(purchase: MockPurchase): MockMovie {
        return movieById(purchase.movieId)
    }

    fun showtimesFor(movieId: String): List<MockShowtime> {
        return showtimesByMovieId[movieId] ?: showtimesByMovieId.values.first()
    }

    fun synopsisFor(movieId: String): String = mockSynopsis[movieId] ?: "Sinopsis no disponible."

    fun castFor(movieId: String): List<String> = mockCast[movieId] ?: emptyList()

    fun friendUsers(): List<MockSocialUser> = socialUsers.filter { it.id in friendIds }

    fun incomingUsers(): List<MockSocialUser> = socialUsers.filter { it.id in incomingRequestIds }

    fun outgoingUsers(): List<MockSocialUser> = socialUsers.filter { it.id in outgoingRequestIds }

    fun addFriendRequest(userId: String) {
        if (userId !in outgoingRequestIds && userId !in friendIds) {
            outgoingRequestIds.add(userId)
        }
    }

    fun acceptFriend(userId: String) {
        incomingRequestIds.remove(userId)
        if (userId !in friendIds) friendIds.add(userId)
    }

    fun rejectFriend(userId: String) {
        incomingRequestIds.remove(userId)
    }

    fun cancelFriendRequest(userId: String) {
        outgoingRequestIds.remove(userId)
    }

    fun sendChatMessage(friendId: String, text: String) {
        chatMessages.add(
            MockChatMessage(
                friendId = friendId,
                sender = "Tú",
                text = text,
                time = "Ahora",
                isMine = true,
            ),
        )
    }

    fun sendRecommendation(friendId: String, movieId: String, note: String) {
        recommendations.add(
            MockMovieRecommendation(
                id = "rec-${recommendations.size + 1}",
                friendId = friendId,
                movieId = movieId,
                note = note,
                date = "Ahora",
                isMine = true,
            ),
        )
    }

    private fun clearCheckoutDraft() {
        clearCheckoutSeatsAndConcessions()
    }

    private fun clearCheckoutSeatsAndConcessions() {
        selectedSeatIds.clear()
        concessionQuantities.clear()
    }

    private fun movieById(movieId: String): MockMovie {
        return movies.find { it.id == movieId } ?: movies.first()
    }

    private fun showtimeForCurrentSelection(): MockShowtime {
        val movieShowtimes = showtimesFor(selectedMovieId)
        return movieShowtimes.firstOrNull { showtimeId(selectedMovieId, it) == selectedShowtimeId }
            ?: movieShowtimes.first()
    }

    private fun defaultFeaturedMovieId(): String {
        return movies.firstOrNull { it.isFeatured }?.id ?: movies.first().id
    }

    private fun defaultShowtimeId(): String {
        val movieId = defaultFeaturedMovieId()
        return showtimesFor(movieId).firstOrNull()?.let { showtimeId(movieId, it) } ?: ""
    }

    private fun initialActivePurchase(): MockPurchase? {
        return purchases.firstOrNull { it.status == "Activa" } ?: purchases.firstOrNull()
    }

    private fun generatePurchaseFolio(): String {
        val nextNumber = purchases.size + 1
        return "CINE-2026-${nextNumber.toString().padStart(4, '0')}"
    }

    private fun showtimeId(movieId: String, showtime: MockShowtime): String {
        return "$movieId|${showtime.time}"
    }
}
