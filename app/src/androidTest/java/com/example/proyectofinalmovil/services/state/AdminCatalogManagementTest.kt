package com.example.proyectofinalmovil.services.state

import com.example.proyectofinalmovil.services.mock.MockShowtime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminCatalogManagementTest {
    @Test
    fun createdMovieReceivesInitialShowtime() {
        val state = AppUiState()

        state.upsertMovie(
            id = null,
            title = "Nueva película",
            genre = "Drama",
            classification = "B",
            duration = "100 min",
            rating = "4.1",
            year = "2026",
        )

        val movie = state.movies.first { it.title == "Nueva película" }
        assertTrue(state.showtimesFor(movie.id).isNotEmpty())
    }

    @Test
    fun adminCanEditShowtimesProductsAndCombos() {
        val state = AppUiState()
        state.upsertMovie(
            id = null,
            title = "Película base",
            genre = "Drama",
            classification = "B",
            duration = "100 min",
            rating = "4.1",
            year = "2026",
        )
        val movieId = state.movies.first().id

        state.upsertShowtime(
            movieId = movieId,
            index = 0,
            showtime = MockShowtime(
                time = "20:00",
                room = "Sala 4",
                roomType = "4D",
                format = "4D · Dob.",
                price = 90,
                availableSeats = 40,
            ),
        )
        state.upsertConcessionItem(
            id = null,
            name = "Agua mineral",
            description = "Botella 600ml",
            cost = 10,
            price = 30,
        )
        state.upsertConcessionCombo(
            id = null,
            name = "Combo refrescante",
            description = "Agua y palomitas",
            price = 80,
            productIds = listOf("agua-mineral"),
        )

        assertEquals("20:00", state.showtimesFor(movieId).first().time)
        assertTrue(state.concessions.any { it.name == "Agua mineral" })
        assertTrue(state.concessionCombos.any { it.name == "Combo refrescante" })
    }
}
