package com.example.proyectofinalmovil.services.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalAppUiState = compositionLocalOf<AppUiState> {
    AppUiState()
}

@Composable
fun ProvideAppUiState(
    appUiState: AppUiState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppUiState provides appUiState) {
        content()
    }
}
