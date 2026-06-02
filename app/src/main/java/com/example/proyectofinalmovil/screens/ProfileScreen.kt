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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val AzulOscuro = Color(0xFF0A4E7A)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)

@Composable
fun ProfileScreen(
    onVerHistorial: () -> Unit,
    onVerResenas: () -> Unit,
    onRecuperarCompra: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(appState.userProfile.name) }
    var email by remember { mutableStateOf(appState.userProfile.email) }
    var phone by remember { mutableStateOf(appState.userProfile.phone) }
    var favoriteGenre by remember { mutableStateOf(appState.userProfile.favoriteGenre) }

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
            ProfileHeader(
                initials = appState.userProfile.initials,
                name = name,
                email = email,
                favoriteGenre = favoriteGenre,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "Compras",
                    value = appState.purchases.size.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Reseñas",
                    value = appState.reviews.count { it.isMine }.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Miembro",
                    value = appState.userProfile.memberSince.takeLast(4),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos personales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (editing) {
                        UiInput(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nombre",
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        UiInput(
                            value = email,
                            onValueChange = { email = it },
                            label = "Correo",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        UiInput(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Teléfono",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        UiInput(
                            value = favoriteGenre,
                            onValueChange = { favoriteGenre = it },
                            label = "Género favorito",
                        )
                    } else {
                        ProfileInfoRow("Nombre", name)
                        ProfileInfoRow("Correo", email)
                        ProfileInfoRow("Teléfono", phone)
                        ProfileInfoRow("Matrícula", appState.userProfile.studentId)
                        ProfileInfoRow("Género favorito", favoriteGenre)
                        ProfileInfoRow("Miembro desde", appState.userProfile.memberSince)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    UiPrimaryButton(
                        text = if (editing) "Guardar cambios" else "Editar perfil",
                        onClick = { editing = !editing },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            UiGhostButton(
                text = "Ver mis reseñas",
                onClick = onVerResenas,
            )
            Spacer(modifier = Modifier.height(10.dp))
            UiGhostButton(
                text = "Ver historial de compras",
                onClick = onVerHistorial,
            )
            Spacer(modifier = Modifier.height(10.dp))
            UiGhostButton(
                text = "Recuperar compra invitada",
                onClick = onRecuperarCompra,
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    initials: String,
    name: String,
    email: String,
    favoriteGenre: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AzulAccion, AzulOscuro),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MI PERFIL",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTexto.copy(alpha = 0.62f),
                )
                Text(
                    text = "Fan de $favoriteGenre",
                    style = MaterialTheme.typography.bodySmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AzulAccion,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = GrisTexto.copy(alpha = 0.62f),
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
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
private fun ProfileScreenPreview() {
    ProyectoFinalMovilTheme {
        ProfileScreen(
            onVerHistorial = {},
            onVerResenas = {},
            onRecuperarCompra = {},
        )
    }
}
