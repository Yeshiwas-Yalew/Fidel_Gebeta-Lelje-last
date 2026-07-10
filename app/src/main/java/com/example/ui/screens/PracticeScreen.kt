package com.example.ui.screens

import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioFormat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import com.example.data.*
import com.example.ui.FidelViewModel

enum class TrailStyle(val displayName: String, val emoji: String) {
    RAINBOW("Rainbow Sparkles", "💫"),
    NEON("Neon Glow", "🌟"),
    HEATMAP("Accuracy Heatmap", "🎯")
}

data class TrailSparkle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val alpha: Float,
    val dx: Float,
    val dy: Float
)

data class AmharicEncouragement(val amharic: String, val phonetic: String, val translation: String)

val AMHARIC_ENCOURAGEMENTS = listOf(
    AmharicEncouragement("ጎበዝ", "Gobez", "Well Done!"),
    AmharicEncouragement("በጣም ጥሩ", "Betam tiru", "Very Good!"),
    AmharicEncouragement("ድንቅ ስራ", "Dinq sira", "Wonderful Work!"),
    AmharicEncouragement("እጅግ በጣም ጎበዝ", "Ejig betam gobez", "Super Smart!"),
    AmharicEncouragement("አስደናቂ ነው", "Asdenaqi new", "It is Amazing!")
)

fun playHandwritingSuccessSound() {
    Thread {
        try {
            val sampleRate = 22050
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize.coerceAtLeast(2048))
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize.coerceAtLeast(2048),
                    AudioTrack.MODE_STREAM
                )
            }

            track.play()

            // Frequencies for C5 (523.25 Hz), E5 (659.25 Hz), G5 (783.99 Hz), C6 (1046.50 Hz)
            val freqs = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
            val durationMs = 120
            
            for (i in freqs.indices) {
                val freq = freqs[i]
                val actualDuration = if (i == freqs.lastIndex) 250 else durationMs
                val numSamples = (sampleRate * (actualDuration / 1000f)).toInt()
                val pcm = ShortArray(numSamples)
                for (j in 0 until numSamples) {
                    val t = j.toDouble() / sampleRate
                    // Linear decay envelope for a beautiful chime bell sound
                    val envelope = 1.0 - (j.toDouble() / numSamples)
                    val value = Math.sin(2.0 * Math.PI * freq * t) * envelope * 0.25 * 32767.0
                    pcm[j] = value.toInt().toShort()
                }
                track.write(pcm, 0, pcm.size)
                Thread.sleep(100)
            }
            
            track.stop()
            track.release()
        } catch (e: Exception) {
            android.util.Log.e("PracticeScreen", "Failed to play success chime: ${e.message}")
        }
    }.start()
}

/**
 * Resolves sequential handwriting stroke coordinate sequences (absolute Offset) for any
 * Amharic character by parsing its parent FidelFamily consonant structure and vowel order.
 */
