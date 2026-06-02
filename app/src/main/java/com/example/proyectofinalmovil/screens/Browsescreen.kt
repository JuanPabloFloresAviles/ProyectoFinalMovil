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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun BrowseScreen(
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val destacada = appState.movies.first { it.isFeatured }

    var filtroActivo by remember { mutableStateOf("Estrenos") }

    val peliculasFiltradas = remember(filtroActivo) {
        when (filtroActivo) {
            "Todo" -> appState.movies.filter { !it.isFeatured }
            "Estrenos" -> appState.movies.filter { it.isNew && !it.isFeatured }
            else -> appState.movies.filter { it.genre == filtroActivo && !it.isFeatured }
        }
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
                    text = "JUEVES · 14 MAYO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Hola, Alejandra",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                IconButton(onClick = {}) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = CinemaBlue)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notificaciones",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
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
            TextButton(onClick = { /* ver todo — pendiente */ }) {
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
private fun PeliculaDestacadaCard(
    movie: MockMovie,
    onComprarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var esFavorita by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.verticalGradient(
                    colors = listOf(movie.accentStart, movie.accentEnd),
                )
            )
            .aspectRatio(4f / 3f),
    ) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onComprarClick,
                    modifier = Modifier.weight(1f),
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

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { esFavorita = !esFavorita },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (esFavorita) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = if (esFavorita) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (esFavorita) Color(0xFFD84545) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(movie.accentStart, movie.accentEnd),
                )
            )
            .clickable(onClick = onClick),
    ) {
        Text(
            text = "CineUABCS · ${movie.year}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
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
        BrowseScreen(onMovieClick = {})
    }
}
