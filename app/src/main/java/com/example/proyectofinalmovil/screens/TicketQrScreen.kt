package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.services.tickets.QrCodeBitmap
import com.example.proyectofinalmovil.services.tickets.TicketQrPayload
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

// Colores de la pantalla (misma paleta del diseño)
private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val AzulClaro = Color(0xFF3A9BC7)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)

/**
 * Pantalla del boleto digital con código QR real.
 * Muestra los datos del boleto y codifica el mismo payload QR de web cuando está disponible.
 */
@Composable
fun TicketQrScreen(
    onIrAlHistorial: () -> Unit,
    onRecuperarCompra: () -> Unit,
    onVolverACartelera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val compra = appState.activeQrPurchase()

    val qrPayload = compra?.let { TicketQrPayload.fromPurchase(it) }.orEmpty()
    val qrBitmap = remember(qrPayload) { QrCodeBitmap.create(qrPayload, 512) }

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

            if (compra == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No hay boletos para hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                val pelicula = appState.movieForPurchase(compra)

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

                TicketCard(
                    compra = compra,
                    peliculaTitulo = pelicula.title,
                    peliculaDetalle = "${pelicula.genre} · ${pelicula.classification} · ${pelicula.duration}",
                    qrBitmap = qrBitmap.asImageBitmap(),
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
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
                    text = "Recuperar compra",
                    onClick = onRecuperarCompra,
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

@Composable
private fun TicketCard(
    compra: MockPurchase,
    peliculaTitulo: String,
    peliculaDetalle: String,
    qrBitmap: ImageBitmap,
) {
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
                text = peliculaTitulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = peliculaDetalle,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.5f),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Datos del boleto en 2 columnas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DatoBoleto(etiqueta = "Fecha", valor = compra.date)
                DatoBoleto(etiqueta = "Horario", valor = compra.time, alinearDerecha = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DatoBoleto(etiqueta = "Sala", valor = compra.room)
                DatoBoleto(etiqueta = "Asientos", valor = compra.seats.joinToString(", "), alinearDerecha = true)
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

            // Código QR real
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
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "Código QR del boleto",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Folio
            Text(
                text = "Folio: ${compra.folio}",
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

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun TicketQrScreenPreview() {
    ProyectoFinalMovilTheme {
        TicketQrScreen(
            onIrAlHistorial = {},
            onRecuperarCompra = {},
            onVolverACartelera = {},
        )
    }
}
