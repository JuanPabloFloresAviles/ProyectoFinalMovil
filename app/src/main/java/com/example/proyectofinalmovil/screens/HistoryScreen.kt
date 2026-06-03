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
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val VerdeSuave = Color(0xFFE7F5E8)

@Composable
fun HistoryScreen(
    onVerBoleto: (String) -> Unit,
    onRecuperarCompra: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current

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
                text = "HISTORIAL",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tus compras recientes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Consulta boletos activos, compras usadas y folios para recuperar compras invitadas.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.65f),
            )
            Spacer(modifier = Modifier.height(18.dp))

            appState.purchases.forEach { purchase ->
                PurchaseCard(
                    purchase = purchase,
                    movieTitle = appState.movieForPurchase(purchase).title,
                    onVerBoleto = { onVerBoleto(purchase.folio) },
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                    text = "Recuperar compra",
                    onClick = onRecuperarCompra,
                )
            }
        }
    }
}

@Composable
private fun PurchaseCard(
    purchase: MockPurchase,
    movieTitle: String,
    onVerBoleto: () -> Unit,
) {
    val statusColor = if (purchase.status == "Activa") AzulAccion else GrisTexto.copy(alpha = 0.55f)

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
                        text = movieTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${purchase.date} · ${purchase.time} · ${purchase.room}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.62f),
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (purchase.status == "Activa") VerdeSuave else FondoCrema,
                ) {
                    Text(
                        text = purchase.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            DetailRow("Folio", purchase.folio)
            DetailRow("Asientos", purchase.seats.joinToString(", "))
            DetailRow("Total", "\$${purchase.total}")
            Spacer(modifier = Modifier.height(12.dp))
            UiGhostButton(
                text = if (purchase.status == "Activa") "Ver boleto QR" else "Ver detalle",
                onClick = onVerBoleto,
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto.copy(alpha = 0.55f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = GrisTexto,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun HistoryScreenPreview() {
    ProyectoFinalMovilTheme {
        HistoryScreen(
            onVerBoleto = {},
            onRecuperarCompra = {},
        )
    }
}
