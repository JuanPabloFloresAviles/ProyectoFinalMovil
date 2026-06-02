package com.example.proyectofinalmovil.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun MovieDetailScreen(
    movieId: String,
    onElegirFuncion: (String) -> Unit,
    onVerResenas: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val pelicula = appState.movies.find { it.id == movieId } ?: appState.movies.first()
    val sinopsis = appState.synopsisFor(movieId)
    val reparto = appState.castFor(movieId)

    var sinopsisExpandida by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "DESDE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$45",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MXN",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                    }

                    UiPrimaryButton(
                        text = "Elegir función",
                        onClick = { onElegirFuncion(movieId) },
                        fillWidth = false,
                        modifier = Modifier.width(200.dp),
                    )
                }
            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
        ) {

            PosterHeader(pelicula = pelicula)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                ) {
                    Text(
                        text = pelicula.classification,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = pelicula.duration,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "·",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "Estreno ${pelicula.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = pelicula.rating,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = CinemaBlue,
                        )
                        Text(
                            text = "/5",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        val rating = pelicula.rating.toFloat()
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (index < rating.toInt()) Color(0xFFFFD245)
                                else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = { onVerResenas(movieId) }) {
                        Text(
                            text = "284 reseñas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "de estudiantes UABCS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 12.dp),
                    )
                }

                IconButton(onClick = { onVerResenas(movieId) }) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Escribir reseña",
                            modifier = Modifier.padding(8.dp).size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "SINOPSIS",
                    style = MaterialTheme.typography.labelSmall,
                    color = CinemaBlue,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = sinopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = if (sinopsisExpandida) Int.MAX_VALUE else 4,
                )

                if (!sinopsisExpandida) {
                    TextButton(
                        onClick = { sinopsisExpandida = true },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "Leer más",
                            color = CinemaBlue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (reparto.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "DIRECCIÓN Y REPARTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = CinemaBlue,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val coloresAvatar = listOf(
                            Color(0xFF4AB07A),
                            Color(0xFFD84545),
                            Color(0xFFFFB245),
                            Color(0xFF2E5D8C),
                        )
                        reparto.forEachIndexed { index, nombre ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    coloresAvatar[index % coloresAvatar.size],
                                                    coloresAvatar[index % coloresAvatar.size].copy(alpha = 0.6f),
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = nombre.first().toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = nombre,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun PosterHeader(
    pelicula: MockMovie,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(
                Brush.verticalGradient(
                    colors = listOf(pelicula.accentStart, pelicula.accentEnd),
                )
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "${pelicula.genre.uppercase()} · ${pelicula.classification} · SUBTITULADOS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pelicula.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun MovieDetailScreenPreview() {
    ProyectoFinalMovilTheme {
        MovieDetailScreen(
            movieId = "estacion-7",
            onElegirFuncion = {},
            onVerResenas = {},
        )
    }
}
