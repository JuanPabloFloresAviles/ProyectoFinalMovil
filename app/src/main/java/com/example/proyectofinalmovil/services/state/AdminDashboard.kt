package com.example.proyectofinalmovil.services.state

import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockShowtime

private const val MOCK_ROOM_CAPACITY = 100

enum class UserRole {
    CLIENT,
    ADMIN,
}

data class AdminDashboardMetrics(
    val averagePurchase: Int,
    val ticketSales: Int,
    val concessionSales: Int,
    val transactions: Int,
    val preferredRoomFormat: String,
    val preferredRoomOccupancyPercent: Int,
)

fun userRoleFromApi(role: String): UserRole {
    return if (role.trim().equals("ADMINISTRADOR", ignoreCase = true)) {
        UserRole.ADMIN
    } else {
        UserRole.CLIENT
    }
}

fun calculateAdminDashboardMetrics(
    purchases: List<MockPurchase>,
    showtimesByMovieId: Map<String, List<MockShowtime>>,
): AdminDashboardMetrics {
    val ticketSales = purchases.sumOf { it.ticketTotal }
    val concessionSales = purchases.sumOf { it.concessionsTotal }
    val transactions = purchases.size
    val averagePurchase = if (transactions == 0) 0 else (ticketSales + concessionSales) / transactions

    val occupancyByFormat = showtimesByMovieId.values
        .flatten()
        .groupBy { normalizedRoomFormat(it) }
        .mapValues { (_, showtimes) ->
            showtimes.map { occupancyPercent(it.availableSeats) }.average().toInt()
        }
    val preferredFormat = occupancyByFormat.maxByOrNull { it.value }

    return AdminDashboardMetrics(
        averagePurchase = averagePurchase,
        ticketSales = ticketSales,
        concessionSales = concessionSales,
        transactions = transactions,
        preferredRoomFormat = preferredFormat?.key ?: "Sin datos",
        preferredRoomOccupancyPercent = preferredFormat?.value ?: 0,
    )
}

private fun normalizedRoomFormat(showtime: MockShowtime): String {
    val description = "${showtime.roomType} ${showtime.format}".uppercase()
    return when {
        "4D" in description -> "4D"
        "3D" in description -> "3D"
        else -> "Tradicional"
    }
}

private fun occupancyPercent(availableSeats: Int): Int {
    val occupiedSeats = (MOCK_ROOM_CAPACITY - availableSeats).coerceIn(0, MOCK_ROOM_CAPACITY)
    return occupiedSeats * 100 / MOCK_ROOM_CAPACITY
}
