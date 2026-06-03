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
import com.example.proyectofinalmovil.components.UiGhostButton
import com.example.proyectofinalmovil.components.UiInput
import com.example.proyectofinalmovil.components.UiPrimaryButton
import com.example.proyectofinalmovil.services.mock.MockChatMessage
import com.example.proyectofinalmovil.services.mock.MockMovie
import com.example.proyectofinalmovil.services.mock.MockMovieRecommendation
import com.example.proyectofinalmovil.services.mock.MockSocialUser
import com.example.proyectofinalmovil.services.mock.mockChatMessages
import com.example.proyectofinalmovil.services.mock.mockInitialFriendIds
import com.example.proyectofinalmovil.services.mock.mockMovies
import com.example.proyectofinalmovil.services.mock.mockRecommendations
import com.example.proyectofinalmovil.services.mock.mockSocialUsers
import com.example.proyectofinalmovil.ui.theme.ProyectoFinalMovilTheme

private val FondoCrema = Color(0xFFF9F6EB)
private val AzulAccion = Color(0xFF1067A6)
private val GrisTexto = Color(0xFF333333)
private val BordeCard = Color(0xFFD6D1C2)
private val VerdeSuave = Color(0xFFE7F5E8)

@Composable
fun ChatListScreen(
    friends: List<MockSocialUser>,
    messages: List<MockChatMessage>,
    onOpenChat: (String) -> Unit,
    onVerRecomendaciones: () -> Unit,
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
                eyebrow = "CHATS",
                title = "Conversaciones",
                body = "Habla con tus amigos y coordina funciones para ver películas juntos.",
            )
            Spacer(modifier = Modifier.height(18.dp))

            if (friends.isEmpty()) {
                EmptyCard("Agrega amigos para iniciar conversaciones.")
            } else {
                friends.forEach { friend ->
                    val lastMessage = messages.lastOrNull { it.friendId == friend.id }
                    ChatPreviewCard(
                        friend = friend,
                        lastMessage = lastMessage,
                        onOpenChat = { onOpenChat(friend.id) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        BottomActions(
            primaryText = "Ver recomendaciones",
            onPrimaryClick = onVerRecomendaciones,
        )
    }
}

@Composable
fun PrivateChatScreen(
    friend: MockSocialUser,
    messages: List<MockChatMessage>,
    onSendMessage: (String) -> Unit,
    onRecommendMovie: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var messageText by remember { mutableStateOf("") }
    val friendMessages = messages.filter { it.friendId == friend.id }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                SocialAvatar(friend)
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Text(
                        text = if (friend.isOnline) "En línea" else "Disponible después",
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulAccion,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            if (friendMessages.isEmpty()) {
                EmptyCard("Todavía no hay mensajes con este amigo.")
            } else {
                friendMessages.forEach { message ->
                    MessageBubble(message)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Surface(
            shadowElevation = 8.dp,
            color = FondoCrema,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                UiInput(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = "Mensaje",
                    placeholder = "Escribe un mensaje",
                )
                Spacer(modifier = Modifier.height(10.dp))
                UiPrimaryButton(
                    text = "Enviar mensaje",
                    onClick = {
                        onSendMessage(messageText.trim())
                        messageText = ""
                    },
                    enabled = messageText.isNotBlank(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = "Recomendar película",
                    onClick = onRecommendMovie,
                )
            }
        }
    }
}

@Composable
fun RecommendMovieScreen(
    friends: List<MockSocialUser>,
    movies: List<MockMovie>,
    onSendRecommendation: (String, String, String) -> Unit,
    onVerRecomendaciones: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFriendId by remember { mutableStateOf(friends.firstOrNull()?.id.orEmpty()) }
    var selectedMovieId by remember { mutableStateOf(movies.firstOrNull()?.id.orEmpty()) }
    var note by remember { mutableStateOf("Creo que esta película te puede gustar.") }
    var sent by remember { mutableStateOf(false) }

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
                eyebrow = "RECOMENDAR",
                title = "Recomendar película",
                body = "Elige un amigo, una película y envía una recomendación.",
            )
            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Amigo")
            if (friends.isEmpty()) {
                EmptyCard("Agrega amigos antes de enviar recomendaciones.")
            } else {
                friends.forEach { friend ->
                    SelectableCard(
                        title = friend.name,
                        body = "Le gusta ${friend.favoriteGenre}",
                        selected = selectedFriendId == friend.id,
                        onClick = {
                            selectedFriendId = friend.id
                            sent = false
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle("Película")
            if (movies.isEmpty()) {
                EmptyCard("No hay películas disponibles para recomendar.")
            } else {
                movies.take(4).forEach { movie ->
                    SelectableCard(
                        title = movie.title,
                        body = "${movie.genre} · ${movie.classification} · ${movie.duration}",
                        selected = selectedMovieId == movie.id,
                        onClick = {
                            selectedMovieId = movie.id
                            sent = false
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            UiInput(
                value = note,
                onValueChange = {
                    note = it
                    sent = false
                },
                label = "Nota",
                placeholder = "Agrega una razón breve",
            )

            if (sent) {
                Spacer(modifier = Modifier.height(12.dp))
                EmptyCard("Recomendación enviada. Ya aparece en tu historial de recomendaciones.")
            }
        }

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
                    text = "Enviar recomendación",
                    onClick = {
                        onSendRecommendation(selectedFriendId, selectedMovieId, note.trim())
                        sent = true
                    },
                    enabled = selectedFriendId.isNotBlank() &&
                        selectedMovieId.isNotBlank() &&
                        note.isNotBlank(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = "Ver recomendaciones",
                    onClick = onVerRecomendaciones,
                )
            }
        }
    }
}

@Composable
fun RecommendationsScreen(
    recommendations: List<MockMovieRecommendation>,
    users: List<MockSocialUser>,
    movies: List<MockMovie>,
    onIrAChats: () -> Unit,
    onRecomendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val received = recommendations.filterNot { it.isMine }
    val sent = recommendations.filter { it.isMine }

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
                eyebrow = "RECOMENDACIONES",
                title = "Películas compartidas",
                body = "Revisa lo que te recomendaron y lo que tú compartiste con amigos.",
            )
            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Recibidas")
            if (received.isEmpty()) {
                EmptyCard("Aún no tienes recomendaciones recibidas.")
            } else {
                received.forEach { recommendation ->
                    RecommendationCard(recommendation, users, movies)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle("Enviadas")
            if (sent.isEmpty()) {
                EmptyCard("Aún no has enviado recomendaciones.")
            } else {
                sent.forEach { recommendation ->
                    RecommendationCard(recommendation, users, movies)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

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
                    text = "Recomendar película",
                    onClick = onRecomendar,
                )
                Spacer(modifier = Modifier.height(8.dp))
                UiGhostButton(
                    text = "Ir a chats",
                    onClick = onIrAChats,
                )
            }
        }
    }
}

@Composable
private fun ChatPreviewCard(
    friend: MockSocialUser,
    lastMessage: MockChatMessage?,
    onOpenChat: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SocialAvatar(friend)
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto,
                    )
                    Text(
                        text = lastMessage?.text ?: "Sin mensajes todavía.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTexto.copy(alpha = 0.65f),
                    )
                }
                Text(
                    text = lastMessage?.time ?: "Nuevo",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulAccion,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            UiPrimaryButton(
                text = "Abrir chat",
                onClick = onOpenChat,
            )
        }
    }
}

@Composable
private fun MessageBubble(message: MockChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.78f),
            shape = MaterialTheme.shapes.large,
            color = if (message.isMine) AzulAccion else Color.White,
            border = if (message.isMine) null
            else androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isMine) Color.White.copy(alpha = 0.78f) else AzulAccion,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isMine) Color.White else GrisTexto,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (message.isMine) Color.White.copy(alpha = 0.7f)
                    else GrisTexto.copy(alpha = 0.52f),
                )
            }
        }
    }
}

@Composable
private fun SelectableCard(
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (selected) VerdeSuave else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) AzulAccion else BordeCard,
        ),
        shadowElevation = 2.dp,
        onClick = onClick,
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
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: MockMovieRecommendation,
    users: List<MockSocialUser>,
    movies: List<MockMovie>,
) {
    val friend = users.find { it.id == recommendation.friendId }
    val movie = movies.find { it.id == recommendation.movieId }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeCard),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = movie?.title ?: "Película",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (recommendation.isMine) {
                    "Enviada a ${friend?.name ?: "un amigo"} · ${recommendation.date}"
                } else {
                    "Recibida de ${friend?.name ?: "un amigo"} · ${recommendation.date}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = AzulAccion,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = recommendation.note,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto.copy(alpha = 0.75f),
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
private fun EmptyCard(text: String) {
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
private fun BottomActions(
    primaryText: String,
    onPrimaryClick: () -> Unit,
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
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EB)
@Composable
private fun ChatListScreenPreview() {
    ProyectoFinalMovilTheme {
        ChatListScreen(
            friends = mockSocialUsers.filter { it.id in mockInitialFriendIds },
            messages = mockChatMessages,
            onOpenChat = {},
            onVerRecomendaciones = {},
        )
    }
}
