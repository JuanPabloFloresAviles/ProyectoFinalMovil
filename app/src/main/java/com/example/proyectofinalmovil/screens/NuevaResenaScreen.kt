package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val AmarilloEstrella = Color(0xFFFFC845)

/**
 * Pantalla para escribir/editar la reseña y calificación de una película.
 */
@Composable
fun NuevaResenaScreen(
    movieId: String,
    onEnviar: (rating: Int, comentario: String) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
) {
    val appState = LocalAppUiState.current
    val movie = appState.movies.find { it.id == movieId }
    val existente = appState.myReviewFor(movieId)

    var rating by remember { mutableIntStateOf(existente?.rating ?: 0) }
    var comentario by remember { mutableStateOf(existente?.comment ?: "") }

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
                text = if (existente != null) "EDITAR RESEÑA" else "NUEVA RESEÑA",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie?.title ?: "Califica esta película",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Comparte tu opinión con la comunidad de CineUABCS.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.65f),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tu calificación",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(5) { index ->
                    val value = index + 1
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Calificar con $value estrellas",
                        tint = if (value <= rating) AmarilloEstrella else BordeCard,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = !isSubmitting) { rating = value },
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tu reseña",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !isSubmitting,
                placeholder = {
                    Text(
                        text = "¿Qué te pareció la película?",
                        color = GrisTexto.copy(alpha = 0.55f),
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulAccion,
                    unfocusedBorderColor = BordeCard,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC0392B),
                )
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
                    text = if (isSubmitting) "Publicando..." else "Publicar reseña",
                    onClick = { onEnviar(rating, comentario.trim()) },
                    enabled = rating in 1..5 && comentario.isNotBlank() && !isSubmitting,
                )
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = "Cancelar",
                    onClick = onCancelar,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun NuevaResenaScreenPreview() {
    ProyectoFinalMovilTheme {
        NuevaResenaScreen(
            movieId = "",
            onEnviar = { _, _ -> },
            onCancelar = {},
        )
    }
}
