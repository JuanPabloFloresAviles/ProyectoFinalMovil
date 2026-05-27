package com.example.proyectofinalmovil

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.proyectofinalmovil.screens.PlaceholderScreen
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme
import com.example.proyectofinalmovil.screens.SplashScreen
import com.example.proyectofinalmovil.screens.LoginScreen
import com.example.proyectofinalmovil.screens.SignupScreen
import com.example.proyectofinalmovil.screens.BrowseScreen
import com.example.proyectofinalmovil.screens.MovieDetailScreen
import com.example.proyectofinalmovil.screens.ShowtimesScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabItems = listOf(
        UiTabItem("Cartelera", AppIcons.Home),
        UiTabItem("Boletos", AppIcons.Tickets),
        UiTabItem("Comunidad", AppIcons.Community),
        UiTabItem("Historial", AppIcons.History),
        UiTabItem("Perfil", AppIcons.Profile),
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                UiAppBar(
                    title = currentTitle(currentRoute),
                    navigationIcon = if (currentRoute == AppDestination.Splash.route) null else AppIcons.Back,
                    actionIcon = AppIcons.Search,
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

@Composable
private fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
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
                    navController.navigate(AppDestination.MovieDetail.route)
                },
            )
        }
        composable(AppDestination.MovieDetail.route) {
            MovieDetailScreen(
                movieId = "estacion-7",
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
                movieId = "estacion-7",
                onContinuarAButacas = { showtimeId ->
                    navController.navigate(AppDestination.Seats.route)
                },
            )
        }
        composable(AppDestination.Seats.route) {
            PlaceholderScreen(
                current = AppDestination.Seats,
                primaryDestinations = listOf(
                    AppDestination.Concessions,
                    AppDestination.Summary,
                ),
                secondaryDestinations = listOf(AppDestination.Showtimes),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Concessions.route) {
            PlaceholderScreen(
                current = AppDestination.Concessions,
                primaryDestinations = listOf(
                    AppDestination.Summary,
                    AppDestination.Seats,
                ),
                secondaryDestinations = listOf(AppDestination.Showtimes),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Summary.route) {
            PlaceholderScreen(
                current = AppDestination.Summary,
                primaryDestinations = listOf(
                    AppDestination.Confirmation,
                    AppDestination.Concessions,
                ),
                secondaryDestinations = listOf(AppDestination.Seats),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Confirmation.route) {
            PlaceholderScreen(
                current = AppDestination.Confirmation,
                primaryDestinations = listOf(
                    AppDestination.TicketQr,
                    AppDestination.History,
                ),
                secondaryDestinations = listOf(AppDestination.Browse),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.TicketQr.route) {
            PlaceholderScreen(
                current = AppDestination.TicketQr,
                primaryDestinations = listOf(
                    AppDestination.History,
                    AppDestination.Profile,
                ),
                secondaryDestinations = listOf(AppDestination.RecoverPurchase),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.History.route) {
            PlaceholderScreen(
                current = AppDestination.History,
                primaryDestinations = listOf(
                    AppDestination.RecoverPurchase,
                    AppDestination.TicketQr,
                ),
                secondaryDestinations = listOf(AppDestination.Profile),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.RecoverPurchase.route) {
            PlaceholderScreen(
                current = AppDestination.RecoverPurchase,
                primaryDestinations = listOf(
                    AppDestination.History,
                    AppDestination.Login,
                ),
                secondaryDestinations = listOf(AppDestination.Profile),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Reviews.route) {
            PlaceholderScreen(
                current = AppDestination.Reviews,
                primaryDestinations = listOf(
                    AppDestination.MovieDetail,
                    AppDestination.Browse,
                ),
                secondaryDestinations = listOf(AppDestination.Profile),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Profile.route) {
            PlaceholderScreen(
                current = AppDestination.Profile,
                primaryDestinations = listOf(
                    AppDestination.RecoverPurchase,
                    AppDestination.History,
                ),
                secondaryDestinations = listOf(
                    AppDestination.Browse,
                    AppDestination.TicketQr,
                ),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.SocialHub.route) {
            PlaceholderScreen(
                current = AppDestination.SocialHub,
                primaryDestinations = listOf(
                    AppDestination.Requests,
                    AppDestination.Friends,
                    AppDestination.ChatList,
                ),
                secondaryDestinations = listOf(
                    AppDestination.Recommendations,
                    AppDestination.SearchUsers,
                ),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Requests.route) {
            PlaceholderScreen(
                current = AppDestination.Requests,
                primaryDestinations = listOf(
                    AppDestination.Friends,
                    AppDestination.SearchUsers,
                ),
                secondaryDestinations = listOf(AppDestination.SocialHub),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Friends.route) {
            PlaceholderScreen(
                current = AppDestination.Friends,
                primaryDestinations = listOf(
                    AppDestination.SearchUsers,
                    AppDestination.ChatList,
                    AppDestination.RecommendMovie,
                ),
                secondaryDestinations = listOf(AppDestination.SocialHub),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.SearchUsers.route) {
            PlaceholderScreen(
                current = AppDestination.SearchUsers,
                primaryDestinations = listOf(
                    AppDestination.Requests,
                    AppDestination.Friends,
                ),
                secondaryDestinations = listOf(AppDestination.SocialHub),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.ChatList.route) {
            PlaceholderScreen(
                current = AppDestination.ChatList,
                primaryDestinations = listOf(
                    AppDestination.PrivateChat,
                    AppDestination.Friends,
                ),
                secondaryDestinations = listOf(
                    AppDestination.SocialHub,
                    AppDestination.Recommendations,
                ),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.PrivateChat.route) {
            PlaceholderScreen(
                current = AppDestination.PrivateChat,
                primaryDestinations = listOf(
                    AppDestination.RecommendMovie,
                    AppDestination.ChatList,
                ),
                secondaryDestinations = listOf(AppDestination.Friends),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.RecommendMovie.route) {
            PlaceholderScreen(
                current = AppDestination.RecommendMovie,
                primaryDestinations = listOf(
                    AppDestination.Recommendations,
                    AppDestination.PrivateChat,
                ),
                secondaryDestinations = listOf(AppDestination.Friends),
                onNavigate = { navController.navigate(it.route) },
            )
        }
        composable(AppDestination.Recommendations.route) {
            PlaceholderScreen(
                current = AppDestination.Recommendations,
                primaryDestinations = listOf(
                    AppDestination.MovieDetail,
                    AppDestination.ChatList,
                ),
                secondaryDestinations = listOf(AppDestination.SocialHub),
                onNavigate = { navController.navigate(it.route) },
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
