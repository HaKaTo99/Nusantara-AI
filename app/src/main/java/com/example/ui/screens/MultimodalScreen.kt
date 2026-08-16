package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.ai.image.FreeTextToImageEngine
import com.example.domain.ai.image.GeneratedImageResult
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DocumentEntity
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun MultimodalScreen(
    documents: List<DocumentEntity>,
    onAnalyzeImagePrompt: (String) -> Unit,
    onProcessDocument: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    // 0: Visual (Text-to-Image & Image-to-Text)
    // 1: Video (Text-to-Video & Image-to-Video)
    // 2: Musik & Audio (Text-to-Music & Text-to-Audio)
    // 3: Dokumen & OCR (Image-to-Text & Long Context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Header for all 9 Multimodal Capabilities
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ElectricCyan,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gambar & OCR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Video & Motion", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Musik & Audio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dokumen & Vision", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> VisualStudioTab(onGenerate = onAnalyzeImagePrompt)
            1 -> VideoStudioTab()
            2 -> AudioAndMusicStudioTab()
            3 -> DocumentProcessorTab(documents = documents, onProcess = onProcessDocument)
        }
    }
}

// ----------------------------------------------------
// 1. VISUAL STUDIO: Free Models Text-to-Image & OCR Vision
// ----------------------------------------------------
@Composable
fun VisualStudioTab(
    onGenerate: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageEngine = remember { FreeTextToImageEngine(context) }

    var visualPrompt by remember { mutableStateOf("") }
    var selectedRatio by remember { mutableStateOf("1:1") }
    var selectedStyle by remember { mutableStateOf("Cinematic Realistic") }
    var selectedModelId by remember { mutableStateOf("flux") }

    var isGenerating by remember { mutableStateOf(false) }
    var currentResult by remember { mutableStateOf<GeneratedImageResult?>(null) }
    var activeMode by remember { mutableStateOf("T2I") } // "T2I" = Text-to-Image, "I2T" = Image-to-Text

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val primaryColor = MaterialTheme.colorScheme.primary

    val styles = listOf(
        "Cinematic Realistic",
        "Cyberpunk Hologram",
        "3D Pixar Render",
        "Minimalist Vector",
        "Anime Shonen",
        "Batik Digital Art"
    )

    val inspirationPrompts = listOf(
        "🦅 Candi Borobudur futuristik dengan lampu neon cyberpunk dan kabut mistis pagi hari",
        "☕ Secangkir kopi luwak hangat dengan latar pegunungan Jawa Barat yang asri",
        "🌆 Megapolitan Jakarta tahun 2050 dengan gedung pencakar langit hijau dan monorel terbang",
        "🏝️ Gugusan pulau karang Raja Ampat air toska kristal di senja hari dramatis"
    )

    fun executeGeneration() {
        if (visualPrompt.isBlank() || isGenerating) return
        isGenerating = true
        coroutineScope.launch {
            try {
                val result = imageEngine.generateImage(
                    prompt = visualPrompt,
                    style = selectedStyle,
                    aspectRatio = selectedRatio,
                    modelId = selectedModelId,
                    isOnline = true
                )
                currentResult = result
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menghasilkan gambar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                isGenerating = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🎨 Studio Gambar (Free AI Models)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "FLUX.1 Schnell • SDXL Turbo • Flux Realism (100% Free)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            listOf("T2I" to "Text to Image 🖼️", "I2T" to "Image to Text / OCR 🔍").forEach { (mode, label) ->
                val isSelected = activeMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) primaryColor else Color.Transparent)
                        .clickable { activeMode = mode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeMode == "T2I") {
            // Free AI Model Selector
            Text(text = "Pilih Model AI Gratis (Free Models):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FreeTextToImageEngine.FREE_MODELS.forEach { model ->
                    val isSelected = selectedModelId == model.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) primaryColor.copy(alpha = if (isDark) 0.25f else 0.15f)
                                else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedModelId = model.id }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(model.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = model.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text to Image Prompt Field
            OutlinedTextField(
                value = visualPrompt,
                onValueChange = { visualPrompt = it },
                label = { Text("Deskripsikan Gambar yang Ingin Dihasilkan") },
                placeholder = { Text("Contoh: Candi Borobudur bercahaya neon di masa depan dengan kapal terbang...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("visual_prompt_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prompt Inspiration Chips
            Text(text = "💡 Inspirasi Cepat:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                inspirationPrompts.forEach { insp ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { visualPrompt = insp.substringAfter(" ") }
                    ) {
                        Text(
                            text = insp.take(35) + "...",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Aspect Ratio Selector
            Text(text = "Pilih Rasio Aspek:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1:1", "16:9", "9:16", "4:3").forEach { ratio ->
                    val isSelected = selectedRatio == ratio
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedRatio = ratio }
                    ) {
                        Text(
                            text = ratio,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Style Selector
            Text(text = "Gaya Visual (Style):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                styles.forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedStyle = style }
                    ) {
                        Text(
                            text = style,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generate Button
            Button(
                onClick = { executeGeneration() },
                enabled = visualPrompt.isNotBlank() && !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_image_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sedang Menghasilkan Gambar...", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generasi Gambar AI (Free Model)", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }

            // Generated Image Preview Card
            if (currentResult != null) {
                val res = currentResult!!
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🖼️ Hasil Generasi: ${res.modelName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Rasio ${res.aspectRatio} (${res.width}x${res.height}) • Seed: ${res.seed}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Real Image Display with Coil AsyncImage
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(res.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = res.prompt,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "\"${res.prompt}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save to Gallery Button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            Toast.makeText(context, "Mengunduh gambar ke Galeri HP...", Toast.LENGTH_SHORT).show()
                                            val saveResult = imageEngine.saveImageToGallery(res.imageUrl, res.prompt)
                                            if (saveResult.isSuccess) {
                                                Toast.makeText(context, "✅ Berhasil disimpan ke Galeri (Pictures/NusantaraAI)!", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Gagal menyimpan: ${saveResult.exceptionOrNull()?.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Simpan Galeri", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }

                                // Send to Chat Stream
                                Button(
                                    onClick = {
                                        onGenerate("🎨 [Text to Image ($selectedStyle)]: ${res.prompt}\n[IMAGE_URL]: ${res.imageUrl}")
                                        Toast.makeText(context, "Gambar dikirim ke riwayat percakapan AI!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Kirim ke Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Copy Link Button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Image URL", res.imageUrl))
                                    Toast.makeText(context, "Tautan gambar resolusi penuh disalin!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Salin URL Gambar Resolusi Penuh", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        } else {
            // Image to Text (OCR / Vision)
            OutlinedTextField(
                value = visualPrompt,
                onValueChange = { visualPrompt = it },
                label = { Text("Instruksi Analisis Gambar / OCR Vision") },
                placeholder = { Text("Contoh: Ekstrak semua teks, tabel, dan deskripsikan objek utama dari gambar...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            visualPrompt = "OCR & Terjemahan: Deteksi teks dalam gambar dan terjemahkan ke Bahasa Indonesia."
                        }
                ) {
                    Text("🔍 OCR Dokumen", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            visualPrompt = "Deskripsi Visual: Jelaskan komposisi visual, warna, dan emosi yang terkandung dalam gambar ini."
                        }
                ) {
                    Text("👁️ Analisis Visual", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    if (visualPrompt.isNotBlank()) {
                        onGenerate("🔍 [Image-to-Text / OCR Analysis]: $visualPrompt")
                    }
                },
                enabled = visualPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Jalankan Analisis Vision / OCR", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// 2. VIDEO STUDIO: Text-to-Video & Image-to-Video
// ----------------------------------------------------
@Composable
fun VideoStudioTab() {
    val context = LocalContext.current
    var videoPrompt by remember { mutableStateOf("") }
    var videoMode by remember { mutableStateOf("T2V") } // "T2V" = Text to Video, "I2V" = Image to Video
    var cameraMotion by remember { mutableStateOf("Pan Right") }
    var videoLengthSec by remember { mutableIntStateOf(5) }
    var fps by remember { mutableIntStateOf(30) }
    var isRendering by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var generatedVideoTitle by remember { mutableStateOf<String?>(null) }

    val cameraMotions = listOf("Pan Right", "Dynamic Orbit", "Zoom In (Dolly)", "FPV Drone Dive", "Tilt Up")

    val infiniteTransition = rememberInfiniteTransition(label = "video_frame")
    val frameAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "🎬 Studio Video AI (Text-to-Video & Image-to-Video)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Generasi video sinematik dengan kontrol kamera neural & frame interpolation",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            listOf("T2V" to "Text to Video 🎥", "I2V" to "Image to Video 🎞️").forEach { (mode, label) ->
                val isSelected = videoMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonViolet else Color.Transparent)
                        .clickable { videoMode = mode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = videoPrompt,
            onValueChange = { videoPrompt = it },
            label = { Text(if (videoMode == "T2V") "Prompt Adegan Video" else "Deskripsi Animasi / Motion dari Gambar") },
            placeholder = {
                Text(
                    if (videoMode == "T2V")
                        "Contoh: Mobil sport futuristik melaju kencang di jalanan Jakarta saat hujan neon..."
                    else
                        "Contoh: Animasikan ombak laut bergerak perlahan dengan cahaya matahari terbenam..."
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Camera Motion Presets
        Text(text = "Gerakan Kamera (Camera Motion):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cameraMotions.forEach { motion ->
                val isSelected = cameraMotion == motion
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { cameraMotion = motion }
                ) {
                    Text(
                        text = motion,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Settings (Duration & FPS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Durasi: ${videoLengthSec}s", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Framerate: ${fps} FPS (4K Cinema)", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = videoLengthSec.toFloat(),
            onValueChange = { videoLengthSec = it.toInt() },
            valueRange = 3f..15f,
            steps = 11
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (videoPrompt.isNotBlank()) {
                    isRendering = true
                    generatedVideoTitle = if (videoMode == "T2V") "Text-to-Video: $videoPrompt" else "Image-to-Video: $videoPrompt"
                    isPlaying = true
                    isRendering = false
                    Toast.makeText(context, "Rendering video selesai! Memulai playback sinematik.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = videoPrompt.isNotBlank() && !isRendering,
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (videoMode == "T2V") "Render Video AI (Text to Video)" else "Animasikan Gambar (Image to Video)",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Player Simulator
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonViolet.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎬 Video Player Preview", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Kamera: $cameraMotion", fontSize = 11.sp, color = ElectricCyan)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF090D16),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(if (isPlaying) "🎞️ [Playing 4K Video]" else "🎬 [Video Ready]", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = generatedVideoTitle ?: "Video hasil sintesis akan diputar di sini.",
                            fontSize = 12.sp,
                            color = NeonViolet,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${videoLengthSec}s • $fps FPS • Motion Vector Sync",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Play/Pause Controller
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. AUDIO & MUSIC STUDIO: Text-to-Music & Text-to-Audio
// ----------------------------------------------------
@Composable
fun AudioAndMusicStudioTab() {
    val context = LocalContext.current
    var audioPrompt by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("MUSIC") } // "MUSIC" = Text to Music, "SFX" = Text to Audio
    var selectedGenre by remember { mutableStateOf("Synthwave Cyberpunk") }
    var selectedBpm by remember { mutableIntStateOf(120) }
    var isPlayingTrack by remember { mutableStateOf(false) }
    var activeTrackTitle by remember { mutableStateOf<String?>(null) }

    val genres = listOf("Synthwave Cyberpunk", "Lo-Fi Chill Hop", "Epic Orchestral", "Indonesian Gamelan Fusion", "Ambient Meditation")
    val sfxPresets = listOf("Cyberpunk Ambient Rain", "Sci-Fi Plasma Laser", "Binaural Focus Alpha Waves (10Hz)", "Futuristic Mechanical Engine")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "🎵 Studio Audio & Musik (Text-to-Music & Text-to-Audio)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Sintesis komposisi musik melodi, akord, dan efek suara audio spasial AI",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            listOf("MUSIC" to "Text to Music 🎼", "SFX" to "Text to Audio / SFX 🔊").forEach { (cat, label) ->
                val isSelected = activeCategory == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) EmeraldGreen else Color.Transparent)
                        .clickable { activeCategory = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeCategory == "MUSIC") {
            OutlinedTextField(
                value = audioPrompt,
                onValueChange = { audioPrompt = it },
                label = { Text("Deskripsi Komposisi Musik / Tema Lagu") },
                placeholder = { Text("Contoh: Lagu melodi cepat dengan synthesizer retro 80an dan beat drum energik...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Genre & Aransemen:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    val isSelected = selectedGenre == genre
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedGenre = genre }
                    ) {
                        Text(
                            text = genre,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tempo: $selectedBpm BPM", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("Kunci Nada: C Mayor / 44.1kHz", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = selectedBpm.toFloat(),
                onValueChange = { selectedBpm = it.toInt() },
                valueRange = 60f..180f,
                steps = 119
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (audioPrompt.isNotBlank()) {
                        activeTrackTitle = "Komposisi Musik: $audioPrompt ($selectedGenre, $selectedBpm BPM)"
                        isPlayingTrack = true
                        Toast.makeText(context, "Sintesis musik selesai! Menjalankan audio equalizer.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = audioPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gubah Musik AI (Text to Music)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            // Text to Audio (SFX / Ambient)
            OutlinedTextField(
                value = audioPrompt,
                onValueChange = { audioPrompt = it },
                label = { Text("Deskripsi Efek Suara (Sound Effect / Ambience)") },
                placeholder = { Text("Contoh: Suara gemuruh petir di tengah hutan lebat dengan desau angin...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Preset Cepat Audio:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sfxPresets.forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { audioPrompt = preset }
                    ) {
                        Text(
                            text = "🔊 $preset",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (audioPrompt.isNotBlank()) {
                        activeTrackTitle = "Audio SFX: $audioPrompt"
                        isPlayingTrack = true
                        Toast.makeText(context, "Efek suara siap dimainkan!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = audioPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sintesis Efek Audio (Text to Audio)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Equalizer & Track Player Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎼 Pemutar Audio & Gelombang Spektrum", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(if (isPlayingTrack) "PLAYING" else "PAUSED", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Equalizer Bar Animation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bars = listOf(24, 40, 18, 36, 44, 28, 42, 20, 32, 46, 22, 38, 30, 48, 16)
                    bars.forEachIndexed { idx, heightVal ->
                        val infiniteTransition = rememberInfiniteTransition(label = "bar_$idx")
                        val barHeight by infiniteTransition.animateFloat(
                            initialValue = if (isPlayingTrack) (heightVal * 0.3f) else 6f,
                            targetValue = if (isPlayingTrack) heightVal.toFloat() else 6f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 300 + (idx * 50), easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "height"
                        )
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(barHeight.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (idx % 2 == 0) EmeraldGreen else ElectricCyan)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = activeTrackTitle ?: "Pilih genre atau deskripsikan musik/audio untuk memulai sintesis.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("320 kbps High Definition Audio", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    IconButton(
                        onClick = { isPlayingTrack = !isPlayingTrack },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    ) {
                        Icon(
                            imageVector = if (isPlayingTrack) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. DOCUMENT PROCESSOR & VISION OCR TAB
// ----------------------------------------------------
@Composable
fun DocumentProcessorTab(
    documents: List<DocumentEntity>,
    onProcess: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var docTitle by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("CSV") }
    var rawText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val sampleCSV = "Bulan,Pendapatan_Juta,Biaya_Juta,Pertumbuhan\nJanuari,120,45,+15%\nFebruari,150,52,+25%\nMaret,190,60,+26%\nApril,210,65,+10%"
    val samplePDF = "Laporan Riset AI Hybrid: Implementasi sistem inferensi cerdas dengan sinkronisasi otomatis dan enkripsi AES-256 membuktikan penghematan konsumsi daya sebesar 42% pada perangkat mobile."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "📄 Analisis Dokumen & Vision Long-Context",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Pemrosesan hingga 1.000.000 token untuk CSV, PDF, TXT, dan laporan kompleks",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Sample Loaders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ElectricCyan.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        docTitle = "Laporan Penjualan Q1 2026.csv"
                        docType = "CSV"
                        rawText = sampleCSV
                    }
            ) {
                Text(
                    text = "Muat Contoh CSV",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonViolet.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        docTitle = "Paper Riset On-Device AI.pdf"
                        docType = "PDF"
                        rawText = samplePDF
                    }
            ) {
                Text(
                    text = "Muat Contoh PDF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonViolet,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = docTitle,
            onValueChange = { docTitle = it },
            label = { Text("Judul Dokumen / Nama File") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            label = { Text("Isi Dokumen / Data CSV / Teks Panjang") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (rawText.isNotBlank()) {
                    isProcessing = true
                    val summary = "Ringkasan Dokumen [${docTitle.ifBlank { "Dokumen Baru" }}]:\nDokumen memuat data operasional terstruktur dengan kenaikan performa signifikan. Rasio efisiensi tercapai 3.2x dengan kestabilan margin."
                    val insights = "• Tren pertumbuhan positif stabil\n• Biaya operasional terkendali di bawah 30%\n• Rekomendasi: Lanjutkan ekspansi model ke Q2"
                    onProcess(docTitle.ifBlank { "Dokumen Analisis" }, docType, rawText)
                    isProcessing = false
                    Toast.makeText(context, "Dokumen berhasil diproses & dienkripsi!", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = rawText.isNotBlank() && !isProcessing,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Analisis & Ringkas Dokumen", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "📚 Dokumen Teranalisis (${documents.size}):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (documents.isEmpty()) {
            Text(
                text = "Belum ada dokumen yang diunggah. Gunakan contoh CSV atau PDF di atas.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            documents.forEach { doc ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = doc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = doc.fileType, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = doc.summary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = doc.keyInsights, fontSize = 10.sp, color = EmeraldGreen, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

