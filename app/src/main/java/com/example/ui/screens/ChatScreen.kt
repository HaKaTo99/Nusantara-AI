package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.PersonaEntity
import com.example.domain.ai.OfflineReasoningEngine
import com.example.ui.components.ChainOfThoughtView
import com.example.ui.components.CodeArtifactView
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.VoiceWaveVisualizer

@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    activePersona: PersonaEntity?,
    isProcessing: Boolean,
    isListening: Boolean,
    voiceAmplitude: Float,
    onSendMessage: (String) -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onSpeakText: (String) -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val starterPrompts = listOf(
        "Berapa 10 kali 10?" to "🧮 Hitung Matematika",
        "Buatkan fungsi algoritma Kotlin" to "💻 Kode Pemrograman",
        "Apa ibu kota Indonesia saat ini?" to "🏛️ Pengetahuan Umum",
        "Buatkan UI Website Modern (HTML)" to "🌐 Desain UI Interaktif"
    )

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Message Canvas / Empty State
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(primaryColor, MaterialTheme.colorScheme.secondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "N",
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Apa yang bisa saya bantu hari ini?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tanyakan matematika, sains, koding, atau analisis data.",
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 4 Starter Cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        starterPrompts.chunked(2).forEach { rowPrompts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPrompts.forEach { (prompt, label) ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { onSendMessage(prompt) }
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = prompt,
                                                fontSize = 12.sp,
                                                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(messages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        onSpeak = { onSpeakText(message.content) }
                    )
                }

                if (isProcessing) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sedang berpikir...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        // Voice wave visualizer when listening
        AnimatedVisibility(visible = isListening) {
            VoiceWaveVisualizer(
                amplitude = voiceAmplitude,
                isActive = isListening
            )
        }

        // Clean, Minimalist High-Contrast Input Bar
        Surface(
            color = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF),
            tonalElevation = if (isDark) 4.dp else 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isDark) 0.dp else 1.dp,
                    color = if (isDark) Color.Transparent else Color(0xFFE2E8F0)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // New Chat Action icon
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .testTag("new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Obrolan Baru",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Text Input Pill (High Contrast)
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isListening) "Mendengarkan..." else "Tanya apa saja...",
                            fontSize = 14.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                        unfocusedTextColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1),
                        focusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else Color(0xFFF8FAFC)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Voice Mic Button
                IconButton(
                    onClick = {
                        if (isListening) onStopVoice() else onStartVoice()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) Color(0xFFFF5252).copy(alpha = 0.2f)
                            else if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else Color(0xFFF1F5F9)
                        )
                        .testTag("voice_input_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isListening) Color(0xFFFF5252) else primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isProcessing,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank()) primaryColor
                            else if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else Color(0xFFE2E8F0)
                        )
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "USER"
    val reasoningSteps = OfflineReasoningEngine.jsonToReasoningSteps(message.reasoningStepsJson)

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val primaryColor = MaterialTheme.colorScheme.primary

    // High Contrast Bubble Colors
    val bubbleColor = if (isUser) {
        if (isDark) Color(0xFF1E293B) else Color(0xFF2563EB) // Deep Sapphire Blue in Light Mode
    } else {
        if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF) // Pure White Card in Light Mode
    }

    val bubbleTextColor = if (isUser) {
        Color.White // Pure White text on blue/dark user bubble
    } else {
        if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A) // Ultra-crisp Slate 900 on AI white card
    }

    val bubbleBorderColor = if (isUser) {
        if (isDark) Color(0xFF334155) else Color(0xFF1D4ED8)
    } else {
        if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1) // Crisp light border
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(primaryColor, MaterialTheme.colorScheme.secondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(vertical = 2.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = bubbleColor,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = bubbleBorderColor,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Segmented Rendering: Pisahkan Teks Penjelasan & Blok Source Code
                    val segments = remember(message.content) {
                        parseMessageSegments(message.content)
                    }

                    segments.forEach { segment ->
                        when (segment) {
                            is MessageSegment.Text -> {
                                val formattedContent = remember(segment.content, bubbleTextColor, isDark) {
                                    parseMarkdownToAnnotatedString(
                                        rawText = segment.content,
                                        baseColor = bubbleTextColor,
                                        isDark = isDark
                                    )
                                }

                                Text(
                                    text = formattedContent,
                                    fontSize = 14.sp,
                                    fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal,
                                    lineHeight = 21.sp,
                                    color = bubbleTextColor,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            is MessageSegment.Code -> {
                                CodeArtifactView(
                                    code = segment.code,
                                    language = segment.language
                                )
                            }
                        }
                    }

                    // Generated Image Preview if present
                    val extractedImageUrl = remember(message.content) {
                        if (message.content.contains("https://image.pollinations.ai/")) {
                            "https://" + message.content.substringAfter("https://").substringBefore(" ").substringBefore("\n")
                        } else if (message.content.contains("[IMAGE_URL]:")) {
                            message.content.substringAfter("[IMAGE_URL]:").trim().substringBefore("\n")
                        } else null
                    }

                    if (!extractedImageUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0xFF182235) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(extractedImageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Generated AI Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Chain of Thought if present
                    if (reasoningSteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        ChainOfThoughtView(
                            steps = reasoningSteps,
                            latencyMs = message.latencyMs
                        )
                    }
                }
            }

            // Message metadata row
            if (!isUser) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val confidenceScore = remember(message.content, message.isOfflineGenerated, message.latencyMs) {
                        OfflineReasoningEngine.detectConfidence(
                            responseText = message.content,
                            isOnline = !message.isOfflineGenerated,
                            latencyMs = message.latencyMs
                        )
                    }
                    ConfidenceBadge(score = confidenceScore)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${message.modelUsed} • ${message.latencyMs}ms",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Dengarkan Suara",
                            tint = primaryColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Parser Markdown cerdas dan berkinerja tinggi untuk Jetpack Compose.
 * Mengonversi format Markdown (### Header, **Bold**, *Italic*, `Code`) menjadi AnnotatedString
 * tanpa menampilkan simbol mentah (* / # / `) ke pengguna.
 */
fun parseMarkdownToAnnotatedString(
    rawText: String,
    baseColor: Color,
    isDark: Boolean
): AnnotatedString {
    return buildAnnotatedString {
        val lines = rawText.split("\n")
        lines.forEachIndexed { lineIdx, line ->
            val trimmedLine = line.trimEnd()
            
            // Deteksi Header Markdown
            val isH1 = trimmedLine.startsWith("# ")
            val isH2 = trimmedLine.startsWith("## ")
            val isH3 = trimmedLine.startsWith("### ")
            
            // Deteksi Bullet List (- item atau * item)
            val isBullet = (trimmedLine.startsWith("- ") || (trimmedLine.startsWith("* ") && !trimmedLine.startsWith("** ")))

            val cleanLine = when {
                isH1 -> trimmedLine.removePrefix("# ").trimStart()
                isH2 -> trimmedLine.removePrefix("## ").trimStart()
                isH3 -> trimmedLine.removePrefix("### ").trimStart()
                isBullet -> "• " + trimmedLine.substring(2).trimStart()
                else -> trimmedLine
            }
            
            val startLinePos = length
            
            // Regex untuk parsing inline tokens: ***bold italic***, **bold**, *italic*, `inline code`
            var cursor = 0
            val regex = Regex("""(\*\*\*(.+?)\*\*\*|\*\*(.+?)\*\*|\*(.+?)\*|`([^`]+)`)""")
            val matches = regex.findAll(cleanLine)
            
            for (match in matches) {
                if (match.range.first > cursor) {
                    append(cleanLine.substring(cursor, match.range.first))
                }
                
                val fullMatch = match.value
                when {
                    fullMatch.startsWith("***") && fullMatch.endsWith("***") && fullMatch.length > 6 -> {
                        val inner = fullMatch.substring(3, fullMatch.length - 3)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                            append(inner)
                        }
                    }
                    fullMatch.startsWith("**") && fullMatch.endsWith("**") && fullMatch.length > 4 -> {
                        val inner = fullMatch.substring(2, fullMatch.length - 2)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(inner)
                        }
                    }
                    fullMatch.startsWith("*") && fullMatch.endsWith("*") && fullMatch.length > 2 -> {
                        val inner = fullMatch.substring(1, fullMatch.length - 1)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(inner)
                        }
                    }
                    fullMatch.startsWith("`") && fullMatch.endsWith("`") && fullMatch.length > 2 -> {
                        val inner = fullMatch.substring(1, fullMatch.length - 1)
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                color = if (isDark) Color(0xFF00F2FE) else Color(0xFF0F52BA),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(" $inner ")
                        }
                    }
                    else -> append(fullMatch)
                }
                cursor = match.range.last + 1
            }
            
            if (cursor < cleanLine.length) {
                append(cleanLine.substring(cursor))
            }
            
            val endLinePos = length
            
            // Terapkan styling Header jika terdeteksi
            if (isH1 || isH2 || isH3) {
                val headerFontSize = if (isH1) 16.sp else if (isH2) 15.sp else 14.5.sp
                addStyle(
                    SpanStyle(
                        fontSize = headerFontSize,
                        fontWeight = FontWeight.Bold,
                        color = baseColor
                    ),
                    startLinePos,
                    endLinePos
                )
            }
            
            if (lineIdx < lines.size - 1) {
                append("\n")
            }
        }
    }
}

/**
 * Representasi blok segmen pesan (Teks Penjelasan vs Source Code IDE Artifact).
 */
sealed class MessageSegment {
    data class Text(val content: String) : MessageSegment()
    data class Code(val code: String, val language: String) : MessageSegment()
}

/**
 * Memecah konten balasan AI menjadi urutan terstruktur antara blok teks dan blok kode.
 */
fun parseMessageSegments(rawText: String): List<MessageSegment> {
    if (!rawText.contains("```")) {
        return listOf(MessageSegment.Text(rawText))
    }

    val segments = mutableListOf<MessageSegment>()
    val codeFenceRegex = Regex("""```([a-zA-Z0-9_+#.-]*)\r?\n([\s\S]*?)```""")
    
    var cursor = 0
    val matches = codeFenceRegex.findAll(rawText)
    
    for (match in matches) {
        val startIdx = match.range.first
        if (startIdx > cursor) {
            val textBefore = rawText.substring(cursor, startIdx).trim()
            if (textBefore.isNotEmpty()) {
                segments.add(MessageSegment.Text(textBefore))
            }
        }
        
        val lang = match.groupValues[1].trim().ifBlank { "code" }
        val codeBody = match.groupValues[2].trimEnd()
        segments.add(MessageSegment.Code(code = codeBody, language = lang))
        
        cursor = match.range.last + 1
    }
    
    if (cursor < rawText.length) {
        val remainingText = rawText.substring(cursor).trim()
        if (remainingText.isNotEmpty()) {
            segments.add(MessageSegment.Text(remainingText))
        }
    }
    
    return if (segments.isNotEmpty()) segments else listOf(MessageSegment.Text(rawText))
}
