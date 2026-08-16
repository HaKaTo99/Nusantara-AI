package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Voice Wave Visualizer — 28 bar dinamis berbasis amplitude RMS.
 *
 * Saat `amplitude` = 0 (diam), bar bergerak lambat sinusoidal (idle wave).
 * Saat `amplitude` > 0 (berbicara), bar bereaksi terhadap amplitudo suara.
 */
@Composable
fun VoiceWaveVisualizer(
    amplitude: Float, // 0f - 1f dari VoiceInteractionManager RMS
    isActive: Boolean,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00F2FE),
    accentColor: Color = Color(0xFF4FACFE)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val barCount = 28
    val brush = Brush.horizontalGradient(listOf(primaryColor, accentColor, primaryColor))

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2f
        val barWidth = canvasWidth / (barCount * 2f)
        val spacing = barWidth

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing) + barWidth / 2f

            // Posisi sinusoidal relatif terhadap canvas
            val normalizedX = i.toFloat() / barCount
            val sineBase = sin(normalizedX * 3 * PI.toFloat() + phase).toFloat()

            // Tinggi bar: gabungkan idle wave + pengaruh amplitude
            val idleHeight = abs(sineBase) * (canvasHeight * 0.25f) + (canvasHeight * 0.08f)
            val amplitudeBoost = if (isActive && amplitude > 0.01f) {
                amplitude * canvasHeight * 0.7f * abs(sineBase + 0.3f)
            } else 0f

            val barHeight = idleHeight + amplitudeBoost
            val halfBar = barHeight.coerceIn(4f, centerY * 1.8f) / 2f

            val alpha = if (isActive) 0.85f + (amplitude * 0.15f) else 0.45f

            drawLine(
                brush = brush,
                start = Offset(x, centerY - halfBar),
                end = Offset(x, centerY + halfBar),
                strokeWidth = barWidth * 0.75f,
                cap = StrokeCap.Round,
                alpha = alpha
            )
        }
    }
}
