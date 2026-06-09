package com.example.proyectofinalmovil.navigation

sealed class AppDestination(
    val route: String,
    val title: String,
) {
    data object Splash : AppDestination("splash", "Splash")
    data object Login : AppDestination("login", "Inicio de sesión")
    data object Signup : AppDestination("signup", "Registro")
    data object Browse : AppDestination("browse", "Cartelera")
    data object AllShowtimes : AppDestination("all-showtimes", "Funciones")
    data object MovieDetail : AppDestination("movie-detail", "Detalle")
    data object Showtimes : AppDestination("showtimes", "Funciones")
    data object Seats : AppDestination("seats", "Butacas")
    data object Concessions : AppDestination("concessions", "Dulceria")
    data object Summary : AppDestination("summary", "Resumen")
    data object Confirmation : AppDestination("confirmation", "Confirmación")
    data object TicketQr : AppDestination("ticket-qr", "QR boletos")
    data object History : AppDestination("history", "Historial")
    data object RecoverPurchase : AppDestination("recover-purchase", "Recuperar compra")
    data object Reviews : AppDestination("reviews", "Reseñas")
    data object NuevaResena : AppDestination("nueva-resena", "Escribir reseña")
    data object Profile : AppDestination("profile", "Perfil")
    data object SocialHub : AppDestination("social-hub", "Centro social")
    data object Requests : AppDestination("requests", "Solicitudes")
    data object Friends : AppDestination("friends", "Mis amigos")
    data object SearchUsers : AppDestination("search-users", "Buscar usuarios")
    data object AddFriendByCode : AppDestination("add-friend-code", "Agregar por código")
    data object ChatList : AppDestination("chat-list", "Lista de chats")
    data object PrivateChat : AppDestination("private-chat", "Chat privado")
    data object RecommendMovie : AppDestination("recommend-movie", "Recomendar pelicula")
    data object Recommendations : AppDestination("recommendations", "Recomendaciones")
    data object AdminDashboard : AppDestination("admin-dashboard", "Panel administrativo")
    data object AdminMovies : AppDestination("admin-movies", "Gestión de películas")
    data object AdminMovieImport : AppDestination("admin-movie-import", "Importar películas")
    data object AdminShowtimes : AppDestination("admin-showtimes", "Gestión de funciones")
    data object AdminNewShowtime : AppDestination("admin-new-showtime", "Nueva función")
    data object AdminEditShowtime : AppDestination("admin-edit-showtime", "Editar función")
    data object AdminConcessions : AppDestination("admin-concessions", "Gestión de dulcería")
    data object AdminRooms : AppDestination("admin-rooms", "Salas y butacas")
    data object AdminReports : AppDestination("admin-reports", "Ventas y estadísticas")
    data object PaymentMethods : AppDestination("payment-methods", "Métodos de pago")
}

val bottomBarDestinations = listOf(
    AppDestination.Browse,
    AppDestination.TicketQr,
    AppDestination.SocialHub,
    AppDestination.History,
    AppDestination.Profile,
)
