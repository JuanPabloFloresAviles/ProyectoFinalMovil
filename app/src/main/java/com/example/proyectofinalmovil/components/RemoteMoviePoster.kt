package com.example.proyectofinalmovil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.proyectofinalmovil.services.mock.MockMovie

@Composable
fun RemoteMoviePoster(
    movie: MockMovie,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(movie.accentStart, movie.accentEnd),
            ),
        ),
    ) {
        if (!movie.posterUrl.isNullOrBlank()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "Poster de ${movie.title}",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
