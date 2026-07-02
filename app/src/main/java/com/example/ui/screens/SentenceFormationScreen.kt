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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.EmojiEvents
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
import kotlinx.coroutines.launch

data class SentenceWordTile(
    val id: Int,
    val text: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SentenceFormationScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    val sentences = remember {
        SentenceData.sentences.shuffled().take(5)
    }

    var currentSentenceIndex by remember { mutableStateOf(0) }
    var gameCompleted by remember { mutableStateOf(false) }

    val currentSentence = sentences[currentSentenceIndex]

    // Scrambled pool tiles initialized for each sentence
    var wordPoolTiles by remember(currentSentenceIndex) {
        val tiles = currentSentence.scrambledWords.shuffled().mapIndexed { idx, txt ->
            SentenceWordTile(idx, txt)
        }
        mutableStateOf(tiles)
    }

    // Slots filled by the user
    val assembledTiles = remember(currentSentenceIndex) { mutableStateListOf<SentenceWordTile>() }

    var checkedAnswer by remember { mutableStateOf(false) }
    var resultIsCorrect by remember { mutableStateOf(false) }

    var localConfettiTrigger by remember { mutableStateOf(0) }

    // Dynamic scale trigger for answer response card
    val scaleAnim = animateFloatAsState(
        targetValue = if (checkedAnswer) 1.0f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = "Sentence Builder 🧩",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.speak("Going back to main map", "Back")
                            onBack()
                        },
                        modifier = Modifier.testTag("sentence_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to map"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!gameCompleted) {
                // Game View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Indicator Info Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sentence ${currentSentenceIndex + 1} of ${sentences.size}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+10 Stars",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9100),
                                fontSize = 14.sp
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (currentSentenceIndex + 1).toFloat() / sentences.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // English Target Box with audio pronunciation speaker guide
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Put words in correct order to translate:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentSentence.english,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Phonetic: ${currentSentence.translit}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Normal
                            )

                            Button(
                                onClick = {
                                    viewModel.speakAmharicLetterWeb(
                                        currentSentence.finalSentence,
                                        currentSentence.translit
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.testTag("sentence_speak_guide_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Sentence Pronunciation"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("መመሪያውን አድምጥ 🔊")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Assembled slots row
                    Text(
                        text = "Your Assembled Sentence Area:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 86.dp)
                            .border(
                                width = 2.dp,
                                color = if (checkedAnswer) {
                                    if (resultIsCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (assembledTiles.isEmpty()) {
                            Text(
                                text = "👉 Tap the Amharic word bubbles below in order! 👈",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // FlowRow layout to arrange slots fluidly across lines
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                maxItemsInEachRow = Int.MAX_VALUE
                            ) {
                                assembledTiles.forEach { tile ->
                                    Card(
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .clickable(enabled = !checkedAnswer) {
                                                // Speak word audio quickly when removed
                                                viewModel.speakAmharicLetterWeb(tile.text, "")
                                                assembledTiles.remove(tile)
                                                wordPoolTiles = wordPoolTiles + tile
                                            }
                                            .testTag("assembled_word_${tile.text}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tile.text,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scrambled pool tiles
                    Text(
                        text = "Amharic Word Tiles:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        wordPoolTiles.forEach { tile ->
                            Card(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .clickable(enabled = !checkedAnswer) {
                                        // Speak Amharic word sound instantly!
                                        viewModel.speakAmharicLetterWeb(tile.text, "")
                                        assembledTiles.add(tile)
                                        wordPoolTiles = wordPoolTiles.filter { it.id != tile.id }
                                    }
                                    .testTag("pool_word_${tile.text}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Text(
                                    text = tile.text,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Buttons
                    if (!checkedAnswer) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Reset words
                                    assembledTiles.clear()
                                    wordPoolTiles = currentSentence.scrambledWords.shuffled().mapIndexed { idx, txt ->
                                        SentenceWordTile(idx, txt)
                                    }
                                    viewModel.speak("Cleared words", "Clear")
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("sentence_clear_button"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Selection")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ሁሉንም አጥፋ", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val userWords = assembledTiles.map { it.text }
                                    val correctWords = currentSentence.correctWords
                                    resultIsCorrect = (userWords == correctWords)
                                    checkedAnswer = true

                                    if (resultIsCorrect) {
                                        viewModel.speak("Excellent sequence matched!", "That is correct!")
                                        viewModel.speakAmharicLetterWeb(currentSentence.finalSentence, "")
                                        viewModel.addCoinsAndStars(15, 10)
                                        localConfettiTrigger += 1
                                    } else {
                                        viewModel.speak("Let's try again! Check your sentence order.", "Check the letters order!")
                                    }
                                },
                                enabled = assembledTiles.isNotEmpty(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(52.dp)
                                    .testTag("sentence_check_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Done, contentDescription = "Check alignment")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("መልስህን አረጋግጥ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    } else {
                        // Answer notification details response
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scaleAnim.value)
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = if (resultIsCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = BorderStroke(2.dp, if (resultIsCorrect) Color(0xFF4CAF50) else Color(0xFFE57373))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (resultIsCorrect) Icons.Default.Celebration else Icons.Default.Close,
                                        contentDescription = "Result Status Icon",
                                        tint = if (resultIsCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (resultIsCorrect) "BINGO! ባክህ! 🎉" else "Almost there! ድጋሚ ሞክር 😅",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (resultIsCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }

                                Text(
                                    text = currentSentence.explanation,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    color = if (resultIsCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (!resultIsCorrect) {
                                        Button(
                                            onClick = {
                                                checkedAnswer = false
                                                assembledTiles.clear()
                                                wordPoolTiles = currentSentence.scrambledWords.shuffled().mapIndexed { idx, txt ->
                                                    SentenceWordTile(idx, txt)
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFEF9A9A),
                                                contentColor = Color(0xFF7F0000)
                                            ),
                                            modifier = Modifier.testTag("sentence_try_again_button")
                                        ) {
                                            Text("እንደገና ሞክር")
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            checkedAnswer = false
                                            if (currentSentenceIndex < sentences.size - 1) {
                                                currentSentenceIndex += 1
                                            } else {
                                                gameCompleted = true
                                                viewModel.saveExerciseRecord(
                                                    name = "Amharic Sentence Builder",
                                                    score = 5,
                                                    total = 5,
                                                    type = "QUIZ"
                                                )
                                                viewModel.speak("Hooray! You completed all Amharic sentences! Awesome!", "Campaign Completed!")
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (resultIsCorrect) Color(0xFF81C784) else MaterialTheme.colorScheme.secondary,
                                            contentColor = if (resultIsCorrect) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSecondary
                                        ),
                                        modifier = Modifier.testTag("sentence_next_button")
                                    ) {
                                        Text(text = if (currentSentenceIndex < sentences.size - 1) "ቀጣይ ዓረፍተ ነገር" else "ጨርስ")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Celebration / Finished Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Gold cup trophy",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(130.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sentence Master! 👑",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "ፊደላትን አቀናጅተህ ማረፍያ ዓረፍተ ነገር ሰራህ! 🎉",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )

                    Text(
                        text = "You formed full Amharic sentences and learned subject, object, and verb alignment guides like a champ!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Your Round Rewards:",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💎", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+75 Gems", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+50 Stars", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.speak("Let's play again!", "Excellent round completion")
                            onBack()
                        },
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                            .testTag("sentence_finish_return_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("ወደ ጀብዱ ካርታ ይመለሱ", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }

            // High brightness starry celebration particle trigger
            StarConfettiEffect(trigger = localConfettiTrigger)
        }
    }
}
