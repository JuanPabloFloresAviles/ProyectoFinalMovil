package com.example.proyectofinalmovil

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalmovil.components.AppIcons
import com.example.proyectofinalmovil.components.UiAppBar
import com.example.proyectofinalmovil.components.UiTabBar
import com.example.proyectofinalmovil.components.UiTabItem
import com.example.proyectofinalmovil.navigation.AppDestination
import com.example.proyectofinalmovil.navigation.bottomBarDestinations
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme
import com.example.proyectofinalmovil.screens.SplashScreen
import com.example.proyectofinalmovil.screens.LoginScreen
import com.example.proyectofinalmovil.screens.SignupScreen
import com.example.proyectofinalmovil.screens.BrowseScreen
import com.example.proyectofinalmovil.screens.MovieDetailScreen
import com.example.proyectofinalmovil.screens.ShowtimesScreen
import com.example.proyectofinalmovil.screens.SeatsScreen
import com.example.proyectofinalmovil.screens.ConcessionsScreen
import com.example.proyectofinalmovil.screens.SummaryScreen
import com.example.proyectofinalmovil.screens.ConfirmationScreen
import com.example.proyectofinalmovil.screens.TicketQrScreen
import com.example.proyectofinalmovil.screens.HistoryScreen
import com.example.proyectofinalmovil.screens.RecoverPurchaseScreen
import com.example.proyectofinalmovil.screens.ProfileScreen
import com.example.proyectofinalmovil.screens.ReviewsScreen
import com.example.proyectofinalmovil.screens.SocialHubScreen
import com.example.proyectofinalmovil.screens.RequestsScreen
import com.example.proyectofinalmovil.screens.FriendsScreen
import com.example.proyectofinalmovil.screens.SearchUsersScreen
import com.example.proyectofinalmovil.screens.ChatListScreen
import com.example.proyectofinalmovil.screens.PrivateChatScreen
import com.example.proyectofinalmovil.screens.RecommendMovieScreen
import com.example.proyectofinalmovil.screens.RecommendationsScreen
import com.example.proyectofinalmovil.services.state.AppUiState
import com.example.proyectofinalmovil.services.state.ProvideAppUiState
import com.example.proyectofinalmovil.services.state.LocalAppUiState

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val appState = remember { AppUiState() }

    val tabItems = listOf(
        UiTabItem("Cartelera", AppIcons.Home),
        UiTabItem("Boletos", AppIcons.Tickets),
        UiTabItem("Comunidad", AppIcons.Community),
        UiTabItem("Historial", AppIcons.History),
        UiTabItem("Perfil", AppIcons.Profile),
    )

    ProvideAppUiState(appUiState = appState) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    UiAppBar(
                        title = currentTitle(currentRoute),
                        navigationIcon = if (currentRoute == AppDestination.Splash.route) null else AppIcons.Back,
                        onNavigationClick = {
                            if (!navController.popBackStack()) {
                                navController.navigate(AppDestination.Splash.route)
                            }
                        },
                    )
                },
                bottomBar = {
                    if (currentRoute in bottomBarDestinations.map { it.route }) {
                        UiTabBar(
                            tabs = tabItems,
                            selectedIndex = selectedTabIndex(currentRoute),
                            onTabSelected = { index ->
                                navController.navigate(bottomBarDestinations[index].route) {
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                },
            ) { innerPadding ->
                AppNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val appState = LocalAppUiState.current

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route,
        modifier = modifier.fillMaxSize(),
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen(
                onComenzar = { navController.navigate(AppDestination.Login.route) },
                onInvitado = { navController.navigate(AppDestination.Browse.route) },
            )
        }
        composable(AppDestination.Login.route) {
            LoginScreen(
                onEntrar = { navController.navigate(AppDestination.Browse.route) },
                onIrARegistro = { navController.navigate(AppDestination.Signup.route) },
            )
        }
        composable(AppDestination.Signup.route) {
            SignupScreen(
                onCrearCuenta = { navController.navigate(AppDestination.Browse.route) },
            )
        }
        composable(AppDestination.Browse.route) {
            BrowseScreen(
                onMovieClick = { movieId ->
                    appState.selectMovie(movieId)
                    navController.navigate(AppDestination.MovieDetail.route)
                },
            )
        }
        composable(AppDestination.MovieDetail.route) {
            MovieDetailScreen(
                movieId = appState.selectedMovieId,
                onElegirFuncion = { movieId ->
                    navController.navigate(AppDestination.Showtimes.route)
                },
                onVerResenas = { movieId ->
                    navController.navigate(AppDestination.Reviews.route)
                },
            )
        }
        composable(AppDestination.Showtimes.route) {
            ShowtimesScreen(
                movieId = appState.selectedMovieId,
                onContinuarAButacas = { showtimeId ->
                    appState.selectShowtime(showtimeId)
                    navController.navigate(AppDestination.Seats.route)
                },
            )
        }
        composable(AppDestination.Seats.route) {
            SeatsScreen(
                movieId = appState.selectedMovieId,
                onContinuarADulceria = {
                    navController.navigate(AppDestination.Concessions.route)
                },
            )
        }
        composable(AppDestination.Concessions.route) {
            ConcessionsScreen(
                onIrAlResumen = {
                    navController.navigate(AppDestination.Summary.route)
                },
                onSaltarDulceria = {
                    navController.navigate(AppDestination.Summary.route)
                },
            )
        }
        composable(AppDestination.Summary.route) {
            SummaryScreen(
                onConfirmarCompra = {
                    appState.confirmCheckout()
                    navController.navigate(AppDestination.Confirmation.route)
                },
            )
        }
        composable(AppDestination.Confirmation.route) {
            ConfirmationScreen(
                onVerBoleto = {
                    navController.navigate(AppDestination.TicketQr.route)
                },
                onVolverACartelera = {
                    navController.navigate(AppDestination.Browse.route) {
                        popUpTo(AppDestination.Browse.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.TicketQr.route) {
            TicketQrScreen(
                onIrAlHistorial = {
                    navController.navigate(AppDestination.History.route)
                },
                onRecuperarCompra = {
                    navController.navigate(AppDestination.RecoverPurchase.route)
                },
                onVolverACartelera = {
                    navController.navigate(AppDestination.Browse.route) {
                        popUpTo(AppDestination.Browse.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.History.route) {
            HistoryScreen(
                onVerBoleto = { folio ->
                    appState.activePurchaseFolio = folio
                    navController.navigate(AppDestination.TicketQr.route)
                },
                onRecuperarCompra = {
                    navController.navigate(AppDestination.RecoverPurchase.route)
                },
            )
        }
        composable(AppDestination.RecoverPurchase.route) {
            RecoverPurchaseScreen(
                onVerBoleto = { folio ->
                    appState.activePurchaseFolio = folio
                    navController.navigate(AppDestination.TicketQr.route)
                },
                onIrAlHistorial = {
                    navController.navigate(AppDestination.History.route)
                },
            )
        }
        composable(AppDestination.Reviews.route) {
            ReviewsScreen(
                onVolverAPerfil = {
                    navController.navigate(AppDestination.Profile.route)
                },
                onVerCartelera = {
                    navController.navigate(AppDestination.Browse.route)
                },
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                onVerHistorial = {
                    navController.navigate(AppDestination.History.route)
                },
                onVerResenas = {
                    navController.navigate(AppDestination.Reviews.route)
                },
                onRecuperarCompra = {
                    navController.navigate(AppDestination.RecoverPurchase.route)
                },
            )
        }
        composable(AppDestination.SocialHub.route) {
            SocialHubScreen(
                friendsCount = appState.friendIds.size,
                incomingRequestsCount = appState.incomingRequestIds.size,
                outgoingRequestsCount = appState.outgoingRequestIds.size,
                onVerSolicitudes = {
                    navController.navigate(AppDestination.Requests.route)
                },
                onVerAmigos = {
                    navController.navigate(AppDestination.Friends.route)
                },
                onBuscarPersonas = {
                    navController.navigate(AppDestination.SearchUsers.route)
                },
                onVerChats = {
                    navController.navigate(AppDestination.ChatList.route)
                },
                onVerRecomendaciones = {
                    navController.navigate(AppDestination.Recommendations.route)
                },
            )
        }
        composable(AppDestination.Requests.route) {
            RequestsScreen(
                users = appState.socialUsers,
                incomingRequestIds = appState.incomingRequestIds,
                outgoingRequestIds = appState.outgoingRequestIds,
                onAccept = { userId ->
                    appState.acceptFriend(userId)
                },
                onReject = { userId ->
                    appState.rejectFriend(userId)
                },
                onCancel = { userId ->
                    appState.cancelFriendRequest(userId)
                },
                onVerAmigos = {
                    navController.navigate(AppDestination.Friends.route)
                },
                onBuscarPersonas = {
                    navController.navigate(AppDestination.SearchUsers.route)
                },
            )
        }
        composable(AppDestination.Friends.route) {
            FriendsScreen(
                friends = appState.friendUsers(),
                onBuscarPersonas = {
                    navController.navigate(AppDestination.SearchUsers.route)
                },
                onVerSolicitudes = {
                    navController.navigate(AppDestination.Requests.route)
                },
                onOpenChat = { userId ->
                    appState.selectedChatFriendId = userId
                    navController.navigate(AppDestination.PrivateChat.route)
                },
                onRecommendMovie = { userId ->
                    appState.selectedRecommendationFriendId = userId
                    navController.navigate(AppDestination.RecommendMovie.route)
                },
            )
        }
        composable(AppDestination.SearchUsers.route) {
            SearchUsersScreen(
                users = appState.socialUsers,
                friendIds = appState.friendIds,
                incomingRequestIds = appState.incomingRequestIds,
                outgoingRequestIds = appState.outgoingRequestIds,
                onAdd = { userId ->
                    appState.addFriendRequest(userId)
                },
                onCancel = { userId ->
                    appState.cancelFriendRequest(userId)
                },
                onVerSolicitudes = {
                    navController.navigate(AppDestination.Requests.route)
                },
                onVerAmigos = {
                    navController.navigate(AppDestination.Friends.route)
                },
            )
        }
        composable(AppDestination.ChatList.route) {
            ChatListScreen(
                friends = appState.friendUsers(),
                messages = appState.chatMessages,
                onOpenChat = { userId ->
                    appState.selectedChatFriendId = userId
                    navController.navigate(AppDestination.PrivateChat.route)
                },
                onVerRecomendaciones = {
                    navController.navigate(AppDestination.Recommendations.route)
                },
            )
        }
        composable(AppDestination.PrivateChat.route) {
            val friend = appState.socialUsers.find { it.id == appState.selectedChatFriendId }
                ?: appState.socialUsers.first()
            PrivateChatScreen(
                friend = friend,
                messages = appState.chatMessages,
                onSendMessage = { text ->
                    appState.sendChatMessage(friend.id, text)
                },
                onRecommendMovie = {
                    appState.selectedRecommendationFriendId = friend.id
                    navController.navigate(AppDestination.RecommendMovie.route)
                },
            )
        }
        composable(AppDestination.RecommendMovie.route) {
            val friends = appState.friendUsers()
            RecommendMovieScreen(
                friends = friends.sortedBy { it.id != appState.selectedRecommendationFriendId },
                movies = appState.movies,
                onSendRecommendation = { friendId, movieId, note ->
                    appState.sendRecommendation(friendId, movieId, note)
                },
                onVerRecomendaciones = {
                    navController.navigate(AppDestination.Recommendations.route)
                },
            )
        }
        composable(AppDestination.Recommendations.route) {
            RecommendationsScreen(
                recommendations = appState.recommendations,
                users = appState.socialUsers,
                movies = appState.movies,
                onIrAChats = {
                    navController.navigate(AppDestination.ChatList.route)
                },
                onRecomendar = {
                    navController.navigate(AppDestination.RecommendMovie.route)
                },
            )
        }
    }
}

private fun currentTitle(route: String?): String {
    return when (route) {
        AppDestination.Splash.route -> AppDestination.Splash.title
        AppDestination.Login.route -> AppDestination.Login.title
        AppDestination.Signup.route -> AppDestination.Signup.title
        AppDestination.Browse.route -> AppDestination.Browse.title
        AppDestination.MovieDetail.route -> AppDestination.MovieDetail.title
        AppDestination.Showtimes.route -> AppDestination.Showtimes.title
        AppDestination.Seats.route -> AppDestination.Seats.title
        AppDestination.Concessions.route -> AppDestination.Concessions.title
        AppDestination.Summary.route -> AppDestination.Summary.title
        AppDestination.Confirmation.route -> AppDestination.Confirmation.title
        AppDestination.TicketQr.route -> AppDestination.TicketQr.title
        AppDestination.History.route -> AppDestination.History.title
        AppDestination.RecoverPurchase.route -> AppDestination.RecoverPurchase.title
        AppDestination.Reviews.route -> AppDestination.Reviews.title
        AppDestination.Profile.route -> AppDestination.Profile.title
        AppDestination.SocialHub.route -> AppDestination.SocialHub.title
        AppDestination.Requests.route -> AppDestination.Requests.title
        AppDestination.Friends.route -> AppDestination.Friends.title
        AppDestination.SearchUsers.route -> AppDestination.SearchUsers.title
        AppDestination.ChatList.route -> AppDestination.ChatList.title
        AppDestination.PrivateChat.route -> AppDestination.PrivateChat.title
        AppDestination.RecommendMovie.route -> AppDestination.RecommendMovie.title
        AppDestination.Recommendations.route -> AppDestination.Recommendations.title
        else -> "CineUABCS"
    }
}

private fun selectedTabIndex(route: String?): Int {
    return when (route) {
        AppDestination.Browse.route -> 0
        AppDestination.TicketQr.route -> 1
        AppDestination.SocialHub.route -> 2
        AppDestination.History.route -> 3
        AppDestination.Profile.route -> 4
        else -> 0
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    ProyectoFinalMovilTheme {
        AppRoot()
    }
}
