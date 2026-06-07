package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.AppIcons
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockPaymentMethod

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccionPm = Color(0xFF1067A6)
private val AzulOscuroPm = Color(0xFF0A4E7A)
private val GrisTextoPm = Color(0xFF333333)
private val BordeCardPm = Color(0xFFD6D1C2)
private val RojoEliminar = Color(0xFFB42318)
private val VerdePredeterminado = Color(0xFF1A7A3C)

@Composable
fun PaymentMethodsScreen(
    paymentMethods: List<MockPaymentMethod>,
    defaultMethodId: String,
    onSetDefault: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAddMethod: (numeroTarjeta: String, holderName: String, expiry: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddForm by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }

    fun resetForm() {
        cardNumber = ""
        holderName = ""
        expiry = ""
        showAddForm = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Métodos de pago",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = GrisTextoPm,
        )
        Text(
            text = "Administra las tarjetas guardadas en tu cuenta.",
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTextoPm.copy(alpha = 0.6f),
        )

        if (paymentMethods.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = BorderStroke(1.dp, BordeCardPm),
            ) {
                Text(
                    text = "No tienes tarjetas guardadas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTextoPm.copy(alpha = 0.55f),
                    modifier = Modifier.padding(20.dp),
                )
            }
        } else {
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    method = method,
                    isDefault = method.id == defaultMethodId,
                    onSetDefault = { onSetDefault(method.id) },
                    onRemove = { onRemove(method.id) },
                )
            }
        }

        if (showAddForm) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = BorderStroke(1.dp, BordeCardPm),
                shadowElevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Nueva tarjeta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTextoPm,
                    )
                    UiInput(
                        value = cardNumber,
                        onValueChange = { if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() } },
                        label = "Número de tarjeta",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    UiInput(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = "Nombre del titular",
                    )
                    UiInput(
                        value = expiry,
                        onValueChange = {
                            val digits = it.filter { c -> c.isDigit() }.take(4)
                            expiry = if (digits.length >= 3) "${digits.take(2)}/${digits.drop(2)}" else digits
                        },
                        label = "Vencimiento (MM/AA)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        UiPrimaryButton(
                            text = "Guardar tarjeta",
                            enabled = cardNumber.length >= 4 && holderName.isNotBlank() && expiry.length >= 4,
                            onClick = {
                                onAddMethod(cardNumber.trim(), holderName.trim(), expiry.trim())
                                resetForm()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        UiGhostButton(
                            text = "Cancelar",
                            onClick = { resetForm() },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        } else {
            UiPrimaryButton(
                text = "Añadir tarjeta",
                onClick = { showAddForm = true },
            )
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: MockPaymentMethod,
    isDefault: Boolean,
    onSetDefault: () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(
            width = if (isDefault) 2.dp else 1.dp,
            color = if (isDefault) AzulAccionPm else BordeCardPm,
        ),
        shadowElevation = if (isDefault) 3.dp else 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Tarjeta •••• ${method.last4}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GrisTextoPm,
                        )
                        if (isDefault) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = VerdePredeterminado.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    text = "Predeterminada",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerdePredeterminado,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = method.holderName,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTextoPm.copy(alpha = 0.6f),
                    )
                    Text(
                        text = "Vence ${method.expiry}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTextoPm.copy(alpha = 0.45f),
                    )
                }
                Row {
                    if (!isDefault) {
                        IconButton(onClick = onSetDefault, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = AppIcons.Star,
                                contentDescription = "Predeterminar",
                                tint = AzulOscuroPm,
                            )
                        }
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = AppIcons.Delete,
                            contentDescription = "Eliminar tarjeta",
                            tint = RojoEliminar,
                        )
                    }
                }
            }
        }
    }
}
