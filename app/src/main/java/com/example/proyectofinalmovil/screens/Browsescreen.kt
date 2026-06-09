package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.RemoteMoviePoster
import com.example.proyectofinalmovil.components.UiLoader
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BrowseScreen(
    onMovieClick: (String) -> Unit,
    onVerTodo: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
) {
    val appState = LocalAppUiState.current
    if (appState.movies.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (errorMessage != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaBlue,
                            contentColor = Color.White,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = "Reintentar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                UiLoader(text = "Cargando cartelera desde la base de datos...")
            }
        }
        return
    }

    // Solo entran a cartelera las películas con al menos una función vigente.
    // El catálogo ya descarta las funciones que terminaron, así que una lista de
    // funciones vacía significa que la película no tiene ninguna próxima función.
    val peliculasEnCartelera = appState.movies.filter { appState.showtimesFor(it.id).isNotEmpty() }
    val destacada = peliculasEnCartelera.firstOrNull { it.isFeatured }
        ?: peliculasEnCartelera.firstOrNull()
        ?: appState.movies.first()
    val todayLabel = remember {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE · d MMMM", Locale("es", "MX")))
            .uppercase(Locale("es", "MX"))
    }
    val greetingName = appState.userProfile.name
        .ifBlank { appState.signedInName }
        .trim()
        .substringBefore(" ")
        .ifBlank { "bienvenido" }
    val greetingText = if (greetingName.equals("bienvenido", ignoreCase = true)) {
        "Hola"
    } else {
        "Hola, $greetingName"
    }

    var filtroActivo by remember { mutableStateOf("Estrenos") }

    val peliculasFiltradas = when (filtroActivo) {
        "Todo" -> peliculasEnCartelera.filter { !it.isFeatured }
        "Estrenos" -> peliculasEnCartelera.filter { it.isNew && !it.isFeatured }
        else -> peliculasEnCartelera.filter { it.genre == filtroActivo && !it.isFeatured }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = todayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Text(
                text = "${peliculasEnCartelera.size} películas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        PeliculaDestacadaCard(
            movie = destacada,
            onComprarClick = { onMovieClick(destacada.id) },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            appState.genres.forEach { genero ->
                FilterChip(
                    selected = genero == filtroActivo,
                    onClick = { filtroActivo = genero },
                    label = {
                        Text(
                            text = genero,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (genero == filtroActivo) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CinemaBlue,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = genero == filtroActivo,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = CinemaBlue,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Estrenos de la semana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onVerTodo) {
                Text(
                    text = "Ver todo",
                    color = CinemaBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(peliculasFiltradas) { pelicula ->
                TarjetaPelicula(
                    movie = pelicula,
                    onClick = { onMovieClick(pelicula.id) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AllShowtimesScreen(
    onShowtimeClick: (movieId: String, showtimeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    var selectedDayOffset by remember { mutableStateOf(0) }
    val selectedDateKey = remember(selectedDayOffset) { dateKeyForOffset(selectedDayOffset) }
    val moviesWithShowtimes = appState.movies
        .filter { movie ->
            appState.showtimesFor(movie.id).any { it.matchesDate(selectedDateKey, selectedDayOffset) }
        }
        .sortedWith(compareByDescending<MockMovie> { it.isFeatured }.thenBy { it.title })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AllShowtimesBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ShowtimesDateHeader(
            dayOffset = selectedDayOffset,
            onPreviousDay = { selectedDayOffset = (selectedDayOffset - 1).coerceAtLeast(0) },
            onNextDay = { selectedDayOffset += 1 },
        )

        if (moviesWithShowtimes.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, AllShowtimesBorder),
            ) {
                Text(
                    text = "No hay funciones disponibles.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AllShowtimesMuted,
                    modifier = Modifier.padding(18.dp),
                )
            }
        } else {
            moviesWithShowtimes.forEach { movie ->
                MovieScheduleBlock(
                    movie = movie,
                    showtimes = appState.showtimesFor(movie.id)
                        .filter { it.matchesDate(selectedDateKey, selectedDayOffset) }
                        .distinctBy { it.id?.takeIf { id -> id.isNotBlank() } ?: "${it.startsAt}-${it.time}-${it.room}" }
                        .sortedBy { it.startsAt ?: it.time },
                    synopsis = appState.synopsisFor(movie.id).takeIf { it != "Sinopsis no disponible." }
                        ?: movie.synopsis,
                    onShowtimeClick = { showtime ->
                        onShowtimeClick(
                            movie.id,
                            showtime.id?.takeIf { it.isNotBlank() }
                                ?: "${movie.id}|${showtime.startsAt ?: ""}|${showtime.roomId ?: showtime.room}|${showtime.time}",
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ShowtimesDateHeader(
    dayOffset: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DateNavButton(
            text = "‹",
            enabled = dayOffset > 0,
            onClick = onPreviousDay,
        )
        Surface(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, AllShowtimesBorder),
        ) {
            Text(
                text = dateLabelForOffset(dayOffset),
                modifier = Modifier.padding(vertical = 14.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AllShowtimesInk,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
        DateNavButton(
            text = "›",
            enabled = true,
            onClick = onNextDay,
        )
    }
}

@Composable
private fun DateNavButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AllShowtimesBorder),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (enabled) CinemaBlue else AllShowtimesMuted.copy(alpha = 0.45f),
        )
    }
}

@Composable
private fun MovieScheduleBlock(
    movie: MockMovie,
    showtimes: List<MockShowtime>,
    synopsis: String,
    onShowtimeClick: (MockShowtime) -> Unit,
) {
    var showSynopsis by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AllShowtimesBorder),
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                RemoteMoviePoster(
                    movie = movie,
                    modifier = Modifier
                        .width(104.dp)
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.medium),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CinemaBlue,
                        ) {
                            Text(
                                text = movie.classification,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                        }
                        Text(
                            text = "|  ${movie.duration}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = AllShowtimesMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AllShowtimesInk,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (synopsis.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showSynopsis = !showSynopsis },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text(
                                text = if (showSynopsis) "Ocultar sinopsis" else "Ver sinopsis",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = CinemaBlue,
                            )
                        }
                        if (showSynopsis) {
                            Text(
                                text = synopsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AllShowtimesMuted,
                            )
                        }
                    }
                }
            }

            ScheduleGroup(
                title = "ESPAÑOL",
                showtimes = showtimes.filterNot { it.format.contains("Subt", ignoreCase = true) },
                onShowtimeClick = onShowtimeClick,
            )
            ScheduleGroup(
                title = "SUBTITULADA",
                showtimes = showtimes.filter { it.format.contains("Subt", ignoreCase = true) },
                onShowtimeClick = onShowtimeClick,
            )
        }
    }
}

@Composable
private fun ScheduleGroup(
    title: String,
    showtimes: List<MockShowtime>,
    onShowtimeClick: (MockShowtime) -> Unit,
) {
    if (showtimes.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AllShowtimesInk,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        showtimes.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { showtime ->
                    ShowtimePill(
                        showtime = showtime,
                        onClick = { onShowtimeClick(showtime) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ShowtimePill(
    showtime: MockShowtime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = MaterialTheme.shapes.medium,
        color = CinemaBlue.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaBlue.copy(alpha = 0.22f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = showtime.time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = CinemaBlue,
            )
        }
    }
}

private fun MockShowtime.matchesDate(dateKey: String, selectedDayOffset: Int): Boolean {
    val startsAtKey = laPazDateKey(startsAt)
    if (startsAtKey == null) return selectedDayOffset == 0
    return startsAtKey == dateKey
}

private fun dateKeyForOffset(dayOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}

private fun dateLabelForOffset(dayOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
    val date = SimpleDateFormat("dd MMM", Locale.forLanguageTag("es-MX")).format(calendar.time)
    return when (dayOffset) {
        0 -> "Hoy $date"
        1 -> "Mañana $date"
        else -> date.replaceFirstChar { char -> char.uppercase() }
    }
}

private fun laPazDateKey(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        Instant.parse(value)
            .atOffset(ZoneOffset.ofHours(-7))
            .toLocalDate()
            .toString()
    }.getOrElse {
        value.takeIf { it.length >= 10 }?.substring(0, 10)
    }
}

@Composable
private fun PeliculaDestacadaCard(
    movie: MockMovie,
    onComprarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .aspectRatio(4f / 3f),
    ) {
        RemoteMoviePoster(
            movie = movie,
            modifier = Modifier.fillMaxSize(),
        )
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFD245),
        ) {
            Text(
                text = "★ Película destacada",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A3A00),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000)),
                    )
                )
                .padding(16.dp),
        ) {
            Text(
                text = "${movie.genre.uppercase()} · ${movie.classification}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Badge de clasificación
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CinemaBlue,
                ) {
                    Text(
                        text = movie.classification,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }

                Text(
                    text = movie.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                )

                Text(text = "·", color = Color.White.copy(alpha = 0.5f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = if (index < movie.rating.toFloat().toInt()) Color(0xFFFFD245) else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = movie.rating,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onComprarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinemaBlue,
                    contentColor = Color.White,
                ),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                Text(
                    text = "Comprar boletos",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun TarjetaPelicula(
    movie: MockMovie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(130.dp)
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
    ) {
        RemoteMoviePoster(
            movie = movie,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xDD000000)),
                    )
                )
                .padding(10.dp),
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontStyle = if (movie.title.contains("Memorias")) FontStyle.Italic else FontStyle.Normal,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun BrowseScreenPreview() {
    ProyectoFinalMovilTheme {
        BrowseScreen(onMovieClick = {}, onVerTodo = {})
    }
}

private val AllShowtimesBackground = Color(0xFFF9F6EB)
private val AllShowtimesInk = Color(0xFF102A43)
private val AllShowtimesBorder = Color(0xFFD6D1C2)
private val AllShowtimesMuted = Color(0xFF6B7280)
