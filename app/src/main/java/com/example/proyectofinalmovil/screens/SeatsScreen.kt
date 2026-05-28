package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.mockMovies
import com.example.proyectofinalmovil.services.mock.mockShowtimesByMovieId
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

// Colores de la pantalla de butacas según el diseño
private val FondoCrema = Color(0xFFF9F6EB)
private val Blanco = Color(0xFFFFFFFF)
private val AzulAccion = Color(0xFF1067A6)
private val AzulClaro = Color(0xFF3A9BC7)
private val AmarilloMovilidad = Color(0xFFF7F2B4)
private val GrisOcupado = Color(0xFFF2EFE4)
private val BordeAsiento = Color(0xFFD6D1C2)
private val GrisTexto = Color(0xFF333333)

// Filas de la sala (A-J)
private val etiquetasFilas = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

// Cantidad de asientos por fila
private const val ASIENTOS_POR_FILA = 8

// Precio por boleto
private const val PRECIO_BOLETO = 45

/**
 * Pantalla de selección de butacas.
 * Muestra el mapa de asientos de la sala y permite al usuario
 * seleccionar o deseleccionar butacas antes de continuar a la dulcería.
 */
@Composable
fun SeatsScreen(
    movieId: String,
    onContinuarADulceria: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Datos de la película y función
    val pelicula = mockMovies.find { it.id == movieId } ?: mockMovies.first()
    val funcion = mockShowtimesByMovieId[movieId]?.firstOrNull()
        ?: mockShowtimesByMovieId.values.first().first()

    // Asientos seleccionados por el usuario (pares fila-columna)
    val seleccionados = remember { mutableStateListOf<Pair<Int, Int>>() }

    // Subtotal basado en la cantidad de asientos seleccionados
    val subtotal = seleccionados.size * PRECIO_BOLETO

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
                    text = "${pelicula.title.uppercase()} · ${funcion.time} · ${funcion.room.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Elige tus butacas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indicador de pantalla
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AzulAccion),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PANTALLA",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisTexto.copy(alpha = 0.5f),
                    letterSpacing = 3.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mapa de asientos con scroll horizontal para pantallas pequeñas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                etiquetasFilas.forEachIndexed { indiceFila, etiqueta ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        // Etiqueta de fila (izquierda)
                        Text(
                            text = etiqueta,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                            modifier = Modifier.width(18.dp),
                            textAlign = TextAlign.Center,
                        )

                        // Asientos de esta fila
                        for (indiceColumna in 0 until ASIENTOS_POR_FILA) {
                            val estaSeleccionado = seleccionados.contains(indiceFila to indiceColumna)

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (estaSeleccionado) AzulAccion else Blanco
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (estaSeleccionado) AzulAccion else BordeAsiento,
                                        shape = RoundedCornerShape(6.dp),
                                    )
                                    .clickable {
                                        val par = indiceFila to indiceColumna
                                        if (seleccionados.contains(par)) {
                                            seleccionados.remove(par)
                                        } else {
                                            seleccionados.add(par)
                                        }
                                    },
                            )
                        }

                        // Etiqueta de fila (derecha)
                        Text(
                            text = etiqueta,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                            modifier = Modifier.width(18.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Leyenda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LeyendaAsiento(color = Blanco, bordeColor = BordeAsiento, texto = "Disponible")
                LeyendaAsiento(color = AzulAccion, bordeColor = AzulAccion, texto = "Tu selección")
                LeyendaAsiento(color = GrisOcupado, bordeColor = GrisOcupado, texto = "Ocupada")
                LeyendaAsiento(color = AmarilloMovilidad, bordeColor = AmarilloMovilidad, texto = "Movilidad")
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                // Chips de asientos seleccionados
                if (seleccionados.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 10.dp),
                    ) {
                        seleccionados.sortedWith(compareBy({ it.first }, { it.second }))
                            .forEach { (fila, columna) ->
                                val nombreAsiento = "${etiquetasFilas[fila]}${columna + 1}"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(AzulAccion)
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = nombreAsiento,
                                        color = Blanco,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                    }
                }

                // Subtotal y botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Info de boletos y precio
                    Column {
                        Text(
                            text = "${seleccionados.size} BOLETOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrisTexto.copy(alpha = 0.6f),
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(
                                text = "$$subtotal",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulClaro,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MXN",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrisTexto.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón para continuar
                    UiPrimaryButton(
                        text = "Continuar a dulcería",
                        onClick = onContinuarADulceria,
                        enabled = seleccionados.isNotEmpty(),
                        fillWidth = false,
                        modifier = Modifier.width(200.dp),
                    )
                }
            }
        }
    }
}

/**
 * Indicador individual de la leyenda del mapa de asientos.
 */
@Composable
private fun LeyendaAsiento(
    color: Color,
    bordeColor: Color,
    texto: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = bordeColor,
                    shape = RoundedCornerShape(3.dp),
                ),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = GrisTexto.copy(alpha = 0.7f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun SeatsScreenPreview() {
    ProyectoFinalMovilTheme {
        SeatsScreen(
            movieId = "estacion-7",
            onContinuarADulceria = {},
        )
    }
}
