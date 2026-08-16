package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val gradientColors: List<Color>
)

@Composable
fun OnboardingScreen(
    onFinish: (startOffline: Boolean) -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            emoji = "🌐",
            title = "Mesin AI Hibrida",
            subtitle = "Online & Offline tanpa batas",
            description = "Nusantara AI bekerja mulus di mana saja — terhubung internet atau di mode pesawat. Beralih otomatis antara Cloud AI dan On-Device AI tanpa jeda.",
            gradientColors = listOf(Color(0xFF00F2FE), Color(0xFF4FACFE))
        ),
        OnboardingPage(
            emoji = "🔒",
            title = "Enkripsi E2EE",
            subtitle = "AES-256-GCM / Android Keystore",
            description = "Setiap pesan dan dokumen Anda dienkripsi menggunakan kunci hardware di chip perangkat. Tidak ada server yang melihat data Anda — Zero Server Log.",
            gradientColors = listOf(Color(0xFF7C3AED), Color(0xFFDB2777))
        ),
        OnboardingPage(
            emoji = "🎙️",
            title = "Voice AI Nusantara",
            subtitle = "Bicara langsung dengan AI",
            description = "Aktifkan asisten suara berbahasa Indonesia. Rekognisi ucapan real-time, text-to-speech responsif, dan visualisasi gelombang suara yang indah.",
            gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981))
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Logo Area
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { -40 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🚀",
                        fontSize = 52.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nusantara AI",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Platform AI Privasi Pertama Indonesia",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val p = pages[page]
                OnboardingPageContent(page = p, visible = visible)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dot Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateFloatAsState(
                        targetValue = if (isSelected) 24f else 8f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF00F2FE)
                                else Color.White.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isLastPage = pagerState.currentPage == pages.size - 1

                Button(
                    onClick = {
                        if (isLastPage) onFinish(false)
                        else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00F2FE)
                    )
                ) {
                    Text(
                        text = if (isLastPage) "🚀 Mulai Pakai Nusantara AI" else "Selanjutnya →",
                        color = Color(0xFF0B0F19),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                if (isLastPage) {
                    OutlinedButton(
                        onClick = { onFinish(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "📴 Mulai Mode Offline",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { onFinish(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Lewati",
                            color = Color.White.copy(alpha = 0.55f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                slideInVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    initialOffsetY = { 60 }
                )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                page.gradientColors.first().copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = page.emoji, fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = page.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = page.subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = page.gradientColors.first(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = page.description,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
