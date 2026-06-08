package com.example.proyectofinalmovil.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.api.AdminRoomOption
import com.example.proyectofinalmovil.services.mock.MockConcessionItem
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.MockShowtime
import com.example.proyectofinalmovil.services.state.AdminConcessionCombo
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AdminInk = Color(0xFF102A43)
private val AdminAccent = Color(0xFF1E5AA8)
private val AdminPanel = Color(0xFFEAF2FA)

@Composable
fun AdminMoviesManagementScreen(
    movies: List<MockMovie>,
    onSaveMovie: (String?, String, String, String, String) -> Unit,
    onImportMovies: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }
    var classification by remember { mutableStateOf("A") }
    var duration by remember { mutableStateOf("90 min") }
    val scrollState = rememberScrollState()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    AdminEditorScaffold(
        title = "Gestión de películas",
        description = "Da de alta y edita títulos visibles en cartelera.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
        scrollState = scrollState,
    ) {
        if (editingId != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.2f)),
            ) {
                Text(
                    text = "Editando película seleccionada. Modifica los campos y guarda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AdminInk,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
        UiGhostButton(
            text = "Importar desde API de películas",
            onClick = onImportMovies,
        )
        AdminTextField(title, { title = it }, "Título")
        AdminTextField(synopsis, { synopsis = it }, "Sinopsis")
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
        UiPrimaryButton(
            text = if (editingId == null) "Dar de alta película" else "Guardar película",
            enabled = title.isNotBlank() && synopsis.isNotBlank(),
            onClick = {
                onSaveMovie(editingId, title, synopsis, classification, duration)
                editingId = null
                title = ""
                synopsis = ""
                classification = "A"
                duration = "90 min"
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
                    synopsis = movie.synopsis
                    classification = movie.classification
                    duration = movie.duration
                    coroutineScope.launch { scrollState.animateScrollTo(0) }
                },
            )
        }
    }
}

@Composable
fun AdminShowtimesManagementScreen(
    movies: List<MockMovie>,
    rooms: List<AdminRoomOption>,
    showtimesByMovieId: Map<String, List<MockShowtime>>,
    onGoToCreateShowtime: () -> Unit,
    onEditShowtime: (String, String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var roomFilterId by remember { mutableStateOf("all") }
    var movieFilterId by remember(movies) { mutableStateOf("all") }
    val filteredShowtimes = remember(showtimesByMovieId, selectedDate, roomFilterId, movieFilterId, rooms, movies) {
        movies.flatMap { movie ->
            val functions = showtimesByMovieId[movie.id].orEmpty()
            functions.map { movie to it }
        }.filter { (movie, showtime) ->
            val sameMovie = movieFilterId == "all" || movie.id == movieFilterId
            val sameRoom = roomFilterId == "all" || showtime.roomId == roomFilterId
            val showtimeDate = startsAtDate(showtime.startsAt)
            val sameDate = showtimeDate != null && showtimeDate == selectedDate
            sameMovie && sameRoom && sameDate
        }
    }
    AdminEditorScaffold(
        title = "Gestión de funciones",
        description = "Programa horarios, salas, formato y precio por película.",
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    ) {
        UiPrimaryButton(
            text = "Nueva función",
            onClick = onGoToCreateShowtime,
        )

        AdminSectionTitle("Agenda del día")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            UiGhostButton(
                text = "‹ Día anterior",
                onClick = { selectedDate = selectedDate.minusDays(1) },
                modifier = Modifier.weight(1f),
            )
            UiGhostButton(
                text = "Siguiente día ›",
                onClick = { selectedDate = selectedDate.plusDays(1) },
                modifier = Modifier.weight(1f),
            )
        }
        AdminDateField(
            label = "Fecha",
            value = selectedDate.format(adminDateInputLabel),
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth,
                ).show()
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminSelectField(
                label = "Filtrar por sala",
                value = rooms.firstOrNull { it.id == roomFilterId }?.name ?: "Todas las salas",
                options = listOf("all" to "Todas las salas") + rooms.map { it.id to it.name },
                onSelect = { roomFilterId = it },
                modifier = Modifier.weight(1f),
            )
            AdminSelectField(
                label = "Filtrar por película",
                value = movies.firstOrNull { it.id == movieFilterId }?.title ?: "Todas las películas",
                options = listOf("all" to "Todas las películas") + movies.map { it.id to it.title },
                onSelect = { movieFilterId = it },
                modifier = Modifier.weight(1f),
            )
        }
        if (filteredShowtimes.isEmpty()) {
            AdminEmptyCard("No hay funciones registradas para ${selectedDate.format(adminDateLabel)} con esos filtros.")
        } else {
            filteredShowtimes.forEach { (movie, showtime) ->
                AdminListCard(
                    title = "${showtime.time} · ${movie.title}",
                    body = "${showtime.room} · ${showtime.roomType} · ${showtime.format} · $${showtime.price}",
                    action = "Editar función",
                    onAction = {
                        onEditShowtime(movie.id, showtime.id.orEmpty())
                    },
                )
            }
        }
    }
}

