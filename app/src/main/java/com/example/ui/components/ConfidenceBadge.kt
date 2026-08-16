package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
 * Rentang warna:
 * - 🔴 < 50%  → Merah (Tebakan)
 * - 🟡 50-79% → Kuning (Sedang)
 * - 🟢 ≥ 80%  → Hijau (Tinggi)
 */
@Composable
fun ConfidenceBadge(
    score: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    var displayedScore by remember { mutableIntStateOf(0) }

    // Animasikan counter dari 0 ke score target
    LaunchedEffect(score) {
        displayedScore = 0
        val step = if (score > 0) score / 15 else 1
        while (displayedScore < score) {
            delay(30)
            displayedScore = minOf(displayedScore + maxOf(step, 1), score)
        }
    }

    val targetColor = when {
        score >= 80 -> Color(0xFF22C55E) // Hijau
        score >= 50 -> Color(0xFFF59E0B) // Kuning
        else        -> Color(0xFFEF4444) // Merah
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
            .background(dotColor.copy(alpha = 0.12f))
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
            fontWeight = FontWeight.SemiBold,
            color = dotColor
        )
        if (showLabel) {
            Text(
                text = "· $label",
                fontSize = 10.sp,
                color = dotColor.copy(alpha = 0.75f)
            )
        }
    }
}
