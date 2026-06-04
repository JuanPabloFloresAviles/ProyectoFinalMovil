package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiLoader
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.api.TmdbApi
import com.example.proyectofinalmovil.services.api.TmdbMovieCandidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminMovieImportScreen(
    tmdbApi: TmdbApi,
    onImportMovie: (TmdbMovieCandidate) -> Unit,
    onBackToMovies: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val movies = remember { mutableStateListOf<TmdbMovieCandidate>() }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadMovies(search: String = query) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            runCatching {
                withContext(Dispatchers.IO) {
                    if (search.isBlank()) tmdbApi.getNowPlaying() else tmdbApi.searchMovies(search)
                }
            }.onSuccess { result ->
                movies.clear()
                movies.addAll(result)
                if (result.isEmpty() && tmdbApi.isConfigured()) {
                    errorMessage = "No encontramos películas para esa búsqueda."
                }
            }.onFailure { error ->
                errorMessage = error.message ?: "No se pudo consultar TMDB."
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (tmdbApi.isConfigured()) {
            loadMovies("")
        } else {
            errorMessage = "Configura TMDB_API_KEY para consultar la API de películas desde Android."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Importar desde TMDB",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF102A43),
        )
        Text(
            text = "La app móvil consulta TMDB directamente y guarda la película elegida en TiDB.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF102A43).copy(alpha = 0.72f),
        )
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                errorMessage = null
            },
            label = { Text("Buscar película") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { loadMovies(query) }),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UiPrimaryButton(
                text = "Buscar",
                onClick = { loadMovies(query) },
                modifier = Modifier.weight(1f),
            )
            UiGhostButton(
                text = "Volver",
                onClick = onBackToMovies,
                modifier = Modifier.weight(1f),
            )
        }

        when {
            isLoading -> UiLoader(text = "Consultando TMDB desde Android...")
            errorMessage != null -> AdminImportMessage(errorMessage.orEmpty())
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(movies, key = { it.tmdbId }) { movie ->
                    TmdbImportCard(
                        movie = movie,
                        onImport = { onImportMovie(movie) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TmdbImportCard(
    movie: TmdbMovieCandidate,
    onImport: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFF1E5AA8).copy(alpha = 0.16f)),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(86.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E5AA8), Color(0xFF102A43)),
                        ),
                    ),
            ) {
                if (!movie.posterUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "Poster de ${movie.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF102A43),
                )
                Text(
                    text = "${movie.releaseYear} · ${classificationFromTmdb(movie)} · TMDB ${String.format("%.1f", movie.voteAverage)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF1E5AA8),
                )
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF102A43).copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                UiPrimaryButton(
                    text = "Importar",
                    onClick = onImport,
                )
            }
        }
    }
}

@Composable
private fun AdminImportMessage(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFF1E5AA8).copy(alpha = 0.2f)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF102A43),
            modifier = Modifier.padding(14.dp),
        )
    }
}

fun classificationFromTmdb(movie: TmdbMovieCandidate): String {
    if (movie.adult) return "C"
    if (movie.voteAverage >= 8) return "A"
    if (movie.voteAverage >= 6) return "B"
    return "B15"
}
