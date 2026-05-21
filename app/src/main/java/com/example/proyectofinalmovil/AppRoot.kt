package com.example.proyectofinalmovil

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.AppIcons
import com.example.proyectofinalmovil.components.UiAppBar
import com.example.proyectofinalmovil.components.UiAvatar
import com.example.proyectofinalmovil.components.UiBadge
import com.example.proyectofinalmovil.components.UiCard
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiLoader
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.components.UiTabBar
import com.example.proyectofinalmovil.components.UiTabItem
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.mockMovies
import com.example.proyectofinalmovil.services.mock.mockUsers
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun AppRoot() {
    var selectedTab by remember { mutableIntStateOf(0) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                UiAppBar(
                    title = "CineUABCS",
                    navigationIcon = AppIcons.Back,
                    actionIcon = AppIcons.Search,
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = "Pantalla de papuejemplo",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Le digo hola ella me dice goodbye, le digo nena como tú ya no hay, dice que tiene novio pero yo no le creo, y es que se complica cada vez que la veo, eo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp),
                ) {
                    items(mockMovies) { movie ->
                        MovieHighlightCard(movie = movie)
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    UiInput(
                        value = "",
                        onValueChange = {},
                        label = "Busqueda",
                        placeholder = "Busca peliculas, amigos o funciones",
                        leadingIcon = AppIcons.Search,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        UiPrimaryButton(
                            text = "Continuar",
                            onClick = {},
                            modifier = Modifier.weight(1f),
                        )
                        UiGhostButton(
                            text = "Luego",
                            onClick = {},
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item {
                UiCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    elevated = true,
                ) {
                    Text(
                        text = "Amigos activos",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(mockUsers) { user ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                UiAvatar(user = user)
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                UiBadge(
                                    text = user.favoriteGenre,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
            item {
                UiCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                ) {
                    Text(
                        text = "Loader base",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    UiLoader(text = "Preparando la cartelera...")
                }
            }
            item {
                UiTabBar(
                    tabs = listOf(
                        UiTabItem("Inicio", AppIcons.Home),
                        UiTabItem("Boletos", AppIcons.Tickets),
                        UiTabItem("Comunidad", AppIcons.Community),
                        UiTabItem("Perfil", AppIcons.Profile),
                    ),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MovieHighlightCard(movie: MockMovie) {
    UiCard(modifier = Modifier.size(width = 212.dp, height = 252.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(movie.accentStart, movie.accentEnd),
                    ),
                ),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            UiBadge(text = movie.classification)
            UiBadge(
                text = "★ ${movie.rating}",
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${movie.genre} · ${movie.duration}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    ProyectoFinalMovilTheme {
        AppRoot()
    }
}
