package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun LoginScreen(
    onEntrar: () -> Unit,
    onIrARegistro: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    // Valida el correo con el patrón oficial de Android — más confiable que solo buscar @ y punto
    val correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val contrasenaValida = contrasena.length >= 6
    val formularioListo = correoValido && contrasenaValida

    // Solo muestra el error si el usuario ya escribió algo pero el correo sigue siendo inválido
    val mostrarErrorCorreo = correo.isNotEmpty() && !correoValido

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "Paso 1 / 2",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACCESO · ESTUDIANTES Y PÚBLICO",
            style = MaterialTheme.typography.labelSmall,
            color = CinemaBlue,
            fontWeight = FontWeight.ExtraBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenida, de vuelta.",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para guardar tus boletos, reseñas y funciones favoritas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de correo — muestra borde rojo si el formato es inválido
        UiInput(
            value = correo,
            onValueChange = { correo = it },
            label = "CORREO",
            placeholder = "alex.rivera@uabcs.mx",
            leadingIcon = Icons.Rounded.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = mostrarErrorCorreo,
        )

        // Mensaje de error debajo del campo — aparece en cuanto el usuario escribe algo inválido
        if (mostrarErrorCorreo) {
            Text(
                text = "Ingresa un correo válido, por ejemplo: nombre@dominio.com",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña con botón mostrar/ocultar
        UiInput(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = "CONTRASEÑA",
            placeholder = "••••••••",
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingContent = {
                TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Text(
                        text = if (mostrarContrasena) "Ocultar" else "Mostrar",
                        color = CinemaBlue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { /* pendiente para integración real */ }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        UiPrimaryButton(
            text = "Entrar",
            onClick = onEntrar,
            enabled = formularioListo,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            Text(
                text = "O CONTINÚA CON",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            UiGhostButton(
                text = "Google",
                onClick = { /* pendiente para integración real */ },
                fillWidth = false,
                modifier = Modifier.weight(1f),
            )
            UiGhostButton(
                text = "Apple",
                onClick = { /* pendiente para integración real */ },
                fillWidth = false,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "¿No tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onIrARegistro) {
                Text(
                    text = "Regístrate",
                    color = CinemaBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun LoginScreenPreview() {
    ProyectoFinalMovilTheme {
        LoginScreen(onEntrar = {}, onIrARegistro = {})
    }
}
