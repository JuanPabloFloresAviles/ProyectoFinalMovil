package com.example.proyectofinalmovil

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.proyectofinalmovil.screens.AllShowtimesScreen
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
import com.example.proyectofinalmovil.screens.AdminAccessDeniedScreen
import com.example.proyectofinalmovil.screens.AdminDashboardScreen
import com.example.proyectofinalmovil.screens.AdminConcessionsManagementScreen
import com.example.proyectofinalmovil.screens.AdminMovieImportScreen
import com.example.proyectofinalmovil.screens.AdminMoviesManagementScreen
import com.example.proyectofinalmovil.screens.AdminReportsScreen
import com.example.proyectofinalmovil.screens.AdminRoomsManagementScreen
import com.example.proyectofinalmovil.screens.AdminShowtimesManagementScreen
import com.example.proyectofinalmovil.screens.classificationFromTmdb
import com.example.proyectofinalmovil.services.state.AppUiState
import com.example.proyectofinalmovil.services.state.AdminSalesRange
import com.example.proyectofinalmovil.services.state.ProvideAppUiState
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.services.api.AuthApi
import com.example.proyectofinalmovil.services.api.AuthException
import com.example.proyectofinalmovil.services.api.CatalogApi
import com.example.proyectofinalmovil.services.api.MobileStateApi
import com.example.proyectofinalmovil.services.api.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val appState = remember { AppUiState() }

    ProvideAppUiState(appUiState = appState) {
        val bottomDestinations = bottomDestinationsFor(appState.isAdmin())
        val tabItems = bottomTabsFor(appState.isAdmin())
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
                    if (currentRoute in bottomDestinations.map { it.route }) {
                        UiTabBar(
                            tabs = tabItems,
                            selectedIndex = selectedTabIndex(currentRoute, bottomDestinations),
                            onTabSelected = { index ->
                                navController.navigate(bottomDestinations[index].route) {
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
    val authApi = remember { AuthApi() }
    val catalogApi = remember { CatalogApi() }
    val mobileStateApi = remember { MobileStateApi() }
    val tmdbApi = remember { TmdbApi() }
    val coroutineScope = rememberCoroutineScope()
    var adminSalesRange by remember { mutableStateOf(AdminSalesRange.LAST_7_DAYS) }

    fun refreshAdminMetrics(range: AdminSalesRange) {
        adminSalesRange = range
        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    mobileStateApi.getAdminMetrics(appState.authToken, range)
                }
            }.onSuccess { metrics ->
                appState.replaceAdminMetrics(metrics)
            }
        }
    }

    LaunchedEffect(Unit) {
        runCatching {
            withContext(Dispatchers.IO) { catalogApi.getCatalog() }
        }.onSuccess { snapshot ->
            appState.replaceCatalog(snapshot)
        }
    }

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
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LoginScreen(
                onEntrar = { email, password ->
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val session = withContext(Dispatchers.IO) {
                                authApi.login(email, password)
                            }
                            appState.signIn(session)
                            runCatching {
                                withContext(Dispatchers.IO) { catalogApi.getCatalog() }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                            if (appState.isAdmin()) {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        mobileStateApi.getAdminMetrics(session.token, adminSalesRange)
                                    }
                                }.onSuccess { metrics ->
                                    appState.replaceAdminMetrics(metrics)
                                }
                            } else {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        mobileStateApi.getUserState(session.token)
                                    }
                                }.onSuccess { snapshot ->
                                    appState.replaceUserState(snapshot)
                                }
                            }
                            val destination = if (appState.isAdmin()) {
                                AppDestination.AdminDashboard
                            } else {
                                AppDestination.Browse
                            }
                            navController.navigate(destination.route)
                        } catch (error: AuthException) {
                            errorMessage = error.message
                        } catch (_: Exception) {
                            errorMessage = "No se pudo conectar con el servidor. Intenta nuevamente."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onIrARegistro = { navController.navigate(AppDestination.Signup.route) },
                isLoading = isLoading,
                errorMessage = errorMessage,
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
                onVerTodo = {
                    navController.navigate(AppDestination.AllShowtimes.route)
                },
            )
        }
        composable(AppDestination.AllShowtimes.route) {
            AllShowtimesScreen(
                onShowtimeClick = { movieId, showtimeId ->
                    appState.selectMovie(movieId)
                    appState.selectShowtime(showtimeId)
                    navController.navigate(AppDestination.Seats.route)
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
                onCerrarSesion = {
                    appState.signOut()
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(AppDestination.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onIniciarSesion = {
                    navController.navigate(AppDestination.Login.route) {
                        launchSingleTop = true
                    }
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
            if (friend == null) {
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
            } else {
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
        composable(AppDestination.AdminDashboard.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminDashboardScreen(
                    metrics = appState.adminDashboardMetrics(),
                    selectedRange = adminSalesRange,
                    onRangeSelected = { range -> refreshAdminMetrics(range) },
                    moviesCount = appState.movies.size,
                    showtimesCount = appState.showtimesByMovieId.values.sumOf { it.size },
                    concessionsCount = appState.concessions.size,
                    onMovies = { navController.navigate(AppDestination.AdminMovies.route) },
                    onShowtimes = { navController.navigate(AppDestination.AdminShowtimes.route) },
                    onConcessions = { navController.navigate(AppDestination.AdminConcessions.route) },
                    onRooms = { navController.navigate(AppDestination.AdminRooms.route) },
                    onReports = { navController.navigate(AppDestination.AdminReports.route) },
                    onClientView = { navController.navigate(AppDestination.Browse.route) },
                )
            }
        }
        composable(AppDestination.AdminMovies.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminMoviesManagementScreen(
                    movies = appState.movies,
                    onSaveMovie = { id, title, synopsis, classification, duration ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveMovie(
                                        token = appState.authToken,
                                        id = id,
                                        title = title,
                                        synopsis = synopsis,
                                        classification = classification,
                                        durationMinutes = duration.filter { it.isDigit() }.toIntOrNull() ?: 90,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                        }
                    },
                    onImportMovies = { navController.navigate(AppDestination.AdminMovieImport.route) },
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
        composable(AppDestination.AdminMovieImport.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminMovieImportScreen(
                    tmdbApi = tmdbApi,
                    onImportMovie = { candidate ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    val detail = tmdbApi.getMovieDetail(candidate.tmdbId)
                                    catalogApi.saveMovie(
                                        token = appState.authToken,
                                        id = null,
                                        title = detail.title,
                                        synopsis = detail.overview,
                                        classification = classificationFromTmdb(candidate),
                                        durationMinutes = detail.runtimeMinutes,
                                        posterUrl = detail.posterUrl,
                                        tmdbId = detail.tmdbId,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                                navController.navigate(AppDestination.AdminMovies.route) {
                                    popUpTo(AppDestination.AdminMovieImport.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    onBackToMovies = { navController.navigate(AppDestination.AdminMovies.route) },
                )
            }
        }
        composable(AppDestination.AdminShowtimes.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminShowtimesManagementScreen(
                    movies = appState.movies,
                    rooms = appState.adminRooms,
                    showtimesByMovieId = appState.showtimesByMovieId,
                    onSaveShowtime = { movieId, index, showtime ->
                        coroutineScope.launch {
                            val existing = index?.let { appState.showtimesFor(movieId).getOrNull(it) }
                            val room = showtime.roomId?.let { roomId ->
                                appState.adminRooms.firstOrNull { it.id == roomId }
                            } ?: appState.adminRooms.firstOrNull { it.name == showtime.room }
                                ?: appState.adminRooms.firstOrNull()
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveShowtime(
                                        token = appState.authToken,
                                        id = existing?.id ?: showtime.id,
                                        movieId = movieId,
                                        roomId = showtime.roomId ?: room?.id ?: existing?.roomId ?: "1",
                                        startsAt = adminDateTimeFrom(showtime.time, existing?.startsAt ?: showtime.startsAt),
                                        price = showtime.price,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                        }
                    },
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
        composable(AppDestination.AdminConcessions.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminConcessionsManagementScreen(
                    products = appState.concessions,
                    combos = appState.concessionCombos,
                    onSaveProduct = { id, name, description, cost, stock, price ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveProduct(
                                        token = appState.authToken,
                                        id = id,
                                        name = name,
                                        category = description,
                                        cost = cost,
                                        price = price,
                                        stock = stock,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                        }
                    },
                    onSaveCombo = { id, name, description, price, productIds ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveCombo(
                                        token = appState.authToken,
                                        id = id,
                                        name = name,
                                        price = price,
                                        productIds = productIds,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                        }
                    },
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
        composable(AppDestination.AdminRooms.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminRoomsManagementScreen(
                    rooms = appState.adminRooms,
                    onSaveRoom = { id, name, rows, columns, inactiveSeats ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveRoom(
                                        token = appState.authToken,
                                        id = id,
                                        name = name,
                                        rows = rows,
                                        columns = columns,
                                        inactiveSeatLabels = inactiveSeats,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                        }
                    },
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
        composable(AppDestination.AdminReports.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminReportsScreen(
                    metrics = appState.adminDashboardMetrics(),
                    selectedRange = adminSalesRange,
                    onRangeSelected = { range -> refreshAdminMetrics(range) },
                    roomsCount = appState.adminRooms.size,
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
    }
}

@Composable
private fun AdminGuard(
    isAdmin: Boolean,
    onBackToLogin: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isAdmin) {
        content()
    } else {
        AdminAccessDeniedScreen(onBackToLogin = onBackToLogin)
    }
}

private fun NavHostController.navigateToLogin() {
    navigate(AppDestination.Login.route) {
        popUpTo(AppDestination.Splash.route)
    }
}

private fun adminDateTimeFrom(time: String, existingStartsAt: String? = null): String {
    val safeTime = if (Regex("\\d{2}:\\d{2}").matches(time)) time else "18:00"
    val datePrefix = existingStartsAt?.takeIf { it.length >= 10 }?.substring(0, 10) ?: "2026-12-31"
    return "${datePrefix}T${safeTime}:00.000Z"
}

private fun currentTitle(route: String?): String {
    return when (route) {
        AppDestination.Splash.route -> AppDestination.Splash.title
        AppDestination.Login.route -> AppDestination.Login.title
        AppDestination.Signup.route -> AppDestination.Signup.title
        AppDestination.Browse.route -> AppDestination.Browse.title
        AppDestination.AllShowtimes.route -> AppDestination.AllShowtimes.title
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
        AppDestination.AdminDashboard.route -> AppDestination.AdminDashboard.title
        AppDestination.AdminMovies.route -> AppDestination.AdminMovies.title
        AppDestination.AdminMovieImport.route -> AppDestination.AdminMovieImport.title
        AppDestination.AdminShowtimes.route -> AppDestination.AdminShowtimes.title
        AppDestination.AdminConcessions.route -> AppDestination.AdminConcessions.title
        AppDestination.AdminRooms.route -> AppDestination.AdminRooms.title
        AppDestination.AdminReports.route -> AppDestination.AdminReports.title
        else -> "CineUABCS"
    }
}

private fun bottomDestinationsFor(isAdmin: Boolean): List<AppDestination> {
    return if (isAdmin) {
        bottomBarDestinations.dropLast(1) + AppDestination.AdminDashboard
    } else {
        bottomBarDestinations
    }
}

private fun bottomTabsFor(isAdmin: Boolean): List<UiTabItem> {
    return listOf(
        UiTabItem("Cartelera", AppIcons.Home),
        UiTabItem("Boletos", AppIcons.Tickets),
        UiTabItem("Comunidad", AppIcons.Community),
        UiTabItem("Historial", AppIcons.History),
        if (isAdmin) {
            UiTabItem("Dashboard", AppIcons.Movies)
        } else {
            UiTabItem("Perfil", AppIcons.Profile)
        },
    )
}

private fun selectedTabIndex(route: String?, destinations: List<AppDestination>): Int {
    return destinations.indexOfFirst { it.route == route }.takeIf { it >= 0 } ?: 0
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    ProyectoFinalMovilTheme {
        AppRoot()
    }
}
