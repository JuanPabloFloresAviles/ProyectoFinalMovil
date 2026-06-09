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
import com.example.proyectofinalmovil.screens.PaymentMethodsScreen
import com.example.proyectofinalmovil.screens.ProfileScreen
import com.example.proyectofinalmovil.screens.ReviewsScreen
import com.example.proyectofinalmovil.screens.NuevaResenaScreen
import com.example.proyectofinalmovil.screens.SocialHubScreen
import com.example.proyectofinalmovil.screens.RequestsScreen
import com.example.proyectofinalmovil.screens.FriendsScreen
import com.example.proyectofinalmovil.screens.SearchUsersScreen
import com.example.proyectofinalmovil.screens.AddFriendByCodeScreen
import com.example.proyectofinalmovil.screens.ChatListScreen
import com.example.proyectofinalmovil.screens.PrivateChatScreen
import com.example.proyectofinalmovil.screens.RecommendMovieScreen
import com.example.proyectofinalmovil.screens.RecommendationsScreen
import com.example.proyectofinalmovil.screens.AdminAccessDeniedScreen
import com.example.proyectofinalmovil.screens.AdminDashboardScreen
import com.example.proyectofinalmovil.screens.AdminConcessionsManagementScreen
import com.example.proyectofinalmovil.screens.AdminEditShowtimeScreen
import com.example.proyectofinalmovil.screens.AdminMovieImportScreen
import com.example.proyectofinalmovil.screens.AdminMoviesManagementScreen
import com.example.proyectofinalmovil.screens.AdminNewShowtimeScreen
import com.example.proyectofinalmovil.screens.AdminReportsScreen
import com.example.proyectofinalmovil.screens.AdminRoomsManagementScreen
import com.example.proyectofinalmovil.screens.AdminShowtimesManagementScreen
import com.example.proyectofinalmovil.screens.classificationFromTmdb
import com.example.proyectofinalmovil.services.state.AppUiState
import com.example.proyectofinalmovil.services.state.AdminSalesRange
import com.example.proyectofinalmovil.services.state.ProvideAppUiState
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.services.api.AuthApi
import com.example.proyectofinalmovil.services.api.ApiException
import com.example.proyectofinalmovil.services.api.AuthException
import com.example.proyectofinalmovil.services.api.CatalogApi
import com.example.proyectofinalmovil.services.api.MobileStateApi
import com.example.proyectofinalmovil.services.api.MobileCheckoutConcessionItem
import com.example.proyectofinalmovil.services.api.MobileCheckoutPayload
import com.example.proyectofinalmovil.services.api.TmdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockPurchaseConcessionItem
import com.example.proyectofinalmovil.services.mock.MockTicketPackage
import com.example.proyectofinalmovil.services.mock.MockConcessionPackage
import org.json.JSONArray
import org.json.JSONObject

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
    val contexto = LocalContext.current
    val authApi = remember { AuthApi() }
    val catalogApi = remember { CatalogApi() }
    val mobileStateApi = remember { MobileStateApi() }
    val tmdbApi = remember { TmdbApi() }
    val coroutineScope = rememberCoroutineScope()
    var adminSalesRange by remember { mutableStateOf(AdminSalesRange.LAST_7_DAYS) }
    var checkoutErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingCheckout by remember { mutableStateOf(false) }
    var catalogErrorMessage by remember { mutableStateOf<String?>(null) }

    fun loadCatalog() {
        catalogErrorMessage = null
        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { catalogApi.getCatalog() }
            }.onSuccess { snapshot ->
                catalogErrorMessage = null
                appState.replaceCatalog(snapshot)
            }.onFailure {
                catalogErrorMessage =
                    "No se pudo cargar la cartelera. Revisa tu conexión e inténtalo de nuevo."
            }
        }
    }

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

    fun refreshUserState() {
        if (appState.authToken.isBlank()) return
        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    mobileStateApi.getUserState(appState.authToken)
                }
            }.onSuccess { snapshot ->
                appState.replaceUserState(snapshot)
            }
        }
    }

    fun runSocialAction(action: suspend () -> Unit) {
        if (appState.authToken.isBlank()) return
        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { action() }
            }
            runCatching {
                withContext(Dispatchers.IO) {
                    mobileStateApi.getUserState(appState.authToken)
                }
            }.onSuccess { snapshot ->
                appState.replaceUserState(snapshot)
            }
        }
    }

    LaunchedEffect(Unit) {
        val comprasGuardadas = withContext(Dispatchers.IO) { cargarComprasInvitadoLocal(contexto) }
        appState.cargarComprasLocales(comprasGuardadas)
        loadCatalog()
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
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        mobileStateApi.listPaymentMethods(session.token)
                                    }
                                }.onSuccess { metodos ->
                                    appState.replacePaymentMethods(metodos)
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
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            SignupScreen(
                onCrearCuenta = { nombre, apellidoPaterno, apellidoMaterno, correo, contrasena ->
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val session = withContext(Dispatchers.IO) {
                                authApi.registrar(nombre, apellidoPaterno, apellidoMaterno, correo, contrasena)
                            }
                            appState.signIn(session)
                            runCatching {
                                withContext(Dispatchers.IO) { mobileStateApi.getUserState(session.token) }
                            }.onSuccess { snapshot ->
                                appState.replaceUserState(snapshot)
                            }
                            runCatching {
                                withContext(Dispatchers.IO) { mobileStateApi.listPaymentMethods(session.token) }
                            }.onSuccess { metodos ->
                                appState.replacePaymentMethods(metodos)
                            }
                            navController.navigate(AppDestination.Browse.route)
                        } catch (error: AuthException) {
                            errorMessage = error.message
                        } catch (_: Exception) {
                            errorMessage = "No se pudo conectar con el servidor. Intenta nuevamente."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                cargando = isLoading,
                mensajeError = errorMessage,
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
                errorMessage = catalogErrorMessage,
                onRetry = { loadCatalog() },
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
                onConfirmarCompra = { cvv ->
                    val currentShowtime = appState.currentShowtime()
                    val paymentMethod = appState.paymentMethods.firstOrNull {
                        it.id == appState.selectedPaymentMethodId
                    }
                    val payload = MobileCheckoutPayload(
                        nombreComprador = appState.signedInName.ifBlank { "Invitado Cine UABCS" },
                        correoComprador = if (appState.signedInEmail.isNotBlank()) {
                            appState.signedInEmail
                        } else {
                            appState.guestCheckoutEmail
                        },
                        telefonoComprador = null,
                        esInvitado = appState.signedInEmail.isBlank(),
                        funcionId = appState.currentShowtimeNumericId(),
                        seats = appState.checkoutSeatLabels(),
                        dulceria = buildList {
                            appState.selectedConcessionItems().forEach { (item, quantity) ->
                                add(
                                    MobileCheckoutConcessionItem(
                                        productoId = item.id.toIntOrNull(),
                                        comboId = null,
                                        cantidad = quantity,
                                        precioUnitario = item.price,
                                    ),
                                )
                            }
                            appState.selectedComboItems().forEach { (combo, quantity) ->
                                add(
                                    MobileCheckoutConcessionItem(
                                        productoId = null,
                                        comboId = combo.id.toIntOrNull(),
                                        cantidad = quantity,
                                        precioUnitario = combo.price,
                                    ),
                                )
                            }
                        },
                        paymentLast4 = paymentMethod?.last4 ?: "4242",
                        cvv = cvv,
                    )

                    checkoutErrorMessage = null
                    isProcessingCheckout = true
                    coroutineScope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                mobileStateApi.checkout(
                                    token = appState.authToken.ifBlank { null },
                                    payload = payload,
                                )
                            }
                        }.onSuccess { purchase ->
                            appState.registerCompletedPurchase(purchase)
                            if (payload.esInvitado) {
                                runCatching {
                                    withContext(Dispatchers.IO) { guardarCompraInvitadoLocal(contexto, purchase) }
                                }
                            }
                            runCatching {
                                withContext(Dispatchers.IO) { catalogApi.getCatalog() }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                            }
                            if (appState.signedInEmail.isNotBlank() && appState.authToken.isNotBlank()) {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        mobileStateApi.getUserState(appState.authToken)
                                    }
                                }.onSuccess { snapshot ->
                                    appState.replaceUserState(snapshot)
                                    appState.activePurchaseFolio = purchase.folio
                                }
                            }
                            navController.navigate(AppDestination.Confirmation.route)
                        }.onFailure { error ->
                            checkoutErrorMessage = apiErrorMessage(error)
                        }
                        isProcessingCheckout = false
                    }
                },
                isProcessing = isProcessingCheckout,
                errorMessage = checkoutErrorMessage,
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
            var isSeparating by remember { mutableStateOf(false) }
            var separarError by remember { mutableStateOf<String?>(null) }
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
                isSeparating = isSeparating,
                separarError = separarError,
                onSepararBoletos = { folio, seats ->
                    if (appState.authToken.isBlank()) {
                        separarError = "Inicia sesión para separar tus boletos."
                        return@TicketQrScreen
                    }
                    if (seats.isEmpty()) return@TicketQrScreen
                    separarError = null
                    isSeparating = true
                    coroutineScope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                mobileStateApi.separarBoletos(
                                    token = appState.authToken,
                                    folio = folio,
                                    seats = seats,
                                )
                            }
                        }.onSuccess { paquete ->
                            appState.applySeparatedTicketPackage(folio, paquete)
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    mobileStateApi.getUserState(appState.authToken)
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceUserState(snapshot)
                                appState.activePurchaseFolio = folio
                            }
                        }.onFailure { error ->
                            separarError = apiErrorMessage(error)
                        }
                        isSeparating = false
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
                onIniciarSesion = {
                    navController.navigate(AppDestination.Login.route) {
                        launchSingleTop = true
                    }
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
                movieId = appState.selectedMovieId.takeIf { it.isNotBlank() },
                onVolverADetalle = {
                    navController.navigate(AppDestination.MovieDetail.route)
                },
                onVolverAPerfil = {
                    navController.navigate(AppDestination.Profile.route)
                },
                onVerCartelera = {
                    navController.navigate(AppDestination.Browse.route)
                },
                onEscribirResena = {
                    navController.navigate(AppDestination.NuevaResena.route)
                },
            )
        }
        composable(AppDestination.NuevaResena.route) {
            var isSubmitting by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            NuevaResenaScreen(
                movieId = appState.selectedMovieId,
                isSubmitting = isSubmitting,
                errorMessage = errorMessage,
                onCancelar = { navController.popBackStack() },
                onEnviar = { rating, comentario ->
                    val movieId = appState.selectedMovieId
                    if (movieId.isBlank() || appState.authToken.isBlank()) {
                        errorMessage = "Inicia sesión para publicar tu reseña."
                        return@NuevaResenaScreen
                    }
                    isSubmitting = true
                    errorMessage = null
                    coroutineScope.launch {
                        val result = runCatching {
                            withContext(Dispatchers.IO) {
                                mobileStateApi.crearResena(appState.authToken, movieId, rating, comentario)
                            }
                        }
                        result.onSuccess {
                            appState.upsertMyReview(movieId, rating, comentario)
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    mobileStateApi.getUserState(appState.authToken)
                                }
                            }.onSuccess { snapshot -> appState.replaceUserState(snapshot) }
                            // Recargar el catálogo para reflejar el nuevo promedio en cartelera.
                            runCatching {
                                withContext(Dispatchers.IO) { catalogApi.getCatalog() }
                            }.onSuccess { snapshot -> appState.replaceCatalog(snapshot) }
                            isSubmitting = false
                            navController.popBackStack()
                        }.onFailure { error ->
                            isSubmitting = false
                            errorMessage = apiErrorMessage(error)
                        }
                    }
                },
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                onVerResenas = {
                    navController.navigate(AppDestination.Reviews.route)
                },
                onAdministrarPagos = {
                    navController.navigate(AppDestination.PaymentMethods.route)
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
        composable(AppDestination.PaymentMethods.route) {
            PaymentMethodsScreen(
                paymentMethods = appState.paymentMethods,
                defaultMethodId = appState.selectedPaymentMethodId,
                onSetDefault = { methodId -> appState.setDefaultPaymentMethod(methodId) },
                onRemove = { methodId ->
                    if (appState.authToken.isNotBlank()) {
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    mobileStateApi.deletePaymentMethod(appState.authToken, methodId)
                                }
                            }.onSuccess { metodos ->
                                appState.replacePaymentMethods(metodos)
                            }.onFailure {
                                appState.removePaymentMethod(methodId)
                            }
                        }
                    } else {
                        appState.removePaymentMethod(methodId)
                    }
                },
                onAddMethod = { numeroTarjeta, holderName, expiry ->
                    if (appState.authToken.isNotBlank()) {
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    mobileStateApi.savePaymentMethod(
                                        token = appState.authToken,
                                        titularTarjeta = holderName,
                                        numeroTarjeta = numeroTarjeta,
                                        vencimientoTarjeta = expiry,
                                    )
                                }
                            }.onSuccess { metodos ->
                                appState.replacePaymentMethods(metodos)
                            }.onFailure {
                                appState.addPaymentMethod(numeroTarjeta.takeLast(4), holderName, expiry)
                            }
                        }
                    } else {
                        appState.addPaymentMethod(numeroTarjeta.takeLast(4), holderName, expiry)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(AppDestination.SocialHub.route) {
            LaunchedEffect(Unit) { refreshUserState() }
            SocialHubScreen(
                friends = appState.friendUsers(),
                messages = appState.chatMessages,
                friendsCount = appState.friendIds.size,
                incomingRequestsCount = appState.incomingRequestIds.size,
                outgoingRequestsCount = appState.outgoingRequestIds.size,
                onVerSolicitudes = {
                    navController.navigate(AppDestination.Requests.route)
                },
                onVerAmigos = {
                    navController.navigate(AppDestination.Friends.route)
                },
                onIniciarSesion = {
                    navController.navigate(AppDestination.Login.route) {
                        launchSingleTop = true
                    }
                },
                onAgregarAmigo = {
                    navController.navigate(AppDestination.AddFriendByCode.route)
                },
                onOpenChat = { userId ->
                    appState.selectedChatFriendId = userId
                    navController.navigate(AppDestination.PrivateChat.route)
                },
                onVerRecomendaciones = {
                    navController.navigate(AppDestination.Recommendations.route)
                },
            )
        }
        composable(AppDestination.Requests.route) {
            LaunchedEffect(Unit) { refreshUserState() }
            RequestsScreen(
                users = appState.socialUsers,
                incomingRequestIds = appState.incomingRequestIds,
                outgoingRequestIds = appState.outgoingRequestIds,
                onAccept = { userId ->
                    appState.acceptFriend(userId)
                    runSocialAction { mobileStateApi.responderSolicitudAmistad(appState.authToken, userId, aceptar = true) }
                },
                onReject = { userId ->
                    appState.rejectFriend(userId)
                    runSocialAction { mobileStateApi.responderSolicitudAmistad(appState.authToken, userId, aceptar = false) }
                },
                onCancel = { userId ->
                    appState.cancelFriendRequest(userId)
                    runSocialAction { mobileStateApi.cancelarSolicitudAmistad(appState.authToken, userId) }
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
                    runSocialAction { mobileStateApi.enviarSolicitudAmistad(appState.authToken, userId) }
                },
                onCancel = { userId ->
                    appState.cancelFriendRequest(userId)
                    runSocialAction { mobileStateApi.cancelarSolicitudAmistad(appState.authToken, userId) }
                },
                onVerSolicitudes = {
                    navController.navigate(AppDestination.Requests.route)
                },
                onVerAmigos = {
                    navController.navigate(AppDestination.Friends.route)
                },
            )
        }
        composable(AppDestination.AddFriendByCode.route) {
            AddFriendByCodeScreen(
                users = appState.socialUsers,
                friendIds = appState.friendIds,
                incomingRequestIds = appState.incomingRequestIds,
                outgoingRequestIds = appState.outgoingRequestIds,
                myFriendCode = appState.userProfile.studentId,
                onAdd = { userId ->
                    appState.addFriendRequest(userId)
                    runSocialAction { mobileStateApi.enviarSolicitudAmistad(appState.authToken, userId) }
                },
                onCancel = { userId ->
                    appState.cancelFriendRequest(userId)
                    runSocialAction { mobileStateApi.cancelarSolicitudAmistad(appState.authToken, userId) }
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
            // Sondea el estado mientras el chat está abierto para reflejar
            // mensajes entrantes sin tener que salir y volver a entrar.
            LaunchedEffect(appState.selectedChatFriendId) {
                if (appState.authToken.isBlank()) return@LaunchedEffect
                while (true) {
                    delay(4000)
                    runCatching {
                        withContext(Dispatchers.IO) {
                            mobileStateApi.getUserState(appState.authToken)
                        }
                    }.onSuccess { snapshot -> appState.replaceUserState(snapshot) }
                }
            }
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
                        runSocialAction { mobileStateApi.enviarMensaje(appState.authToken, friend.id, text) }
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
                    runSocialAction { mobileStateApi.enviarRecomendacion(appState.authToken, friendId, movieId, note) }
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
                    onGoToCreateShowtime = {
                        navController.navigate(AppDestination.AdminNewShowtime.route)
                    },
                    onEditShowtime = { movieId, showtimeId ->
                        appState.selectAdminShowtime(movieId, showtimeId)
                        navController.navigate(AppDestination.AdminEditShowtime.route)
                    },
                    onBackToDashboard = { navController.navigate(AppDestination.AdminDashboard.route) },
                )
            }
        }
        composable(AppDestination.AdminNewShowtime.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminNewShowtimeScreen(
                    movies = appState.movies,
                    rooms = appState.adminRooms,
                    onCreateShowtime = { movieId, showtime ->
                        coroutineScope.launch {
                            val room = showtime.roomId?.let { roomId ->
                                appState.adminRooms.firstOrNull { it.id == roomId }
                            } ?: appState.adminRooms.firstOrNull { it.name == showtime.room }
                                ?: appState.adminRooms.firstOrNull()
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveShowtime(
                                        token = appState.authToken,
                                        id = null,
                                        movieId = movieId,
                                        roomId = showtime.roomId ?: room?.id ?: "1",
                                        startsAt = adminDateTimeFrom(showtime.time, showtime.startsAt),
                                        price = showtime.price,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                                navController.navigate(AppDestination.AdminShowtimes.route) {
                                    popUpTo(AppDestination.AdminNewShowtime.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    onBackToShowtimes = {
                        navController.navigate(AppDestination.AdminShowtimes.route)
                    },
                )
            }
        }
        composable(AppDestination.AdminEditShowtime.route) {
            AdminGuard(
                isAdmin = appState.isAdmin(),
                onBackToLogin = { navController.navigateToLogin() },
            ) {
                AdminEditShowtimeScreen(
                    movies = appState.movies,
                    rooms = appState.adminRooms,
                    showtimesByMovieId = appState.showtimesByMovieId,
                    initialMovieId = appState.selectedAdminShowtimeMovieId,
                    initialShowtime = appState.selectedAdminShowtime(),
                    onSaveShowtime = { movieId, showtime ->
                        coroutineScope.launch {
                            val room = showtime.roomId?.let { roomId ->
                                appState.adminRooms.firstOrNull { it.id == roomId }
                            } ?: appState.adminRooms.firstOrNull { it.name == showtime.room }
                                ?: appState.adminRooms.firstOrNull()
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.saveShowtime(
                                        token = appState.authToken,
                                        id = showtime.id,
                                        movieId = movieId,
                                        roomId = showtime.roomId ?: room?.id ?: "1",
                                        startsAt = adminDateTimeFrom(showtime.time, showtime.startsAt),
                                        price = showtime.price,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                                navController.navigate(AppDestination.AdminShowtimes.route) {
                                    popUpTo(AppDestination.AdminEditShowtime.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    onDeleteShowtime = { showtimeId ->
                        coroutineScope.launch {
                            runCatching {
                                withContext(Dispatchers.IO) {
                                    catalogApi.deleteShowtime(
                                        token = appState.authToken,
                                        id = showtimeId,
                                    )
                                }
                            }.onSuccess { snapshot ->
                                appState.replaceCatalog(snapshot)
                                navController.navigate(AppDestination.AdminShowtimes.route) {
                                    popUpTo(AppDestination.AdminEditShowtime.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    onBackToShowtimes = {
                        navController.navigate(AppDestination.AdminShowtimes.route)
                    },
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
    val datePrefix = existingStartsAt?.takeIf { it.length >= 10 }?.substring(0, 10)
        ?: LocalDate.now().toString()
    // datePrefix + safeTime es hora de pared de La Paz (UTC-7); el backend
    // almacena el instante en UTC, así que convertimos antes de enviar.
    return runCatching {
        LocalDateTime.parse("${datePrefix}T${safeTime}:00")
            .toInstant(ZoneOffset.ofHours(-7))
            .toString()
    }.getOrElse {
        "${datePrefix}T${safeTime}:00.000Z"
    }
}

private fun apiErrorMessage(error: Throwable): String {
    return when (error) {
        is ApiException -> Regex("\"error\"\\s*:\\s*\"([^\"]+)\"")
            .find(error.responseBody)
            ?.groupValues
            ?.getOrNull(1)
            ?: "Error ${error.statusCode} al consultar la API"
        else -> error.message ?: "No se pudo registrar la compra."
    }
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
        AppDestination.NuevaResena.route -> AppDestination.NuevaResena.title
        AppDestination.Profile.route -> AppDestination.Profile.title
        AppDestination.SocialHub.route -> AppDestination.SocialHub.title
        AppDestination.Requests.route -> AppDestination.Requests.title
        AppDestination.Friends.route -> AppDestination.Friends.title
        AppDestination.SearchUsers.route -> AppDestination.SearchUsers.title
        AppDestination.AddFriendByCode.route -> AppDestination.AddFriendByCode.title
        AppDestination.ChatList.route -> AppDestination.ChatList.title
        AppDestination.PrivateChat.route -> AppDestination.PrivateChat.title
        AppDestination.RecommendMovie.route -> AppDestination.RecommendMovie.title
        AppDestination.Recommendations.route -> AppDestination.Recommendations.title
        AppDestination.AdminDashboard.route -> AppDestination.AdminDashboard.title
        AppDestination.AdminMovies.route -> AppDestination.AdminMovies.title
        AppDestination.AdminMovieImport.route -> AppDestination.AdminMovieImport.title
        AppDestination.AdminShowtimes.route -> AppDestination.AdminShowtimes.title
        AppDestination.AdminNewShowtime.route -> AppDestination.AdminNewShowtime.title
        AppDestination.AdminEditShowtime.route -> AppDestination.AdminEditShowtime.title
        AppDestination.AdminConcessions.route -> AppDestination.AdminConcessions.title
        AppDestination.AdminRooms.route -> AppDestination.AdminRooms.title
        AppDestination.AdminReports.route -> AppDestination.AdminReports.title
        AppDestination.PaymentMethods.route -> AppDestination.PaymentMethods.title
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

private const val PREFS_NOMBRE = "cine_uabcs_prefs"
private const val CLAVE_COMPRAS_INVITADO = "compras_invitado"

private fun guardarCompraInvitadoLocal(contexto: Context, compra: MockPurchase) {
    val prefs = contexto.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
    val existentes = runCatching {
        JSONArray(prefs.getString(CLAVE_COMPRAS_INVITADO, "[]") ?: "[]")
    }.getOrElse { JSONArray() }

    val actualizado = JSONArray()
    for (i in 0 until existentes.length()) {
        val obj = existentes.getJSONObject(i)
        if (obj.optString("folio") != compra.folio) actualizado.put(obj)
    }
    actualizado.put(compraAJson(compra))

    prefs.edit().putString(CLAVE_COMPRAS_INVITADO, actualizado.toString()).apply()
}

private fun cargarComprasInvitadoLocal(contexto: Context): List<MockPurchase> {
    val prefs = contexto.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
    val json = prefs.getString(CLAVE_COMPRAS_INVITADO, "[]") ?: "[]"
    return runCatching {
        val arr = JSONArray(json)
        List(arr.length()) { i -> jsonACompra(arr.getJSONObject(i)) }
    }.getOrElse { emptyList() }
}

private fun compraAJson(compra: MockPurchase): JSONObject {
    val asientos = JSONArray().also { arr -> compra.seats.forEach { arr.put(it) } }
    val productos = JSONArray().also { arr ->
        compra.concessionItems.forEach { item ->
            arr.put(
                JSONObject()
                    .put("id", item.id).put("name", item.name)
                    .put("quantity", item.quantity).put("type", item.type)
            )
        }
    }
    val paquetesBoletos = JSONArray().also { arr ->
        compra.ticketPackages.forEach { pkg ->
            val pkgAsientos = JSONArray().also { s -> pkg.seats.forEach { s.put(it) } }
            arr.put(
                JSONObject()
                    .put("id", pkg.id).put("label", pkg.label)
                    .put("seats", pkgAsientos).put("qrCode", pkg.qrCode)
            )
        }
    }
    val paquetesDulceria = JSONArray().also { arr ->
        compra.concessionPackages.forEach { pkg ->
            val items = JSONArray().also { it2 ->
                pkg.items.forEach { item ->
                    it2.put(
                        JSONObject()
                            .put("id", item.id).put("name", item.name)
                            .put("quantity", item.quantity).put("type", item.type)
                    )
                }
            }
            arr.put(
                JSONObject()
                    .put("id", pkg.id).put("label", pkg.label)
                    .put("items", items).put("qrCode", pkg.qrCode)
            )
        }
    }
    return JSONObject()
        .put("folio", compra.folio)
        .put("email", compra.email)
        .put("movieId", compra.movieId)
        .put("date", compra.date)
        .put("time", compra.time)
        .put("room", compra.room)
        .put("seats", asientos)
        .put("status", compra.status)
        .put("ticketTotal", compra.ticketTotal)
        .put("concessionsTotal", compra.concessionsTotal)
        .put("qrCode", compra.qrCode)
        .put("paymentMethodLabel", compra.paymentMethodLabel)
        .put("guestPurchase", compra.guestPurchase)
        .put("concessionItems", productos)
        .put("ticketPackages", paquetesBoletos)
        .put("concessionPackages", paquetesDulceria)
        .apply { compra.qrExpiresAtMillis?.let { put("qrExpiresAtMillis", it) } }
}

private fun jsonACompra(json: JSONObject): MockPurchase {
    val asientos = json.optJSONArray("seats") ?: JSONArray()
    val productos = json.optJSONArray("concessionItems") ?: JSONArray()
    val paquetesBoletos = json.optJSONArray("ticketPackages") ?: JSONArray()
    val paquetesDulceria = json.optJSONArray("concessionPackages") ?: JSONArray()

    fun parsearItems(arr: JSONArray) = List(arr.length()) { i ->
        val o = arr.getJSONObject(i)
        MockPurchaseConcessionItem(
            id = o.optString("id"), name = o.optString("name"),
            quantity = o.optInt("quantity"), type = o.optString("type", "producto"),
        )
    }

    return MockPurchase(
        folio = json.optString("folio"),
        email = json.optString("email"),
        movieId = json.optString("movieId"),
        date = json.optString("date"),
        time = json.optString("time"),
        room = json.optString("room"),
        seats = List(asientos.length()) { asientos.getString(it) },
        status = json.optString("status"),
        ticketTotal = json.optInt("ticketTotal"),
        concessionsTotal = json.optInt("concessionsTotal"),
        qrCode = json.optString("qrCode"),
        qrExpiresAtMillis = if (json.has("qrExpiresAtMillis")) json.optLong("qrExpiresAtMillis") else null,
        paymentMethodLabel = json.optString("paymentMethodLabel"),
        guestPurchase = json.optBoolean("guestPurchase", true),
        concessionItems = parsearItems(productos),
        ticketPackages = List(paquetesBoletos.length()) { i ->
            val pkg = paquetesBoletos.getJSONObject(i)
            val pkgAsientos = pkg.optJSONArray("seats") ?: JSONArray()
            MockTicketPackage(
                id = pkg.optString("id"), label = pkg.optString("label"),
                seats = List(pkgAsientos.length()) { pkgAsientos.getString(it) },
                qrCode = pkg.optString("qrCode"),
            )
        },
        concessionPackages = List(paquetesDulceria.length()) { i ->
            val pkg = paquetesDulceria.getJSONObject(i)
            MockConcessionPackage(
                id = pkg.optString("id"), label = pkg.optString("label"),
                items = parsearItems(pkg.optJSONArray("items") ?: JSONArray()),
                qrCode = pkg.optString("qrCode"),
            )
        },
    )
}
