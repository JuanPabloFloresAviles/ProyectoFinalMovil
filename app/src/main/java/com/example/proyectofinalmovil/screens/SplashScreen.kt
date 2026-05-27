package com.example.proyectofinalmovil.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinalmovil.components.AppIcons
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.ui.theme.CinemaBlue
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

@Composable
fun SplashScreen(
    onComenzar: () -> Unit,
    onInvitado: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp),

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = AppIcons.Movies,
                    contentDescription = "Logo CineUABCS",
                    tint = CinemaBlue,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "CineUABCS",
                    style = MaterialTheme.typography.titleLarge,
                    color = CinemaBlue,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "CINE UNIVERSITARIO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(44.dp))

            val eslogan = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                        letterSpacing = MaterialTheme.typography.displayMedium.letterSpacing,
                    )
                ) {
                    append("Tu butaca\n")
                }
                withStyle(
                    SpanStyle(
                        color = CinemaBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                        letterSpacing = MaterialTheme.typography.displayMedium.letterSpacing,
                    )
                ) {
                    append("te está esperando.")
                }
            }

            Text(
                text = eslogan,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.displayMedium.lineHeight,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Cartelera, funciones y dulcería del cine\nuniversitario, en la palma de tu mano.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UiPrimaryButton(
                text = "Comenzar",
                onClick = onComenzar,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            UiGhostButton(
                text = "Continuar como invitado",
                onClick = onInvitado,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "V 2.4 · SALA 4K DOLBY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EA)
@Composable
private fun SplashScreenPreview() {
    ProyectoFinalMovilTheme {
        SplashScreen(
            onComenzar = {},
            onInvitado = {},
        )
    }
}