@Composable
fun AdminNewShowtimeScreen(
    movies: List<MockMovie>,
    rooms: List<AdminRoomOption>,
    onCreateShowtime: (String, MockShowtime) -> Unit,
    onBackToShowtimes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedMovieId by remember(movies) { mutableStateOf(movies.firstOrNull()?.id ?: "") }
    var time by remember { mutableStateOf("18:00") }
    var selectedRoomId by remember(rooms) { mutableStateOf(rooms.firstOrNull()?.id ?: "") }
    var roomType by remember { mutableStateOf("Tradicional") }
    var language by remember { mutableStateOf("Doblada") }
    var price by remember { mutableStateOf("55") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val selectedMovie = movies.firstOrNull { it.id == selectedMovieId }
    val selectedRoom = rooms.firstOrNull { it.id == selectedRoomId } ?: rooms.firstOrNull()

    AdminEditorScaffold(
        title = "Nueva función",
        description = "Crea una nueva función con fecha, sala, idioma y precio.",
        onBackToDashboard = onBackToShowtimes,
        modifier = modifier,
    ) {
        AdminSelectField(
            label = "Película",
            value = selectedMovie?.title ?: "Sin película",
            options = movies.map { it.id to "${it.title} · ${it.genre}" },
            onSelect = { selectedMovieId = it },
        )
        AdminDateField(
            label = "Fecha",
            value = selectedDate.format(adminDateInputLabel),
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth,
                ).show()
            },
        )
        AdminSelectField(
            label = "Sala",
            value = selectedRoom?.name ?: "Sin salas",
            options = rooms.map { it.id to "${it.name} · ${it.activeSeats} butacas" },
            onSelect = { selectedRoomId = it },
        )
        AdminTimeField(
            label = "Hora",
            value = time,
            onClick = {
                val (initialHour, initialMinute) = timeParts(time)
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        time = "%02d:%02d".format(hourOfDay, minute)
                    },
                    initialHour,
                    initialMinute,
                    true,
                ).show()
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminSelectField(
                label = "Tipo",
                value = roomType,
                options = listOf("Tradicional" to "Tradicional", "3D" to "3D", "4D" to "4D"),
                onSelect = { roomType = it },
                modifier = Modifier.weight(1f),
            )
            AdminSelectField(
                label = "Idioma",
                value = language,
                options = listOf("Doblada" to "Doblada", "Subtitulada" to "Subtitulada"),
                onSelect = { language = it },
                modifier = Modifier.weight(1f),
            )
        }
        AdminTextField(
            value = price,
            onValueChange = { price = it },
            label = "Precio",
            keyboardType = KeyboardType.Number,
        )
        UiPrimaryButton(
            text = "Crear función",
            enabled = selectedMovieId.isNotBlank() && time.isNotBlank() && selectedRoom != null,
            onClick = {
                onCreateShowtime(
                    selectedMovieId,
                    MockShowtime(
                        time = time.trim(),
                        room = selectedRoom?.name.orEmpty(),
                        roomType = roomType.trim(),
                        format = language.trim(),
                        price = price.toIntOrNull() ?: 0,
                        availableSeats = selectedRoom?.activeSeats ?: 0,
                        roomId = selectedRoom?.id,
                        startsAt = selectedDate.toString(),
                    ),
                )
            },
        )
    }
}

