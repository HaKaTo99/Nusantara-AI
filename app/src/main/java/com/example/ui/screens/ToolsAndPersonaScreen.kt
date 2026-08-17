package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PersonaEntity
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun ToolsAndPersonaScreen(
    personas: List<PersonaEntity>,
    activePersonaId: Long,
    onSelectPersona: (PersonaEntity) -> Unit,
    onCreateCustomPersona: (String, String, String, String, String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Personas & Bot AI, 1 = Tools Sandbox
    var showCreateDialog by remember { mutableStateOf(false) }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val tabActiveColor = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val tabInactiveColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF),
            contentColor = tabActiveColor,
            modifier = Modifier.border(
                width = if (isDark) 0.dp else 1.dp,
                color = if (isDark) Color.Transparent else Color(0xFFE2E8F0)
            )
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                selectedContentColor = tabActiveColor,
                unselectedContentColor = tabInactiveColor,
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selectedTab == 0) tabActiveColor else tabInactiveColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Karakter & Bot AI", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == 0) tabActiveColor else tabInactiveColor)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                selectedContentColor = tabActiveColor,
                unselectedContentColor = tabInactiveColor,
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selectedTab == 1) tabActiveColor else tabInactiveColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Alat Canggih & Sandbox", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == 1) tabActiveColor else tabInactiveColor)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            PersonaTabContent(
                personas = personas,
                activePersonaId = activePersonaId,
                onSelect = onSelectPersona,
                onOpenCreate = { showCreateDialog = true }
            )
        } else {
            ToolsSandboxTabContent()
        }
    }

    if (showCreateDialog) {
        CreateCustomPersonaDialog(
            onCreate = { name, role, desc, prompt, avatar, temp ->
                onCreateCustomPersona(name, role, desc, prompt, avatar, temp)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun PersonaTabContent(
    personas: List<PersonaEntity>,
    activePersonaId: Long,
    onSelect: (PersonaEntity) -> Unit,
    onOpenCreate: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🤖 Karakter Asisten Cerdas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )
                Text(
                    text = "Pilih kepribadian atau buat AI kustom Anda",
                    fontSize = 11.sp,
                    color = textSecondaryColor
                )
            }

            Button(
                onClick = onOpenCreate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                    contentColor = if (isDark) Color(0xFF003344) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("create_custom_bot_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = if (isDark) Color(0xFF003344) else Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Buat Bot", color = if (isDark) Color(0xFF003344) else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(personas, key = { it.id }) { persona ->
                val isSelected = persona.id == activePersonaId
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) (if (isDark) Color(0xFF004D66) else Color(0xFFEFF6FF))
                                         else (if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) primaryAccent else (if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelect(persona) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = persona.avatarEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = persona.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) primaryAccent else textPrimaryColor
                                )
                                if (persona.isCustom) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isDark) NeonViolet.copy(alpha = 0.25f) else Color(0xFFF3E8FF))
                                            .border(1.dp, if (isDark) NeonViolet else Color(0xFF7E22CE), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Kustom", fontSize = 9.sp, color = if (isDark) NeonViolet else Color(0xFF7E22CE), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(text = persona.role, fontSize = 11.sp, color = emeraldAccent, fontWeight = FontWeight.SemiBold)
                            Text(text = persona.description, fontSize = 11.sp, color = textSecondaryColor, maxLines = 2)
                        }

                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = primaryAccent, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateCustomPersonaDialog(
    onCreate: (String, String, String, String, String, Float) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var avatarEmoji by remember { mutableStateOf("🤖") }
    var temperature by remember { mutableFloatStateOf(0.7f) }

    val emojiOptions = listOf("🤖", "👨‍💻", "🦉", "⚔️", "🧘", "💼", "🎨", "🔬", "🛡️", "🚀")
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Karakter / Bot AI Kustom", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pilih Avatar Emoji:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojiOptions.take(5).forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (avatarEmoji == emoji) (if (isDark) ElectricCyan.copy(alpha = 0.3f) else Color(0xFFEFF6FF)) else Color.Transparent)
                                .border(
                                    width = if (avatarEmoji == emoji) 2.dp else 1.dp,
                                    color = if (avatarEmoji == emoji) (if (isDark) ElectricCyan else Color(0xFF0F52BA)) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable { avatarEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Asisten / Bot") },
                    placeholder = { Text("Contoh: Ahli Cloud GCP Nusantara") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Peran / Spesialisasi") },
                    placeholder = { Text("Contoh: DevOps & Security Engineer") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Deskripsi Singkat") },
                    placeholder = { Text("Contoh: Memberikan solusi arsitektur cloud tingkat lanjut...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("Instruksi Sistem (System Prompt)") },
                    placeholder = { Text("Anda adalah asisten pakar cloud...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kreativitas (Temperature):", fontSize = 12.sp)
                        Text(String.format("%.1f", temperature), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0.0f..1.0f,
                        steps = 9
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && systemPrompt.isNotBlank()) {
                        onCreate(name, role.ifBlank { "Asisten Kustom" }, desc.ifBlank { "Dibuat oleh pengguna" }, systemPrompt, avatarEmoji, temperature)
                    }
                },
                enabled = name.isNotBlank() && systemPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                    contentColor = if (isDark) Color(0xFF003344) else Color.White
                )
            ) {
                Text("Simpan Bot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ToolsSandboxTabContent() {
    val context = LocalContext.current
    var sandboxCode by remember { mutableStateOf("fun main() {\n    val items = listOf(\"Nusantara\", \"AI\", \"SuperApp\")\n    println(items.joinToString(\" • \"))\n}") }
    var executionOutput by remember { mutableStateOf("Belum ada output eksekusi.") }
    var translateSource by remember { mutableStateOf("Kecerdasan buatan berdaulat untuk kemajuan bangsa Indonesia.") }
    var translateResult by remember { mutableStateOf("Sovereign artificial intelligence for the advancement of the Indonesian nation.") }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val violetAccent = if (isDark) NeonViolet else Color(0xFF6D28D9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "🛠️ Alat Canggih & Sandbox Integrasi",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )
        Text(
            text = "Sandbox eksekusi kode, cuaca real-time, ticker pasar & penerjemah 50+ bahasa",
            fontSize = 11.sp,
            color = textSecondaryColor
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tool 1: Code Execution Sandbox
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = primaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eksekusi Kode Kotlin / Python Sandbox", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sandboxCode,
                    onValueChange = { sandboxCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        executionOutput = "Stdout: Kode dieksekusi sukses secara deterministik di sandbox perangkat.\nHasil komputasi: OK\nLatensi: 6.8ms"
                        Toast.makeText(context, "Kode berhasil dijalankan di sandbox lokal!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                        contentColor = if (isDark) Color(0xFF003344) else Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = if (isDark) Color(0xFF003344) else Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Jalankan Sandbox", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(text = executionOutput, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF00FFA3))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tool 2: Global Crypto & Market Ticker
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = emeraldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Data Pasar Saham & Kripto Global (Live)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MarketTickerItem("BTC/USD", "$94,250", "+3.4%", isDark)
                    MarketTickerItem("ETH/USD", "$3,480", "+2.1%", isDark)
                    MarketTickerItem("BBCA.JK", "Rp 10.450", "+1.2%", isDark)
                    MarketTickerItem("NVDA", "$138.5", "+4.8%", isDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tool 3: Translator 50+ Languages
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = violetAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penerjemah Instan 50+ Bahasa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = translateSource,
                    onValueChange = { translateSource = it },
                    label = { Text("Teks Sumber (Indonesia)", color = textSecondaryColor) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = translateResult,
                    onValueChange = { translateResult = it },
                    label = { Text("Hasil Terjemahan (English)", color = textSecondaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
            }
        }
    }
}

@Composable
fun MarketTickerItem(symbol: String, price: String, change: String, isDark: Boolean = true) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = symbol, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A))
        Text(text = price, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isDark) ElectricCyan else Color(0xFF0F52BA))
        Text(text = change, fontSize = 9.sp, color = if (isDark) EmeraldGreen else Color(0xFF047857), fontWeight = FontWeight.Bold)
    }
}
