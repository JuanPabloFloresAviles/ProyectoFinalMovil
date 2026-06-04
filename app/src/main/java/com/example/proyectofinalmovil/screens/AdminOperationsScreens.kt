package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.api.AdminRoomOption
import com.example.proyectofinalmovil.services.state.AdminDashboardMetrics
import com.example.proyectofinalmovil.services.state.AdminReportItem
import com.example.proyectofinalmovil.services.state.AdminSalesRange

private val OpsInk = Color(0xFF102A43)
private val OpsBlue = Color(0xFF1E5AA8)
private val OpsGold = Color(0xFFD99A22)
private val OpsMist = Color(0xFFEAF2FA)

@Composable
fun AdminRoomsManagementScreen(
    rooms: List<AdminRoomOption>,
    onSaveRoom: (String?, String, Int, Int, List<String>) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingId by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf("6") }
    var columns by remember { mutableStateOf("8") }
    val inactiveSeatLabels = remember { mutableStateListOf<String>() }
    val previewRows = rows.toIntOrNull()?.coerceIn(1, 16) ?: 1
    val previewColumns = columns.toIntOrNull()?.coerceIn(1, 20) ?: 1

    AdminOpsScaffold(
        title = "Salas y butacas",
        description = "Configura infraestructura operativa, capacidad y butacas inactivas.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        AdminOpsTextField(name, { name = it }, "Nombre de la sala")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminOpsTextField(
                value = rows,
                onValueChange = { rows = it },
                label = "Filas",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            AdminOpsTextField(
                value = columns,
                onValueChange = { columns = it },
                label = "Columnas",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = "Toca una butaca para marcarla inactiva o activa.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SeatGridPreview(
            rows = previewRows,
            columns = previewColumns,
            inactiveSeatLabels = inactiveSeatLabels,
            onToggle = { label ->
                if (label in inactiveSeatLabels) {
                    inactiveSeatLabels.remove(label)
                } else {
                    inactiveSeatLabels.add(label)
                }
            },
        )
        UiPrimaryButton(
            text = if (editingId == null) "Crear sala" else "Guardar sala",
            enabled = name.isNotBlank() && rows.toIntOrNull() != null && columns.toIntOrNull() != null,
            onClick = {
                onSaveRoom(
                    editingId,
                    name.trim(),
                    rows.toIntOrNull()?.coerceIn(1, 16) ?: 6,
                    columns.toIntOrNull()?.coerceIn(1, 20) ?: 8,
                    inactiveSeatLabels.toList(),
                )
                editingId = null
                name = ""
                rows = "6"
                columns = "8"
                inactiveSeatLabels.clear()
            },
        )

        AdminOpsSectionTitle("Salas registradas")
        if (rooms.isEmpty()) {
            AdminOpsEmptyCard("Aún no hay salas activas en la base de datos.")
        } else {
            rooms.forEach { room ->
                AdminOpsListCard(
                    title = room.name,
                    body = "${room.rows} filas x ${room.columns} columnas · ${room.activeSeats}/${room.capacity} butacas activas",
                    action = "Editar mapa",
                    onAction = {
                        editingId = room.id
                        name = room.name
                        rows = room.rows.toString()
                        columns = room.columns.toString()
                        inactiveSeatLabels.clear()
                        inactiveSeatLabels.addAll(room.inactiveSeatLabels)
                    },
                )
            }
        }
    }
}

@Composable
fun AdminReportsScreen(
    metrics: AdminDashboardMetrics,
    selectedRange: AdminSalesRange,
    onRangeSelected: (AdminSalesRange) -> Unit,
    roomsCount: Int,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AdminOpsScaffold(
        title = "Ventas y estadísticas",
        description = "Reportes comerciales sincronizados con TiDB.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        AdminOpsRangeSelector(
            selectedRange = selectedRange,
            onRangeSelected = onRangeSelected,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard("Boletos", metrics.ticketsSold.toString(), Modifier.weight(1f))
            ReportMetricCard("Ocupación", "${metrics.roomOccupancyPercent}%", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportMetricCard("Salas", roomsCount.toString(), Modifier.weight(1f))
            ReportMetricCard("Stock bajo", metrics.lowStockCount.toString(), Modifier.weight(1f))
        }

        AdminOpsSectionTitle("Ventas recientes")
        ReportItemsList(metrics.salesSeries, valuePrefix = "$")

        AdminOpsSectionTitle("Películas con más ingreso")
        ReportItemsList(metrics.topMovies, valuePrefix = "$")

        AdminOpsSectionTitle("Productos top")
        ProductItemsList(metrics.topProducts)
    }
}

