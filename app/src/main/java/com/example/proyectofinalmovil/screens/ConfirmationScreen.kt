package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

// Colores de la pantalla (misma paleta del diseño)
private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val VerdeExito = Color(0xFF2E7D32)
private val BordeCard = Color(0xFFD6D1C2)

/**
 * Pantalla de confirmación de compra exitosa.
 * Muestra un indicador de éxito, datos de la función y folio generado.
 */
@Composable
fun ConfirmationScreen(
    onVerBoleto: () -> Unit,
    onVolverACartelera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val compra = appState.activePurchase()
    val pelicula = appState.movieForPurchase(compra)
    val funcion = appState.showtimesFor(compra.movieId).firstOrNull { it.time == compra.time }
        ?: appState.showtimesFor(compra.movieId).first()
    val asientos = compra.seats
    val folio = compra.folio
    val snackSummary = compra.concessionItems.joinToString(" · ") { "${it.quantity} ${it.name}" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {

        // Contenido con scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // Icono de éxito
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(VerdeExito),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Compra exitosa",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mensaje principal
            Text(
                text = "¡Compra exitosa!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu compra ha sido procesada correctamente",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tarjeta con datos de la función
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = BordeCard,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                ) {
                    // Película
                    FilaDato(etiqueta = "Película", valor = pelicula.title)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sala y horario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "Sala",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrisTexto.copy(alpha = 0.5f),
                            )
                            Text(
                                text = funcion.room,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GrisTexto,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Horario",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrisTexto.copy(alpha = 0.5f),
                            )
                            Text(
                                text = funcion.time,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GrisTexto,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Asientos
                    FilaDato(etiqueta = "Asientos", valor = asientos.joinToString(", "))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Folio
                    FilaDato(etiqueta = "Folio", valor = folio)

                    Spacer(modifier = Modifier.height(12.dp))

                    FilaDato(etiqueta = "Pago", valor = compra.paymentMethodLabel)
                    if (compra.guestPurchase) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaDato(etiqueta = "Recuperación", valor = compra.email)
                    }
                    if (snackSummary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaDato(etiqueta = "Dulcería", valor = snackSummary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (compra.ticketPackages.isNotEmpty() || compra.concessionPackages.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Paquetes generados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        compra.ticketPackages.forEach { paquete ->
                            Text(
                                text = "${paquete.label}: ${paquete.seats.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisTexto.copy(alpha = 0.75f),
                            )
                        }
                        compra.concessionPackages.forEach { paquete ->
                            val detalle = paquete.items.joinToString(", ") { "${it.quantity} ${it.name}" }
                            Text(
                                text = "${paquete.label}: $detalle",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisTexto.copy(alpha = 0.75f),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Barra inferior fija
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
                    text = "Ver mi boleto  ›",
                    onClick = onVerBoleto,
                )

                Spacer(modifier = Modifier.height(8.dp))

                UiGhostButton(
                    text = "Volver a cartelera",
                    onClick = onVolverACartelera,
                )
            }
        }
    }
}

/**
 * Fila simple con etiqueta y valor para los datos de la confirmación.
 */
@Composable
private fun FilaDato(
    etiqueta: String,
    valor: String,
) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = GrisTexto.copy(alpha = 0.5f),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = GrisTexto,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun ConfirmationScreenPreview() {
    ProyectoFinalMovilTheme {
        ConfirmationScreen(
            onVerBoleto = {},
            onVolverACartelera = {},
        )
    }
}
