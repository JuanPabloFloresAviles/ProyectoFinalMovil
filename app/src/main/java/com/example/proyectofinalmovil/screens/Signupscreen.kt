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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun SignupScreen(
    onCrearCuenta: (nombre: String, apellidoPaterno: String, apellidoMaterno: String?, correo: String, contrasena: String) -> Unit,
    cargando: Boolean = false,
    mensajeError: String? = null,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }

    val correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val mostrarErrorCorreo = correo.isNotEmpty() && !correoValido

    val fuerzaContrasena = calcularFuerza(contrasena)
    val textoFuerza = when {
        contrasena.isEmpty() -> ""
        fuerzaContrasena < 0.4f -> "Débil · agrega números o mayúsculas"
        fuerzaContrasena < 0.75f -> "buena · al menos 1 número"
        else -> "Fuerte"
    }

    val formularioListo = nombre.trim().length >= 2
        && apellidoPaterno.trim().length >= 2
        && correoValido
        && contrasena.length >= 6
        && aceptaTerminos
        && !cargando

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
                text = "Paso 2 / 2",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CREA TU CUENTA",
            style = MaterialTheme.typography.labelSmall,
            color = CinemaBlue,
            fontWeight = FontWeight.ExtraBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Únete al club.",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(28.dp))

        UiInput(
            value = nombre,
            onValueChange = { nombre = it },
            label = "NOMBRE(S)",
            placeholder = "Alejandra",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(modifier = Modifier.height(16.dp))

        UiInput(
            value = apellidoPaterno,
            onValueChange = { apellidoPaterno = it },
            label = "APELLIDO PATERNO",
            placeholder = "Rivera",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(modifier = Modifier.height(16.dp))

        UiInput(
            value = apellidoMaterno,
            onValueChange = { apellidoMaterno = it },
            label = "APELLIDO MATERNO (opcional)",
            placeholder = "Soto",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(modifier = Modifier.height(16.dp))

        UiInput(
            value = correo,
            onValueChange = { correo = it },
            label = "CORREO",
            placeholder = "alex.rivera@uabcs.mx",
            leadingIcon = Icons.Rounded.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = mostrarErrorCorreo,
        )

        if (mostrarErrorCorreo) {
            Text(
                text = "Ingresa un correo válido, por ejemplo: nombre@dominio.com",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        UiInput(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = "CONTRASEÑA",
            placeholder = "••••••••",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        if (contrasena.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { fuerzaContrasena },
                modifier = Modifier.fillMaxWidth(),
                color = colorFuerza(fuerzaContrasena),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fuerza: $textoFuerza",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = CinemaBlue,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                ),
            )

            val textoTerminos = buildAnnotatedString {
                append("Acepto los ")
                withStyle(SpanStyle(color = CinemaBlue, fontWeight = FontWeight.SemiBold)) {
                    append("términos")
                }
                append(" y la ")
                withStyle(SpanStyle(color = CinemaBlue, fontWeight = FontWeight.SemiBold)) {
                    append("política de privacidad")
                }
                append(" del cine universitario UABCS.")
            }

            Text(
                text = textoTerminos,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        if (mensajeError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensajeError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            UiPrimaryButton(
                text = "Crear cuenta",
                onClick = {
                    onCrearCuenta(
                        nombre.trim(),
                        apellidoPaterno.trim(),
                        apellidoMaterno.trim().ifBlank { null },
                        correo.trim(),
                        contrasena,
                    )
                },
                enabled = formularioListo,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun calcularFuerza(contrasena: String): Float {
    var puntos = 0
    if (contrasena.length >= 6) puntos++
    if (contrasena.length >= 10) puntos++
    if (contrasena.any { it.isDigit() }) puntos++
    if (contrasena.any { it.isUpperCase() }) puntos++
    return puntos / 4f
}

private fun colorFuerza(fuerza: Float): Color {
    return when {
        fuerza < 0.4f -> Color(0xFFD84545)
        fuerza < 0.75f -> Color(0xFF1269A2)
        else -> Color(0xFF3EC07A)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun SignupScreenPreview() {
    ProyectoFinalMovilTheme {
        SignupScreen(onCrearCuenta = { _, _, _, _, _ -> })
    }
}
