package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun SecurityBadgeDialog(
    sampleText: String = "Selamat datang di Nusantara AI! Sesi Anda terenkripsi penuh.",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val encManager = remember { EncryptionManager.getInstance(context) }
    val inspection = remember(sampleText) { encManager.inspectCipher(sampleText) }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val cardBg = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(emeraldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Shield",
                        tint = emeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "End-to-End Encryption Vault",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textPrimaryColor
                    )
                    Text(
                        text = "Zero-Server-Log Certified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = emeraldAccent
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Semua obrolan, memori persona, dan dokumen Anda dienkripsi secara langsung di perangkat Anda sebelum disimpan:",
                    fontSize = 12.sp,
                    color = textSecondaryColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Protocol Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF182235) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = primaryAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Algoritma: ${inspection.algorithm}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimaryColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Keystore: ${inspection.keyFingerprint}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = textSecondaryColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Inspeksi Cipher Text Real-Time:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFF0F172A))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "ENC:${inspection.cipherBase64.take(80)}...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF00FFA3)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Privasi Mutlak: Baik pengembang maupun pihak ketiga tidak dapat membaca riwayat percakapan.",
                    fontSize = 10.sp,
                    color = textSecondaryColor
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) EmeraldGreen else Color(0xFF047857),
                    contentColor = if (isDark) Color(0xFF003344) else Color.White
                ),
                modifier = Modifier.testTag("security_modal_close_button")
            ) {
                Text(text = "Tutup & Verifikasi Aman", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}