@Composable
fun AdminEditShowtimeScreen(
    movies: List<MockMovie>,
    rooms: List<AdminRoomOption>,
    showtimesByMovieId: Map<String, List<MockShowtime>>,
    initialMovieId: String,
    initialShowtime: MockShowtime?,
    onSaveShowtime: (String, MockShowtime) -> Unit,
    onDeleteShowtime: (String) -> Unit,
    onBackToShowtimes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fallbackMovieId = initialMovieId.ifBlank { movies.firstOrNull()?.id.orEmpty() }
    var selectedMovieId by remember(movies, initialMovieId) { mutableStateOf(fallbackMovieId) }
    var time by remember(initialShowtime) { mutableStateOf(initialShowtime?.time ?: "18:00") }
    var selectedRoomId by remember(rooms, initialShowtime) {
        mutableStateOf(
            initialShowtime?.roomId
                ?: rooms.firstOrNull { it.name == initialShowtime?.room }?.id
                ?: rooms.firstOrNull()?.id
                ?: "",
        )
    }
    var roomType by remember(initialShowtime) { mutableStateOf(initialShowtime?.roomType ?: "Tradicional") }
    var language by remember(initialShowtime) { mutableStateOf(initialShowtime?.format ?: "Doblada") }
    var price by remember(initialShowtime) { mutableStateOf((initialShowtime?.price ?: 55).toString()) }
    var selectedDate by remember(initialShowtime) {
        mutableStateOf(startsAtDate(initialShowtime?.startsAt) ?: LocalDate.now())
    }
    val selectedMovie = movies.firstOrNull { it.id == selectedMovieId }
    val selectedRoom = rooms.firstOrNull { it.id == selectedRoomId } ?: rooms.firstOrNull()
    val originalDate = startsAtDate(initialShowtime?.startsAt)
    val overlapMessage = showtimeOverlapMessage(
        movies = movies,
        showtimesByMovieId = showtimesByMovieId,
        selectedMovieId = selectedMovieId,
        originalMovieId = initialMovieId,
        editingShowtimeId = initialShowtime?.id,
        originalRoomId = initialShowtime?.roomId,
        originalDate = originalDate,
        originalTime = initialShowtime?.time,
        selectedRoomId = selectedRoom?.id,
        selectedDate = selectedDate,
        time = time,
    )

    AdminEditorScaffold(
        title = "Editar función",
        description = "Actualiza la función seleccionada o elimínala si ya no debe mostrarse.",
        onBackToDashboard = onBackToShowtimes,
        modifier = modifier,
    ) {
        if (initialShowtime == null) {
            AdminEmptyCard("No se encontró la función seleccionada. Vuelve a gestión de funciones e inténtalo otra vez.")
            return@AdminEditorScaffold
        }

        AdminSelectField(
            label = "Película",
            value = selectedMovie?.title ?: "Sin película",
            options = movies.map { it.id to "${it.title} · ${it.genre}" },
            onSelect = { selectedMovieId = it },
        )
        AdminDateField(
            label = "Fecha",
            value = selectedDate.format(adminDateInputLabel),
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth,
                ).show()
            },
        )
        AdminSelectField(
            label = "Sala",
            value = selectedRoom?.name ?: "Sin salas",
            options = rooms.map { it.id to "${it.name} · ${it.activeSeats} butacas" },
            onSelect = { selectedRoomId = it },
        )
        AdminTimeField(
            label = "Hora",
            value = time,
            onClick = {
                val (initialHour, initialMinute) = timeParts(time)
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        time = "%02d:%02d".format(hourOfDay, minute)
                    },
                    initialHour,
                    initialMinute,
                    true,
                ).show()
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminSelectField(
                label = "Tipo",
                value = roomType,
                options = listOf("Tradicional" to "Tradicional", "3D" to "3D", "4D" to "4D"),
                onSelect = { roomType = it },
                modifier = Modifier.weight(1f),
            )
            AdminSelectField(
                label = "Idioma",
                value = language,
                options = listOf("Doblada" to "Doblada", "Subtitulada" to "Subtitulada"),
                onSelect = { language = it },
                modifier = Modifier.weight(1f),
            )
        }
        AdminTextField(
            value = price,
            onValueChange = { price = it },
            label = "Precio",
            keyboardType = KeyboardType.Number,
        )
        if (overlapMessage != null) {
            Text(
                text = overlapMessage,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB42318),
            )
        }
        UiPrimaryButton(
            text = "Guardar cambios",
            enabled = selectedMovieId.isNotBlank() &&
                time.isNotBlank() &&
                selectedRoom != null &&
                overlapMessage == null,
            onClick = {
                onSaveShowtime(
                    selectedMovieId,
                    MockShowtime(
                        id = initialShowtime.id,
                        movieId = selectedMovieId,
                        time = time.trim(),
                        room = selectedRoom?.name.orEmpty(),
                        roomType = roomType.trim(),
                        format = language.trim(),
                        price = price.toIntOrNull() ?: 0,
                        availableSeats = selectedRoom?.activeSeats ?: 0,
                        roomId = selectedRoom?.id,
                        startsAt = selectedDate.toString(),
                    ),
                )
            },
        )
        UiGhostButton(
            text = "Eliminar función",
            onClick = {
                initialShowtime.id?.takeIf { it.isNotBlank() }?.let(onDeleteShowtime)
            },
        )
    }
}

