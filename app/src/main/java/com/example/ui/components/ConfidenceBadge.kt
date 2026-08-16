package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Badge kecil yang menampilkan skor kepercayaan (confidence) respons AI.
 *
 * Rentang warna adaptif:
 * - 🔴 < 50%  → Merah
 * - 🟡 50-79% → Amber
 * - 🟢 ≥ 80%  → Hijau
 */
@Composable
fun ConfidenceBadge(
    score: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    var displayedScore by remember { mutableIntStateOf(0) }

    LaunchedEffect(score) {
        displayedScore = 0
        val step = if (score > 0) score / 15 else 1
        while (displayedScore < score) {
            delay(30)
            displayedScore = minOf(displayedScore + maxOf(step, 1), score)
        }
    }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val targetColor = when {
        score >= 80 -> if (isDark) Color(0xFF00FFA3) else Color(0xFF047857) // Deep Emerald in Light Mode
        score >= 50 -> if (isDark) Color(0xFFFFB300) else Color(0xFFB45309) // Deep Amber in Light Mode
        else        -> if (isDark) Color(0xFFFF5252) else Color(0xFFDC2626) // Deep Red in Light Mode
    }

    val dotColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(600),
        label = "badge_color"
    )

    val label = when {
        score >= 80 -> "Tinggi"
        score >= 50 -> "Sedang"
        else        -> "Rendah"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(dotColor.copy(alpha = if (isDark) 0.16f else 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = "$displayedScore%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = dotColor
        )
        if (showLabel) {
            Text(
                text = "($label)",
                fontSize = 9.sp,
                color = dotColor.copy(alpha = 0.9f)
            )
        }
    }
}
