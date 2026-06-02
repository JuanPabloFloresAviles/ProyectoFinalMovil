package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val VerdeSuave = Color(0xFFE7F5E8)
private val RojoSuave = Color(0xFFFFE9E5)

@Composable
fun RecoverPurchaseScreen(
    onVerBoleto: () -> Unit,
    onIrAlHistorial: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    var folio by remember { mutableStateOf("CINE-2026-4A7F") }
    var email by remember { mutableStateOf("invitado@cineuabcs.mx") }
    var searchDone by remember { mutableStateOf(false) }

    val recoveredPurchase = remember(folio, email, searchDone) {
        if (!searchDone) {
            null
        } else {
            appState.recoverPurchase(folio, email)
        }
    }

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
                text = "RECUPERAR",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recupera tu compra",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ingresa el folio y correo usados al comprar como invitado.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.65f),
            )
            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    UiInput(
                        value = folio,
                        onValueChange = {
                            folio = it
                            searchDone = false
                        },
                        label = "Folio",
                        placeholder = "CINE-2026-4A7F",
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    UiInput(
                        value = email,
                        onValueChange = {
                            email = it
                            searchDone = false
                        },
                        label = "Correo",
                        placeholder = "correo@ejemplo.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    UiPrimaryButton(
                        text = "Buscar compra",
                        onClick = { searchDone = true },
                        enabled = folio.isNotBlank() && email.isNotBlank(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                recoveredPurchase != null -> RecoveredPurchaseCard(
                    purchase = recoveredPurchase,
                    onVerBoleto = onVerBoleto,
                )
                searchDone -> SearchMessage(
                    title = "No encontramos esa compra",
                    body = "Revisa que el folio y el correo estén escritos igual que en tu confirmación.",
                    background = RojoSuave,
                )
                else -> SearchMessage(
                    title = "Dato de prueba",
                    body = "Puedes buscar CINE-2026-4A7F con invitado@cineuabcs.mx para simular una recuperación.",
                    background = VerdeSuave,
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
                UiGhostButton(
                    text = "Ir al historial",
                    onClick = onIrAlHistorial,
                )
            }
        }
    }
}

@Composable
private fun RecoveredPurchaseCard(
    purchase: MockPurchase,
    onVerBoleto: () -> Unit,
) {
    val appState = LocalAppUiState.current
    val movie = appState.movieForPurchase(purchase)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Compra encontrada",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AzulAccion,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${purchase.date} · ${purchase.time} · ${purchase.room}",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.65f),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Asientos: ${purchase.seats.joinToString(", ")} · Total: \$${purchase.total}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(14.dp))
            UiPrimaryButton(
                text = "Ver boleto QR",
                onClick = onVerBoleto,
            )
        }
    }
}

@Composable
private fun SearchMessage(
    title: String,
    body: String,
    background: Color,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = background,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.72f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun RecoverPurchaseScreenPreview() {
    ProyectoFinalMovilTheme {
        RecoverPurchaseScreen(
            onVerBoleto = {},
            onIrAlHistorial = {},
        )
    }
}
