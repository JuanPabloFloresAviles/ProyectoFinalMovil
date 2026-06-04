package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import com.example.proyectofinalmovil.services.mock.MockConcessionItem
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.state.AdminConcessionCombo

private val AdminInk = Color(0xFF102A43)
private val AdminAccent = Color(0xFF1E5AA8)
private val AdminPanel = Color(0xFFEAF2FA)

@Composable
fun AdminMoviesManagementScreen(
    movies: List<MockMovie>,
    onSaveMovie: (String?, String, String, String, String, String, String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var classification by remember { mutableStateOf("A") }
    var duration by remember { mutableStateOf("90 min") }
    var rating by remember { mutableStateOf("4.0") }
    var year by remember { mutableStateOf("2026") }

    AdminEditorScaffold(
        title = "Gestión de películas",
        description = "Da de alta y edita títulos visibles en cartelera.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        AdminTextField(title, { title = it }, "Título")
        AdminTextField(genre, { genre = it }, "Género")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(
                value = classification,
                onValueChange = { classification = it },
                label = "Clasificación",
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                value = duration,
                onValueChange = { duration = it },
                label = "Duración",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(
                value = rating,
                onValueChange = { rating = it },
                label = "Rating",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                value = year,
                onValueChange = { year = it },
                label = "Año",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }
        UiPrimaryButton(
            text = if (editingId == null) "Dar de alta película" else "Guardar película",
            enabled = title.isNotBlank() && genre.isNotBlank(),
            onClick = {
                onSaveMovie(editingId, title, genre, classification, duration, rating, year)
                editingId = null
                title = ""
                genre = ""
                classification = "A"
                duration = "90 min"
                rating = "4.0"
                year = "2026"
            },
        )

        AdminSectionTitle("Cartelera registrada")
        movies.forEach { movie ->
            AdminListCard(
                title = movie.title,
                body = "${movie.genre} · ${movie.classification} · ${movie.duration} · ${movie.year}",
                action = "Editar",
                onAction = {
                    editingId = movie.id
                    title = movie.title
                    genre = movie.genre
                    classification = movie.classification
                    duration = movie.duration
                    rating = movie.rating
                    year = movie.year
                },
            )
        }
    }
}

@Composable
fun AdminShowtimesManagementScreen(
    movies: List<MockMovie>,
    showtimesByMovieId: Map<String, List<MockShowtime>>,
    onSaveShowtime: (String, Int?, MockShowtime) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedMovieId by remember(movies) { mutableStateOf(movies.firstOrNull()?.id ?: "") }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var time by remember { mutableStateOf("18:00") }
    var room by remember { mutableStateOf("Sala 1") }
    var roomType by remember { mutableStateOf("Tradicional") }
    var format by remember { mutableStateOf("2D · Dob.") }
    var price by remember { mutableStateOf("55") }
    var availableSeats by remember { mutableStateOf("80") }
    val selectedMovie = movies.firstOrNull { it.id == selectedMovieId }
    val showtimes = showtimesByMovieId[selectedMovieId] ?: emptyList()

    AdminEditorScaffold(
        title = "Gestión de funciones",
        description = "Programa horarios, salas, formato y precio por película.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        AdminSectionTitle("Película")
        movies.forEach { movie ->
            AdminListCard(
                title = movie.title,
                body = movie.genre,
                action = if (movie.id == selectedMovieId) "Seleccionada" else "Elegir",
                onAction = {
                    selectedMovieId = movie.id
                    editingIndex = null
                },
            )
        }

        AdminSectionTitle("Formulario de función")
        Text(
            text = selectedMovie?.title ?: "Sin película seleccionada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AdminInk,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(time, { time = it }, "Hora", modifier = Modifier.weight(1f))
            AdminTextField(room, { room = it }, "Sala", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(roomType, { roomType = it }, "Tipo", modifier = Modifier.weight(1f))
            AdminTextField(format, { format = it }, "Formato", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(
                value = price,
                onValueChange = { price = it },
                label = "Precio",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                value = availableSeats,
                onValueChange = { availableSeats = it },
                label = "Butacas disponibles",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }
        UiPrimaryButton(
            text = if (editingIndex == null) "Agregar función" else "Guardar función",
            enabled = selectedMovieId.isNotBlank() && time.isNotBlank() && room.isNotBlank(),
            onClick = {
                onSaveShowtime(
                    selectedMovieId,
                    editingIndex,
                    MockShowtime(
                        time = time.trim(),
                        room = room.trim(),
                        roomType = roomType.trim(),
                        format = format.trim(),
                        price = price.toIntOrNull() ?: 0,
                        availableSeats = availableSeats.toIntOrNull() ?: 0,
                    ),
                )
                editingIndex = null
                time = "18:00"
                room = "Sala 1"
                roomType = "Tradicional"
                format = "2D · Dob."
                price = "55"
                availableSeats = "80"
            },
        )

        AdminSectionTitle("Funciones programadas")
        if (showtimes.isEmpty()) {
            AdminEmptyCard("Aún no hay funciones para esta película.")
        } else {
            showtimes.forEachIndexed { index, showtime ->
                AdminListCard(
                    title = "${showtime.time} · ${showtime.room}",
                    body = "${showtime.roomType} · ${showtime.format} · $${showtime.price} · ${showtime.availableSeats} butacas",
                    action = "Editar",
                    onAction = {
                        editingIndex = index
                        time = showtime.time
                        room = showtime.room
                        roomType = showtime.roomType
                        format = showtime.format
                        price = showtime.price.toString()
                        availableSeats = showtime.availableSeats.toString()
                    },
                )
            }
        }
    }
}

@Composable
fun AdminConcessionsManagementScreen(
    products: List<MockConcessionItem>,
    combos: List<AdminConcessionCombo>,
    onSaveProduct: (String?, String, String, Int, Int) -> Unit,
    onSaveCombo: (String?, String, String, Int, List<String>) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var productId by remember { mutableStateOf<String?>(null) }
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("0") }
    var productPrice by remember { mutableStateOf("0") }

    var comboId by remember { mutableStateOf<String?>(null) }
    var comboName by remember { mutableStateOf("") }
    var comboDescription by remember { mutableStateOf("") }
    var comboPrice by remember { mutableStateOf("0") }
    val selectedProductIds = remember { mutableStateListOf<String>() }

    AdminEditorScaffold(
        title = "Gestión de dulcería",
        description = "Mantén productos, costos, precios y combos.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        AdminSectionTitle("Producto")
        AdminTextField(productName, { productName = it }, "Nombre")
        AdminTextField(productDescription, { productDescription = it }, "Descripción")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminTextField(
                value = productStock,
                onValueChange = { productStock = it },
                label = "Stock",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = "Precio",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }
        UiPrimaryButton(
            text = if (productId == null) "Agregar producto" else "Guardar producto",
            enabled = productName.isNotBlank() && productDescription.isNotBlank(),
            onClick = {
                onSaveProduct(
                    productId,
                    productName,
                    productDescription,
                    productStock.toIntOrNull() ?: 0,
                    productPrice.toIntOrNull() ?: 0,
                )
                productId = null
                productName = ""
                productDescription = ""
                productStock = "0"
                productPrice = "0"
            },
        )

        AdminSectionTitle("Productos registrados")
        products.forEach { product ->
            AdminListCard(
                title = product.name,
                body = "${product.description} · stock ${product.stock} · precio $${product.price}",
                action = "Editar",
                onAction = {
                    productId = product.id
                    productName = product.name
                    productDescription = product.description
                    productStock = product.stock.toString()
                    productPrice = product.price.toString()
                },
            )
        }

        AdminSectionTitle("Combo")
        AdminTextField(comboName, { comboName = it }, "Nombre del combo")
        AdminTextField(comboDescription, { comboDescription = it }, "Descripción")
        AdminTextField(
            value = comboPrice,
            onValueChange = { comboPrice = it },
            label = "Precio del combo",
            keyboardType = KeyboardType.Number,
        )
        products.forEach { product ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = product.id in selectedProductIds,
                    onCheckedChange = { checked ->
                        if (checked) selectedProductIds.add(product.id) else selectedProductIds.remove(product.id)
                    },
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AdminInk,
                )
            }
        }
        UiPrimaryButton(
            text = if (comboId == null) "Agregar combo" else "Guardar combo",
            enabled = comboName.isNotBlank() && selectedProductIds.isNotEmpty(),
            onClick = {
                onSaveCombo(
                    comboId,
                    comboName,
                    comboDescription,
                    comboPrice.toIntOrNull() ?: 0,
                    selectedProductIds.toList(),
                )
                comboId = null
                comboName = ""
                comboDescription = ""
                comboPrice = "0"
                selectedProductIds.clear()
            },
        )

        AdminSectionTitle("Combos registrados")
        if (combos.isEmpty()) {
            AdminEmptyCard("Aún no hay combos registrados.")
        } else {
            combos.forEach { combo ->
                AdminListCard(
                    title = combo.name,
                    body = "${combo.description} · $${combo.price} · ${combo.productIds.size} productos",
                    action = "Editar",
                    onAction = {
                        comboId = combo.id
                        comboName = combo.name
                        comboDescription = combo.description
                        comboPrice = combo.price.toString()
                        selectedProductIds.clear()
                        selectedProductIds.addAll(combo.productIds)
                    },
                )
            }
        }
    }
}

@Composable
private fun AdminEditorScaffold(
    title: String,
    description: String,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
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
            color = AdminInk,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = AdminPanel,
            border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.18f)),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
        }
        UiGhostButton(
            text = "Volver al dashboard",
            onClick = onBackToDashboard,
        )
    }
}

@Composable
private fun AdminTextField(
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
private fun AdminSectionTitle(text: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        color = AdminAccent,
    )
}

@Composable
private fun AdminListCard(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AdminInk,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            UiGhostButton(
                text = action,
                onClick = onAction,
            )
        }
    }
}

@Composable
private fun AdminEmptyCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.14f)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
        )
    }
}
