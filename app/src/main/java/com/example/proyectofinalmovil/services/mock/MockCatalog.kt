package com.example.proyectofinalmovil.services.mock

import androidx.compose.ui.graphics.Color

data class MockMovie(
    val id: String,
    val title: String,
    val genre: String,
    val classification: String,
    val duration: String,
    val rating: String,
    val year: String = "2026",
    val accentStart: Color,
    val accentEnd: Color,
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
)

data class MockUser(
    val name: String,
    val initials: String,
    val favoriteGenre: String,
    val avatarStart: Color,
    val avatarEnd: Color,
    val isOnline: Boolean = false,
)

val generosFiltro = listOf("Todo", "Estrenos", "Drama", "Sci-Fi", "Thriller", "Animación")

val mockMovies = listOf(
    MockMovie(
        id = "el-ultimo-faro",
        title = "El Último Faro",
        genre = "Drama",
        classification = "B",
        duration = "118 min",
        rating = "4.6",
        accentStart = Color(0xFFFF7A45),
        accentEnd = Color(0xFFC8281A),
        isFeatured = true,
        isNew = true,
    ),
    MockMovie(
        id = "cosecha-roja",
        title = "Cosecha Roja",
        genre = "Thriller",
        classification = "B15",
        duration = "104 min",
        rating = "4.2",
        year = "2025",
        accentStart = Color(0xFFD84545),
        accentEnd = Color(0xFF7A1010),
        isNew = true,
    ),
    MockMovie(
        id = "aurora-3024",
        title = "Aurora 3024",
        genre = "Sci-Fi",
        classification = "B",
        duration = "136 min",
        rating = "4.4",
        accentStart = Color(0xFF2E5D8C),
        accentEnd = Color(0xFF0A141F),
        isNew = true,
    ),
    MockMovie(
        id = "memorias-del-rio",
        title = "Memorias del Río",
        genre = "Drama",
        classification = "A",
        duration = "92 min",
        rating = "4.8",
        accentStart = Color(0xFF3DA86A),
        accentEnd = Color(0xFF0F4A28),
        isNew = true,
    ),
    MockMovie(
        id = "estacion-7",
        title = "Estación 7",
        genre = "Thriller",
        classification = "B15",
        duration = "127 min",
        rating = "4.5",
        year = "2025",
        accentStart = Color(0xFF2E5D8C),
        accentEnd = Color(0xFF0A141F),
    ),
    MockMovie(
        id = "cielo-de-sal",
        title = "Cielo de Sal",
        genre = "Animación",
        classification = "A",
        duration = "88 min",
        rating = "4.7",
        accentStart = Color(0xFFFFD245),
        accentEnd = Color(0xFFC08818),
    ),
    MockMovie(
        id = "nocturnia",
        title = "Nocturnia",
        genre = "Thriller",
        classification = "C",
        duration = "108 min",
        rating = "4.3",
        accentStart = Color(0xFF8C1A2E),
        accentEnd = Color(0xFF3A0A14),
    ),
)

val mockUsers = listOf(
    MockUser(
        name = "Daniel Rojas",
        initials = "DR",
        favoriteGenre = "Thriller",
        avatarStart = Color(0xFFD8454A),
        avatarEnd = Color(0xFF781820),
        isOnline = true,
    ),
    MockUser(
        name = "Mariana López",
        initials = "ML",
        favoriteGenre = "Sci-Fi",
        avatarStart = Color(0xFF3A6A8C),
        avatarEnd = Color(0xFF1A3A5A),
        isOnline = true,
    ),
    MockUser(
        name = "Sofía Tamez",
        initials = "ST",
        favoriteGenre = "Documental",
        avatarStart = Color(0xFF4AB07A),
        avatarEnd = Color(0xFF1F5A3D),
    ),
)
