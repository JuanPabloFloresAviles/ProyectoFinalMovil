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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.CardNumberVisualTransformation
import com.example.proyectofinalmovil.components.ExpiryVisualTransformation
import com.example.proyectofinalmovil.components.expiryError
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.components.formatExpiry
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
    onConfirmarCompra: (cvv: String) -> Unit,
    isProcessing: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    var showingPaymentForm by remember { mutableStateOf(false) }
    var newCardNumber by remember { mutableStateOf("") }
    var newCardHolder by remember { mutableStateOf(appState.signedInName) }
    var newCardExpiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    val pelicula = appState.currentMovie()
    val funcion = appState.currentShowtime()
    val fechaFuncion = appState.currentShowtimeDateLabel()
    val asientosEjemplo = appState.checkoutSeatLabels()
    val precioBoleto = funcion.price
    val subtotalBoletos = appState.ticketTotal()
    val itemsDulceria = appState.selectedConcessionItems().map { (producto, cantidad) ->
        DulceriaResumenItem(producto, cantidad)
    }
    val itemsCombo = appState.selectedComboItems()
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
                    text = "$fechaFuncion · ${funcion.room} · ${funcion.time} · ${funcion.format}",
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
                if (itemsDulceria.isEmpty() && itemsCombo.isEmpty()) {
                    Text(
                        text = "Sin productos agregados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto.copy(alpha = 0.62f),
                    )
                } else {
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
                    itemsCombo.forEach { (combo, cantidad) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${combo.name} × $cantidad",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrisTexto.copy(alpha = 0.7f),
                                )
                                val detalle = appState.comboProductNames(combo).joinToString(", ")
                                if (detalle.isNotBlank()) {
                                    Text(
                                        text = detalle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTexto.copy(alpha = 0.5f),
                                    )
                                }
                            }
                            Text(
                                text = "$${cantidad * combo.price}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GrisTexto,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionResumen(titulo = "Pago") {
                if (appState.signedInEmail.isBlank()) {
                    val correoInvitado = appState.guestCheckoutEmail.trim()
                    val correoInvalido = !esCorreoValido(correoInvitado)
                    UiInput(
                        value = appState.guestCheckoutEmail,
                        onValueChange = { appState.updateGuestCheckoutEmail(it) },
                        label = "Correo para recuperar la compra",
                        placeholder = "correo@ejemplo.com",
                        isError = correoInvitado.isNotEmpty() && correoInvalido,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (correoInvalido) {
                            "Ingresa un correo válido: lo necesitas para recuperar tus boletos y dulcería más tarde."
                        } else {
                            "Se guardará junto al folio para recuperar boletos y dulcería más tarde."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (correoInvalido) {
                            MaterialTheme.colorScheme.error
                        } else {
                            GrisTexto.copy(alpha = 0.62f)
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "Método de pago",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (showingPaymentForm) {
                    UiInput(
                        value = newCardNumber,
                        onValueChange = { value ->
                            newCardNumber = value.filter { it.isDigit() }.take(16)
                        },
                        label = "Número de tarjeta",
                        placeholder = "4242 4242 4242 4242",
                        visualTransformation = CardNumberVisualTransformation,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UiInput(
                        value = newCardHolder,
                        onValueChange = { newCardHolder = it },
                        label = "Titular",
                        placeholder = "Nombre como aparece en la tarjeta",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val expiryErrorMsg = expiryError(newCardExpiry)
                    UiInput(
                        value = newCardExpiry,
                        onValueChange = { value ->
                            newCardExpiry = value.filter { it.isDigit() }.take(4)
                        },
                        label = "Vencimiento",
                        placeholder = "09/29",
                        visualTransformation = ExpiryVisualTransformation,
                        isError = expiryErrorMsg != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if (expiryErrorMsg != null) {
                        Text(
                            text = expiryErrorMsg,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UiPrimaryButton(
                            text = "Guardar tarjeta",
                            onClick = {
                                appState.addPaymentMethod(
                                    last4 = newCardNumber,
                                    holderName = newCardHolder,
                                    expiry = formatExpiry(newCardExpiry),
                                )
                                showingPaymentForm = false
                                newCardNumber = ""
                                newCardHolder = appState.signedInName
                                newCardExpiry = ""
                            },
                            enabled = newCardNumber.length >= 4 &&
                                newCardHolder.isNotBlank() &&
                                newCardExpiry.length == 4 &&
                                expiryErrorMsg == null,
                            fillWidth = false,
                            modifier = Modifier.weight(1f),
                        )
                        PaymentActionButton(
                            text = "Cancelar",
                            onClick = {
                                showingPaymentForm = false
                                newCardNumber = ""
                                newCardHolder = appState.signedInName
                                newCardExpiry = ""
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    PaymentActionButton(
                        text = "Agregar nuevo método",
                        onClick = { showingPaymentForm = true },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                appState.paymentMethods.forEach { method ->
                    val selected = method.id == appState.selectedPaymentMethodId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) AzulAccion.copy(alpha = 0.08f) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (selected) AzulAccion.copy(alpha = 0.35f) else BordeCard,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { appState.setSelectedPaymentMethod(method.id) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Tarjeta • ${method.last4}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GrisTexto,
                            )
                            Text(
                                text = "${method.holderName} · vence ${method.expiry}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisTexto.copy(alpha = 0.55f),
                            )
                        }
                        if (selected) {
                            Text(
                                text = "Usando",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulAccion,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (appState.selectedPaymentMethodId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    UiInput(
                        value = cvv,
                        onValueChange = { value -> cvv = value.filter { it.isDigit() }.take(4) },
                        label = "CVV",
                        placeholder = "123",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    )
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

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
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
                onClick = { onConfirmarCompra(cvv) },
                enabled = !isProcessing &&
                    totalGeneral > 0 &&
                    asientosEjemplo.isNotEmpty() &&
                    appState.selectedPaymentMethodId.isNotBlank() &&
                    cvv.length in 3..4 &&
                    (appState.signedInEmail.isNotBlank() || esCorreoValido(appState.guestCheckoutEmail.trim())),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

private val correoRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/** Validación básica de correo: no vacío y con formato usuario@dominio.tld. */
private fun esCorreoValido(correo: String): Boolean = correoRegex.matches(correo.trim())

private data class DulceriaResumenItem(
    val producto: MockConcessionItem,
    val cantidad: Int,
)

@Composable
private fun PaymentActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BordeCard, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AzulAccion,
        )
    }
}

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