@Composable
fun AdminConcessionsManagementScreen(
    products: List<MockConcessionItem>,
    combos: List<AdminConcessionCombo>,
    onSaveProduct: (String?, String, String, Int, Int, Int) -> Unit,
    onSaveCombo: (String?, String, String, Int, List<String>) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var productId by remember { mutableStateOf<String?>(null) }
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productCost by remember { mutableStateOf("0") }
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
                value = productCost,
                onValueChange = { productCost = it },
                label = "Costo",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
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
                    productCost.toIntOrNull() ?: 0,
                    productStock.toIntOrNull() ?: 0,
                    productPrice.toIntOrNull() ?: 0,
                )
                productId = null
                productName = ""
                productDescription = ""
                productCost = "0"
                productStock = "0"
                productPrice = "0"
            },
        )

        AdminSectionTitle("Productos registrados")
        products.forEach { product ->
            AdminListCard(
                title = product.name,
                body = "${product.description} · costo $${product.cost} · precio $${product.price} · margen $${product.price - product.cost} · stock ${product.stock}",
                action = "Editar",
                onAction = {
                    productId = product.id
                    productName = product.name
                    productDescription = product.description
                    productCost = product.cost.toString()
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
                    body = "${combo.description} · $${combo.price} · ${combo.productIds.size} productos · ${productNames(products, combo)}",
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

private val adminDateLabel: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
private val adminDateInputLabel: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "MX"))

@Composable
private fun AdminEditorScaffold(
    title: String,
    description: String,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
private fun AdminSelectField(
    label: String,
    value: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.35f)),
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AdminAccent,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = AdminInk,
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelect(id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AdminDateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.35f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AdminAccent,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AdminInk,
            )
        }
    }
}

