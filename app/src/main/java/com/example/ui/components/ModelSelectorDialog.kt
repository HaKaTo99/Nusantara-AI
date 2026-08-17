package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ai.DiscoveredLocalModel
import com.example.domain.ai.LocalModelScanner
import com.example.domain.ai.hub.HubModelItem
import com.example.domain.ai.hub.ModelDownloadStatus
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AIModelOption(
    val name: String,
    val provider: String,
    val type: String, // "CLOUD", "LOCAL", "REASONING"
    val description: String,
    val contextWindow: String,
    val speed: String
)

@Composable
fun ModelSelectorDialog(
    currentModel: String,
    currentMode: String, // "ONLINE", "OFFLINE", "HYBRID"
    hubModels: List<HubModelItem> = emptyList(),
    onModelSelected: (String, String) -> Unit,
    onDownloadModel: ((String) -> Unit)? = null,
    onCancelDownload: ((String) -> Unit)? = null,
    onDeleteModel: ((String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scanner = remember { LocalModelScanner(context) }

    var selectedModel by remember { mutableStateOf(currentModel) }
    var selectedMode by remember { mutableStateOf(currentMode) }
    var temperature by remember { mutableFloatStateOf(0.7f) }

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Model Utama, 1 = Scan File HP, 2 = Model Hub (GGUF)
    var isScanning by remember { mutableStateOf(false) }
    var localModelsList by remember { mutableStateOf<List<DiscoveredLocalModel>>(emptyList()) }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val violetAccent = if (isDark) NeonViolet else Color(0xFF6D28D9)
    val cardBg = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)
    val tabContainerBg = if (isDark) Color(0xFF182235) else Color(0xFFF1F5F9)

    // Initial scan on load
    LaunchedEffect(Unit) {
        localModelsList = scanner.scanDeviceStorage()
    }

    val builtInModels = listOf(
        AIModelOption(
            name = "Gemini 3.5 Flash",
            provider = "Google Private Cloud",
            type = "CLOUD",
            description = "Kecepatan ultra-tinggi, penalaran multimodal & web grounding terkini.",
            contextWindow = "1M Tokens",
            speed = "⚡ < 0.8s"
        ),
        AIModelOption(
            name = "Gemini 3.1 Pro",
            provider = "Google DeepMind",
            type = "REASONING",
            description = "Mode penalaran mendalam langkah-demi-langkah untuk STEM & coding kompleks.",
            contextWindow = "2M Tokens",
            speed = "🧠 1.5s"
        ),
        AIModelOption(
            name = "Qwen 2.5 (72B)",
            provider = "Nusantara Cloud Node",
            type = "CLOUD",
            description = "Kekuatan analisis dokumen panjang, penulisan kreatif & multibahasa akurat.",
            contextWindow = "128K Tokens",
            speed = "⚡ 1.1s"
        ),
        AIModelOption(
            name = "Gemma 2 (9B Local)",
            provider = "On-Device Neural Core",
            type = "LOCAL",
            description = "Model lokal 100% offline, hemat daya baterai, privasi tanpa internet.",
            contextWindow = "8K Tokens",
            speed = "🚀 < 0.3s"
        ),
        AIModelOption(
            name = "Llama 3.2 (3B On-Device)",
            provider = "On-Device GGUF Matrix",
            type = "LOCAL",
            description = "Sangat ringan, respons instan untuk chat & terjemahan di pesawat.",
            contextWindow = "4K Tokens",
            speed = "⚡ < 0.2s"
        ),
        AIModelOption(
            name = "Garuda AI (3.2B Sovereign)",
            provider = "Sovereign Foundation Model",
            type = "LOCAL",
            description = "Model fondasi nasional Indonesia untuk tata kelola pemerintahan, hukum & budaya formal.",
            contextWindow = "8K Tokens",
            speed = "🚀 < 0.3s"
        ),
        AIModelOption(
            name = "DeepSeek R1 (Reasoning)",
            provider = "Autonomous Reasoner",
            type = "REASONING",
            description = "Chain-of-thought transparan dengan bukti logika matematika & algoritma.",
            contextWindow = "64K Tokens",
            speed = "🧠 1.8s"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Manajer Model & Mesin AI",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textPrimaryColor
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tab Selection: Model Preset vs Scan HP vs Model Hub
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = tabContainerBg,
                    contentColor = primaryAccent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Text(
                                text = "Model Utama",
                                fontSize = 11.sp,
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 0) primaryAccent else textSecondaryColor
                            )
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 1) emeraldAccent else textSecondaryColor)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Scan (${localModelsList.size})",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeTab == 1) emeraldAccent else textSecondaryColor
                                )
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (activeTab == 2) primaryAccent else textSecondaryColor)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Hub GGUF",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeTab == 2) primaryAccent else textSecondaryColor
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (activeTab == 0) {
                    // TAB 0: MODE EKSEKUSI & MODEL PRESET
                    Text(
                        text = "Pilih Mode Eksekusi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val modes = listOf("HYBRID", "ONLINE", "OFFLINE")
                    val modeLabels = listOf("Hibrida Otomatis", "Cloud Saja", "Offline Lokal")
                    val selectedTabIndex = modes.indexOf(selectedMode).coerceAtLeast(0)

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = tabContainerBg,
                        contentColor = primaryAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                    ) {
                        modes.forEachIndexed { index, modeKey ->
                            Tab(
                                selected = selectedMode == modeKey,
                                onClick = { selectedMode = modeKey },
                                text = {
                                    Text(
                                        text = modeLabels[index],
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedMode == modeKey) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedMode == modeKey) primaryAccent else textSecondaryColor
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Daftar Model AI Terintegrasi:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimaryColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    builtInModels.forEach { model ->
                        val isSelected = selectedModel == model.name
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) (if (isDark) Color(0xFF004D66) else Color(0xFFEFF6FF)) else cardBg,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) primaryAccent else cardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedModel = model.name
                                    if (model.type == "LOCAL") selectedMode = "OFFLINE"
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = model.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) primaryAccent else textPrimaryColor
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val typeColor = when (model.type) {
                                            "CLOUD" -> primaryAccent
                                            "LOCAL" -> emeraldAccent
                                            else -> violetAccent
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(typeColor.copy(alpha = 0.15f))
                                                .border(1.dp, typeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = model.type,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = typeColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = model.description,
                                        fontSize = 11.sp,
                                        color = textSecondaryColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Konteks: ${model.contextWindow}",
                                            fontSize = 10.sp,
                                            color = textSecondaryColor
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = model.speed,
                                            fontSize = 10.sp,
                                            color = emeraldAccent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = primaryAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                } else if (activeTab == 1) {
                    // TAB 1: SCAN STORAGE LOKAL HP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Model Ditemukan di Storage:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimaryColor
                        )

                        IconButton(
                            onClick = {
                                isScanning = true
                                coroutineScope.launch {
                                    delay(500)
                                    localModelsList = scanner.scanDeviceStorage()
                                    isScanning = false
                                    Toast.makeText(context, "Selesai memindai storage perangkat", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = emeraldAccent)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Pindai Ulang", tint = emeraldAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (localModelsList.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = textSecondaryColor, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tidak ada file model AI (.gguf, .tflite, .bin) di folder Download atau Dokumen.",
                                    fontSize = 11.sp,
                                    color = textSecondaryColor,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        localModelsList.forEach { modelFile ->
                            val isSelected = selectedModel == modelFile.name
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) (if (isDark) Color(0xFF004D66) else Color(0xFFEFF6FF)) else cardBg,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) emeraldAccent else cardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedModel = modelFile.name
                                        selectedMode = "OFFLINE"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = modelFile.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) emeraldAccent else textPrimaryColor
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(emeraldAccent.copy(alpha = 0.2f))
                                                    .border(1.dp, emeraldAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = modelFile.format,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = emeraldAccent
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Ukuran: ${modelFile.formattedSize} • Kuantisasi: ${modelFile.quantization}",
                                            fontSize = 11.sp,
                                            color = textSecondaryColor
                                        )
                                        Text(
                                            text = "📍 ${modelFile.filePath}",
                                            fontSize = 9.sp,
                                            color = textSecondaryColor.copy(alpha = 0.8f),
                                            maxLines = 1
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = emeraldAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 2: MODEL HUB (UNDUH MODEL GGUF DARI HUGGINGFACE)
                    Text(
                        text = "Katalog Model GGUF Resmi (HuggingFace Hub):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    hubModels.forEach { hubItem ->
                        val isSelected = selectedModel == hubItem.name
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) (if (isDark) Color(0xFF004D66) else Color(0xFFEFF6FF)) else cardBg,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) primaryAccent else cardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = hubItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) primaryAccent else textPrimaryColor
                                        )
                                        Text(
                                            text = hubItem.repository,
                                            fontSize = 10.sp,
                                            color = textSecondaryColor
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(primaryAccent.copy(alpha = 0.15f))
                                            .border(1.dp, primaryAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${hubItem.quantization} • ${hubItem.formattedSize}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = hubItem.description,
                                    fontSize = 11.sp,
                                    color = textSecondaryColor
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ Rekomendasi: ${hubItem.recommendedRam}",
                                        fontSize = 10.sp,
                                        color = emeraldAccent,
                                        fontWeight = FontWeight.Medium
                                    )

                                    when (hubItem.downloadStatus) {
                                        ModelDownloadStatus.NOT_DOWNLOADED -> {
                                            Button(
                                                onClick = { onDownloadModel?.invoke(hubItem.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                                                    contentColor = if (isDark) Color(0xFF003344) else Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = if (isDark) Color(0xFF003344) else Color.White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Unduh", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        ModelDownloadStatus.DOWNLOADING -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = primaryAccent)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("%.0f%% (%.1f MB/s)".format(hubItem.downloadProgressPercent, hubItem.downloadSpeedMBs), fontSize = 10.sp, color = primaryAccent)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                TextButton(
                                                    onClick = { onCancelDownload?.invoke(hubItem.id) },
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                                    modifier = Modifier.height(24.dp)
                                                ) {
                                                    Text("Batal", fontSize = 10.sp, color = Color.Red)
                                                }
                                            }
                                        }
                                        ModelDownloadStatus.VERIFYING -> {
                                            Text("Memverifikasi SHA-256...", fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                                        }
                                        ModelDownloadStatus.READY, ModelDownloadStatus.ACTIVE -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Button(
                                                    onClick = {
                                                        selectedModel = hubItem.name
                                                        selectedMode = "OFFLINE"
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isSelected) emeraldAccent else (if (isDark) Color(0xFF004D66) else Color(0xFFEFF6FF)),
                                                        contentColor = if (isSelected) Color.White else primaryAccent
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(28.dp)
                                                ) {
                                                    Text(
                                                        text = if (isSelected) "Sedang Aktif" else "Pilih Model",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { onDeleteModel?.invoke(hubItem.id) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = textSecondaryColor, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                        ModelDownloadStatus.ERROR -> {
                                            TextButton(
                                                onClick = { onDownloadModel?.invoke(hubItem.id) },
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Coba Lagi", fontSize = 10.sp, color = Color.Red)
                                            }
                                        }
                                    }
                                }

                                if (hubItem.downloadStatus == ModelDownloadStatus.DOWNLOADING) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (hubItem.downloadProgressPercent / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = primaryAccent,
                                        trackColor = if (isDark) Color.DarkGray.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                // Temperature Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Kreativitas (Temperature):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textPrimaryColor)
                    Text(text = "%.1f".format(temperature), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryAccent)
                }
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = primaryAccent,
                        activeTrackColor = primaryAccent,
                        inactiveTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onModelSelected(selectedModel, selectedMode)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                    contentColor = if (isDark) Color(0xFF003344) else Color.White
                ),
                modifier = Modifier.testTag("confirm_model_button")
            ) {
                Text(text = "Gunakan Model Ini", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Batal", color = textSecondaryColor)
            }
        }
    )
}
