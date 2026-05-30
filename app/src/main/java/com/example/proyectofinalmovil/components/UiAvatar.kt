package com.example.proyectofinalmovil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.services.mock.MockClient

@Composable
fun UiAvatar(
    user: MockClient,
    modifier: Modifier = Modifier,
    size: Int = 52,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(user.avatarStart, user.avatarEnd),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = user.initials,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        if (user.isOnline) {
            OnlineDot()
        }
    }
}

@Composable
private fun BoxScope.OnlineDot() {
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(12.dp)
            .clip(CircleShape)
            .background(Color(0xFF3EC07A)),
    )
}