fun getStrokePathsForLetter(character: String, family: FidelFamily, width: Float, height: Float): List<List<Offset>> {
    val boxSize = Math.min(width, height) * 0.55f
    val cx = width / 2f
    val cy = height / 2f
    val startX = cx - boxSize / 2f
    val startY = cy - boxSize / 2f

    fun toAbsolute(pctPoints: List<Pair<Float, Float>>): List<Offset> {
        return pctPoints.map { (px, py) ->
            Offset(startX + px * boxSize, startY + py * boxSize)
        }
    }

    val baseChar = family.mainConsonant
    val baseStrokesPercent = when (baseChar) {
        "ሀ" -> listOf(
            listOf(0.25f to 0.15f, 0.25f to 0.85f),
            listOf(0.25f to 0.15f, 0.75f to 0.15f),
            listOf(0.75f to 0.15f, 0.75f to 0.85f)
        )
        "ለ" -> listOf(
            listOf(0.5f to 0.15f, 0.25f to 0.85f),
            listOf(0.38f to 0.5f, 0.75f to 0.85f)
        )
        "ሐ" -> listOf(
            listOf(0.25f to 0.25f, 0.75f to 0.25f),
            listOf(0.25f to 0.25f, 0.25f to 0.85f),
            listOf(0.75f to 0.25f, 0.75f to 0.85f)
        )
        "መ" -> listOf(
            listOf(0.35f to 0.35f, 0.2f to 0.5f, 0.35f to 0.65f, 0.5f to 0.5f, 0.35f to 0.35f),
            listOf(0.5f to 0.5f, 0.65f to 0.35f, 0.8f to 0.5f, 0.65f to 0.65f, 0.5f to 0.5f)
        )
        "ሠ" -> listOf(
            listOf(0.25f to 0.2f, 0.25f to 0.7f),
            listOf(0.5f to 0.2f, 0.5f to 0.7f),
            listOf(0.75f to 0.2f, 0.75f to 0.7f),
            listOf(0.25f to 0.7f, 0.75f to 0.7f)
        )
        "ረ" -> listOf(
            listOf(0.35f to 0.15f, 0.35f to 0.45f, 0.75f to 0.85f)
        )
        "ሰ" -> listOf(
            listOf(0.3f to 0.8f, 0.3f to 0.2f),
            listOf(0.3f to 0.2f, 0.7f to 0.2f),
            listOf(0.7f to 0.2f, 0.7f to 0.8f)
        )
        "ሸ" -> listOf(
            listOf(0.3f to 0.8f, 0.3f to 0.35f),
            listOf(0.3f to 0.35f, 0.7f to 0.35f),
            listOf(0.7f to 0.35f, 0.7f to 0.8f),
            listOf(0.4f to 0.15f, 0.5f to 0.35f, 0.6f to 0.15f)
        )
        "ቀ" -> listOf(
            listOf(0.5f to 0.45f, 0.3f to 0.25f, 0.5f to 0.15f, 0.7f to 0.25f, 0.5f to 0.45f),
            listOf(0.5f to 0.45f, 0.5f to 0.85f)
        )
        "በ" -> listOf(
            listOf(0.25f to 0.2f, 0.25f to 0.85f),
            listOf(0.25f to 0.85f, 0.75f to 0.85f),
            listOf(0.75f to 0.85f, 0.75f to 0.2f)
        )
        "ተ" -> listOf(
            listOf(0.3f to 0.2f, 0.3f to 0.8f),
            listOf(0.3f to 0.8f, 0.7f to 0.8f),
            listOf(0.7f to 0.8f, 0.7f to 0.2f),
            listOf(0.3f to 0.45f, 0.7f to 0.45f)
        )
        "ቸ" -> listOf(
            listOf(0.3f to 0.3f, 0.3f to 0.8f),
            listOf(0.3f to 0.8f, 0.7f to 0.8f),
            listOf(0.7f to 0.8f, 0.7f to 0.3f),
            listOf(0.3f to 0.5f, 0.7f to 0.5f),
            listOf(0.45f to 0.15f, 0.5f to 0.3f, 0.55f to 0.15f)
        )
        "ኀ" -> listOf(
            listOf(0.35f to 0.2f, 0.2f to 0.4f, 0.35f to 0.6f, 0.5f to 0.4f, 0.35f to 0.2f),
            listOf(0.35f to 0.6f, 0.65f to 0.85f)
        )
        "ነ" -> listOf(
            listOf(0.3f to 0.2f, 0.3f to 0.85f),
            listOf(0.3f to 0.45f, 0.7f to 0.45f, 0.7f to 0.85f)
        )
        "ኘ" -> listOf(
            listOf(0.3f to 0.3f, 0.3f to 0.85f),
            listOf(0.3f to 0.5f, 0.7f to 0.5f, 0.7f to 0.85f),
            listOf(0.45f to 0.15f, 0.5f to 0.3f, 0.55f to 0.15f)
        )
        "አ" -> listOf(
            listOf(0.5f to 0.2f, 0.2f to 0.45f, 0.5f to 0.65f),
            listOf(0.5f to 0.2f, 0.5f to 0.85f),
            listOf(0.5f to 0.45f, 0.8f to 0.7f)
        )
        "ከ" -> listOf(
            listOf(0.25f to 0.2f, 0.25f to 0.8f),
            listOf(0.25f to 0.2f, 0.75f to 0.2f, 0.75f to 0.8f),
            listOf(0.25f to 0.5f, 0.55f to 0.5f)
        )
        "ኸ" -> listOf(
            listOf(0.25f to 0.3f, 0.25f to 0.8f),
            listOf(0.25f to 0.3f, 0.75f to 0.3f, 0.75f to 0.8f),
            listOf(0.25f to 0.55f, 0.55f to 0.55f),
            listOf(0.45f to 0.15f, 0.5f to 0.3f, 0.55f to 0.15f)
        )
        "ወ" -> listOf(
            listOf(0.3f to 0.35f, 0.2f to 0.15f, 0.4f to 0.15f, 0.3f to 0.35f),
            listOf(0.7f to 0.35f, 0.6f to 0.15f, 0.8f to 0.15f, 0.7f to 0.35f),
            listOf(0.5f to 0.35f, 0.5f to 0.85f)
        )
        "ዐ" -> listOf(
            listOf(0.5f to 0.2f, 0.2f to 0.5f, 0.5f to 0.8f, 0.8f to 0.5f, 0.5f to 0.2f)
        )
        "ዘ" -> listOf(
            listOf(0.3f to 0.25f, 0.3f to 0.55f),
            listOf(0.3f to 0.55f, 0.7f to 0.55f),
            listOf(0.7f to 0.55f, 0.7f to 0.85f)
        )
        "ዠ" -> listOf(
            listOf(0.3f to 0.35f, 0.3f to 0.55f),
            listOf(0.3f to 0.55f, 0.7f to 0.55f),
            listOf(0.7f to 0.55f, 0.7f to 0.85f),
            listOf(0.45f to 0.15f, 0.5f to 0.35f, 0.55f to 0.15f)
        )
        "የ" -> listOf(
            listOf(0.25f to 0.25f, 0.45f to 0.55f, 0.5f to 0.85f),
            listOf(0.75f to 0.25f, 0.55f to 0.55f, 0.5f to 0.85f)
        )
        "ደ" -> listOf(
            listOf(0.3f to 0.2f, 0.3f to 0.8f),
            listOf(0.3f to 0.5f, 0.7f to 0.5f, 0.7f to 0.8f)
        )
        "ጀ" -> listOf(
            listOf(0.3f to 0.3f, 0.3f to 0.8f),
            listOf(0.3f to 0.55f, 0.7f to 0.55f, 0.7f to 0.8f),
            listOf(0.45f to 0.15f, 0.5f to 0.3f, 0.55f to 0.15f)
        )
        "ገ" -> listOf(
            listOf(0.35f to 0.2f, 0.35f to 0.85f),
            listOf(0.35f to 0.35f, 0.7f to 0.55f, 0.55f to 0.85f)
        )
        "ጠ" -> listOf(
            listOf(0.3f to 0.25f, 0.7f to 0.25f),
            listOf(0.5f to 0.25f, 0.25f to 0.55f, 0.5f to 0.85f, 0.75f to 0.55f, 0.5f to 0.25f)
        )
        "ጨ" -> listOf(
            listOf(0.3f to 0.35f, 0.7f to 0.35f),
            listOf(0.5f to 0.35f, 0.25f to 0.6f, 0.5f to 0.85f, 0.75f to 0.6f, 0.5f to 0.35f),
            listOf(0.45f to 0.15f, 0.5f to 0.35f, 0.55f to 0.15f)
        )
        "ጰ" -> listOf(
            listOf(0.5f to 0.2f, 0.5f to 0.85f),
            listOf(0.5f to 0.45f, 0.25f to 0.65f, 0.5f to 0.8f, 0.75f to 0.65f, 0.5f to 0.45f)
        )
        "ጸ" -> listOf(
            listOf(0.3f to 0.25f, 0.5f to 0.45f, 0.7f to 0.25f),
            listOf(0.5f to 0.45f, 0.5f to 0.85f)
        )
        "ፀ" -> listOf(
            listOf(0.5f to 0.25f, 0.25f to 0.55f, 0.5f to 0.8f, 0.75f to 0.55f, 0.5f to 0.25f),
            listOf(0.25f to 0.55f, 0.75f to 0.55f)
        )
        "ፈ" -> listOf(
            listOf(0.35f to 0.35f, 0.2f to 0.15f, 0.45f to 0.15f, 0.35f to 0.35f),
            listOf(0.35f to 0.35f, 0.35f to 0.85f),
            listOf(0.35f to 0.6f, 0.75f to 0.85f)
        )
        "ፐ" -> listOf(
            listOf(0.25f to 0.2f, 0.25f to 0.85f),
            listOf(0.75f to 0.2f, 0.75f to 0.85f),
            listOf(0.25f to 0.45f, 0.75f to 0.45f)
        )
        else -> listOf( // Fallback
            listOf(0.5f to 0.2f, 0.2f to 0.5f, 0.5f to 0.8f, 0.8f to 0.5f, 0.5f to 0.2f),
            listOf(0.5f to 0.2f, 0.5f to 0.8f)
        )
    }

    val finalStrokesPercent = baseStrokesPercent.toMutableList()

    // Add extra order modifiers (1 to 7) based on vowel forms
    val letterObj = family.letters.find { it.character == character }
    val order = letterObj?.order ?: 1
    if (order > 1) {
        when (order) {
            2 -> { // Ka'ib: middle-right horizontal tick
                finalStrokesPercent.add(listOf(0.6f to 0.5f, 0.85f to 0.5f))
            }
            3 -> { // Salis: bottom-right foot hanger
                finalStrokesPercent.add(listOf(0.75f to 0.8f, 0.85f to 0.95f))
            }
            4 -> { // Rabi': modified / loop right leg
                finalStrokesPercent.add(listOf(0.75f to 0.5f, 0.9f to 0.75f))
            }
            5 -> { // Hamis: loop/extension on the right foot
                finalStrokesPercent.add(listOf(0.75f to 0.8f, 0.82f to 0.92f, 0.9f to 0.8f))
            }
            6 -> { // Sadis: small tick on the left side / bottom
                finalStrokesPercent.add(listOf(0.25f to 0.6f, 0.15f to 0.55f))
            }
            7 -> { // Sab'i: small circle/tick on the top/left
                finalStrokesPercent.add(listOf(0.25f to 0.15f, 0.35f to 0.2f, 0.25f to 0.25f, 0.15f to 0.2f, 0.25f to 0.15f))
            }
        }
    }

    return finalStrokesPercent.map { toAbsolute(it) }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    val isExpertMode = userProgress.difficultyMode == "EXPERT"

    // Expert Mode State
    val expertAlphabetSeq = remember(isExpertMode) {
        if (isExpertMode) {
            FidelData.families.flatMap { it.letters }.shuffled()
        } else {
            emptyList()
        }
    }
    var expertSeqIndex by remember(isExpertMode) { mutableStateOf(0) }

    // Session progress for Easy Mode (5 letters per session)
    var sessionDrawnCount by remember { mutableStateOf(0) }
    var showSessionCompleteDialog by remember { mutableStateOf(false) }

    var activeFamilyIndex by remember { mutableStateOf(0) }
    val regularFamily = FidelData.families[activeFamilyIndex]
    var selectedLetterChar by remember(activeFamilyIndex) { mutableStateOf(regularFamily.mainConsonant) }

    // Active Letter under either mode
    val activeLetter = if (isExpertMode) {
        expertAlphabetSeq.getOrNull(expertSeqIndex)?.character ?: "ሀ"
    } else {
        selectedLetterChar
    }

    // Active Family under either mode
    val activeFamily = if (isExpertMode) {
        remember(activeLetter) {
            FidelData.families.find { fam -> fam.letters.any { it.character == activeLetter } } ?: FidelData.families[0]
        }
    } else {
        regularFamily
    }

    val activeLetterObj = remember(activeLetter, activeFamily) {
        activeFamily.letters.find { it.character == activeLetter }
    }

    val exerciseRecords by viewModel.exerciseRecords.collectAsState()
    val successfullyTracedLetters = remember(exerciseRecords) {
        exerciseRecords
            .filter { it.type == "TRACING" && it.score >= 4 }
            .mapNotNull { record ->
                val name = record.exerciseName
                if (name.startsWith("Letter ") && name.contains(" - Tracing")) {
                    name.substringAfter("Letter ").substringBefore(" - Tracing").trim()
                } else if (name.startsWith("Draw Letter ")) {
                    name.substringAfter("Draw Letter ").trim()
                } else null
            }
            .toSet()
    }

    // Tracing path recording
    val points = remember { mutableStateListOf<Offset>() }
    var completeMessage by remember { mutableStateOf("") }
    var starRewardText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var activeEncouragement by remember { mutableStateOf<AmharicEncouragement?>(null) }

    var selectedTrailStyle by remember { mutableStateOf(TrailStyle.NEON) }
    val sparkles = remember { mutableStateListOf<TrailSparkle>() }
    var arenaWidth by remember { mutableStateOf(500f) }
    var arenaHeight by remember { mutableStateOf(500f) }

    // Fading effect loop for the sparkle emitters
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(24)
            if (sparkles.isNotEmpty()) {
                val updated = sparkles.map { sparkle ->
                    sparkle.copy(
                        x = sparkle.x + sparkle.dx,
                        y = sparkle.y + sparkle.dy,
                        alpha = sparkle.alpha - 0.06f,
                        size = sparkle.size * 0.94f
                    )
                }.filter { it.alpha > 0.05f }
                sparkles.clear()
                sparkles.addAll(updated)
            }
        }
    }

    // Interactive stroke demonstration states
    var isPlayingDemo by remember { mutableStateOf(false) }
    var demoPointOffset by remember { mutableStateOf<Offset?>(null) }
    val demoTracedPaths = remember { mutableStateListOf<List<Offset>>() }

    // Coroutine managing the sequential tracing animation
    LaunchedEffect(activeLetter, isPlayingDemo) {
        if (isPlayingDemo && arenaWidth > 0f && arenaHeight > 0f) {
            demoTracedPaths.clear()
            demoPointOffset = null
            
            // Allow a tiny delay for layout stabilization
            delay(100)
            val strokes = getStrokePathsForLetter(activeLetter, activeFamily, arenaWidth, arenaHeight)
            
            for (stroke in strokes) {
                if (stroke.isEmpty()) continue
                if (!isPlayingDemo) break
                
                val currentStrokePoints = mutableStateListOf<Offset>()
                demoTracedPaths.add(currentStrokePoints)
                
                // Trace through the points in this stroke sequence
                for (i in 0 until stroke.size - 1) {
                    val start = stroke[i]
                    val end = stroke[i + 1]
                    val segmentSteps = 10
                    for (step in 0..segmentSteps) {
                        if (!isPlayingDemo) break
                        val t = step.toFloat() / segmentSteps
                        val currentPos = Offset(
                            x = start.x + (end.x - start.x) * t,
                            y = start.y + (end.y - start.y) * t
                        )
                        demoPointOffset = currentPos
                        currentStrokePoints.add(currentPos)
                        
                        // Add magical trail sparkles following the demo pointer
                        repeat(1) {
                            sparkles.add(
                                TrailSparkle(
                                    x = currentPos.x,
                                    y = currentPos.y,
                                    color = when (selectedTrailStyle) {
                                        TrailStyle.RAINBOW -> Color(0xFFFFD700) // Golden sparkles
                                        TrailStyle.NEON -> Color(0xFF00E5FF) // Cyber neon cyan
                                        TrailStyle.HEATMAP -> Color(0xFF4CAF50) // Emerald green
                                    },
                                    size = (8..15).random().toFloat(),
                                    alpha = 1.0f,
                                    dx = ((-150..150).random() / 100f),
                                    dy = ((-150..150).random() / 100f)
                                )
                            )
                        }
                        delay(20)
                    }
                }
                // Small breather between strokes to show stroke order clearly
                delay(200)
            }
            demoPointOffset = null
            // Hold the completed form for a moment
            delay(600)
            isPlayingDemo = false
            demoTracedPaths.clear()
        }
    }

    // Auto-play the stroke demonstration on letter changes so children see correct stroke order first
    LaunchedEffect(activeLetter) {
        points.clear()
        completeMessage = ""
        starRewardText = ""
        // Auto-play!
        isPlayingDemo = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Handwriting Arena ✏️", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("practice_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val isMuted = !userProgress.textToSpeechEnabled
                    IconButton(
                        onClick = { viewModel.toggleTtsEnabled() },
                        modifier = Modifier.testTag("mute_unmute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Audio" else "Mute Audio",
                            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pentastar Rewards Goals banner 🏅
            val count = successfullyTracedLetters.size
            Card(
                modifier = Modifier
                    .fillHorizontalPercent(0.95f)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🏆 Pentastar Tracer Goal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (count >= 5) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (count >= 5) 
                                "Completed! You unlocked the Pentastar Tracer badge! 🏅"
                            else 
                                "Trace 5 unique Amharic letters to earn custom 🏅 badge & 15 extra stars!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val progressFraction = (count.toFloat() / 5f).coerceAtMost(1f)
                        LinearProgressIndicator(
                            progress = progressFraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (count >= 5) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = if (count >= 5) Color(0xFFFFD700) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (count >= 5) "🏅" else "🔒",
                            fontSize = 24.sp
                        )
                    }
                }
            }

            // Select letter family ribbon or random sequence navigator
            Row(
                modifier = Modifier
                    .fillHorizontalPercent(0.95f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isExpertMode) {
                    Button(
                        onClick = {
                            if (expertSeqIndex > 0) expertSeqIndex-- else expertSeqIndex = expertAlphabetSeq.size - 1
                            points.clear()
                            completeMessage = ""
                            starRewardText = ""
                            viewModel.speak("ሳሉ ፦ ${expertAlphabetSeq[expertSeqIndex].character}", "Let's draw")
                        },
                        modifier = Modifier.testTag("practice_prev")
                    ) {
                        Text("ያለፈው")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = "Letter: $activeLetter",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = {
                                    viewModel.speakAmharicLetterWeb(activeLetter, activeLetterObj?.ttsPhonetic ?: activeLetter)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("replay_active_letter_practice_expert"),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Replay pronunciation",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Expert Shuffled Sequence #${expertSeqIndex + 1}/${expertAlphabetSeq.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Streak: 🔥 ${userProgress.tracingStreak}/10",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF9100)
                        )
                    }

                    Button(
                        onClick = {
                            if (expertSeqIndex < expertAlphabetSeq.size - 1) expertSeqIndex++ else expertSeqIndex = 0
                            points.clear()
                            completeMessage = ""
                            starRewardText = ""
                            viewModel.speak("ሳሉ ፦ ${expertAlphabetSeq[expertSeqIndex].character}", "Let's draw")
                        },
                        modifier = Modifier.testTag("practice_next")
                    ) {
                        Text("ቀጣይ")
                    }
                } else {
                    Button(
                        onClick = {
                            if (activeFamilyIndex > 0) activeFamilyIndex-- else activeFamilyIndex = FidelData.families.size - 1
                            points.clear()
                            completeMessage = ""
                            starRewardText = ""
                            viewModel.speak("ሳሉ ፦ ${FidelData.families[activeFamilyIndex].mainConsonant}", "Draw")
                        },
                        modifier = Modifier.testTag("practice_prev")
                    ) {
                        Text("ያለፈው")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Text(
                                text = "Letter: $activeLetter (${activeFamily.familyName})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = {
                                    viewModel.speakAmharicLetterWeb(activeLetter, activeLetterObj?.ttsPhonetic ?: activeLetter)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("replay_active_letter_practice_easy"),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Replay pronunciation",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Easy Session Progress $sessionDrawnCount/5 🎨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Streak: 🔥 ${userProgress.tracingStreak}/10",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF9100)
                        )
                    }

                    Button(
                        onClick = {
                            if (activeFamilyIndex < FidelData.families.size - 1) activeFamilyIndex++ else activeFamilyIndex = 0
                            points.clear()
                            completeMessage = ""
                            starRewardText = ""
                            viewModel.speak("ሳሉ ፦ ${FidelData.families[activeFamilyIndex].mainConsonant}", "Draw")
                        },
                        modifier = Modifier.testTag("practice_next")
                    ) {
                        Text("ቀጣይ")
                    }
                }
            }

            // 7 characters block for the current family
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                activeFamily.letters.forEach { letter ->
                    val isSelected = letter.character == activeLetter
                    val isTraced = successfullyTracedLetters.contains(letter.character)
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else if (isTraced) {
                                    Color(0xFFE8F5E9)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                            .border(
                                width = if (isSelected) 3.dp else if (isTraced) 1.dp else 0.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else if (isTraced) {
                                    Color(0xFF2E7D32)
                                } else {
                                    Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                selectedLetterChar = letter.character
                                points.clear()
                                completeMessage = ""
                                starRewardText = ""
                                viewModel.speak("ሳሉ ፦ ${letter.character}", "Let's draw")
                            }
                            .testTag("practice_select_${letter.character}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(
                                text = letter.character,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else if (isTraced) {
                                    Color(0xFF2E7D32)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            if (isTraced && !isSelected) {
                                Text(
                                    text = "⭐",
                                    fontSize = 7.sp,
                                    modifier = Modifier.offset(y = (-4).dp)
                                )
                            }
                        }
                    }
                }
            }

            val liveAccuracyPercent = remember(points.size, activeLetter, activeFamily, arenaWidth, arenaHeight) {
                if (points.isEmpty()) {
                    100
                } else {
                    val strokes = getStrokePathsForLetter(activeLetter, activeFamily, arenaWidth, arenaHeight)
                    val guidePoints = strokes.flatten()
                    if (guidePoints.isEmpty()) {
                        100
                    } else {
                        var totalScore = 0f
                        val sampledPoints = if (points.size > 150) {
                            points.filterIndexed { idx, _ -> idx % (points.size / 75) == 0 }
                        } else {
                            points
                        }
                        
                        sampledPoints.forEach { p ->
                            var minDistSq = Float.MAX_VALUE
                            guidePoints.forEach { g ->
                                val dx = p.x - g.x
                                val dy = p.y - g.y
                                val distSq = dx * dx + dy * dy
                                if (distSq < minDistSq) {
                                    minDistSq = distSq
                                }
                            }
                            val dist = Math.sqrt(minDistSq.toDouble()).toFloat()
                            val score = (100f * (1f - (dist / 140f))).coerceIn(30f, 100f)
                            totalScore += score
                        }
                        val avg = totalScore / sampledPoints.size
                        avg.toInt()
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guidance instruction for children / non-readers
            Text(
                text = "Trace over the gray letter outline with your finger! 👉",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Replay / Watch Stroke Guide button
            Button(
                onClick = {
                    isPlayingDemo = false
                    points.clear()
                    completeMessage = ""
                    starRewardText = ""
                    viewModel.speak("እንዴት እንደሚጻፍ ይመልከቱ", "Let's see how to write it")
                    isPlayingDemo = true
                },
                modifier = Modifier
                    .testTag("play_stroke_demo_button")
                    .padding(vertical = 2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("እንዴት እንደሚጻፍ ይመልከቱ 🪄 (Watch Guide)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Trail Brush selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Brush:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                TrailStyle.values().forEach { style ->
                    val isSelected = selectedTrailStyle == style
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTrailStyle = style
                                viewModel.speak("Using ${style.displayName}", "Brush changed")
                            }
                            .testTag("trail_style_chip_${style.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(style.emoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = style.displayName.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Accuracy HUD Card
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯 Live Alignment:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    val color = when {
                        liveAccuracyPercent >= 90 -> Color(0xFF4CAF50)
                        liveAccuracyPercent >= 75 -> Color(0xFFFFA726)
                        else -> Color(0xFFEF5350)
                    }
                    Text(
                        text = "$liveAccuracyPercent%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                }

                val ratingText = when {
                    points.isEmpty() -> "Ready to trace! ✏️"
                    liveAccuracyPercent >= 92 -> "PERFECT! 👑"
                    liveAccuracyPercent >= 83 -> "SUPERB! ⭐️"
                    liveAccuracyPercent >= 75 -> "GREAT! 👍"
                    else -> "Stay in the gray line! 💡"
                }
                Text(
                    text = ratingText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Tracing Arena Box with dashed outline representation of character
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .onSizeChanged { size ->
                        arenaWidth = size.width.toFloat()
                        arenaHeight = size.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            if (isPlayingDemo) {
                                isPlayingDemo = false
                                demoPointOffset = null
                                demoTracedPaths.clear()
                            }
                            change.consume()
                            val pos = change.position
                            points.add(pos)
                            
                            // Emit shimmering physics trail elements if Rainbow is active
                            if (selectedTrailStyle == TrailStyle.RAINBOW) {
                                repeat(2) {
                                    sparkles.add(
                                        TrailSparkle(
                                            x = pos.x,
                                            y = pos.y,
                                            color = when ((0..4).random()) {
                                                0 -> Color(0xFFE040FB)
                                                1 -> Color(0xFF00E5FF)
                                                2 -> Color(0xFF00E676)
                                                3 -> Color(0xFFFFD700)
                                                else -> Color(0xFFFF9100)
                                            },
                                            size = (8..18).random().toFloat(),
                                            alpha = 1.0f,
                                            dx = ((-250..250).random() / 100f),
                                            dy = ((-300..100).random() / 100f) - 1.2f
                                        )
                                    )
                                }
                            }
                        }
                    }
                    .testTag("tracing_canvas_arena"),
                contentAlignment = Alignment.Center
            ) {
                // Large reference letter in the background in very soft gray
                Text(
                    text = activeLetter,
                    fontSize = 180.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.LightGray.copy(alpha = 0.12f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Canvas rendering user dragged strokes
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val centerX = canvasWidth / 2f
                    val centerY = canvasHeight / 2f

                    // 1. Draw static thick gray skeleton guide paths for the current letter
                    val strokes = getStrokePathsForLetter(activeLetter, activeFamily, canvasWidth, canvasHeight)
                    strokes.forEach { strokePoints ->
                        if (strokePoints.size > 1) {
                            val strokePath = Path().apply {
                                moveTo(strokePoints[0].x, strokePoints[0].y)
                                for (i in 1 until strokePoints.size) {
                                    lineTo(strokePoints[i].x, strokePoints[i].y)
                                }
                            }
                            // Thick soft gray brush guide
                            drawPath(
                                path = strokePath,
                                color = Color.LightGray.copy(alpha = 0.65f),
                                style = Stroke(
                                    width = 24.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                            // Elegant thin white centerline for precision child guidance
                            drawPath(
                                path = strokePath,
                                color = Color.White.copy(alpha = 0.75f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    if (points.size > 1) {
                        when (selectedTrailStyle) {
                            TrailStyle.RAINBOW -> {
                                for (i in 0 until points.size - 1) {
                                    val p1 = points[i]
                                    val p2 = points[i + 1]
                                    val fraction = i.toFloat() / points.size
                                    val color = Color.hsv(
                                        hue = (fraction * 360f) % 360f,
                                        saturation = 0.9f,
                                        value = 0.95f
                                    )
                                    drawLine(
                                        color = color,
                                        start = p1,
                                        end = p2,
                                        strokeWidth = 14.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                            TrailStyle.NEON -> {
                                val path = Path().apply {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0x3D00E5FF),
                                    style = Stroke(
                                        width = 24.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                                drawPath(
                                    path = path,
                                    color = Color(0xFF00E5FF),
                                    style = Stroke(
                                        width = 8.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                            TrailStyle.HEATMAP -> {
                                val maxExpectedRadius = centerX * 0.72f
                                val minExpectedRadius = centerX * 0.15f
                                
                                for (i in 0 until points.size - 1) {
                                    val p1 = points[i]
                                    val p2 = points[i + 1]
                                    val midX = (p1.x + p2.x) / 2f
                                    val midY = (p1.y + p2.y) / 2f
                                    val dx = Math.abs(midX - centerX)
                                    val dy = Math.abs(midY - centerY)
                                    val distanceToCenter = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                                    
                                    val segmentColor = if (distanceToCenter < maxExpectedRadius) {
                                        if (distanceToCenter > minExpectedRadius) {
                                            Color(0xFF4CAF50)
                                        } else {
                                            Color(0xFFFFA726)
                                        }
                                    } else {
                                        Color(0xFFEF5350)
                                    }
                                    drawLine(
                                        color = segmentColor,
                                        start = p1,
                                        end = p2,
                                        strokeWidth = 14.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }

                    // Render floating sparkles trail if in RAINBOW style
                    if (selectedTrailStyle == TrailStyle.RAINBOW) {
                        sparkles.forEach { sparkle ->
                            drawCircle(
                                color = sparkle.color.copy(alpha = sparkle.alpha),
                                radius = sparkle.size,
                                center = Offset(sparkle.x, sparkle.y)
                            )
                        }
                    }

                    // Render dynamic pulsing halo at the touch active tip
                    if (points.isNotEmpty()) {
                        val lastPoint = points.last()
                        drawCircle(
                            color = when (selectedTrailStyle) {
                                TrailStyle.NEON -> Color(0x6600E5FF)
                                TrailStyle.HEATMAP -> Color(0x664CAF50)
                                TrailStyle.RAINBOW -> Color(0x66E040FB)
                            },
                            radius = 18.dp.toPx(),
                            center = lastPoint
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = lastPoint
                        )
                    }

                    // Render interactive stroke guide animation if active
                    if (isPlayingDemo) {
                        demoTracedPaths.forEach { strokePoints ->
                            if (strokePoints.size > 1) {
                                val path = Path().apply {
                                    moveTo(strokePoints[0].x, strokePoints[0].y)
                                    for (i in 1 until strokePoints.size) {
                                        lineTo(strokePoints[i].x, strokePoints[i].y)
                                    }
                                }
                                // Thick golden ambient glow
                                drawPath(
                                    path = path,
                                    color = Color(0x33FFD700), // Glowing Gold
                                    style = Stroke(
                                        width = 24.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                                // Precision golden line
                                drawPath(
                                    path = path,
                                    color = Color(0xFFFFD700), // Bright Gold
                                    style = Stroke(
                                        width = 8.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }

                        // Pulsating magic cursor tip representing a golden pencil/brush
                        demoPointOffset?.let { tip ->
                            drawCircle(
                                color = Color(0x66FFD700),
                                radius = 22.dp.toPx(),
                                center = tip
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 8.dp.toPx(),
                                center = tip
                            )
                        }
                    }
                }

                // Floating button that triggers audio playback of correct pronunciation via Web Speech Synthesis API
                FilledIconButton(
                    onClick = {
                        viewModel.speakAmharicLetterWeb(activeLetter, activeLetterObj?.ttsPhonetic ?: activeLetter)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .testTag("speak_pronunciation_canvas_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Pronounce Letter"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cheerful gamified feed-back triggers
            if (completeMessage.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = completeMessage,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = starRewardText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Action bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Clear Brush
                Button(
                    onClick = {
                        points.clear()
                        completeMessage = ""
                        starRewardText = ""
                        viewModel.speak("Cleared", "cleared")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Clear")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("አጽዳ")
                }

                // Check Tracing Button (Evaluates accuracy heuristics, positive reinforcement!)
                Button(
                    onClick = {
                        if (points.size < 6) {
                            completeMessage = "Whoops! Try tracing a bit more! 🤔"
                            starRewardText = ""
                            viewModel.speak("Try tracing a bit more!", "Try tracing a bit more")
                        } else {
                            val encouragement = AMHARIC_ENCOURAGEMENTS.random()
                            activeEncouragement = encouragement
                            completeMessage = "Fantastic Drawing! ⭐️ ⭐️ ⭐️ ⭐️ ⭐️\n${encouragement.amharic} (${encouragement.phonetic}) - ${encouragement.translation}"
                            starRewardText = "+15 Gems & +2 Stars rewarded!"
                            viewModel.saveExerciseRecord("Draw Letter $activeLetter", 5, 5, "TRACING")
                            viewModel.triggerConfetti()
                            viewModel.speak("Sensational work! You got five stars!", "Sensational work")
                            // Play native encouraging Amharic phrase combined with the character pronunciation
                            viewModel.speakAmharicSuccessWeb(
                                character = activeLetter,
                                fallbackPhonetic = activeLetterObj?.ttsPhonetic ?: activeLetter,
                                encouragementAmharic = encouragement.amharic,
                                encouragementPhonetic = encouragement.phonetic
                            )
                            showSuccessDialog = true
                            playHandwritingSuccessSound()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)), // Drop-in green
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("check_tracing_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Check")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ጨረስኩ!")
                }
            }
        }
    }

    if (showSuccessDialog) {
        val avatarEmoji = AVATARS.firstOrNull { it.id == userProgress.avatarId }?.emoji ?: "🦁"
        SuccessCelebrationDialog(
            letter = activeLetter,
            familyEmoji = activeFamily.exampleEmoji,
            avatarEmoji = avatarEmoji,
            encouragement = activeEncouragement,
            onListen = {
                val enc = activeEncouragement
                if (enc != null) {
                    viewModel.speakAmharicSuccessWeb(
                        character = activeLetter,
                        fallbackPhonetic = activeLetterObj?.ttsPhonetic ?: activeLetter,
                        encouragementAmharic = enc.amharic,
                        encouragementPhonetic = enc.phonetic
                    )
                } else {
                    viewModel.speakAmharicLetterWeb(activeLetter, activeLetterObj?.ttsPhonetic ?: activeLetter)
                }
            },
            onDismiss = {
                points.clear()
                completeMessage = ""
                starRewardText = ""
                activeEncouragement = null
                showSuccessDialog = false
            },
            onNext = {
                if (isExpertMode) {
                    if (expertSeqIndex < expertAlphabetSeq.size - 1) {
                        expertSeqIndex++
                    } else {
                        expertSeqIndex = 0
                    }
                    val nextLetter = expertAlphabetSeq[expertSeqIndex].character
                    viewModel.speak("ሳሉ ፦ $nextLetter", "Draw")
                } else {
                    sessionDrawnCount++
                    if (sessionDrawnCount >= 5) {
                        viewModel.addCoinsAndStars(50, 0)
                        showSessionCompleteDialog = true
                    } else {
                        val currentLetterIdx = activeFamily.letters.indexOfFirst { it.character == activeLetter }
                        val nextLetter = if (currentLetterIdx != -1 && currentLetterIdx < activeFamily.letters.size - 1) {
                            val nextChar = activeFamily.letters[currentLetterIdx + 1].character
                            selectedLetterChar = nextChar
                            nextChar
                        } else {
                            val nextFamIdx = if (activeFamilyIndex < FidelData.families.size - 1) activeFamilyIndex + 1 else 0
                            activeFamilyIndex = nextFamIdx
                            FidelData.families[nextFamIdx].mainConsonant
                        }
                        viewModel.speak("ሳሉ ፦ $nextLetter", "Draw")
                    }
                }
                points.clear()
                completeMessage = ""
                starRewardText = ""
                activeEncouragement = null
                showSuccessDialog = false
            }
        )
    }

    if (showSessionCompleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showSessionCompleteDialog = false
                sessionDrawnCount = 0
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSessionCompleteDialog = false
                        sessionDrawnCount = 0
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("session_complete_confirm")
                ) {
                    Text("Start New Session! 🚀")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return back")
                }
            },
            title = {
                Text(
                    text = "Session Completed! 🎉🥳",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color(0xFF2E7D32)
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🌟 Fantastic! 🌟",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "You successfully traced 5 Amharic letters in this session! You have earned +50 bonus coins!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Keep up the amazing work!",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("session_complete_dialog")
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SuccessCelebrationDialog(
    letter: String,
    familyEmoji: String,
    avatarEmoji: String,
    encouragement: AmharicEncouragement?,
    onListen: () -> Unit,
    onDismiss: () -> Unit,
    onNext: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    
    // Smooth pulsing background ripple
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Up and down bouncing motion
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -18f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    var showContent by remember { mutableStateOf(false) }
    var localConfettiTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        showContent = true
        delay(250)
        localConfettiTrigger += 1
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("celebration_success_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    // Title with happy starburst emojis
                    Text(
                        text = "🎉 SPLENDID JOB! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF9100),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Celebratory central graphic
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Expanding background pulse circle for depth
                        Box(
                            modifier = Modifier
                                .size((110 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF9C4).copy(alpha = 0.6f))
                        )

                        // Bouncing gold star symbol
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .offset(y = bounceOffset.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 80.sp
                            )
                        }

                        // Cheering companion avatar overlay at bottom-right corner
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(avatarEmoji, fontSize = 32.sp)
                        }

                        // Scaled indicator of completed character at bottom-left corner
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.BottomStart)
                                .clip(CircleShape)
                                .background(Color(0xFFE1F5FE))
                                .border(2.dp, Color(0xFF03A9F4), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(letter, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Sensational Drawing! 📝✨",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (encouragement != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = encouragement.amharic,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${encouragement.phonetic} • ${encouragement.translation}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "You earned fantastic rewards:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Clean, highly readable card for gems and stars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💎", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+15 Gems",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 24.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+2 Stars",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFFFF9100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onListen,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("celebration_listen_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ድምጹን አድምጥ 🔊",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Button(
                            onClick = onNext,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF66BB6A) // Next green
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("celebration_next_button")
                        ) {
                            Text("ቀጣይ ፊደል 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("celebration_replay_button")
                        ) {
                            Text("በድጋሚ ሳል 🔄", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // High-contrast celebratory star particles floating over the dialog
        StarConfettiEffect(trigger = localConfettiTrigger)
    }
}
}

