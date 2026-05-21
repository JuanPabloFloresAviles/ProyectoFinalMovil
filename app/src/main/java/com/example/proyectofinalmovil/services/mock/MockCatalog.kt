package com.example.proyectofinalmovil.services.mock

import androidx.compose.ui.graphics.Color

data class MockMovie(
    val title: String,
    val genre: String,
    val classification: String,
    val duration: String,
    val rating: String,
    val accentStart: Color,
    val accentEnd: Color,
    val isFeatured: Boolean = false,
)

data class MockUser(
    val name: String,
    val initials: String,
    val favoriteGenre: String,
    val avatarStart: Color,
    val avatarEnd: Color,
    val isOnline: Boolean = false,
)

val mockMovies = listOf(
    MockMovie(
        title = "El Ultimo Faro",
        genre = "Drama",
        classification = "B",
        duration = "118 min",
        rating = "4.6",
        accentStart = Color(0xFFFF7A45),
        accentEnd = Color(0xFFC8281A),
        isFeatured = true,
    ),
    MockMovie(
        title = "Aurora 3024",
        genre = "Sci-Fi",
        classification = "B",
        duration = "136 min",
        rating = "4.4",
        accentStart = Color(0xFF2E5D8C),
        accentEnd = Color(0xFF0A141F),
    ),
    MockMovie(
        title = "Cielo de Sal",
        genre = "Animacion",
        classification = "A",
        duration = "88 min",
        rating = "4.7",
        accentStart = Color(0xFFFFD245),
        accentEnd = Color(0xFFC08818),
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
        name = "Mariana Lopez",
        initials = "ML",
        favoriteGenre = "Sci-Fi",
        avatarStart = Color(0xFF3A6A8C),
        avatarEnd = Color(0xFF1A3A5A),
        isOnline = true,
    ),
    MockUser(
        name = "Sofia Tamez",
        initials = "ST",
        favoriteGenre = "Documental",
        avatarStart = Color(0xFF4AB07A),
        avatarEnd = Color(0xFF1F5A3D),
    ),
)
