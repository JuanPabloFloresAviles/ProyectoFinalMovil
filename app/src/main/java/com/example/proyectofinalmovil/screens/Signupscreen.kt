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
import java.util.Calendar

@Composable
fun SignupScreen(
    onCrearCuenta: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }

    val correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val mostrarErrorCorreo = correo.isNotEmpty() && !correoValido

    val fechaValida = validarFecha(fechaNacimiento)
    val mostrarErrorFecha = fechaNacimiento.isNotEmpty() && !fechaValida

    val fuerzaContrasena = calcularFuerza(contrasena)
    val textoFuerza = when {
        contrasena.isEmpty() -> ""
        fuerzaContrasena < 0.4f -> "Débil · agrega números o mayúsculas"
        fuerzaContrasena < 0.75f -> "buena · al menos 1 número"
        else -> "Fuerte"
    }

    val formularioListo = nombre.isNotBlank()
        && correoValido
        && fechaValida
        && contrasena.length >= 6
        && aceptaTerminos

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
            label = "NOMBRE COMPLETO",
            placeholder = "Alejandra Rivera Soto",
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
            value = fechaNacimiento,
            onValueChange = { entrada ->
                val soloDigitos = entrada.filter { it.isDigit() }.take(8)
                fechaNacimiento = when {
                    soloDigitos.length > 4 -> "${soloDigitos.substring(0, 2)}/${soloDigitos.substring(2, 4)}/${soloDigitos.substring(4)}"
                    soloDigitos.length > 2 -> "${soloDigitos.substring(0, 2)}/${soloDigitos.substring(2)}"
                    else -> soloDigitos
                }
            },
            label = "FECHA DE NACIMIENTO",
            placeholder = "14 / 03 / 2003",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = mostrarErrorFecha,
        )

        if (mostrarErrorFecha) {
            val mensajeFecha = when {
                fechaNacimiento.length == 10 && !esMayorDe13(fechaNacimiento) ->
                    "Debes tener al menos 13 años para crear una cuenta"
                fechaNacimiento.length == 10 ->
                    "La fecha no existe. Verifica día, mes y año"
                else -> "Formato: DD/MM/AAAA"
            }
            Text(
                text = mensajeFecha,
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

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        UiPrimaryButton(
            text = "Crear cuenta",
            onClick = onCrearCuenta,
            enabled = formularioListo,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun validarFecha(fecha: String): Boolean {
    if (fecha.length != 10) return false
    return try {
        val partes = fecha.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()

        // Verificación básica de rangos
        if (mes < 1 || mes > 12) return false
        if (dia < 1 || dia > 31) return false
        if (anio < 1900 || anio > Calendar.getInstance().get(Calendar.YEAR)) return false

        val cal = Calendar.getInstance()
        cal.isLenient = false
        cal.set(anio, mes - 1, dia)
        cal.time
        esMayorDe13(fecha)
    } catch (e: Exception) {
        false
    }
}

private fun esMayorDe13(fecha: String): Boolean {
    return try {
        val partes = fecha.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()

        val nacimiento = Calendar.getInstance().apply { set(anio, mes - 1, dia) }
        val hoy = Calendar.getInstance()
        val edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        val cumpleEsteAnio = hoy.get(Calendar.DAY_OF_YEAR) >= nacimiento.get(Calendar.DAY_OF_YEAR)

        if (cumpleEsteAnio) edad >= 13 else edad - 1 >= 13
    } catch (e: Exception) {
        false
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
        SignupScreen(onCrearCuenta = {})
    }
}
