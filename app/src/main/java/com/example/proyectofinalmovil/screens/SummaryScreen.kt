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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockConcessionItem
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

// Colores de la pantalla (misma paleta del diseño)
private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val AzulClaro = Color(0xFF3A9BC7)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)

/**
 * Pantalla de resumen de compra.
 * Muestra el desglose de boletos y dulcería con el total general
 * antes de confirmar la compra.
 */
@Composable
fun SummaryScreen(
    onConfirmarCompra: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val pelicula = appState.currentMovie()
    val funcion = appState.currentShowtime()
    val asientosEjemplo = appState.checkoutSeatLabels()
    val precioBoleto = funcion.price
    val subtotalBoletos = appState.ticketTotal()
    val itemsDulceria = appState.selectedConcessionItems().map { (producto, cantidad) ->
        DulceriaResumenItem(producto, cantidad)
    }
    val subtotalDulceria = appState.concessionTotal()
    val totalGeneral = appState.totalToPay()

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
        ) {

            // Encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "RESUMEN DE COMPRA",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Revisa tu pedido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de boletos
            SeccionResumen(titulo = "Boletos") {
                // Info de la película
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = pelicula.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${funcion.room} · ${funcion.time} · ${funcion.format}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTexto.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asientos: ${asientosEjemplo.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTexto.copy(alpha = 0.6f),
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Desglose boletos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${asientosEjemplo.size} boleto${if (asientosEjemplo.size > 1) "s" else ""} × $$precioBoleto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto.copy(alpha = 0.7f),
                    )
                    Text(
                        text = "$$subtotalBoletos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección de dulcería
            SeccionResumen(titulo = "Dulcería") {
                itemsDulceria.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${item.producto.name} × ${item.cantidad}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto.copy(alpha = 0.7f),
                        )
                        Text(
                            text = "$${item.cantidad * item.producto.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total general
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = AzulAccion.copy(alpha = 0.08f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Total a pagar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$$totalGeneral",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AzulAccion,
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "MXN",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrisTexto.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Barra inferior fija
        Surface(
            shadowElevation = 8.dp,
            color = FondoCrema,
        ) {
            UiPrimaryButton(
                text = "Confirmar compra  ›",
                onClick = onConfirmarCompra,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

private data class DulceriaResumenItem(
    val producto: MockConcessionItem,
    val cantidad: Int,
)

/**
 * Sección con título y contenido dentro de una tarjeta.
 */
@Composable
private fun SeccionResumen(
    titulo: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = titulo.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = AzulClaro,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = BordeCard,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun SummaryScreenPreview() {
    ProyectoFinalMovilTheme {
        SummaryScreen(
            onConfirmarCompra = {},
        )
    }
}
