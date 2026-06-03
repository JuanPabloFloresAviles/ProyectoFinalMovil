package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Chair
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocalMovies
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.state.AdminDashboardMetrics
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val AdminNavy = Color(0xFF102A43)
private val AdminBlue = Color(0xFF1E5AA8)
private val AdminGold = Color(0xFFD99A22)
private val AdminMist = Color(0xFFEAF2FA)

@Composable
fun AdminDashboardScreen(
    metrics: AdminDashboardMetrics,
    moviesCount: Int,
    showtimesCount: Int,
    concessionsCount: Int,
    onMovies: () -> Unit,
    onShowtimes: () -> Unit,
    onConcessions: () -> Unit,
    onRooms: () -> Unit,
    onReports: () -> Unit,
    onClientView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
    ) {
        AdminHero()
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Resumen operativo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = AdminNavy,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                label = "PROMEDIO POR COMPRA",
                value = money(metrics.averagePurchase),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label = "TRANSACCIONES",
                value = metrics.transactions.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                label = "VENTA DE BOLETOS",
                value = money(metrics.ticketSales),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label = "VENTA DE DULCERÍA",
                value = money(metrics.concessionSales),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        PreferredFormatCard(metrics)

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Operación del cine",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = AdminNavy,
        )
        Spacer(modifier = Modifier.height(12.dp))

        AdminModuleCard(
            title = "Películas",
            body = "$moviesCount títulos en cartelera",
            icon = Icons.Rounded.LocalMovies,
            onClick = onMovies,
        )
        AdminModuleCard(
            title = "Funciones",
            body = "$showtimesCount funciones programadas",
            icon = Icons.Rounded.Schedule,
            onClick = onShowtimes,
        )
        AdminModuleCard(
            title = "Dulcería",
            body = "$concessionsCount productos y combos",
            icon = Icons.Rounded.Inventory2,
            onClick = onConcessions,
        )
        AdminModuleCard(
            title = "Salas y butacas",
            body = "Configura infraestructura operativa",
            icon = Icons.Rounded.Chair,
            onClick = onRooms,
        )
        AdminModuleCard(
            title = "Ventas y estadísticas",
            body = "Consulta reportes comerciales",
            icon = Icons.Rounded.Analytics,
            onClick = onReports,
        )

        Spacer(modifier = Modifier.height(8.dp))
        UiGhostButton(
            text = "Ir a la vista del cliente",
            onClick = onClientView,
        )
    }
}

@Composable
fun AdminModuleScreen(
    title: String,
    description: String,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AdminIcon(Icons.Rounded.Shield)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = AdminNavy,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Surface(
            color = AdminMist,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, AdminBlue.copy(alpha = 0.2f)),
        ) {
            Text(
                text = "La estructura de navegación ya está lista. La gestión completa se implementará en los siguientes commits.",
                style = MaterialTheme.typography.bodyMedium,
                color = AdminNavy,
                modifier = Modifier.padding(16.dp),
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        UiPrimaryButton(
            text = "Volver al dashboard",
            onClick = onBackToDashboard,
        )
    }
}

@Composable
fun AdminAccessDeniedScreen(
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AdminIcon(Icons.Rounded.Shield)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Acceso restringido",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = AdminNavy,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Este módulo sólo está disponible para cuentas con rol de administrador.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(22.dp))
        UiPrimaryButton(
            text = "Volver al inicio de sesión",
            onClick = onBackToLogin,
        )
    }
}

@Composable
private fun AdminHero() {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(AdminNavy, AdminBlue),
                    ),
                )
                .padding(20.dp),
        ) {
            Text(
                text = "CINECONTROL · ADMIN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AdminGold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "La operación del cine, en una sola vista.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Consulta indicadores clave y entra a los módulos administrativos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.78f),
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, AdminBlue.copy(alpha = 0.16f)),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AdminBlue,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AdminNavy,
            )
        }
    }
}

@Composable
private fun PreferredFormatCard(metrics: AdminDashboardMetrics) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = AdminMist,
        border = BorderStroke(1.dp, AdminBlue.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AdminIcon(Icons.Rounded.Analytics)
            Column {
                Text(
                    text = "FORMATO PREFERIDO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AdminBlue,
                )
                Text(
                    text = metrics.preferredRoomFormat,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AdminNavy,
                )
                Text(
                    text = "${metrics.preferredRoomOccupancyPercent}% de ocupación promedio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AdminModuleCard(
    title: String,
    body: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, AdminBlue.copy(alpha = 0.14f)),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AdminIcon(icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AdminNavy,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall,
                color = AdminBlue,
            )
        }
    }
}

@Composable
private fun AdminIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(AdminBlue, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

private fun money(value: Int): String = "$$value"

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun AdminDashboardPreview() {
    ProyectoFinalMovilTheme {
        AdminDashboardScreen(
            metrics = AdminDashboardMetrics(
                averagePurchase = 180,
                ticketSales = 270,
                concessionSales = 205,
                transactions = 3,
                preferredRoomFormat = "Tradicional",
                preferredRoomOccupancyPercent = 64,
            ),
            moviesCount = 7,
            showtimesCount = 8,
            concessionsCount = 6,
            onMovies = {},
            onShowtimes = {},
            onConcessions = {},
            onRooms = {},
            onReports = {},
            onClientView = {},
        )
    }
}
