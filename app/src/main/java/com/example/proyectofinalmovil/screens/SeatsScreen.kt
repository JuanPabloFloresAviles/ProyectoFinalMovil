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
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val Blanco = Color(0xFFFFFFFF)
private val AzulAccion = Color(0xFF1067A6)
private val AzulClaro = Color(0xFF3A9BC7)
private val AmarilloMovilidad = Color(0xFFF7F2B4)
private val GrisOcupado = Color(0xFFF2EFE4)
private val BordeAsiento = Color(0xFFD6D1C2)
private val GrisTexto = Color(0xFF333333)

@Composable
fun SeatsScreen(
    movieId: String,
    onContinuarADulceria: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val pelicula = appState.movies.find { it.id == movieId } ?: appState.movies.first()
    val funcion = appState.currentShowtime()
    val seleccionados = appState.selectedSeatIds
    val ocupados = appState.occupiedSeatsForCurrentShowtime()
    val subtotal = appState.ticketTotal()

    // Dimensiones reales de la sala (la BD las trae en el catálogo). Antes
    // estaban fijas en 10 filas y 8 columnas, por lo que una sala 10x10 perdía
    // 2 columnas. Si por alguna razón no se encuentra la sala, se asume 10x10.
    val sala = appState.adminRooms.firstOrNull { it.id == funcion.roomId }
    val numFilas = (sala?.rows ?: 10).coerceIn(1, 26)
    val numColumnas = (sala?.columns ?: 10).coerceAtLeast(1)
    val butacasInactivas = sala?.inactiveSeatLabels?.toSet() ?: emptySet()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (indiceFila in 0 until numFilas) {
                    val etiqueta = ('A' + indiceFila).toString()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = etiqueta,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                            modifier = Modifier.width(18.dp),
                            textAlign = TextAlign.Center,
                        )

                        for (indiceColumna in 0 until numColumnas) {
                            val nombreAsiento = "$etiqueta${indiceColumna + 1}"
                            val estaSeleccionado = nombreAsiento in seleccionados
                            val estaOcupado = nombreAsiento in ocupados
                            val estaInactivo = nombreAsiento in butacasInactivas

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            estaSeleccionado -> AzulAccion
                                            estaInactivo -> AmarilloMovilidad
                                            estaOcupado -> GrisOcupado
                                            else -> Blanco
                                        },
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            estaSeleccionado -> AzulAccion
                                            estaInactivo -> AmarilloMovilidad
                                            estaOcupado -> GrisOcupado
                                            else -> BordeAsiento
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                    )
                                    .clickable(enabled = !estaOcupado && !estaInactivo) {
                                        appState.toggleSeat(nombreAsiento)
                                    },
                            )
                        }

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

        Surface(
            shadowElevation = 8.dp,
            color = FondoCrema,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                if (seleccionados.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 10.dp),
                    ) {
                        seleccionados.sorted().forEach { nombreAsiento ->
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "${seleccionados.size} BOLETOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrisTexto.copy(alpha = 0.6f),
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
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
