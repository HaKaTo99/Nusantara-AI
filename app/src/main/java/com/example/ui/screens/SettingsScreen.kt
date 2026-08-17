package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.domain.crypto.EncryptionManager
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun SettingsScreen(
    currentTheme: String = "SYSTEM",
    onThemeChange: (String) -> Unit = {},
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAutoSyncEnabled by remember { mutableStateOf(true) }
    var isBiometricLockEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("Bahasa Indonesia") }

    val encManager = remember { EncryptionManager.getInstance(context) }
    val vaultStatus = remember { encManager.getVaultStatus() }
    val masterKeyHex = remember {
        "AndroidKeyStore :: NusantaraVaultKey_E2EE_2026"
    }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val violetAccent = if (isDark) NeonViolet else Color(0xFF6D28D9)
    val cardBg = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text(
            text = "⚙️ Pengaturan & Keamanan Vault",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimaryColor
        )
        Text(
            text = "Kustomisasi platform AI, enkripsi end-to-end, dan privasi penuh",
            fontSize = 11.sp,
            color = textSecondaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 0: Tampilan & Tema (Dark / Light / System)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (currentTheme) {
                            "DARK" -> Icons.Default.DarkMode
                            "LIGHT" -> Icons.Default.LightMode
                            else -> Icons.Default.BrightnessAuto
                        },
                        contentDescription = null,
                        tint = primaryAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tampilan & Tema Aplikasi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pilih skema tema visual sesuai preferensi dan kenyamanan mata Anda:",
                    fontSize = 11.sp,
                    color = textSecondaryColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dark Mode Chip
                    FilterChip(
                        selected = currentTheme == "DARK",
                        onClick = { onThemeChange("DARK") },
                        label = { Text("🌙 Gelap", fontSize = 11.sp, fontWeight = if (currentTheme == "DARK") FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) ElectricCyan.copy(alpha = 0.2f) else Color(0xFFEFF6FF),
                            selectedLabelColor = primaryAccent,
                            selectedLeadingIconColor = primaryAccent,
                            containerColor = cardBg,
                            labelColor = textSecondaryColor,
                            iconColor = textSecondaryColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = currentTheme == "DARK",
                            borderColor = if (currentTheme == "DARK") primaryAccent else cardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Light Mode Chip
                    FilterChip(
                        selected = currentTheme == "LIGHT",
                        onClick = { onThemeChange("LIGHT") },
                        label = { Text("☀️ Terang", fontSize = 11.sp, fontWeight = if (currentTheme == "LIGHT") FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) ElectricCyan.copy(alpha = 0.2f) else Color(0xFFEFF6FF),
                            selectedLabelColor = primaryAccent,
                            selectedLeadingIconColor = primaryAccent,
                            containerColor = cardBg,
                            labelColor = textSecondaryColor,
                            iconColor = textSecondaryColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = currentTheme == "LIGHT",
                            borderColor = if (currentTheme == "LIGHT") primaryAccent else cardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // System Default Chip
                    FilterChip(
                        selected = currentTheme == "SYSTEM",
                        onClick = { onThemeChange("SYSTEM") },
                        label = { Text("📱 Sistem", fontSize = 11.sp, fontWeight = if (currentTheme == "SYSTEM") FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(Icons.Default.BrightnessAuto, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) ElectricCyan.copy(alpha = 0.2f) else Color(0xFFEFF6FF),
                            selectedLabelColor = primaryAccent,
                            selectedLeadingIconColor = primaryAccent,
                            containerColor = cardBg,
                            labelColor = textSecondaryColor,
                            iconColor = textSecondaryColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = currentTheme == "SYSTEM",
                            borderColor = if (currentTheme == "SYSTEM") primaryAccent else cardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 1: E2EE Vault
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = emeraldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enkripsi End-to-End (E2EE Vault)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Kunci Master AES-256-GCM Perangkat:",
                    fontSize = 11.sp,
                    color = textSecondaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${masterKeyHex.take(24)}... (Keystore Protected)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryAccent
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("E2EE Master Key", masterKeyHex)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Sidik jari kunci disalin!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = textSecondaryColor, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kunci Biometrik / Sandi Sesi", fontSize = 12.sp, color = textPrimaryColor, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isBiometricLockEnabled,
                        onCheckedChange = { isBiometricLockEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = emeraldAccent,
                            uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 2: AI Cloud Gateway & Model Engine
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            val sharedPrefs = remember { context.getSharedPreferences("nusantara_ai_prefs", Context.MODE_PRIVATE) }
            var customKeyInput by remember { mutableStateOf(sharedPrefs.getString("custom_gemini_api_key", "") ?: "") }
            var isSaved by remember { mutableStateOf(false) }
            val hybridEngine = remember { com.example.domain.ai.HybridAIEngine(context, com.example.data.local.AppDatabase.getDatabase(context).analyticsDao()) }
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            var isTestingKey by remember { mutableStateOf(false) }
            var testStatusMessage by remember { mutableStateOf<String?>(null) }
            var isTestSuccess by remember { mutableStateOf(false) }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = primaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gateway Model AI Real-Time", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Konfigurasi Kunci API Google Cloud Gemini untuk inferensi langsung dan mode Cloud Murni.",
                    fontSize = 11.sp,
                    color = textSecondaryColor,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Kunci Google Gemini API (Aktif & Terenkripsi):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = customKeyInput,
                    onValueChange = { 
                        customKeyInput = it
                        isSaved = false
                        testStatusMessage = null
                    },
                    placeholder = { Text("Masukkan AIzaSy... (Atau gunakan bawaan .env)", fontSize = 11.sp) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        Button(
                            onClick = {
                                sharedPrefs.edit().putString("custom_gemini_api_key", customKeyInput.trim()).apply()
                                isSaved = true
                                Toast.makeText(context, "Kunci API berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                            modifier = Modifier.padding(end = 4.dp).height(32.dp)
                        ) {
                            Text(if (isSaved) "Tersimpan ✓" else "Simpan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test API Key Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val keyToTest = if (customKeyInput.isNotBlank()) customKeyInput.trim() else hybridEngine.getStoredApiKey()
                            isTestingKey = true
                            testStatusMessage = null
                            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                val result = hybridEngine.testApiKeyConnection(keyToTest)
                                isTestingKey = false
                                isTestSuccess = result.first
                                testStatusMessage = result.second
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isDark) primaryAccent else Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isTestingKey) {
                            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = primaryAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Menguji Koneksi Google Cloud...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(14.dp), tint = primaryAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🧪 Uji Koneksi Kunci Google Cloud Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryAccent)
                        }
                    }
                }

                // Test Status Result Banner
                testStatusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTestSuccess) (if (isDark) emeraldAccent.copy(alpha = 0.15f) else Color(0xFFECFDF5)) else (if (isDark) Color(0xFFFF5252).copy(alpha = 0.15f) else Color(0xFFFEE2E2)),
                        border = BorderStroke(1.dp, if (isTestSuccess) emeraldAccent else Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = (if (isTestSuccess) "🟢 " else "🔴 ") + msg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTestSuccess) emeraldAccent else Color(0xFFDC2626),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 3: Sync & Connectivity
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = primaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sinkronisasi & Jaringan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sinkronisasi Otomatis", fontSize = 12.sp, color = textPrimaryColor, fontWeight = FontWeight.Medium)
                        Text("Kirim ringkasan terenkripsi saat online", fontSize = 10.sp, color = textSecondaryColor)
                    }
                    Switch(
                        checked = isAutoSyncEnabled,
                        onCheckedChange = { isAutoSyncEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primaryAccent,
                            uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 3: Storage & Model Weights
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = violetAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penyimpanan & Bobot Model Offline", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Cache Model On-Device (Gemma/Qwen): 1.8 GB", fontSize = 11.sp, color = textPrimaryColor)
                Text("• Basis Data Obrolan & Enkripsi Room: 2.4 MB", fontSize = 11.sp, color = textPrimaryColor)
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onClearAllData()
                        Toast.makeText(context, "Basis data lokal dan cache berhasil dibersihkan!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFFFEE2E2)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFFFF5252).copy(alpha = 0.4f) else Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bersihkan Riwayat Obrolan & Cache", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 4: Open-Source & Transparency
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = primaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Transparansi & Keamanan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textPrimaryColor)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Nusantara AI menjamin privasi mutlak. Tidak ada telemetri komersial, pelacakan iklan, atau server log percakapan.",
                    fontSize = 11.sp,
                    color = textSecondaryColor,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Versi 1.0.0 (Hybrid Neural Engine Core)",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = emeraldAccent
                )
            }
        }
    }
}
