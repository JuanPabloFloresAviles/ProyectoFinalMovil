package com.example.proyectofinalmovil.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val PrimaryButtonShadow = Color(0xFF0A4E7A)

@Composable
fun UiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
    enabled: Boolean = true,
) {
    val shape = MaterialTheme.shapes.medium
    val shadowColor = if (enabled) {
        PrimaryButtonShadow
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Box(modifier = if (fillWidth) modifier.fillMaxWidth() else modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp)
                .background(shadowColor, shape),
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp,
            ),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun UiGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = if (fillWidth) modifier.fillMaxWidth() else modifier,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
