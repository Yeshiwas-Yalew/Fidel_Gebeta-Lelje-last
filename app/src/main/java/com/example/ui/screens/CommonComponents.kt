package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Beautiful child-friendly Avatars
data class Avatar(val id: Int, val emoji: String, val name: String, val color: Color)

val AVATARS = listOf(
    Avatar(0, "🦁", "Leo the Lion", Color(0xFFFFB74D)),
    Avatar(1, "🐼", "Peter Panda", Color(0xFFE0E0E0)),
    Avatar(2, "🦊", "Felix Fox", Color(0xFFFF7043)),
    Avatar(3, "🐵", "Milo Monkey", Color(0xFFA1887F)),
    Avatar(4, "🐸", "Flippy Frog", Color(0xFF81C784))
)

// Particle effect for stars / points rewards
@Composable
fun StarConfettiEffect(trigger: Int) {
    if (trigger == 0) return

    var active by remember { mutableStateOf(false) }
    LaunchedEffect(trigger) {
        active = true
        delay(2200) // longer celebration
        active = false
    }

    if (active) {
        val colors = listOf(
            Color(0xFFFFD54F), // Gold ⭐
            Color(0xFFFF7043), // Coral 🌸
            Color(0xFF4FC3F7), // Light Blue 💎
            Color(0xFF81C784), // Light Green 🍃
            Color(0xFFBA68C8), // Purple 🔮
            Color(0xFFFF4081)  // Hot Pink 💖
        )
        val emojis = listOf("⭐", "🏅", "🎉", "🥳", "🎖️", "🏆", "🎨", "📝")

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            val screenW = maxWidth
            val screenH = maxHeight

            // Compile dynamic list of particles upon trigger
            val particles = remember(trigger) {
                val list = mutableListOf<ParticleSpec>()
                
                // 1. Bottom Left Corner Fountains (type = 1)
                repeat(25) { id ->
                    list.add(
                        ParticleSpec(
                            id = id,
                            xPercent = 0.0f,
                            yPercent = 1.0f,
                            angle = -Random.nextFloat() * 50f - 20f, // -20 to -70 deg (pointing up-right)
                            speed = Random.nextFloat() * 1.2f + 0.8f,
                            color = colors.random(),
                            size = Random.nextFloat() * 10f + 6f,
                            type = 1,
                            emoji = if (Random.nextFloat() < 0.2f) emojis.random() else null,
                            spinSpeed = Random.nextFloat() * 360f - 180f
                        )
                    )
                }

                // 2. Bottom Right Corner Fountains (type = 2)
                repeat(25) { id ->
                    list.add(
                        ParticleSpec(
                            id = id + 100,
                            xPercent = 1.0f,
                            yPercent = 1.0f,
                            angle = -Random.nextFloat() * 50f - 110f, // -110 to -160 deg (pointing up-left)
                            speed = Random.nextFloat() * 1.2f + 0.8f,
                            color = colors.random(),
                            size = Random.nextFloat() * 10f + 6f,
                            type = 2,
                            emoji = if (Random.nextFloat() < 0.2f) emojis.random() else null,
                            spinSpeed = Random.nextFloat() * 360f - 180f
                        )
                    )
                }

                // 3. Falling items from Top (type = 0)
                repeat(20) { id ->
                    list.add(
                        ParticleSpec(
                            id = id + 200,
                            xPercent = Random.nextFloat(),
                            yPercent = -0.1f,
                            angle = 90f, // straight down
                            speed = Random.nextFloat() * 0.4f + 0.3f,
                            color = colors.random(),
                            size = Random.nextFloat() * 12f + 8f,
                            type = 0,
                            emoji = if (Random.nextFloat() < 0.4f) emojis.random() else null,
                            spinSpeed = Random.nextFloat() * 240f - 120f
                        )
                    )
                }

                list
            }

            particles.forEach { particle ->
                val progress by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(2000, easing = LinearOutSlowInEasing),
                    label = "confetti_progress"
                )

                // Physics simulation
                val angleRad = Math.toRadians(particle.angle.toDouble())
                var posX = 0.dp
                var posY = 0.dp

                when (particle.type) {
                    0 -> { // Falling list from top
                        val startX = screenW * particle.xPercent
                        // horizontal weave side to side
                        val weave = Math.sin(progress.toDouble() * Math.PI * 2) * 20.0
                        posX = startX + weave.dp
                        posY = (screenH * progress) - 40.dp
                    }
                    1 -> { // Bottom-Left burst
                        val baseDistance = (particle.speed * progress * (screenW.value + screenH.value) * 0.5f)
                        val dx = (Math.cos(angleRad) * baseDistance).toFloat()
                        val dy = (Math.sin(angleRad) * baseDistance).toFloat()
                        // add gravity effect (drop slightly as progress develops)
                        val gravity = (progress * progress * 150f)
                        posX = dx.dp
                        posY = screenH + dy.dp + gravity.dp
                    }
                    2 -> { // Bottom-Right burst
                        val baseDistance = (particle.speed * progress * (screenW.value + screenH.value) * 0.5f)
                        val dx = (Math.cos(angleRad) * baseDistance).toFloat()
                        val dy = (Math.sin(angleRad) * baseDistance).toFloat()
                        val gravity = (progress * progress * 150f)
                        posX = screenW + dx.dp
                        posY = screenH + dy.dp + gravity.dp
                    }
                }

                val currentRotation = particle.spinSpeed * progress
                val currentAlpha = (1f - progress).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .offset(x = posX, y = posY)
                        .rotate(currentRotation)
                        .alpha(currentAlpha)
                ) {
                    if (particle.emoji != null) {
                        Text(
                            text = particle.emoji,
                            fontSize = (particle.size * 1.5f).sp
                        )
                    } else {
                        // Let's render shapes (circles or diamonds)
                        val isCircle = particle.id % 2 == 0
                        Box(
                            modifier = Modifier
                                .size(particle.size.dp)
                                .background(
                                    color = particle.color,
                                    shape = if (isCircle) CircleShape else RoundedCornerShape((particle.size / 3).dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

private data class ParticleSpec(
    val id: Int,
    val xPercent: Float,
    val yPercent: Float,
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val type: Int, // 0 = falling, 1 = bottom-left, 2 = bottom-right
    val emoji: String? = null,
    val spinSpeed: Float = 0f
)
