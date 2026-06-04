package com.example.proyectofinalmovil.services.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.proyectofinalmovil.services.api.AuthSession
import com.example.proyectofinalmovil.services.api.CatalogSnapshot
import com.example.proyectofinalmovil.services.api.AdminRoomOption
import com.example.proyectofinalmovil.services.api.MobileUserSnapshot
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
import com.example.proyectofinalmovil.services.mock.mockSynopsis
import com.example.proyectofinalmovil.services.mock.mockCast

private const val DEFAULT_PURCHASE_EMAIL = "invitado@cineuabcs.mx"

data class AdminConcessionCombo(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val productIds: List<String>,
)

@Stable
class AppUiState {
    val movies = mutableStateListOf<MockMovie>()
    val showtimesByMovieId = mutableStateMapOf<String, List<MockShowtime>>()
    val concessions = mutableStateListOf<MockConcessionItem>()
    val concessionCombos = mutableStateListOf<AdminConcessionCombo>()
    val adminRooms = mutableStateListOf<AdminRoomOption>()
    var userProfile by mutableStateOf(emptyUserProfile())
        private set
    val socialUsers = mutableStateListOf<MockSocialUser>()
    val genres: List<String> = generosFiltro

    val purchases = mutableStateListOf<MockPurchase>()
    val reviews = mutableStateListOf<MockReview>()
    val chatMessages = mutableStateListOf<MockChatMessage>()
    val recommendations = mutableStateListOf<MockMovieRecommendation>()

    val friendIds = mutableStateListOf<String>()
    val incomingRequestIds = mutableStateListOf<String>()
    val outgoingRequestIds = mutableStateListOf<String>()

    var selectedMovieId by mutableStateOf("")
    var selectedShowtimeId by mutableStateOf("")
    var selectedChatFriendId by mutableStateOf("")
    var selectedRecommendationFriendId by mutableStateOf("")
    var activePurchaseFolio by mutableStateOf("")
    var signedInEmail by mutableStateOf("")
        private set
    var signedInName by mutableStateOf("")
        private set
    var authToken by mutableStateOf("")
        private set
    var userRole by mutableStateOf(UserRole.CLIENT)
        private set
    private var adminMetrics by mutableStateOf<AdminDashboardMetrics?>(null)

    val selectedSeatIds = mutableStateListOf<String>()
    val concessionQuantities = mutableStateMapOf<String, Int>()

    fun signIn(session: AuthSession) {
        signedInEmail = session.email
        signedInName = session.name
        authToken = session.token
        userRole = userRoleFromApi(session.role)
    }

    fun isAdmin(): Boolean = userRole == UserRole.ADMIN

    fun adminDashboardMetrics(): AdminDashboardMetrics {
        return adminMetrics ?: calculateAdminDashboardMetrics(
            purchases = purchases,
            showtimesByMovieId = showtimesByMovieId,
        )
    }

    fun replaceAdminMetrics(metrics: AdminDashboardMetrics) {
        adminMetrics = metrics
    }

    fun replaceCatalog(snapshot: CatalogSnapshot) {
        if (snapshot.movies.isNotEmpty()) {
            movies.clear()
            movies.addAll(snapshot.movies)
            selectedMovieId = defaultFeaturedMovieId()
        }
        showtimesByMovieId.clear()
        showtimesByMovieId.putAll(snapshot.showtimesByMovieId)
        selectedShowtimeId = defaultShowtimeId()
        concessions.clear()
        concessions.addAll(snapshot.concessions)
        concessionCombos.clear()
        concessionCombos.addAll(snapshot.combos)
        adminRooms.clear()
        adminRooms.addAll(snapshot.rooms)
        concessionQuantities.clear()
    }

    fun replaceUserState(snapshot: MobileUserSnapshot) {
        userProfile = snapshot.profile
        socialUsers.clear()
        socialUsers.addAll(snapshot.socialUsers)
        purchases.clear()
        purchases.addAll(snapshot.purchases)
        reviews.clear()
        reviews.addAll(snapshot.reviews)
        chatMessages.clear()
        chatMessages.addAll(snapshot.chatMessages)
        recommendations.clear()
        recommendations.addAll(snapshot.recommendations)
        friendIds.clear()
        friendIds.addAll(snapshot.friendIds)
        incomingRequestIds.clear()
        incomingRequestIds.addAll(snapshot.incomingRequestIds)
        outgoingRequestIds.clear()
        outgoingRequestIds.addAll(snapshot.outgoingRequestIds)
        selectedChatFriendId = friendIds.firstOrNull() ?: ""
        selectedRecommendationFriendId = friendIds.firstOrNull() ?: ""
        activePurchaseFolio = initialActivePurchase()?.folio ?: ""
    }

