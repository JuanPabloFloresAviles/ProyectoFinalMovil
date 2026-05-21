package com.example.proyectofinalmovil.navigation

sealed class AppDestination(
    val route: String,
    val title: String,
) {
    data object Splash : AppDestination("splash", "Splash")
    data object Login : AppDestination("login", "Inicio de sesión")
    data object Signup : AppDestination("signup", "Registro")
    data object Browse : AppDestination("browse", "Cartelera")
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
    data object Profile : AppDestination("profile", "Perfil")
    data object SocialHub : AppDestination("social-hub", "Centro social")
    data object Requests : AppDestination("requests", "Solicitudes")
    data object Friends : AppDestination("friends", "Mis amigos")
    data object SearchUsers : AppDestination("search-users", "Buscar usuarios")
    data object ChatList : AppDestination("chat-list", "Lista de chats")
    data object PrivateChat : AppDestination("private-chat", "Chat privado")
    data object RecommendMovie : AppDestination("recommend-movie", "Recomendar pelicula")
    data object Recommendations : AppDestination("recommendations", "Recomendaciones")
}

val bottomBarDestinations = listOf(
    AppDestination.Browse,
    AppDestination.TicketQr,
    AppDestination.SocialHub,
    AppDestination.History,
    AppDestination.Profile,
)
