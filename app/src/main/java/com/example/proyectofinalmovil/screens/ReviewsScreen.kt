package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockReview
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val AmarilloEstrella = Color(0xFFFFC845)

@Composable
fun ReviewsScreen(
    movieId: String? = null,
    onVolverAPerfil: () -> Unit,
    onVerCartelera: () -> Unit,
    onVolverADetalle: () -> Unit = onVerCartelera,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val selectedMovie = movieId?.let { id -> appState.movies.find { it.id == id } }
    val filteredReviews = movieId?.let { id -> appState.reviews.filter { it.movieId == id } } ?: appState.reviews
    val myReviews = filteredReviews.filter { it.isMine }
    val communityReviews = filteredReviews.filterNot { it.isMine }
    val isMovieContext = selectedMovie != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "RESEÑAS",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isMovieContext) "Reseñas de ${selectedMovie?.title}" else "Opiniones de CineUABCS",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isMovieContext) {
                    "Consulta lo que la comunidad ha dicho sobre esta película."
                } else {
                    "Revisa tus comentarios y las reseñas recientes asociadas a películas."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.65f),
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (isMovieContext) {
                if (filteredReviews.isEmpty()) {
                    EmptyReviewsCard(
                        text = "Aún no hay reseñas para esta película.",
                    )
                } else {
                    if (myReviews.isNotEmpty()) {
                        SectionTitle("Tu reseña")
                        myReviews.forEach { review ->
                            ReviewCard(review = review)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (communityReviews.isNotEmpty()) {
                        SectionTitle("Comunidad")
                        communityReviews.forEach { review ->
                            ReviewCard(review = review)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                SectionTitle("Mis reseñas")
                if (myReviews.isEmpty()) {
                    EmptyReviewsCard(
                        text = "Aún no has escrito reseñas. Cuando califiques una película aparecerá aquí.",
                    )
                } else {
                    myReviews.forEach { review ->
                        ReviewCard(review = review)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                SectionTitle("Comunidad")
                communityReviews.forEach { review ->
                    ReviewCard(review = review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Surface(
            shadowElevation = 8.dp,
            color = FondoCrema,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                UiPrimaryButton(
                    text = if (isMovieContext) "Volver al detalle" else "Volver a perfil",
                    onClick = if (isMovieContext) onVolverADetalle else onVolverAPerfil,
                )
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = if (isMovieContext) "Ir a cartelera" else "Ver cartelera",
                    onClick = onVerCartelera,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GrisTexto,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun ReviewCard(review: MockReview) {
    val appState = LocalAppUiState.current
    val movie = appState.movies.find { it.id == review.movieId } ?: appState.movies.first()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${review.author} · ${review.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.62f),
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (review.isMine) AzulAccion.copy(alpha = 0.12f) else FondoCrema,
                ) {
                    Text(
                        text = if (review.isMine) "Mía" else "UABCS",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AzulAccion,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            StarsRow(rating = review.rating)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.78f),
            )
        }
    }
}

@Composable
private fun StarsRow(rating: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = if (index < rating) AmarilloEstrella else BordeCard,
            )
        }
    }
}

@Composable
private fun EmptyReviewsCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto.copy(alpha = 0.65f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun ReviewsScreenPreview() {
    ProyectoFinalMovilTheme {
        ReviewsScreen(
            movieId = null,
            onVolverADetalle = {},
            onVolverAPerfil = {},
            onVerCartelera = {},
        )
    }
}
