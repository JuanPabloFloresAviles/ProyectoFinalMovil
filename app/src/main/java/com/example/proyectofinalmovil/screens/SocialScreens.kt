package com.example.proyectofinalmovil.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinalmovil.components.AppIcons
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockChatMessage
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.mockChatMessages
import com.example.proyectofinalmovil.services.mock.mockIncomingRequestIds
import com.example.proyectofinalmovil.services.mock.mockInitialFriendIds
import com.example.proyectofinalmovil.services.mock.mockOutgoingRequestIds
import com.example.proyectofinalmovil.services.mock.mockSocialUsers
import com.example.proyectofinalmovil.services.state.LocalAppUiState
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val VerdeSuave = Color(0xFFE7F5E8)

@Composable
fun SocialHubScreen(
    friends: List<MockSocialUser>,
    messages: List<MockChatMessage>,
    friendsCount: Int,
    incomingRequestsCount: Int,
    outgoingRequestsCount: Int,
    onVerSolicitudes: () -> Unit,
    onVerAmigos: () -> Unit,
    onIniciarSesion: () -> Unit,
    onAgregarAmigo: () -> Unit,
    onOpenChat: (String) -> Unit,
    onVerRecomendaciones: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppUiState.current
    val isSignedIn = appState.authToken.isNotBlank()
    var searchQuery by remember { mutableStateOf("") }
    val recentChats = remember(friends, messages, searchQuery) {
        friends.mapNotNull { friend ->
            val thread = messages.filter { it.friendId == friend.id }
            val lastMessage = thread.lastOrNull() ?: return@mapNotNull null
            friend to lastMessage
        }.filter { (friend, _) ->
            searchQuery.isBlank() ||
                friend.name.contains(searchQuery, ignoreCase = true)
        }.sortedByDescending { (_, message) -> messageSortKey(message.time) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (!isSignedIn) {
            SignedOutSocialState(onIniciarSesion = onIniciarSesion)
            return@Column
        }

        ScreenIntro(
            eyebrow = "COMUNIDAD",
            title = "Centro social",
            body = "Encuentra compañeros, revisa solicitudes y arma tu red cinéfila dentro de CineUABCS.",
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialStatCard(
                label = "Amigos",
                value = friendsCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = onVerAmigos,
            )
            SocialStatCard(
                label = "Entrantes",
                value = incomingRequestsCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = onVerSolicitudes,
            )
            SocialStatCard("Enviadas", outgoingRequestsCount.toString(), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            UiInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Buscar amigos",
                placeholder = "Escribe un nombre",
                modifier = Modifier.weight(1f),
            )
            Surface(
                onClick = onAgregarAmigo,
                shape = MaterialTheme.shapes.medium,
                color = AzulAccion,
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = AppIcons.AddFriend,
                        contentDescription = "Agregar amigo por código",
                        tint = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        ActionCard(
            title = "Recomendaciones",
            body = "Consulta películas compartidas por tu red.",
            primaryText = "Ver recomendaciones",
            onPrimaryClick = onVerRecomendaciones,
        )

        Spacer(modifier = Modifier.height(18.dp))
        SectionTitle("Chats recientes")
        when {
            friends.isEmpty() -> EmptySocialCard("Agrega amigos para empezar a chatear.")
            recentChats.isEmpty() && searchQuery.isNotBlank() ->
                EmptySocialCard("No encontramos amigos o chats con esa búsqueda.")
            recentChats.isEmpty() ->
                EmptySocialCard("Todavía no hay conversaciones activas.")
            else -> recentChats.forEach { (friend, lastMessage) ->
                ChatPreviewCard(
                    friend = friend,
                    lastMessage = lastMessage,
                    onOpenChat = { onOpenChat(friend.id) },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SignedOutSocialState(
    onIniciarSesion: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Aún no has iniciado sesión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(16.dp))
            UiPrimaryButton(
                text = "Iniciar Sesión",
                onClick = onIniciarSesion,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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

private fun messageSortKey(time: String): Int {
    val trimmed = time.trim()
    return when {
        trimmed.equals("Ahora", ignoreCase = true) -> 100_000
        trimmed.equals("Nuevo", ignoreCase = true) -> 99_000
        trimmed.equals("Ayer", ignoreCase = true) -> 50_000
        Regex("""\d{2}:\d{2}""").matches(trimmed) -> {
            val parts = trimmed.split(":")
            parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0
        }
        else -> 1
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
fun AddFriendByCodeScreen(
    users: List<MockSocialUser>,
    friendIds: List<String>,
    incomingRequestIds: List<String>,
    outgoingRequestIds: List<String>,
    myFriendCode: String,
    onAdd: (String) -> Unit,
    onCancel: (String) -> Unit,
    onVerSolicitudes: () -> Unit,
    onVerAmigos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var codeQuery by remember { mutableStateOf("") }
    var submittedCode by remember { mutableStateOf("") }

    val match = remember(users, submittedCode) {
        val normalized = submittedCode.trim()
        if (normalized.isBlank()) {
            null
        } else {
            users.find { it.friendCode.isNotBlank() && it.friendCode.equals(normalized, ignoreCase = true) }
        }
    }

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
                eyebrow = "AGREGAR",
                title = "Agregar por código",
                body = "Pide el código de amigo de la persona que quieras agregar y escríbelo para enviarle una solicitud.",
            )

            if (myFriendCode.isNotBlank()) {
                Spacer(modifier = Modifier.height(18.dp))
                MyFriendCodeCard(code = myFriendCode)
            }

            Spacer(modifier = Modifier.height(18.dp))
            UiInput(
                value = codeQuery,
                onValueChange = { codeQuery = it },
                label = "Código de amigo",
                placeholder = "Ej. CINE-AB12",
            )
            Spacer(modifier = Modifier.height(12.dp))
            UiPrimaryButton(
                text = "Buscar código",
                onClick = { submittedCode = codeQuery },
                enabled = codeQuery.isNotBlank(),
            )

            Spacer(modifier = Modifier.height(18.dp))
            when {
                submittedCode.isBlank() -> Unit
                match == null ->
                    EmptySocialCard("No encontramos a nadie con el código \"${submittedCode.trim()}\".")
                else -> {
                    val isFriend = match.id in friendIds
                    val isOutgoing = match.id in outgoingRequestIds
                    val isIncoming = match.id in incomingRequestIds
                    SocialUserCard(
                        user = match,
                        primaryText = when {
                            isFriend -> "Ya es amigo"
                            isOutgoing -> "Cancelar solicitud"
                            isIncoming -> "Responder solicitud"
                            else -> "Agregar"
                        },
                        onPrimaryClick = {
                            when {
                                isOutgoing -> onCancel(match.id)
                                !isFriend && !isIncoming -> onAdd(match.id)
                            }
                        },
                        enabled = !isFriend && !isIncoming,
                    )
                }
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
private fun MyFriendCodeCard(code: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = VerdeSuave,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TU CÓDIGO DE AMIGO",
                style = MaterialTheme.typography.labelSmall,
                color = AzulAccion,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Compártelo para que otras personas te agreguen.",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto.copy(alpha = 0.65f),
            )
        }
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
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(vertical = 12.dp),
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
            friends = mockSocialUsers.filter { it.id in mockInitialFriendIds },
            messages = mockChatMessages,
            friendsCount = mockInitialFriendIds.size,
            incomingRequestsCount = mockIncomingRequestIds.size,
            outgoingRequestsCount = mockOutgoingRequestIds.size,
            onVerSolicitudes = {},
            onVerAmigos = {},
            onIniciarSesion = {},
            onAgregarAmigo = {},
            onOpenChat = {},
            onVerRecomendaciones = {},
        )
    }
}
