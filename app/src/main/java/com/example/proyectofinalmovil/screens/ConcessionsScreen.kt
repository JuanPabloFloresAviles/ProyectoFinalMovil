package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
 * Pantalla de dulcería / concesiones.
 * Permite al usuario agregar productos como palomitas, refrescos, etc.
 * antes de continuar al resumen de compra.
 */
@Composable
fun ConcessionsScreen(
    onIrAlResumen: () -> Unit,
    onSaltarDulceria: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val cantidades = appState.concessionQuantities
    val totalProductos = cantidades.values.sum()
    val totalMonto = appState.concessionTotal()

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
                    text = "DULCERÍA",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¿Se te antoja algo?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de productos
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                appState.concessions.forEach { item ->
                    val cantidad = cantidades[item.id] ?: 0
                    TarjetaProducto(
                        item = item,
                        cantidad = cantidad,
                        onIncrementar = {
                            appState.setConcessionQuantity(item.id, cantidad + 1)
                        },
                        onDecrementar = {
                            if (cantidad > 0) {
                                appState.setConcessionQuantity(item.id, cantidad - 1)
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                // Resumen de lo agregado
                if (totalProductos > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "$totalProductos producto${if (totalProductos > 1) "s" else ""} agregado${if (totalProductos > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto.copy(alpha = 0.7f),
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$$totalMonto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulClaro,
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "MXN",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrisTexto.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 2.dp),
                            )
                        }
                    }
                }

                // Botones
                UiPrimaryButton(
                    text = "Ir al resumen  ›",
                    onClick = onIrAlResumen,
                )

                Spacer(modifier = Modifier.height(8.dp))

                UiGhostButton(
                    text = "Saltar dulcería",
                    onClick = onSaltarDulceria,
                )
            }
        }
    }
}

/**
 * Tarjeta individual de producto de dulcería con selector de cantidad.
 */
@Composable
private fun TarjetaProducto(
    item: MockConcessionItem,
    cantidad: Int,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (cantidad > 0) AzulAccion.copy(alpha = 0.4f) else BordeCard,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Info del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTexto.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$${item.price} MXN",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulClaro,
                )
            }

            // Selector de cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Botón decrementar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (cantidad > 0) AzulAccion.copy(alpha = 0.1f) else Color(0xFFF2EFE4)
                        )
                        .clickable(enabled = cantidad > 0) { onDecrementar() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (cantidad > 0) AzulAccion else GrisTexto.copy(alpha = 0.3f),
                    )
                }

                // Cantidad actual
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center,
                )

                // Botón incrementar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AzulAccion)
                        .clickable { onIncrementar() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun ConcessionsScreenPreview() {
    ProyectoFinalMovilTheme {
        ConcessionsScreen(
            onIrAlResumen = {},
            onSaltarDulceria = {},
        )
    }
}
