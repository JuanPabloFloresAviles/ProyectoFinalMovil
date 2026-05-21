package com.example.proyectofinalmovil.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UiCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    elevated: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (elevated) 1.5.dp else 1.dp,
            color = if (elevated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (elevated) 6.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content,
        )
    }
}