@Composable
private fun SeatGridPreview(
    rows: Int,
    columns: Int,
    inactiveSeatLabels: List<String>,
    onToggle: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (rowIndex in 0 until rows) {
            val rowLabel = ('A'.code + rowIndex).toChar().toString()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                for (column in 1..columns) {
                    val label = "$rowLabel$column"
                    val inactive = label in inactiveSeatLabels
                    Surface(
                        onClick = { onToggle(label) },
                        shape = CircleShape,
                        color = if (inactive) Color(0xFFE9B4A7) else OpsBlue,
                        modifier = Modifier.size(if (columns > 12) 18.dp else 24.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = column.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                }
                Text(
                    text = rowLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OpsInk,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun ReportMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, OpsBlue.copy(alpha = 0.16f)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = OpsBlue,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = OpsInk,
            )
        }
    }
}

@Composable
private fun ReportItemsList(
    items: List<AdminReportItem>,
    valuePrefix: String = "",
    valueSuffix: String = "",
) {
    if (items.isEmpty()) {
        AdminOpsEmptyCard("Sin datos para este reporte.")
    } else {
        items.forEach { item ->
            AdminOpsListCard(
                title = item.label,
                body = item.detail,
                action = "$valuePrefix${item.value}$valueSuffix",
                onAction = {},
            )
        }
    }
}

@Composable
private fun ProductItemsList(items: List<AdminReportItem>) {
    if (items.isEmpty()) {
        AdminOpsEmptyCard("Sin datos para este reporte.")
    } else {
        items.forEach { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                border = BorderStroke(1.dp, OpsBlue.copy(alpha = 0.14f)),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OpsInk,
                        )
                        Text(
                            text = item.detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    ReportBadge("${item.value} uds")
                    ReportBadge("$${item.secondaryValue ?: 0}")
                }
            }
        }
    }
}

@Composable
private fun ReportBadge(text: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = OpsGold.copy(alpha = 0.14f),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = OpsInk,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun AdminOpsRangeSelector(
    selectedRange: AdminSalesRange,
    onRangeSelected: (AdminSalesRange) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AdminSalesRange.entries.forEach { range ->
            val selected = range == selectedRange
            Surface(
                onClick = { onRangeSelected(range) },
                shape = CircleShape,
                color = if (selected) OpsBlue else Color.White,
                border = BorderStroke(1.dp, OpsBlue.copy(alpha = if (selected) 1f else 0.25f)),
            ) {
                Text(
                    text = range.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selected) Color.White else OpsBlue,
                )
            }
        }
    }
}

@Composable
private fun AdminOpsScaffold(
    title: String,
    description: String,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = OpsInk,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = OpsMist,
            border = BorderStroke(1.dp, OpsBlue.copy(alpha = 0.18f)),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                content()
            }
        }
        UiGhostButton(
            text = "Volver al dashboard",
            onClick = onBackToDashboard,
        )
    }
}

@Composable
private fun AdminOpsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun AdminOpsSectionTitle(text: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        color = OpsBlue,
    )
}

@Composable
private fun AdminOpsListCard(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, OpsBlue.copy(alpha = 0.14f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OpsInk,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                onClick = onAction,
                shape = MaterialTheme.shapes.medium,
                color = OpsGold.copy(alpha = 0.14f),
            ) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OpsInk,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun AdminOpsEmptyCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, OpsBlue.copy(alpha = 0.14f)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
        )
    }
}
