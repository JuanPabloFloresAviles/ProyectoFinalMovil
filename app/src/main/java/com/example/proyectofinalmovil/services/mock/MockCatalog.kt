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
    val synopsis: String = "",
    val cast: List<String> = emptyList(),
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

data class MockShowtime(
    val time: String,
    val room: String,
    val roomType: String,
    val format: String,
    val price: Int,
    val availableSeats: Int,
)

val mockShowtimesEstacion7 = listOf(
    MockShowtime("15:40", "Sala 1", "Cinepremium", "2D · Subt.", 45, 89),
    MockShowtime("18:10", "Sala 2", "Cinepremium", "2D · Subt.", 45, 24),
    MockShowtime("20:50", "Sala 3", "Sala IMAX",   "IMAX · Subt.", 72, 102),
    MockShowtime("22:30", "Sala 1", "Cinepremium", "2D · Dob.", 55, 56),
)

val mockShowtimesByMovieId: Map<String, List<MockShowtime>> = mapOf(
    "estacion-7"    to mockShowtimesEstacion7,
    "el-ultimo-faro" to listOf(
        MockShowtime("16:00", "Sala 2", "Cinepremium", "2D · Subt.", 45, 60),
        MockShowtime("19:30", "Sala 1", "Cinepremium", "2D · Dob.", 45, 30),
    ),
    "aurora-3024" to listOf(
        MockShowtime("17:00", "Sala 3", "Sala IMAX", "IMAX · Subt.", 72, 80),
        MockShowtime("21:00", "Sala 2", "Cinepremium", "2D · Subt.", 45, 12),
    ),
)

val mockSynopsis: Map<String, String> = mapOf(
    "estacion-7" to "En una remota estación ferroviaria del altiplano, siete desconocidos esperan un tren que tal vez nunca llegue. A medida que la noche avanza, descubren que sus historias están más entrelazadas de lo que imaginaban.",
    "el-ultimo-faro" to "Un marinero retirado regresa a su pueblo natal para encontrarse con un faro que lleva décadas apagado. Lo que descubre ahí cambiará su comprensión de la familia y el tiempo.",
    "aurora-3024" to "En el año 3024, una misión de rescate llega a una colonia espacial abandonada. La tripulación descubre que los colonos no se fueron solos.",
    "cosecha-roja" to "Una periodista investiga una serie de desapariciones en un valle agrícola. Cada pista la lleva más profundo hacia una red de secretos que el pueblo lleva décadas ocultando.",
    "memorias-del-rio" to "Un río que atraviesa tres generaciones de una familia indígena sirve de hilo conductor en este documental poético sobre la memoria, el territorio y la resistencia.",
)

val mockCast: Map<String, List<String>> = mapOf(
    "estacion-7"     to listOf("Lucía M.", "Tomás A.", "Renata S.", "Joaquín D."),
    "el-ultimo-faro" to listOf("Carlos V.", "Sofía R.", "Andrés P.", "Elena M."),
    "aurora-3024"    to listOf("Mariana L.", "Diego F.", "Valentina C.", "Omar R."),
)

// Productos de dulcería
data class MockConcessionItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
)

val mockConcessions = listOf(
    MockConcessionItem(
        id = "palomitas-grandes",
        name = "Palomitas Grandes",
        description = "Balde de palomitas con mantequilla",
        price = 95,
    ),
    MockConcessionItem(
        id = "palomitas-medianas",
        name = "Palomitas Medianas",
        description = "Bolsa mediana de palomitas",
        price = 65,
    ),
    MockConcessionItem(
        id = "refresco",
        name = "Refresco 600ml",
        description = "Coca-Cola, Sprite o Fanta",
        price = 45,
    ),
    MockConcessionItem(
        id = "nachos",
        name = "Nachos con Queso",
        description = "Totopos con queso cheddar",
        price = 75,
    ),
    MockConcessionItem(
        id = "hot-dog",
        name = "Hot Dog",
        description = "Salchicha con pan, mostaza y catsup",
        price = 55,
    ),
)