    fun upsertMovie(
        id: String?,
        title: String,
        genre: String,
        classification: String,
        duration: String,
        rating: String,
        year: String,
    ) {
        val safeId = id ?: uniqueIdFrom(title, movies.map { it.id })
        val existingIndex = movies.indexOfFirst { it.id == safeId }
        val current = movies.getOrNull(existingIndex)
        val movie = MockMovie(
            id = safeId,
            title = title.trim(),
            genre = genre.trim(),
            classification = classification.trim().ifBlank { "A" },
            duration = duration.trim().ifBlank { "90 min" },
            rating = rating.trim().ifBlank { "4.0" },
            year = year.trim().ifBlank { "2026" },
            accentStart = current?.accentStart ?: Color(0xFF1E5AA8),
            accentEnd = current?.accentEnd ?: Color(0xFF102A43),
            isFeatured = current?.isFeatured ?: false,
            isNew = current?.isNew ?: true,
        )
        if (existingIndex >= 0) {
            movies[existingIndex] = movie
        } else {
            movies.add(0, movie)
            showtimesByMovieId[safeId] = listOf(defaultAdminShowtime())
        }
    }

    fun upsertShowtime(movieId: String, index: Int?, showtime: MockShowtime) {
        val showtimes = showtimesFor(movieId).toMutableList()
        if (index != null && index in showtimes.indices) {
            showtimes[index] = showtime
        } else {
            showtimes.add(showtime)
        }
        showtimesByMovieId[movieId] = showtimes
    }

    fun upsertConcessionItem(
        id: String?,
        name: String,
        description: String,
        cost: Int,
        price: Int,
    ) {
        val safeId = id ?: uniqueIdFrom(name, concessions.map { it.id })
        val item = MockConcessionItem(
            id = safeId,
            name = name.trim(),
            description = description.trim(),
            cost = cost.coerceAtLeast(0),
            price = price.coerceAtLeast(0),
        )
        val index = concessions.indexOfFirst { it.id == safeId }
        if (index >= 0) concessions[index] = item else concessions.add(0, item)
    }

    fun upsertConcessionCombo(
        id: String?,
        name: String,
        description: String,
        price: Int,
        productIds: List<String>,
    ) {
        val safeId = id ?: uniqueIdFrom(name, concessionCombos.map { it.id })
        val combo = AdminConcessionCombo(
            id = safeId,
            name = name.trim(),
            description = description.trim(),
            price = price.coerceAtLeast(0),
            productIds = productIds,
        )
        val index = concessionCombos.indexOfFirst { it.id == safeId }
        if (index >= 0) concessionCombos[index] = combo else concessionCombos.add(0, combo)
    }

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
        ?: purchases.firstOrNull()
        ?: emptyPurchase()

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
        return showtimesByMovieId[movieId] ?: emptyList()
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
        return movies.find { it.id == movieId } ?: fallbackMovie()
    }

    private fun showtimeForCurrentSelection(): MockShowtime {
        val movieShowtimes = showtimesFor(selectedMovieId)
        return movieShowtimes.firstOrNull { showtimeId(selectedMovieId, it) == selectedShowtimeId }
            ?: movieShowtimes.firstOrNull()
            ?: defaultAdminShowtime()
    }

    private fun defaultFeaturedMovieId(): String {
        return movies.firstOrNull { it.isFeatured }?.id ?: movies.firstOrNull()?.id ?: ""
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

    private fun uniqueIdFrom(value: String, existingIds: List<String>): String {
        val base = value
            .trim()
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifBlank { "nuevo-registro" }
        var candidate = base
        var suffix = 2
        while (candidate in existingIds) {
            candidate = "$base-$suffix"
            suffix += 1
        }
        return candidate
    }

    private fun defaultAdminShowtime(): MockShowtime {
        return MockShowtime(
            time = "18:00",
            room = "Sala 1",
            roomType = "Tradicional",
            format = "2D · Dob.",
            price = 55,
            availableSeats = 80,
        )
    }

    private fun fallbackMovie(): MockMovie {
        return MockMovie(
            id = "sin-catalogo",
            title = "Catálogo no disponible",
            genre = "Cartelera",
            classification = "A",
            duration = "0 min",
            rating = "0.0",
            accentStart = Color(0xFF1E5AA8),
            accentEnd = Color(0xFF102A43),
        )
    }
}

private fun emptyUserProfile(): MockUserProfile {
    return MockUserProfile(
        name = "",
        email = "",
        phone = "",
        studentId = "",
        favoriteGenre = "Cartelera",
        memberSince = "",
        initials = "CU",
    )
}

private fun emptyPurchase(): MockPurchase {
    return MockPurchase(
        folio = "",
        email = "",
        movieId = "",
        date = "",
        time = "",
        room = "",
        seats = emptyList(),
        status = "",
        ticketTotal = 0,
        concessionsTotal = 0,
    )
}
