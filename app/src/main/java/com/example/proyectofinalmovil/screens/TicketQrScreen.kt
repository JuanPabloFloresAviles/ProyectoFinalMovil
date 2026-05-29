package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.services.mock.mockMovies
import com.example.proyectofinalmovil.services.mock.mockShowtimesByMovieId
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

// Colores de la pantalla (misma paleta del diseño)
private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val AzulClaro = Color(0xFF3A9BC7)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)

/**
 * Pantalla del boleto digital con código QR simulado.
 * Muestra los datos del boleto y un QR visual generado con composables.
 */
@Composable
fun TicketQrScreen(
    onIrAlHistorial: () -> Unit,
    onVolverACartelera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Datos mock del boleto
    val pelicula = mockMovies.find { it.id == "estacion-7" } ?: mockMovies.first()
    val funcion = mockShowtimesByMovieId["estacion-7"]?.firstOrNull()
        ?: mockShowtimesByMovieId.values.first().first()
    val asientos = listOf("B7", "B8")
    val folio = "CINE-2026-4A7F"

    // Patrón QR simulado (cuadrícula de 11x11)
    val patronQr = remember { generarPatronQr() }

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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Encabezado
            Text(
                text = "TU BOLETO",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta del boleto tipo ticket
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Nombre de la película
                    Text(
                        text = pelicula.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${pelicula.genre} · ${pelicula.classification} · ${pelicula.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.5f),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Datos del boleto en 2 columnas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DatoBoleto(etiqueta = "Fecha", valor = "28 May 2026")
                        DatoBoleto(etiqueta = "Horario", valor = funcion.time, alinearDerecha = true)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DatoBoleto(etiqueta = "Sala", valor = funcion.room)
                        DatoBoleto(etiqueta = "Asientos", valor = asientos.joinToString(", "), alinearDerecha = true)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Línea punteada divisoria
                    val bordeColor = BordeCard
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .drawBehind {
                                drawLine(
                                    color = bordeColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 2f,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 10f),
                                        0f,
                                    ),
                                )
                            },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Código QR simulado
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = BordeCard,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Cuadrícula QR simulada
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            patronQr.forEach { fila ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    fila.forEach { celdaActiva ->
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(
                                                    if (celdaActiva) GrisTexto else Color.White
                                                ),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Folio
                    Text(
                        text = "Folio: $folio",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulAccion,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Presenta este código en la entrada",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.5f),
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
                UiPrimaryButton(
                    text = "Ir al historial",
                    onClick = onIrAlHistorial,
                )

                Spacer(modifier = Modifier.height(8.dp))

                UiGhostButton(
                    text = "Volver a cartelera",
                    onClick = onVolverACartelera,
                )
            }
        }
    }
}

/**
 * Dato individual del boleto con etiqueta y valor.
 */
@Composable
private fun DatoBoleto(
    etiqueta: String,
    valor: String,
    alinearDerecha: Boolean = false,
) {
    Column(
        horizontalAlignment = if (alinearDerecha) Alignment.End else Alignment.Start,
    ) {
        Text(
            text = etiqueta.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = AzulClaro,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = GrisTexto,
        )
    }
}

/**
 * Genera un patrón de QR simulado de 11x11 de forma determinista.
 * No es un QR real, solo una representación visual decorativa.
 */
private fun generarPatronQr(): List<List<Boolean>> {
    // Patrón determinista que simula un QR visual
    val semilla = 42
    val tamano = 11
    val patron = MutableList(tamano) { MutableList(tamano) { false } }

    // Cuadros de posición en esquinas (patrón típico de QR)
    // Esquina superior izquierda
    for (i in 0..2) for (j in 0..2) patron[i][j] = true
    patron[1][1] = false

    // Esquina superior derecha
    for (i in 0..2) for (j in (tamano - 3) until tamano) patron[i][j] = true
    patron[1][tamano - 2] = false

    // Esquina inferior izquierda
    for (i in (tamano - 3) until tamano) for (j in 0..2) patron[i][j] = true
    patron[tamano - 2][1] = false

    // Patrón interior (determinista con operaciones simples)
    for (i in 3 until tamano - 3) {
        for (j in 3 until tamano - 3) {
            patron[i][j] = ((i * semilla + j * 7) % 3 == 0)
        }
    }

    // Líneas de sincronización
    for (i in 3 until tamano - 3) {
        patron[i][0] = i % 2 == 0
        patron[0][i] = i % 2 == 0
    }

    return patron
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun TicketQrScreenPreview() {
    ProyectoFinalMovilTheme {
        TicketQrScreen(
            onIrAlHistorial = {},
            onVolverACartelera = {},
        )
    }
}
