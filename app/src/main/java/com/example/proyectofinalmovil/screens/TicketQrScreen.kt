package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
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
    onSepararBoletos: (folio: String, seats: List<String>) -> Unit = { _, _ -> },
    isSeparating: Boolean = false,
    separarError: String? = null,
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

                if (compra.ticketPackages.isNotEmpty() || compra.concessionPackages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Paquetes de acceso y recolección",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = GrisTexto,
                            )
                            compra.ticketPackages.forEach { paquete ->
                                Spacer(modifier = Modifier.height(12.dp))
                                PaqueteQrItem(
                                    titulo = paquete.label,
                                    detalle = "Butacas: ${paquete.seats.joinToString(", ")}",
                                    qrContent = paquete.qrCode,
                                )
                            }
                            compra.concessionPackages.forEach { paquete ->
                                Spacer(modifier = Modifier.height(12.dp))
                                PaqueteQrItem(
                                    titulo = paquete.label,
                                    detalle = paquete.items.joinToString(", ") { "${it.quantity} ${it.name}" },
                                    qrContent = paquete.qrCode,
                                )
                            }
                        }
                    }
                }

                val separables = appState.separableSeats(compra.folio)
                if (compra.seats.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SepararBoletosCard(
                        separableSeats = separables,
                        isSeparating = isSeparating,
                        separarError = separarError,
                        onSeparar = { seleccion -> onSepararBoletos(compra.folio, seleccion) },
                    )
                }

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
                    text = "Recuperar compra",
                    onClick = onRecuperarCompra,
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
    val isMoviePurchase = compra.seats.isNotEmpty()
    val snackSummary = compra.concessionItems.joinToString(", ") { "${it.quantity} ${it.name}" }
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
            Text(
                text = if (isMoviePurchase) peliculaTitulo else "Recolectar dulcería",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isMoviePurchase) peliculaDetalle else "Presenta este QR en la barra de dulcería",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isMoviePurchase) {
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
                    DatoBoleto(
                        etiqueta = "Asientos",
                        valor = compra.seats.joinToString(", "),
                        alinearDerecha = true,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DatoBoleto(etiqueta = "Fecha", valor = compra.date)
                    DatoBoleto(etiqueta = "Tipo", valor = "Dulcería", alinearDerecha = true)
                }

                Spacer(modifier = Modifier.height(16.dp))

                DatoBoleto(
                    etiqueta = "Productos",
                    valor = snackSummary.ifBlank { "Pedido en preparación" },
                )
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
                text = compra.paymentMethodLabel,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.65f),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isMoviePurchase) {
                    "Presenta este código en la entrada"
                } else {
                    "Presenta este código al recoger tu pedido"
                },
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Muestra un paquete separado con su código QR escaneable.
 */
@Composable
private fun PaqueteQrItem(
    titulo: String,
    detalle: String,
    qrContent: String,
) {
    val qrBitmap = remember(qrContent) { QrCodeBitmap.create(qrContent.ifBlank { "CINE-UABCS" }, 400) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = FondoCrema,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            if (detalle.isNotBlank()) {
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTexto.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, BordeCard, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Código QR de $titulo",
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (qrContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = qrContent,
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisTexto.copy(alpha = 0.5f),
                )
            }
        }
    }
}

/**
 * Tarjeta para separar boletos de una compra en un QR aparte,
 * para las personas que llegarán por su cuenta.
 */
@Composable
private fun SepararBoletosCard(
    separableSeats: List<String>,
    isSeparating: Boolean,
    separarError: String?,
    onSeparar: (List<String>) -> Unit,
) {
    var seleccion by remember { mutableStateOf(emptySet<String>()) }

    // Al separar un QR, las butacas ya separadas desaparecen de separableSeats.
    // Limpiamos la selección para no reenviar butacas que ya no se pueden separar.
    LaunchedEffect(separableSeats) {
        val vigentes = seleccion.intersect(separableSeats.toSet())
        if (vigentes.size != seleccion.size) {
            seleccion = vigentes
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Separar boletos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Genera un QR aparte para las personas que llegarán por su cuenta.",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (separableSeats.isEmpty()) {
                Text(
                    text = "Todos los boletos de esta compra ya fueron separados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTexto.copy(alpha = 0.7f),
                )
            } else {
                separableSeats.forEach { seat ->
                    val checked = seat in seleccion
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                color = if (checked) AzulAccion else BordeCard,
                                shape = RoundedCornerShape(10.dp),
                            )
                            .background(if (checked) AzulAccion.copy(alpha = 0.08f) else Color.White)
                            .clickable(enabled = !isSeparating) {
                                seleccion = if (checked) seleccion - seat else seleccion + seat
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (checked) AzulAccion else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (checked) AzulAccion else BordeCard,
                                    shape = RoundedCornerShape(6.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (checked) {
                                Text(
                                    text = "✓",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "Butaca $seat",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                        )
                    }
                }

                if (separarError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = separarError,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC0392B),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                UiPrimaryButton(
                    text = if (isSeparating) "Generando QR..." else "Generar QR separado",
                    onClick = { onSeparar(seleccion.toList()) },
                    enabled = seleccion.isNotEmpty() && !isSeparating,
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
