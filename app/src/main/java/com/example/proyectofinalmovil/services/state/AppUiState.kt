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
import com.example.proyectofinalmovil.services.mock.MockPaymentMethod
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockPurchaseConcessionItem
import com.example.proyectofinalmovil.services.mock.MockReview
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.MockUserProfile
import com.example.proyectofinalmovil.services.tickets.TicketQrVisibility
import com.example.proyectofinalmovil.services.mock.mockSynopsis
import com.example.proyectofinalmovil.services.mock.mockCast
import com.example.proyectofinalmovil.services.mock.mockPaymentMethods
import com.example.proyectofinalmovil.services.mock.MockConcessionPackage
import com.example.proyectofinalmovil.services.mock.MockTicketPackage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    val paymentMethods = mutableStateListOf<MockPaymentMethod>()
    val adminRooms = mutableStateListOf<AdminRoomOption>()
    var userProfile by mutableStateOf(emptyUserProfile())
        private set
    val socialUsers = mutableStateListOf<MockSocialUser>()

    /**
     * Chips de filtro de cartelera. "Todo" y "Estrenos" son fijos; el resto se
     * derivan de los géneros reales presentes en el catálogo (provienen de la
     * columna `genero` de la BD / TMDB). Se excluye el placeholder "Cartelera"
     * de las películas sin género para no mostrar un chip vacío.
     */
    val genres: List<String>
        get() = buildList {
            add("Todo")
            add("Estrenos")
            movies.asSequence()
                .map { it.genre.trim() }
                .filter { it.isNotEmpty() && !it.equals("Cartelera", ignoreCase = true) }
                .distinct()
                .sorted()
                .forEach { add(it) }
        }

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
    var selectedAdminShowtimeMovieId by mutableStateOf("")
    var selectedAdminShowtimeId by mutableStateOf("")
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
    val comboQuantities = mutableStateMapOf<String, Int>()
    var selectedPaymentMethodId by mutableStateOf("")
    var guestCheckoutEmail by mutableStateOf(DEFAULT_PURCHASE_EMAIL)

    fun signIn(session: AuthSession) {
        signedInEmail = session.email
        signedInName = session.name
        authToken = session.token
        userRole = userRoleFromApi(session.role)
        if (guestCheckoutEmail == DEFAULT_PURCHASE_EMAIL) {
            guestCheckoutEmail = session.email
        }
    }

    fun signOut() {
        signedInEmail = ""
        signedInName = ""
        authToken = ""
        userRole = UserRole.CLIENT
        userProfile = emptyUserProfile()
        purchases.clear()
        reviews.clear()
        chatMessages.clear()
        recommendations.clear()
        socialUsers.clear()
        friendIds.clear()
        incomingRequestIds.clear()
        outgoingRequestIds.clear()
        selectedChatFriendId = ""
        selectedRecommendationFriendId = ""
        selectedAdminShowtimeMovieId = ""
        selectedAdminShowtimeId = ""
        activePurchaseFolio = ""
        selectedSeatIds.clear()
        concessionQuantities.clear()
        comboQuantities.clear()
        selectedPaymentMethodId = ""
        guestCheckoutEmail = DEFAULT_PURCHASE_EMAIL
        adminMetrics = null
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
        if (paymentMethods.isEmpty()) {
            paymentMethods.clear()
            paymentMethods.addAll(mockPaymentMethods)
            selectedPaymentMethodId = paymentMethods.firstOrNull { it.isDefault }?.id
                ?: paymentMethods.firstOrNull()?.id
                ?: ""
        }
        adminRooms.clear()
        adminRooms.addAll(snapshot.rooms)
        concessionQuantities.clear()
        comboQuantities.clear()
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
        if (seatId in occupiedSeatsForCurrentShowtime()) return
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

    fun setComboQuantity(comboId: String, quantity: Int) {
        if (quantity <= 0) {
            comboQuantities.remove(comboId)
        } else {
            comboQuantities[comboId] = quantity
        }
    }

    fun setSelectedPaymentMethod(methodId: String) {
        selectedPaymentMethodId = methodId
    }

    fun setDefaultPaymentMethod(methodId: String) {
        val updated = paymentMethods.map { it.copy(isDefault = it.id == methodId) }
        paymentMethods.clear()
        paymentMethods.addAll(updated)
        selectedPaymentMethodId = methodId
    }

    fun removePaymentMethod(methodId: String) {
        paymentMethods.removeAll { it.id == methodId }
        if (selectedPaymentMethodId == methodId) {
            selectedPaymentMethodId = paymentMethods.firstOrNull { it.isDefault }?.id
                ?: paymentMethods.firstOrNull()?.id
                ?: ""
        }
    }

    fun addPaymentMethod(
        last4: String,
        holderName: String,
        expiry: String,
    ) {
        val cleanLast4 = last4.takeLast(4)
        val method = MockPaymentMethod(
            id = "pm-${paymentMethods.size + 1}-${cleanLast4}",
            last4 = cleanLast4,
            holderName = holderName.trim().ifBlank { signedInName.ifBlank { "Titular" } },
            expiry = expiry.trim(),
            isDefault = paymentMethods.isEmpty(),
        )
        paymentMethods.add(0, method)
        selectedPaymentMethodId = method.id
    }

    /** Reemplaza las tarjetas con las persistidas en el backend (TiDB). */
    fun replacePaymentMethods(methods: List<MockPaymentMethod>) {
        val previousSelected = selectedPaymentMethodId
        paymentMethods.clear()
        val normalized = methods.mapIndexed { index, method ->
            method.copy(isDefault = index == 0)
        }
        paymentMethods.addAll(normalized)
        selectedPaymentMethodId = when {
            normalized.any { it.id == previousSelected } -> previousSelected
            else -> normalized.firstOrNull()?.id ?: ""
        }
    }

    fun updateGuestCheckoutEmail(email: String) {
        guestCheckoutEmail = email
    }

    fun currentMovie(): MockMovie = movieById(selectedMovieId)

    fun currentShowtime(): MockShowtime = showtimeForCurrentSelection()

    fun currentShowtimeDateLabel(): String {
        return showtimeDateLabel(currentShowtime().startsAt)
    }

    fun currentShowtimeNumericId(): Int? = currentShowtime().id?.toIntOrNull()

    fun occupiedSeatsForCurrentShowtime(): Set<String> {
        val currentShowtime = currentShowtime()
        val fromShowtime = currentShowtime.takenSeats.toSet()
        val movieId = selectedMovieId
        val currentDate = currentShowtimeDateLabel()
        val fromPurchases = if (movieId.isBlank()) emptySet() else purchases
            .asSequence()
            .filter { purchase ->
                purchase.movieId == movieId &&
                    purchase.room == currentShowtime.room &&
                    purchase.time == currentShowtime.time &&
                    purchase.date == currentDate &&
                    purchase.status != "Cancelada"
            }
            .flatMap { it.seats.asSequence() }
            .toSet()
        return fromShowtime + fromPurchases
    }

    fun selectedConcessionItems(): List<Pair<MockConcessionItem, Int>> {
        return concessions.mapNotNull { item ->
            val quantity = concessionQuantities[item.id] ?: 0
            if (quantity > 0) item to quantity else null
        }
    }

    fun selectedComboItems(): List<Pair<AdminConcessionCombo, Int>> {
        return concessionCombos.mapNotNull { combo ->
            val quantity = comboQuantities[combo.id] ?: 0
            if (quantity > 0) combo to quantity else null
        }
    }

    fun ticketTotal(): Int = selectedSeatIds.size * currentShowtime().price

    fun concessionTotal(): Int =
        selectedConcessionItems().sumOf { (item, quantity) -> item.price * quantity } +
            selectedComboItems().sumOf { (combo, quantity) -> combo.price * quantity }

    fun totalToPay(): Int = ticketTotal() + concessionTotal()

    fun checkoutSeatLabels(): List<String> = selectedSeatIds.sorted()

    fun confirmCheckout(): MockPurchase {
        val folio = generatePurchaseFolio()
        val ticketPackage = MockTicketPackage(
            id = "ticket-$folio",
            label = "Boletos de sala",
            seats = checkoutSeatLabels(),
            qrCode = "TICKETS-$folio",
        )
        val snacks = purchaseConcessionItems()
        val snackPackages = if (snacks.isEmpty()) {
            emptyList()
        } else {
            listOf(
                MockConcessionPackage(
                    id = "snacks-$folio",
                    label = "Recolectar en dulcería",
                    items = snacks,
                    qrCode = "SNACKS-$folio",
                ),
            )
        }
        val paymentLabel = paymentMethods.firstOrNull { it.id == selectedPaymentMethodId }?.let {
            "Tarjeta • ${it.last4}"
        } ?: "Invitado · folio + correo"
        val email = if (signedInEmail.isNotBlank()) signedInEmail else guestCheckoutEmail.ifBlank { DEFAULT_PURCHASE_EMAIL }
        val purchase = MockPurchase(
            folio = folio,
            email = email,
            movieId = selectedMovieId,
            date = currentShowtimeDateLabel(),
            time = currentShowtime().time,
            room = currentShowtime().room,
            seats = checkoutSeatLabels(),
            status = "Activa",
            ticketTotal = ticketTotal(),
            concessionsTotal = concessionTotal(),
            qrCode = folio,
            paymentMethodLabel = paymentLabel,
            guestPurchase = signedInEmail.isBlank(),
            concessionItems = snacks,
            ticketPackages = listOf(ticketPackage),
            concessionPackages = snackPackages,
        )
        purchases.add(0, purchase)
        reserveSeatsForCurrentShowtime(purchase.seats.size)
        activePurchaseFolio = purchase.folio
        clearCheckoutDraft()
        return purchase
    }

    fun activePurchase(): MockPurchase = purchases.firstOrNull { it.folio == activePurchaseFolio }
        ?: purchases.firstOrNull()
        ?: emptyPurchase()

    fun registerCompletedPurchase(purchase: MockPurchase) {
        purchases.removeAll { it.folio == purchase.folio }
        purchases.add(0, purchase)
        activePurchaseFolio = purchase.folio
        reserveSeatsForCurrentShowtime(purchase.seats.size)
        clearCheckoutDraft()
    }

    /** Butacas de una compra que todavía no se han separado en un QR aparte. */
    fun separableSeats(folio: String): List<String> {
        val purchase = purchases.firstOrNull { it.folio == folio } ?: return emptyList()
        val yaSeparadas = purchase.ticketPackages.flatMap { it.seats }.toSet()
        return purchase.seats.filter { it !in yaSeparadas }
    }

    /** Agrega un paquete de boletos separado a la compra correspondiente. */
    fun applySeparatedTicketPackage(folio: String, ticketPackage: MockTicketPackage) {
        val index = purchases.indexOfFirst { it.folio == folio }
        if (index < 0) return
        val current = purchases[index]
        if (ticketPackage.id.isNotBlank() && current.ticketPackages.any { it.id == ticketPackage.id }) {
            return
        }
        purchases[index] = current.copy(
            ticketPackages = current.ticketPackages + ticketPackage,
        )
    }

    fun activeQrPurchase(nowMillis: Long = System.currentTimeMillis()): MockPurchase? {
        val selected = purchases.firstOrNull {
            it.folio == activePurchaseFolio && TicketQrVisibility.isVisible(it, nowMillis)
        }
        return selected ?: purchases.firstOrNull { TicketQrVisibility.isVisible(it, nowMillis) }
    }

    fun recoverPurchase(folio: String, email: String): MockPurchase? {
        return purchases.firstOrNull {
            it.folio.equals(folio.trim(), ignoreCase = true) &&
                it.email.equals(email.trim(), ignoreCase = true)
        }
    }

    fun movieForPurchase(purchase: MockPurchase): MockMovie {
        return movieById(purchase.movieId)
    }

    fun paymentMethodLabel(): String {
        return paymentMethods.firstOrNull { it.id == selectedPaymentMethodId }?.let { "Tarjeta • ${it.last4}" }
            ?: "Invitado · folio + correo"
    }

    fun comboProductNames(combo: AdminConcessionCombo): List<String> {
        return combo.productIds.mapNotNull { productId ->
            concessions.firstOrNull { it.id == productId }?.name
        }
    }

    fun showtimesFor(movieId: String): List<MockShowtime> {
        return showtimesByMovieId[movieId] ?: emptyList()
    }

    fun selectAdminShowtime(movieId: String, showtimeId: String) {
        selectedAdminShowtimeMovieId = movieId
        selectedAdminShowtimeId = showtimeId
    }

    fun selectedAdminShowtime(): MockShowtime? {
        val movieId = selectedAdminShowtimeMovieId
        val showtimeId = selectedAdminShowtimeId
        if (movieId.isBlank() || showtimeId.isBlank()) return null
        return showtimesFor(movieId).firstOrNull { showtimeId(movieId, it) == showtimeId }
    }

    fun synopsisFor(movieId: String): String {
        val deCatalogo = movies.firstOrNull { it.id == movieId }?.synopsis
        if (!deCatalogo.isNullOrBlank()) return deCatalogo
        return mockSynopsis[movieId] ?: "Sinopsis no disponible."
    }

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
        comboQuantities.clear()
    }

    private fun purchaseConcessionItems(): List<MockPurchaseConcessionItem> {
        val products = selectedConcessionItems().map { (item, quantity) ->
            MockPurchaseConcessionItem(
                id = item.id,
                name = item.name,
                quantity = quantity,
            )
        }
        val combos = selectedComboItems().map { (combo, quantity) ->
            MockPurchaseConcessionItem(
                id = combo.id,
                name = combo.name,
                quantity = quantity,
                type = "combo",
            )
        }
        return products + combos
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
        return showtime.id?.takeIf { it.isNotBlank() }
            ?: listOf(
                movieId,
                showtime.startsAt ?: "",
                showtime.roomId ?: showtime.room,
                showtime.time,
            ).joinToString("|")
    }

    private fun reserveSeatsForCurrentShowtime(seatsPurchased: Int) {
        if (seatsPurchased <= 0) return
        val movieId = selectedMovieId
        if (movieId.isBlank()) return
        val updated = showtimesFor(movieId).map { showtime ->
            if (showtimeId(movieId, showtime) != selectedShowtimeId) return@map showtime
            showtime.copy(
                availableSeats = (showtime.availableSeats - seatsPurchased).coerceAtLeast(0),
            )
        }
        showtimesByMovieId[movieId] = updated
    }

    private fun showtimeDateLabel(startsAt: String?): String {
        if (startsAt.isNullOrBlank()) return "Hoy"
        return runCatching {
            Instant.parse(startsAt)
                .atZone(laPazZone)
                .format(showtimeDateFormatter)
        }.getOrElse {
            startsAt.take(10)
        }
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
            format = "Doblada",
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

private val laPazZone: ZoneId = ZoneId.of("America/Mazatlan")
private val showtimeDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

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
