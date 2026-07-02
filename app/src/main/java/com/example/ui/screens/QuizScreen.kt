package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FidelViewModel

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctAnswer: String,
    val speechPrompt: String,
    val correctReplySpeech: String
)

fun mapOrderToAmharic(orderName: String): String {
    return when (orderName) {
        "Ge'ez" -> "ግዕዝ (1ኛ)"
        "Ka'ib" -> "ካዕብ (2ኛ)"
        "Salis" -> "ሳልስ (3ኛ)"
        "Rabi'" -> "ራብዕ (4ኛ)"
        "Hamis" -> "ኃምስ (5ኛ)"
        "Sadis" -> "ሳድስ (6ኛ)"
        "Sab'i" -> "ሳብዕ (7ኛ)"
        else -> orderName
    }
}

fun generateVarietyOfQuestions(families: List<FidelFamily>): List<QuizQuestion> {
    val list = mutableListOf<QuizQuestion>()

    // Variety 1: Sound matching for letters across all families (up to 34 * 7 = 238 items)
    families.forEachIndexed { famIndex, family ->
        val letters = family.letters
        letters.forEachIndexed { orderIdx, letter ->
            val correctLetter = letter.character
            val wrongOptions = (letters.filter { it.character != correctLetter }.map { it.character } + 
                                families[(famIndex + 1) % families.size].letters.map { it.character })
                                .distinct()
                                .shuffled()
                                .take(3)
            val options = (wrongOptions + correctLetter).shuffled()
            val orderAmh = mapOrderToAmharic(letter.orderName)
            list.add(
                QuizQuestion(
                    prompt = "በ '${family.mainConsonant}' (የ${family.familyName}) መደብ ውስጥ '${letter.phonetic}' (${orderAmh}) የሚባለውን ድምፅ የሚወክለውን ፊደል ይምረጡ።",
                    options = options,
                    correctAnswer = correctLetter,
                    speechPrompt = "Find the letter representing the vowel sound ${letter.ttsPhonetic}",
                    correctReplySpeech = "Phenomenal! You selected the correct letter!"
                )
            )
        }
    }

    // Variety 2: Vocabulary starting letters for all families (34 items)
    families.forEach { family ->
        val correctMsg = "Excellent! You matched the starting letter!"
        val correctLetter = family.mainConsonant
        val wrongOptions = families.filter { it.mainConsonant != correctLetter }
            .map { it.mainConsonant }
            .shuffled()
            .take(3)
        val options = (wrongOptions + correctLetter).shuffled()
        list.add(
            QuizQuestion(
                prompt = "'${family.exampleWord}' ${family.exampleEmoji} (${family.exampleEnglish}) የሚለው የአማርኛ ቃል በየትኛው መሪ (ዋና) ፊደል ይጀምራል?",
                options = options,
                correctAnswer = correctLetter,
                speechPrompt = "What letter does ${family.exampleTranslit} start with?",
                correctReplySpeech = correctMsg
            )
        )
    }

    // Variety 3: Letter to Phonetic pronunciation sound for all letters (34 * 7 = 238 items)
    families.forEach { family ->
        family.letters.forEach { letter ->
            val correctPhonetic = letter.phonetic
            val wrongOptions = family.letters.filter { it.phonetic != correctPhonetic }
                .map { it.phonetic }
                .shuffled()
                .take(3)
            val options = (wrongOptions + correctPhonetic).shuffled()
            list.add(
                QuizQuestion(
                    prompt = "የአማርኛ ፊደል '${letter.character}' ትክክለኛው የድምፅ አጠራር (ንባብ) የትኛው ነው?",
                    options = options,
                    correctAnswer = correctPhonetic,
                    speechPrompt = "What sound is produced by the character on screen?",
                    correctReplySpeech = "Fantastic! It is pronounced as ${letter.ttsPhonetic}!"
                )
            )
        }
    }

    // Variety 4: English dynamic matching for words of all families (34 items)
    families.forEach { family ->
        val correctVal = "${family.exampleEmoji} ${family.exampleEnglish}"
        val wrongOptions = families.filter { it.mainConsonant != family.mainConsonant }
            .map { "${it.exampleEmoji} ${it.exampleEnglish}" }
            .distinct()
            .shuffled()
            .take(3)
        val options = (wrongOptions + correctVal).shuffled()
        list.add(
            QuizQuestion(
                prompt = "'${family.exampleWord}' (${family.exampleTranslit}) ለሚለው የአማርኛ ቃል ትክክለኛው የእንግሊዝኛ ትርጉም እና ምስል የትኛው ነው?",
                options = options,
                correctAnswer = correctVal,
                speechPrompt = "Select the correct translation and meaning",
                correctReplySpeech = "Incredible vocabulary skills!"
            )
        )
    }

    // Variety 5: Order naming identification for all letters (34 * 7 = 238 items)
    families.forEach { family ->
        family.letters.forEach { letter ->
            val correctAnswer = mapOrderToAmharic(letter.orderName)
            val optionsList = listOf("ግዕዝ (1ኛ)", "ካዕብ (2ኛ)", "ሳልስ (3ኛ)", "ራብዕ (4ኛ)", "ኃምስ (5ኛ)", "ሳድስ (6ኛ)", "ሳብዕ (7ኛ)")
            val options = optionsList.shuffled().take(4)
            val finalOptions = if (correctAnswer in options) options else (options.take(3) + correctAnswer).shuffled()
            list.add(
                QuizQuestion(
                    prompt = "'${letter.character}' የሚለው ፊደል በየትኛው የሰዋስው መደብ (ንባብ ክፍል) ውስጥ ይገኛል?",
                    options = finalOptions,
                    correctAnswer = correctAnswer,
                    speechPrompt = "Which order does the letter belong to?",
                    correctReplySpeech = "Smart! That belongs to the ${letter.orderName} order."
                )
            )
        }
    }

    // Variety 6: Identify fifth order 'Hamis' and seventh order 'Sab'i' for all families (34 * 2 = 68 items)
    families.forEach { family ->
        if (family.letters.size >= 5) {
            val correctLetter = family.letters[4] // Hamis (5th order)
            val options = family.letters.map { it.character }.shuffled().take(4)
            val finalOptions = if (correctLetter.character in options) options else (options.take(3) + correctLetter.character).shuffled()
            list.add(
                QuizQuestion(
                    prompt = "በ '${family.mainConsonant}' ቤት ውስጥ ኃምስ (5ኛው መደብ/ባለ 'ኤ' ዜማ) የሆነው ፊደል የትኛው ነው?",
                    options = finalOptions,
                    correctAnswer = correctLetter.character,
                    speechPrompt = "Select the fifth order Hamis letter of the ${family.mainConsonant} family",
                    correctReplySpeech = "Great work! You found ${correctLetter.character}!"
                )
            )
        }
        if (family.letters.size >= 7) {
            val correctLetter = family.letters[6] // Sab'i (7th order)
            val options = family.letters.map { it.character }.shuffled().take(4)
            val finalOptions = if (correctLetter.character in options) options else (options.take(3) + correctLetter.character).shuffled()
            list.add(
                QuizQuestion(
                    prompt = "በ '${family.mainConsonant}' ቤት ውስጥ ሳብዕ (7ኛው መደብ/ባለ 'ኦ' ዜማ) የሆነው ፊደል የትኛው ነው?",
                    options = finalOptions,
                    correctAnswer = correctLetter.character,
                    speechPrompt = "Select the seventh order Sab'i letter of the ${family.mainConsonant} family",
                    correctReplySpeech = "Great work! You found ${correctLetter.character}!"
                )
            )
        }
    }

    return list
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    // Current quiz state variables
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }

    val isExpert = userProgress.difficultyMode == "EXPERT"
    val maxQuestionsCount = if (isExpert) 35 else 8

    // Generate static quiz pulling questions based on difficulty mode
    val quizQuestions = remember(isExpert) {
        val pool = generateVarietyOfQuestions(FidelData.families)
        pool.shuffled().take(maxQuestionsCount)
    }

    val currentQuestion = quizQuestions[currentQuestionIndex]

    // Speech trigger on new question load
    LaunchedEffect(currentQuestionIndex) {
        if (!quizCompleted) {
            viewModel.speak(currentQuestion.prompt, currentQuestion.speechPrompt)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("የፊደል ፈተና 🎮", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("quiz_back_button")) {
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
                            tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            if (!quizCompleted) {
                // ACTIVE QUIZ ROUND SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Question index bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ጥያቄ ${currentQuestionIndex + 1} ከ ${quizQuestions.size}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "ውጤት፦ $score ⭐️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = (currentQuestionIndex + 1) / quizQuestions.size.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Big Question prompt card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = currentQuestion.prompt,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Multiple Choice Button list
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        currentQuestion.options.forEach { option ->
                            val isSelected = selectedAnswer == option
                            val isCorrect = option == currentQuestion.correctAnswer

                            val containerColor = when {
                                selectedAnswer != null && isCorrect -> Color(0xFFC8E6C9) // Green for correct
                                isSelected && !isCorrect -> Color(0xFFFFCDD2) // Red for selected incorrect
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val contentColor = when {
                                selectedAnswer != null && isCorrect -> Color(0xFF2E7D32)
                                isSelected && !isCorrect -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .clickable(enabled = selectedAnswer == null) {
                                        selectedAnswer = option
                                        if (isCorrect) {
                                            score++
                                            viewModel.triggerConfetti()
                                            viewModel.speak(
                                                currentQuestion.correctReplySpeech,
                                                currentQuestion.correctReplySpeech
                                            )
                                        } else {
                                            viewModel.speak(
                                                "Nice try! Correct was ${currentQuestion.correctAnswer}",
                                                "Nice try"
                                            )
                                        }
                                    }
                                    .testTag("quiz_option_$option"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )

                                    if (selectedAnswer != null) {
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = "Correct",
                                                tint = Color(0xFF2E7D32)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Incorrect",
                                                tint = Color(0xFFC62828)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Next/Finish Action bar button
                    AnimatedVisibility(visible = selectedAnswer != null) {
                        Button(
                            onClick = {
                                if (currentQuestionIndex < quizQuestions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswer = null
                                } else {
                                    quizCompleted = true
                                    viewModel.saveExerciseRecord("Fidel Master Quiz", score, quizQuestions.size, "QUIZ")
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(54.dp)
                                .testTag("quiz_next_button")
                        ) {
                            Text(
                                text = if (currentQuestionIndex < quizQuestions.size - 1) "ቀጣይ ጥያቄ ➡️" else "ውጤቱን ይመልከቱ! 🏆",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                             )
                        }
                    }
                }
            } else {
                // QUIZ COMPLETE CARD / REPORT SUMMARY - High gamified feedback
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = "Gamified rewards celebration logo",
                        tint = Color(0xFFFBC02D),
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ፈተናው ተጠናቋል! 🎉",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "የእርስዎ ውጤት፦",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$score / ${quizQuestions.size}",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Stars reward badge",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "+${score * 10} ሳንቲሞችን አግኝተዋል! 🪙",
                                color = Color(0xFF43A047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            currentQuestionIndex = 0
                            selectedAnswer = null
                            score = 0
                            quizCompleted = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.81f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "እንደገና ይጫወቱ! 🔄",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.81f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "ወደ ካርታው ይመለሱ 🗺️",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
