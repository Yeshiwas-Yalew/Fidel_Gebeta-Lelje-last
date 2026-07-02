package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FidelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SoundGameQuestion(
    val correctLetter: AmharicLetter,
    val options: List<AmharicLetter>
)

fun generateSoundQuestions(): List<SoundGameQuestion> {
    val allLetters = FidelData.families.flatMap { it.letters }.distinctBy { it.character }
    if (allLetters.isEmpty()) return emptyList()

    return List(5) {
        val corr = allLetters.random()
        val extraWrong = allLetters.filter { it.character != corr.character }
            .shuffled()
            .take(3)
        SoundGameQuestion(
            correctLetter = corr,
            options = (extraWrong + corr).shuffled()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundMatchingScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf(generateSoundQuestions()) }
    var currentRoundIndex by remember { mutableStateOf(0) }
    var selectedLetter by remember { mutableStateOf<AmharicLetter?>(null) }
    var score by remember { mutableStateOf(0) }
    var gameCompleted by remember { mutableStateOf(false) }
    
    // Animation pulse for play speaker button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_audio")
    val speakerPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaker_pulse"
    )

    // Current target and options
    val currentQuestion = if (questions.isNotEmpty() && currentRoundIndex < questions.size) {
        questions[currentRoundIndex]
    } else {
        null
    }

    // Auto-play sound immediately on round load
    LaunchedEffect(currentRoundIndex, currentQuestion) {
        if (currentQuestion != null && !gameCompleted) {
            delay(400) // slight entry buffer
            viewModel.speakAmharicLetterWeb(
                currentQuestion.correctLetter.character,
                currentQuestion.correctLetter.ttsPhonetic
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("ድምፅ አዛምድ! 🎮", fontWeight = FontWeight.Bold, fontSize = 20.sp) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("sound_match_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val isMuted = !userProgress.textToSpeechEnabled
                    IconButton(
                        onClick = { viewModel.toggleTtsEnabled() },
                        modifier = Modifier.testTag("sound_match_mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Volume Muted" else "Volume On",
                            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!gameCompleted && currentQuestion != null) {
                // ACTIVE MINI-GAME BOARD
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header progress and score indicator
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ዙር ${currentRoundIndex + 1} ከ ${questions.size}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ውጤት፦ $score",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFB300)
                            )
                        }
                    }

                    // Rounded Progress tracker indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(questions.size) { index ->
                            val color = when {
                                index == currentRoundIndex -> MaterialTheme.colorScheme.primary
                                index < currentRoundIndex -> Color(0xFF81C784) // green
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cozy speech box illustration or speaker button for target sound
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "አድምጠህ ትክክለኛውን ፊደል ምረጥ! 👂",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the speaker to play the sound anytime!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Large pulsing Speaker Action Button
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .scale(if (selectedLetter == null) speakerPulseScale else 1.0f)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.speakAmharicLetterWeb(
                                            currentQuestion.correctLetter.character,
                                            currentQuestion.correctLetter.ttsPhonetic
                                        )
                                    }
                                    .testTag("sound_match_speaker_play"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Re-play sound prompt",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2x2 grid of selection option cards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val pairs = currentQuestion.options.chunked(2)
                        pairs.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                pair.forEach { option ->
                                    val isSelected = selectedLetter == option
                                    val isCorrect = option.character == currentQuestion.correctLetter.character
                                    
                                    val isShowingResults = selectedLetter != null

                                    val cardColor = when {
                                        isShowingResults && isCorrect -> Color(0xFFC8E6C9) // clear pastel green
                                        isSelected && !isCorrect -> Color(0xFFFFCDD2) // pastel red
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    }

                                    val textColor = when {
                                        isShowingResults && isCorrect -> Color(0xFF1B5E20)
                                        isSelected && !isCorrect -> Color(0xFFB71C1C)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }

                                    val borderStroke = when {
                                        isShowingResults && isCorrect -> BorderStroke(2.dp, Color(0xFF4CAF50))
                                        isSelected && !isCorrect -> BorderStroke(2.dp, Color(0xFFF44336))
                                        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                    }

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(100.dp)
                                            .clickable(enabled = !isShowingResults) {
                                                selectedLetter = option
                                                if (isCorrect) {
                                                    score++
                                                    viewModel.triggerConfetti()
                                                    viewModel.speak(
                                                        "Fantastic! You matched the pronunciation!",
                                                        "Fantastic pronunciation match"
                                                    )
                                                } else {
                                                    viewModel.speak(
                                                        "Great try! Correct was ${currentQuestion.correctLetter.character}",
                                                        "Great try"
                                                    )
                                                }
                                            }
                                            .testTag("sound_match_option_${option.character}"),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardColor),
                                        border = borderStroke
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = option.character,
                                                    fontSize = 42.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = textColor
                                                )
                                                
                                                // Dynamic phonetic subtitle when results are shown
                                                if (isShowingResults) {
                                                    Text(
                                                        text = "\"${option.phonetic}\"",
                                                        fontSize = 11.sp,
                                                        color = textColor.copy(alpha = 0.8f),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            // Small status indicator badges
                                            if (isShowingResults) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(8.dp)
                                                ) {
                                                    if (isCorrect) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Correct Badge",
                                                            tint = Color(0xFF2E7D32),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    } else if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Cancel,
                                                            contentDescription = "Incorrect Badge",
                                                            tint = Color(0xFFC62828),
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue navigation ribbon
                    AnimatedVisibility(visible = selectedLetter != null) {
                        Button(
                            onClick = {
                                if (currentRoundIndex < questions.size - 1) {
                                    currentRoundIndex++
                                    selectedLetter = null
                                } else {
                                    gameCompleted = true
                                    viewModel.saveExerciseRecord(
                                        name = "Fidel Sound Matcher Game",
                                        score = score,
                                        total = questions.size,
                                        type = "GAME_SOUND"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(54.dp)
                                .testTag("sound_match_next_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (currentRoundIndex < questions.size - 1) "ቀጣይ ዙር ➡️" else "ውጤቱን አሳይ! 🏆",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // GAME COMPLETED SCOREBOARD & HIGH CELEBRATION!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = "Success celebration logo",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "በጣም ጎበዝ! 🎉",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "You are a Sound Matching Master!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "የዙር ውጤት (Total Score)",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$score / ${questions.size}",
                                    fontSize = 54.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Glowing Star Badge",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Coin bonus summary!
                            val bonusStars = if (score == questions.size) 5 else 2
                            Text(
                                text = "+${score * 10} extra coins 🪙 & +$bonusStars stars! ⭐",
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Buttons
                    Button(
                        onClick = {
                            questions = generateSoundQuestions()
                            currentRoundIndex = 0
                            selectedLetter = null
                            score = 0
                            gameCompleted = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "እንደገና ይጫወቱ! 🔄 Play Again",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(52.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "ወደ ማርታው ተመለስ 🗺️ Go to Map",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
