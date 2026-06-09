package com.example.proyectofinalmovil.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.time.YearMonth

/**
 * Formato visual para el número de tarjeta: un espacio cada 4 dígitos.
 *
 * El estado guarda SOLO dígitos; el espacio es puramente visual. El
 * [OffsetMapping] mantiene el cursor en su lugar (de lo contrario, al insertar
 * el separador, el cursor "saltaba" de posición).
 */
val CardNumberVisualTransformation = VisualTransformation { text ->
    val digits = text.text
    val formateado = buildString {
        digits.forEachIndexed { index, c ->
            if (index > 0 && index % 4 == 0) append(' ')
            append(c)
        }
    }
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 0) return 0
            return offset + (offset - 1) / 4
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offset - offset / 5
        }
    }
    TransformedText(AnnotatedString(formateado), offsetMapping)
}

/**
 * Formato visual para el vencimiento MM/AA: inserta "/" después de los 2
 * primeros dígitos. El estado guarda SOLO dígitos (máx. 4); la "/" es visual,
 * así que escribir "1232" muestra "12/32" sin descolocar el cursor.
 */
val ExpiryVisualTransformation = VisualTransformation { text ->
    val digits = text.text
    val formateado = if (digits.length > 2) {
        "${digits.take(2)}/${digits.drop(2)}"
    } else {
        digits
    }
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return if (offset <= 2) offset else offset + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return if (offset <= 2) offset else offset - 1
        }
    }
    TransformedText(AnnotatedString(formateado), offsetMapping)
}

/** Convierte 4 dígitos en "MM/AA" para guardar/mostrar. */
fun formatExpiry(digits: String): String {
    val limpio = digits.filter { it.isDigit() }.take(4)
    return if (limpio.length > 2) "${limpio.take(2)}/${limpio.drop(2)}" else limpio
}

/**
 * Valida el vencimiento (dígitos MMAA). Devuelve un mensaje de error o `null`
 * si es válido. Mientras está incompleto (menos de 4 dígitos) no se muestra
 * error: la validación de "completo" la hace el largo del campo.
 *
 * Se considera vencida si el mes/año ya pasó respecto al mes actual; una
 * tarjeta es válida durante todo su mes de vencimiento.
 */
fun expiryError(digits: String): String? {
    val limpio = digits.filter { it.isDigit() }
    if (limpio.length < 4) return null
    val mes = limpio.take(2).toIntOrNull() ?: return "Fecha inválida"
    val anio = 2000 + (limpio.drop(2).toIntOrNull() ?: return "Fecha inválida")
    if (mes !in 1..12) return "Mes inválido"
    val vencimiento = YearMonth.of(anio, mes)
    if (vencimiento.isBefore(YearMonth.now())) return "La tarjeta está vencida"
    return null
}