@Composable
private fun AdminTimeField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = BorderStroke(1.dp, AdminAccent.copy(alpha = 0.35f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AdminAccent,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AdminInk,
            )
        }
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

private fun showtimeOverlapMessage(
    movies: List<MockMovie>,
    showtimesByMovieId: Map<String, List<MockShowtime>>,
    selectedMovieId: String,
    originalMovieId: String? = null,
    editingShowtimeId: String?,
    originalRoomId: String? = null,
    originalDate: LocalDate? = null,
    originalTime: String? = null,
    selectedRoomId: String?,
    selectedDate: LocalDate,
    time: String,
): String? {
    val roomId = selectedRoomId ?: return null
    val start = minutesFromTime(time) ?: return "La hora debe tener formato HH:mm."
    val selectedMovie = movies.firstOrNull { it.id == selectedMovieId } ?: return null
    val duration = durationMinutes(selectedMovie)
    val end = start + duration
    val overlaps = showtimesByMovieId.any { (movieId, showtimes) ->
        val movieDuration = durationMinutes(movies.firstOrNull { it.id == movieId })
        showtimes.anyIndexed { _, showtime ->
            val sameEditedShowtimeById = !editingShowtimeId.isNullOrBlank() && showtime.id == editingShowtimeId
            val sameEditedShowtimeBySnapshot =
                originalMovieId != null &&
                    movieId == originalMovieId &&
                    originalDate != null &&
                    startsAtDate(showtime.startsAt) == originalDate &&
                    originalTime != null &&
                    showtime.time == originalTime &&
                    originalRoomId != null &&
                    showtime.roomId == originalRoomId
            val sameEditedShowtime = sameEditedShowtimeById || sameEditedShowtimeBySnapshot
            val sameRoom = showtime.roomId == roomId
            val sameDate = startsAtDate(showtime.startsAt) == selectedDate
            val existingStart = minutesFromTime(showtime.time)
            val existingEnd = existingStart?.plus(movieDuration)
            !sameEditedShowtime &&
                sameRoom &&
                sameDate &&
                existingStart != null &&
                existingEnd != null &&
                start < existingEnd &&
                end > existingStart
        }
    }
    return if (overlaps) {
        "Esta función se sobrepone con otra función de la sala seleccionada."
    } else {
        null
    }
}

private fun timeParts(value: String): Pair<Int, Int> {
    val parts = value.trim().split(":")
    val hours = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 18
    val minutes = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
    return hours to minutes
}

private fun durationMinutes(movie: MockMovie?): Int {
    return movie?.duration?.filter { it.isDigit() }?.toIntOrNull()?.coerceAtLeast(1) ?: 90
}

private fun minutesFromTime(value: String): Int? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null
    val hours = parts[0].toIntOrNull() ?: return null
    val minutes = parts[1].toIntOrNull() ?: return null
    if (hours !in 0..23 || minutes !in 0..59) return null
    return hours * 60 + minutes
}

private inline fun <T> Iterable<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (item in this) {
        if (predicate(index, item)) return true
        index += 1
    }
    return false
}

private fun startsAtDate(startsAt: String?): LocalDate? {
    if (startsAt.isNullOrBlank()) return null
    // Si es un instante UTC del backend, lo llevamos al día de La Paz (UTC-7);
    // si ya es una fecha "yyyy-MM-dd" del selector, la usamos tal cual.
    return runCatching {
        Instant.parse(startsAt).atOffset(ZoneOffset.ofHours(-7)).toLocalDate()
    }.getOrNull()
        ?: runCatching { LocalDate.parse(startsAt.take(10)) }.getOrNull()
}

private fun productNames(
    products: List<MockConcessionItem>,
    combo: AdminConcessionCombo,
): String {
    return combo.productIds.mapNotNull { id ->
        products.firstOrNull { it.id == id }?.name
    }.joinToString(", ")
}
