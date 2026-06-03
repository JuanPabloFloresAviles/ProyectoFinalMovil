package com.example.proyectofinalmovil.services.state

import com.example.proyectofinalmovil.services.mock.MockPurchase
import com.example.proyectofinalmovil.services.mock.MockShowtime
import org.junit.Assert.assertEquals
import org.junit.Test

class AdminDashboardTest {
    @Test
    fun administratorApiRoleReceivesAdministratorRole() {
        assertEquals(UserRole.ADMIN, userRoleFromApi("ADMINISTRADOR"))
        assertEquals(UserRole.ADMIN, userRoleFromApi(" administrador "))
    }

    @Test
    fun clientApiRoleReceivesClientRole() {
        assertEquals(UserRole.CLIENT, userRoleFromApi("CLIENTE"))
    }

    @Test
    fun dashboardCalculatesAveragePurchaseAndSalesTotals() {
        val purchases = listOf(
            purchase(ticketTotal = 90, concessionsTotal = 140),
            purchase(ticketTotal = 45, concessionsTotal = 65),
        )

        val metrics = calculateAdminDashboardMetrics(
            purchases = purchases,
            showtimesByMovieId = emptyMap(),
        )

        assertEquals(170, metrics.averagePurchase)
        assertEquals(135, metrics.ticketSales)
        assertEquals(205, metrics.concessionSales)
        assertEquals(2, metrics.transactions)
    }

    @Test
    fun dashboardReturnsZeroValuesWithoutPurchases() {
        val metrics = calculateAdminDashboardMetrics(
            purchases = emptyList(),
            showtimesByMovieId = emptyMap(),
        )

        assertEquals(0, metrics.averagePurchase)
        assertEquals(0, metrics.ticketSales)
        assertEquals(0, metrics.concessionSales)
        assertEquals(0, metrics.transactions)
    }

    @Test
    fun dashboardIdentifiesPreferredRoomFormatByOccupancy() {
        val metrics = calculateAdminDashboardMetrics(
            purchases = emptyList(),
            showtimesByMovieId = mapOf(
                "movie" to listOf(
                    showtime(roomType = "Tradicional", availableSeats = 80),
                    showtime(roomType = "3D", availableSeats = 20),
                    showtime(roomType = "4D", availableSeats = 50),
                ),
            ),
        )

        assertEquals("3D", metrics.preferredRoomFormat)
        assertEquals(80, metrics.preferredRoomOccupancyPercent)
    }

    private fun purchase(ticketTotal: Int, concessionsTotal: Int) = MockPurchase(
        folio = "folio",
        email = "cliente@cineuabcs.mx",
        movieId = "movie",
        date = "Hoy",
        time = "18:00",
        room = "Sala 1",
        seats = listOf("A1"),
        status = "Activa",
        ticketTotal = ticketTotal,
        concessionsTotal = concessionsTotal,
    )

    private fun showtime(roomType: String, availableSeats: Int) = MockShowtime(
        time = "18:00",
        room = "Sala 1",
        roomType = roomType,
        format = roomType,
        price = 50,
        availableSeats = availableSeats,
    )
}
