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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.mockIncomingRequestIds
import com.example.proyectofinalmovil.services.mock.mockInitialFriendIds
import com.example.proyectofinalmovil.services.mock.mockOutgoingRequestIds
import com.example.proyectofinalmovil.services.mock.mockSocialUsers
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val VerdeSuave = Color(0xFFE7F5E8)

@Composable
fun SocialHubScreen(
    friendsCount: Int,
    incomingRequestsCount: Int,
    outgoingRequestsCount: Int,
    onVerSolicitudes: () -> Unit,
    onVerAmigos: () -> Unit,
    onBuscarPersonas: () -> Unit,
    onVerChats: () -> Unit,
    onVerRecomendaciones: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        ScreenIntro(
            eyebrow = "COMUNIDAD",
            title = "Centro social",
            body = "Encuentra compañeros, revisa solicitudes y arma tu red cinéfila dentro de CineUABCS.",
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialStatCard("Amigos", friendsCount.toString(), Modifier.weight(1f))
            SocialStatCard("Entrantes", incomingRequestsCount.toString(), Modifier.weight(1f))
            SocialStatCard("Enviadas", outgoingRequestsCount.toString(), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        ActionCard(
            title = "Solicitudes de amistad",
            body = "Acepta, rechaza o cancela solicitudes pendientes.",
            primaryText = "Ver solicitudes",
            onPrimaryClick = onVerSolicitudes,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            title = "Mis amigos",
            body = "Consulta quiénes ya forman parte de tu red.",
            primaryText = "Ver amigos",
            onPrimaryClick = onVerAmigos,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            title = "Buscar personas",
            body = "Descubre estudiantes con gustos similares.",
            primaryText = "Buscar usuarios",
            onPrimaryClick = onBuscarPersonas,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            title = "Chats",
            body = "Abre conversaciones privadas con tus amigos.",
            primaryText = "Ver chats",
            onPrimaryClick = onVerChats,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionCard(
            title = "Recomendaciones",
            body = "Consulta películas compartidas por tu red.",
            primaryText = "Ver recomendaciones",
            onPrimaryClick = onVerRecomendaciones,
        )
    }
}

@Composable
fun RequestsScreen(
    users: List<MockSocialUser>,
    incomingRequestIds: List<String>,
    outgoingRequestIds: List<String>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onCancel: (String) -> Unit,
    onVerAmigos: () -> Unit,
    onBuscarPersonas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            ScreenIntro(
                eyebrow = "SOLICITUDES",
                title = "Amistades pendientes",
                body = "Gestiona solicitudes entrantes y las que ya enviaste.",
            )

            Spacer(modifier = Modifier.height(18.dp))
            SectionTitle("Entrantes")
            val incomingUsers = users.filter { it.id in incomingRequestIds }
            if (incomingUsers.isEmpty()) {
                EmptySocialCard("No tienes solicitudes entrantes por ahora.")
            } else {
                incomingUsers.forEach { user ->
                    SocialUserCard(
                        user = user,
                        primaryText = "Aceptar",
                        secondaryText = "Rechazar",
                        onPrimaryClick = { onAccept(user.id) },
                        onSecondaryClick = { onReject(user.id) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            SectionTitle("Enviadas")
            val outgoingUsers = users.filter { it.id in outgoingRequestIds }
            if (outgoingUsers.isEmpty()) {
                EmptySocialCard("No tienes solicitudes enviadas.")
            } else {
                outgoingUsers.forEach { user ->
                    SocialUserCard(
                        user = user,
                        primaryText = "Cancelar solicitud",
                        onPrimaryClick = { onCancel(user.id) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        BottomActions(
            primaryText = "Ver amigos",
            onPrimaryClick = onVerAmigos,
            secondaryText = "Buscar personas",
            onSecondaryClick = onBuscarPersonas,
        )
    }
}

@Composable
fun FriendsScreen(
    friends: List<MockSocialUser>,
    onBuscarPersonas: () -> Unit,
    onVerSolicitudes: () -> Unit,
    onOpenChat: (String) -> Unit,
    onRecommendMovie: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            ScreenIntro(
                eyebrow = "AMIGOS",
                title = "Mis amigos",
                body = "Compañeros agregados para compartir gustos y preparar futuras recomendaciones.",
            )

            Spacer(modifier = Modifier.height(18.dp))
            if (friends.isEmpty()) {
                EmptySocialCard("Tu lista de amigos está vacía. Busca personas para empezar.")
            } else {
                friends.forEach { user ->
                    SocialUserCard(
                        user = user,
                        primaryText = "Abrir chat",
                        secondaryText = "Recomendar película",
                        onPrimaryClick = { onOpenChat(user.id) },
                        onSecondaryClick = { onRecommendMovie(user.id) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        BottomActions(
            primaryText = "Buscar personas",
            onPrimaryClick = onBuscarPersonas,
            secondaryText = "Ver solicitudes",
            onSecondaryClick = onVerSolicitudes,
        )
    }
}

@Composable
fun SearchUsersScreen(
    users: List<MockSocialUser>,
    friendIds: List<String>,
    incomingRequestIds: List<String>,
    outgoingRequestIds: List<String>,
    onAdd: (String) -> Unit,
    onCancel: (String) -> Unit,
    onVerSolicitudes: () -> Unit,
    onVerAmigos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            ScreenIntro(
                eyebrow = "BUSCAR",
                title = "Descubrir personas",
                body = "Encuentra estudiantes con gustos parecidos y envía una solicitud.",
            )

            Spacer(modifier = Modifier.height(18.dp))
            users.forEach { user ->
                val isFriend = user.id in friendIds
                val isOutgoing = user.id in outgoingRequestIds
                val isIncoming = user.id in incomingRequestIds
                SocialUserCard(
                    user = user,
                    primaryText = when {
                        isFriend -> "Ya es amigo"
                        isOutgoing -> "Cancelar solicitud"
                        isIncoming -> "Responder solicitud"
                        else -> "Agregar"
                    },
                    onPrimaryClick = {
                        when {
                            isOutgoing -> onCancel(user.id)
                            !isFriend && !isIncoming -> onAdd(user.id)
                        }
                    },
                    enabled = !isFriend,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        BottomActions(
            primaryText = "Ver solicitudes",
            onPrimaryClick = onVerSolicitudes,
            secondaryText = "Ver amigos",
            onSecondaryClick = onVerAmigos,
        )
    }
}

@Composable
private fun ScreenIntro(
    eyebrow: String,
    title: String,
    body: String,
) {
    Text(
        text = eyebrow,
        style = MaterialTheme.typography.labelSmall,
        color = AzulAccion,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = GrisTexto,
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = GrisTexto.copy(alpha = 0.65f),
    )
}

@Composable
private fun SocialStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = AzulAccion,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = GrisTexto.copy(alpha = 0.62f),
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    body: String,
    primaryText: String,
    onPrimaryClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.65f),
            )
            Spacer(modifier = Modifier.height(14.dp))
            UiPrimaryButton(
                text = primaryText,
                onClick = onPrimaryClick,
            )
        }
    }
}

@Composable
private fun SocialUserCard(
    user: MockSocialUser,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SocialAvatar(user = user)
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Text(
                        text = user.career,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.62f),
                    )
                    Text(
                        text = "Le gusta ${user.favoriteGenre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulAccion,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.72f),
            )
            Spacer(modifier = Modifier.height(14.dp))
            UiPrimaryButton(
                text = primaryText,
                onClick = onPrimaryClick,
                enabled = enabled,
            )
            if (secondaryText != null && onSecondaryClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = secondaryText,
                    onClick = onSecondaryClick,
                )
            }
        }
    }
}

@Composable
private fun SocialAvatar(user: MockSocialUser) {
    Box(
        modifier = Modifier
            .size(56.dp)
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
        if (user.isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3EC07A)),
            )
        }
    }
}

@Composable
private fun EmptySocialCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = VerdeSuave,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto.copy(alpha = 0.72f),
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GrisTexto,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun BottomActions(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
) {
    Surface(
        shadowElevation = 8.dp,
        color = FondoCrema,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            UiPrimaryButton(
                text = primaryText,
                onClick = onPrimaryClick,
            )
            Spacer(modifier = Modifier.height(8.dp))
            UiGhostButton(
                text = secondaryText,
                onClick = onSecondaryClick,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun SocialHubScreenPreview() {
    ProyectoFinalMovilTheme {
        SocialHubScreen(
            friendsCount = mockInitialFriendIds.size,
            incomingRequestsCount = mockIncomingRequestIds.size,
            outgoingRequestsCount = mockOutgoingRequestIds.size,
            onVerSolicitudes = {},
            onVerAmigos = {},
            onBuscarPersonas = {},
            onVerChats = {},
            onVerRecomendaciones = {},
        )
    }
}
