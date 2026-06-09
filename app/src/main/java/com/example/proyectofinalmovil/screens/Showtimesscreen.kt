package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

private fun generarDias(): List<Pair<String, String>> {
    val dias = mutableListOf<Pair<String, String>>()
    val cal = Calendar.getInstance()
    val nombresDia = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    repeat(7) {
        val diaSemana = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7
        dias.add(nombresDia[diaSemana] to cal.get(Calendar.DAY_OF_MONTH).toString())
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return dias
}

private val formatosFiltro = listOf("Todo", "Tradicional", "3D", "4D", "Subtitulada", "Doblada")

@Composable
fun ShowtimesScreen(
    movieId: String,
    onContinuarAButacas: (showtimeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val pelicula = appState.movies.find { it.id == movieId } ?: appState.movies.first()
    val todasLasFunciones = appState.showtimesFor(movieId)
    val dias = remember { generarDias() }

    var diaSeleccionado by remember { mutableStateOf(0) }
    var formatoActivo by remember { mutableStateOf("Todo") }
    var funcionSeleccionada by remember { mutableStateOf<MockShowtime?>(null) }
    val fechaSeleccionada = remember(diaSeleccionado) { dateKeyForOffset(diaSeleccionado) }

    val funcionesFiltradas = remember(todasLasFunciones, fechaSeleccionada, diaSeleccionado, formatoActivo) {
        val funcionesDelDia = todasLasFunciones
            .filter { it.matchesDate(fechaSeleccionada, diaSeleccionado) }
            .distinctBy { it.id?.takeIf { id -> id.isNotBlank() } ?: "${it.startsAt}-${it.time}-${it.room}" }
            .sortedBy { it.startsAt ?: it.time }

        when (formatoActivo) {
            "Todo" -> funcionesDelDia
            "Tradicional" -> funcionesDelDia.filter { it.roomType.equals("Tradicional", ignoreCase = true) }
            "3D" -> funcionesDelDia.filter { it.roomType.contains("3D", ignoreCase = true) }
            "4D" -> funcionesDelDia.filter { it.roomType.contains("4D", ignoreCase = true) }
            "Subtitulada" -> funcionesDelDia.filter { it.format.contains("Subt", ignoreCase = true) }
            "Doblada" -> funcionesDelDia.filter { it.format.contains("Dob", ignoreCase = true) }
            else -> funcionesDelDia
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

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
                    text = "ELIGE FUNCIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = CinemaBlue,
                )
                Text(
                    text = pelicula.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                dias.forEachIndexed { index, (nombreDia, numeroDia) ->
                    val seleccionado = index == diaSeleccionado
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(if (seleccionado) CinemaBlue else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (seleccionado) CinemaBlue else Color.Transparent,
                                shape = MaterialTheme.shapes.medium,
                            )
                            .clickable { diaSeleccionado = index }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = nombreDia,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = numeroDia,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                formatosFiltro.forEach { formato ->
                    FilterChip(
                        selected = formato == formatoActivo,
                        onClick = { formatoActivo = formato },
                        label = {
                            Text(
                                text = formato,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (formato == formatoActivo) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CinemaBlue,
                            selectedLabelColor = Color.White,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = formato == formatoActivo,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = CinemaBlue,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (funcionesFiltradas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No hay funciones disponibles con ese formato.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    funcionesFiltradas.forEach { funcion ->
                        TarjetaFuncion(
                            showtime = funcion,
                            seleccionada = funcion == funcionSeleccionada,
                            onClick = { funcionSeleccionada = funcion },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IndicicadorDisponibilidad(color = Color(0xFF4AB07A), texto = "Amplia disponibilidad")
                IndicicadorDisponibilidad(color = Color(0xFFFFB245), texto = "Pocos asientos")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            UiPrimaryButton(
                text = "Continuar a butacas  ›",
                onClick = {
                    funcionSeleccionada?.let {
                        onContinuarAButacas(
                            it.id?.takeIf { id -> id.isNotBlank() }
                                ?: "$movieId|${it.startsAt ?: ""}|${it.roomId ?: it.room}|${it.time}",
                        )
                    }
                },
                enabled = funcionSeleccionada != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun TarjetaFuncion(
    showtime: MockShowtime,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pocosAsientos = showtime.availableSeats < 25

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (seleccionada) 0.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = if (seleccionada) 2.dp else 1.dp,
            color = if (seleccionada) CinemaBlue else MaterialTheme.colorScheme.outline,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = showtime.time,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (seleccionada) CinemaBlue else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(72.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = showtime.room,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = showtime.roomType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = showtime.format,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pocosAsientos) Color(0xFFFFB245) else Color(0xFF4AB07A)
                            ),
                    )
                    Text(
                        text = "${showtime.availableSeats} butacas libres",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${showtime.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = CinemaBlue,
                )
                Text(
                    text = "MXN c/u",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun IndicicadorDisponibilidad(
    color: Color,
    texto: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun MockShowtime.matchesDate(dateKey: String, selectedDayOffset: Int): Boolean {
    val startsAtKey = laPazDateKey(startsAt)
    if (startsAtKey == null) return selectedDayOffset == 0
    return startsAtKey == dateKey
}

private fun dateKeyForOffset(dayOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}

private fun laPazDateKey(startsAt: String?): String? {
    if (startsAt.isNullOrBlank()) return null
    return runCatching {
        Instant.parse(startsAt)
            .atOffset(ZoneOffset.ofHours(-7))
            .toLocalDate()
            .toString()
    }.getOrElse {
        startsAt.takeIf { it.length >= 10 }?.substring(0, 10)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun ShowtimesScreenPreview() {
    ProyectoFinalMovilTheme {
        ShowtimesScreen(
            movieId = "estacion-7",
            onContinuarAButacas = {},
        )
    }
}
