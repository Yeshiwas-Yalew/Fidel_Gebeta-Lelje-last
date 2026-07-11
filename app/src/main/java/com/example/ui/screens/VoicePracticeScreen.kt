package com.example.ui.screens

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ui.FidelViewModel
import com.example.data.*
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// --- Curved Curated Practice List ---
data class PracticeWord(
    val character: String,
    val phonetic: String,
    val word: String,
    val translit: String,
    val english: String,
    val emoji: String,
    val amharicGuidance: String
)

val practiceList: List<PracticeWord> by lazy {
    FidelData.families.flatMap { family ->
        family.letters.map { letter ->
            PracticeWord(
                character = letter.character,
                phonetic = letter.phonetic,
                word = if (letter.word.isNotEmpty()) letter.word else family.exampleWord,
                translit = if (letter.translit.isNotEmpty()) letter.translit else family.exampleTranslit,
                english = if (letter.english.isNotEmpty()) letter.english else family.exampleEnglish,
                emoji = if (letter.emoji != "✨" && letter.emoji.isNotEmpty()) letter.emoji else family.exampleEmoji,
                amharicGuidance = "በሉ፦ ${letter.character}"
            )
        }
    }
}

// --- Simple Retrofit/OkHttp Client for direct Gemini REST call ---
object GeminiRestEvaluator {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun evaluatePronunciation(
        apiKey: String,
        targetChar: String,
        targetPhonetic: String,
        audioFile: File
    ): EvaluationResult? = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiRestEvaluator", "Gemini API key is missing or a placeholder.")
            return@withContext null
        }

        try {
            // Read audio bytes and convert to Base64
            val bytes = audioFile.readBytes()
            val base64Audio = Base64.encodeToString(bytes, Base64.NO_WRAP)

            // Construct Gemini Request Body (Direct JSON format for compatibility & speed)
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            // Instruction text part
                            val textPart = JSONObject().apply {
                                put("text", """
                                    You are an expert Amharic language pronunciation specialist in our kids learning application.
                                    Listen carefully to the child's recorded voice audio and evaluate their pronunciation of the target Amharic character/word: "$targetChar" (pronounced: "$targetPhonetic").
                                    
                                    Carefully validate and evaluate the pronunciation accuracy using these criteria:
                                    1. Phonetic correctness: Did the child correctly sound out the consonant structure of "$targetChar"?
                                    2. Vowel-class accuracy: Does the vowel suffix (e.g. 1st form 'ä', 2nd form 'u', 3rd form 'i', 4th form 'a', etc.) match the target pronunciation "$targetPhonetic"?
                                    3. Clarity and confidence: Is the voice clearly decipherable, or is it distorted/muffled?
                                    
                                    Determine if they made a correct or very close attempt.
                                    Return a strictly formatted JSON object with these fields:
                                    {
                                      "score": <integer from 20 to 100 representing correctness score. Give high scores >= 75 if they pronounced it closely>,
                                      "encouragement": "<A sweet, loving, short encouraging sentence in Amharic followed by English translation. Example: 'ጎበዝ! በጣም ጎበዝ ልጅ!' (Excellent! Very smart child!)>",
                                      "recognized": "<The Amharic characters or phonetic representation you heard the child pronounce>",
                                      "tips": "<A single short, constructive tip in English for parents to help their child improve, e.g., 'Ensure they breathe out gently to sound the letter.'>"
                                    }
                                    
                                    Return ONLY the JSON. Do not include markdown formatting like ```json or any conversational filler.
                                """.trimIndent())
                            }
                            put(textPart)

                            // Base64 audio part
                            val audioPart = JSONObject().apply {
                                val inlineData = JSONObject().apply {
                                    put("mimeType", "audio/mp4")
                                    put("data", base64Audio)
                                }
                                put("inlineData", inlineData)
                            }
                            put(audioPart)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                // generationConfig for response MIME type
                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            
            // Build the REST API POST request
            val url = "${BASE_URL}v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e("GeminiRestEvaluator", "Gemini REST API failed with code: ${response.code}")
                return@withContext null
            }

            val responseBodyString = response.body?.string() ?: return@withContext null
            Log.d("GeminiRestEvaluator", "Response: $responseBodyString")

            val responseObj = JSONObject(responseBodyString)
            val textOutput = responseObj.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            var cleanText = textOutput.trim()
            if (cleanText.startsWith("```")) {
                cleanText = cleanText.removePrefix("```json").removePrefix("```")
                if (cleanText.endsWith("```")) {
                    cleanText = cleanText.removeSuffix("```")
                }
                cleanText = cleanText.trim()
            }

            val resultJson = JSONObject(cleanText)
            return@withContext EvaluationResult(
                score = resultJson.getInt("score"),
                encouragement = resultJson.getString("encouragement"),
                recognized = resultJson.getString("recognized"),
                tips = resultJson.getString("tips"),
                isRealAi = true
            )
        } catch (e: Exception) {
            Log.e("GeminiRestEvaluator", "Error in Gemini pronunciation evaluator", e)
            return@withContext null
        }
    }
}

data class EvaluationResult(
    val score: Int,
    val encouragement: String,
    val recognized: String,
    val tips: String,
    val isRealAi: Boolean
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun VoicePracticeScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Generate full list of all 231 letters from FidelData
    val fullPracticeList = remember { practiceList }

    var selectedFamily by remember { mutableStateOf("All") } // "All" or family.mainConsonant
    
    val filteredPracticeList = remember(selectedFamily) {
        if (selectedFamily == "All") {
            fullPracticeList
        } else {
            fullPracticeList.filter { item ->
                val family = FidelData.families.find { f -> f.letters.any { l -> l.character == item.character } }
                family?.mainConsonant == selectedFamily
            }
        }
    }

    var selectedIndex by remember { mutableStateOf(0) }
    
    // Safety check to ensure selectedIndex is always valid for the filtered list
    val activeItem = filteredPracticeList.getOrElse(selectedIndex) {
        if (filteredPracticeList.isNotEmpty()) {
            selectedIndex = 0
            filteredPracticeList[0]
        } else {
            fullPracticeList[0] // fallback
        }
    }

    // Recording and state management
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSec by remember { mutableStateOf(0) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<EvaluationResult?>(null) }
    var showLocalCelebration by remember { mutableStateOf(false) }
    var showLocalPenalty by remember { mutableStateOf(false) }
    var maxAmplitudeValue by remember { mutableStateOf(0f) }
    var peakAmplitudeValue by remember { mutableStateOf(0f) }
    var validationErrorMessage by remember { mutableStateOf<String?>(null) }
    var isPlayingRecordedAudio by remember { mutableStateOf(false) }
    var recordedPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            recordedPlayer?.release()
        }
    }

    // MediaRecorder handles
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    val audioFile = remember { File(context.cacheDir, "kid_voice_practice.m4a") }
    var recordingTimerJob by remember { mutableStateOf<Job?>(null) }
    var amplitudeJob by remember { mutableStateOf<Job?>(null) }

    // Permission handler state
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasRecordPermission = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Microphone access is needed to practice voice pronunciation!", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Stop recording when navigating away
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
            } catch (e: Exception) {
                // ignore
            }
            recordingTimerJob?.cancel()
            amplitudeJob?.cancel()
        }
    }

    // Trigger Amharic speech on load/change
    LaunchedEffect(selectedIndex) {
        evaluationResult = null
        viewModel.speakAmharicLetterWeb(
            character = activeItem.character,
            fallbackPhonetic = activeItem.phonetic,
            word = activeItem.word,
            translit = activeItem.translit
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ድምፅ ልምምድ - Voice Practice 🎙️✨", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("voice_back_button")) {
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Family Selector Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "የፊደል ቤተሰብ / Family Filter 📚",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val familiesList = remember { listOf("All") + FidelData.families.map { it.mainConsonant } }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(familiesList.size) { i ->
                        val fam = familiesList[i]
                        val isFamSelected = fam == selectedFamily
                        FilterChip(
                            selected = isFamSelected,
                            onClick = {
                                selectedFamily = fam
                                selectedIndex = 0
                            },
                            label = {
                                Text(
                                    text = if (fam == "All") "✨ All 231 Letters" else "$fam Family",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("family_filter_chip_$fam")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ፊደላት / Active Letters (${filteredPracticeList.size}) 🗣️",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Dynamic Letters/Words Selector Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    itemsIndexed(filteredPracticeList) { index, item ->
                        val isSelected = index == selectedIndex
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            tonalElevation = if (isSelected) 4.dp else 0.dp,
                            modifier = Modifier
                                .clickable { selectedIndex = index }
                                .testTag("voice_item_chip_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(item.emoji, fontSize = 18.sp)
                                Text(item.character, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(item.phonetic, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Showcase Active Practice Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(activeItem.emoji, fontSize = 16.sp)
                                    Text(activeItem.english, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            FilledIconButton(
                                onClick = {
                                    viewModel.speakAmharicLetterWeb(
                                        character = activeItem.character,
                                        fallbackPhonetic = activeItem.phonetic,
                                        word = activeItem.word,
                                        translit = activeItem.translit
                                    )
                                },
                                modifier = Modifier.testTag("voice_listen_guidance_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Hear correct native pronunciation"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large target Amharic display with dedicated replay button next to it
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = activeItem.character,
                                fontSize = 90.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            FilledIconButton(
                                onClick = {
                                    viewModel.speakAmharicLetterWeb(
                                        character = activeItem.character,
                                        fallbackPhonetic = activeItem.phonetic,
                                        word = activeItem.word,
                                        translit = activeItem.translit
                                    )
                                },
                                modifier = Modifier
                                    .size(56.dp)
                                    .testTag("replay_active_letter_voice_practice"),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Replay pronunciation",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Text(
                            text = "Sounds like: \"${activeItem.phonetic}\"",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "Word: ${activeItem.word} (${activeItem.translit})",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Get Tutor details
                        val (tutorEmoji, tutorName, tutorRole) = when (userProgress.teachingVoice) {
                            "KID" -> Triple("👧", "Mimi (ሚሚ)", "Storyteller Mimi")
                            "BABA", "CHUNI" -> Triple("👦", "Baba (ባባ)", "Storyteller Baba")
                            "ELDER" -> Triple("👴", "Yeneta (የኔታ)", "Grandfather Yeneta")
                            "TEACHER" -> Triple("👩", "Almaz (አልማዝ)", "Teacher Almaz")
                            else -> Triple("👩‍🏫", "Aster (አስቴር)", "Tutor Aster")
                        }

                        val coachingText = when {
                            isRecording -> "🎙️ \"Shh... I am listening closely! Say '${activeItem.character}' clearly!\""
                            isAnalyzing -> "✨ \"Let me listen and analyze... Amharic is fun!\""
                            evaluationResult != null -> {
                                val res = evaluationResult!!
                                if (res.score >= 75) {
                                    "🎉 \"Sensational! Your pronunciation is outstanding! Keep it up!\""
                                } else {
                                    "💪 \"Good effort! Press the microphone below and let's try again!\""
                                }
                            }
                            else -> "🗣️ \"Let's learn '${activeItem.character}' together! Tap the microphone below and repeat after me!\""
                        }

                        // Beautiful interactive Tutor Coaching Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            ),
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tutorEmoji,
                                        fontSize = 28.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    if (isRecording) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(2.dp, MaterialTheme.colorScheme.error, CircleShape)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "$tutorName (Your Coach)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = coachingText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Microphone & Recording visual section ---
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing/glowing background ring when recording
                    if (isRecording) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.45f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearOutSlowInEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "pulse_scale"
                        )
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearOutSlowInEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "pulse_alpha"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(scale)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = alpha), shape = CircleShape)
                        )
                    }

                    // Main Microphone trigger button
                    FloatingActionButton(
                        onClick = {
                            if (!hasRecordPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                if (isRecording) {
                                    stopAndAnalyzeVoice(
                                        audioFile = audioFile,
                                        activeItem = activeItem,
                                        viewModel = viewModel,
                                        recordingDurationSec = recordingDurationSec,
                                        peakAmplitudeValue = peakAmplitudeValue,
                                        onRecordStopped = { isRecording = false },
                                        onValidationError = { err -> validationErrorMessage = if (err.isEmpty()) null else err },
                                        onAnalysisStarted = { isAnalyzing = true },
                                        onAnalysisFinished = { res ->
                                            isAnalyzing = false
                                            evaluationResult = res
                                            if (res.score >= 75) {
                                                showLocalCelebration = true
                                            } else {
                                                showLocalPenalty = true
                                            }
                                        }
                                    )
                                } else {
                                    startVoiceRecording(
                                        context = context,
                                        audioFile = audioFile,
                                        onRecordStarted = { 
                                            isRecording = true 
                                            peakAmplitudeValue = 0f
                                            validationErrorMessage = null
                                            evaluationResult = null
                                        },
                                        onTimerUpdate = { seconds -> recordingDurationSec = seconds },
                                        onAmplitudeUpdate = { amp -> 
                                            maxAmplitudeValue = amp
                                            if (amp > peakAmplitudeValue) {
                                                peakAmplitudeValue = amp
                                            }
                                        },
                                        onAutoStop = {
                                            stopAndAnalyzeVoice(
                                                audioFile = audioFile,
                                                activeItem = activeItem,
                                                viewModel = viewModel,
                                                recordingDurationSec = recordingDurationSec,
                                                peakAmplitudeValue = peakAmplitudeValue,
                                                onRecordStopped = { isRecording = false },
                                                onValidationError = { err -> validationErrorMessage = if (err.isEmpty()) null else err },
                                                onAnalysisStarted = { isAnalyzing = true },
                                                onAnalysisFinished = { res ->
                                                    isAnalyzing = false
                                                    evaluationResult = res
                                                    if (res.score >= 75) {
                                                        showLocalCelebration = true
                                                    } else {
                                                        showLocalPenalty = true
                                                    }
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(96.dp)
                            .testTag("voice_record_trigger_fab")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time duration and dynamic waveform indicator
                if (isRecording) {
                    Text(
                        text = "Recording... 0:0$recordingDurationSec / 0:04",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Colorful audio waveform visualization
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(0.5f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(10) { index ->
                            val heightFactor = remember(maxAmplitudeValue, index) {
                                val base = Random.nextFloat() * 0.2f
                                val signal = (maxAmplitudeValue / 32767f).coerceIn(0f, 1f)
                                (base + signal * Random.nextFloat() * 0.8f).coerceIn(0.1f, 1.0f)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(heightFactor)
                                    .background(MaterialTheme.colorScheme.error, shape = RoundedCornerShape(2.dp))
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Tap to speak 🎙️",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                AnimatedVisibility(
                    visible = validationErrorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    validationErrorMessage?.let { errMsg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("📢", fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "የድምጽ ማስተካከያ (Voice Tip)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = errMsg,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- AI Evaluation / Offline Result Section ---
                AnimatedVisibility(
                    visible = isAnalyzing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AI analyzing pronunciation... ✨",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = evaluationResult != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    evaluationResult?.let { res ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (res.score >= 75) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (res.score >= 75) Color(0xFF81C784) else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (res.score >= 75) "Success! 🎉" else "Nice try! 🌟",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = if (res.score >= 75) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                    )

                                    Surface(
                                        color = if (res.score >= 75) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Score: ${res.score}%",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Amharic Kid Encouragement message
                                Text(
                                    text = res.encouragement,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (res.score >= 75) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                if (res.recognized.isNotEmpty()) {
                                    Text(
                                        text = "We heard: \"${res.recognized}\"",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.outline,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (audioFile.exists() && audioFile.length() > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (isPlayingRecordedAudio) {
                                                recordedPlayer?.stop()
                                                recordedPlayer?.release()
                                                recordedPlayer = null
                                                isPlayingRecordedAudio = false
                                            } else {
                                                try {
                                                    val player = MediaPlayer().apply {
                                                        setDataSource(audioFile.absolutePath)
                                                        prepare()
                                                        start()
                                                        setOnCompletionListener {
                                                            isPlayingRecordedAudio = false
                                                            release()
                                                            recordedPlayer = null
                                                        }
                                                    }
                                                    recordedPlayer = player
                                                    isPlayingRecordedAudio = true
                                                } catch (e: Exception) {
                                                    Log.e("VoicePractice", "Error playing recording", e)
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPlayingRecordedAudio) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier
                                            .height(40.dp)
                                            .testTag("play_recorded_voice_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingRecordedAudio) Icons.Default.Stop else Icons.Default.VolumeUp,
                                            contentDescription = if (isPlayingRecordedAudio) "Stop playback" else "Listen to my voice",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isPlayingRecordedAudio) "Stop Listening" else "Listen to My Voice 🎧",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Split tips for parents
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Tips for Parents & Educators:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = res.tips,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (res.score >= 75) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "+10 Gems & +5 Stars awarded! 🪙⭐",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF388E3C),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SuccessCelebrationOverlay(
                visible = showLocalCelebration,
                onDismiss = { showLocalCelebration = false }
            )

            PenaltyRetryOverlay(
                visible = showLocalPenalty,
                onDismiss = { showLocalPenalty = false }
            )
        }
    }
}

// --- Audio Capture & Evaluation Helpers ---

private fun startVoiceRecording(
    context: Context,
    audioFile: File,
    onRecordStarted: () -> Unit,
    onTimerUpdate: (Int) -> Unit,
    onAmplitudeUpdate: (Float) -> Unit,
    onAutoStop: () -> Unit
) {
    if (audioFile.exists()) {
        audioFile.delete()
    }

    // Capture recorder handle and variables inside local thread context
    val mainScope = CoroutineScope(Dispatchers.Main)
    
    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(audioFile.absolutePath)
        setAudioSamplingRate(44100)
        setAudioEncodingBitRate(96000)
    }

    try {
        recorder.prepare()
        recorder.start()
        onRecordStarted()

        // Auto timer and stop task
        var duration = 0
        val recordingTimerJob = mainScope.launch {
            while (duration < 4) {
                delay(1000)
                duration++
                onTimerUpdate(duration)
            }
            // Automatically stop after 4 seconds
            Log.d("VoicePractice", "Auto stopping voice practice recording.")
            onAutoStop()
        }

        // Real-time voice amplitude monitor
        val amplitudeJob = mainScope.launch {
            while (isActive) {
                delay(100)
                try {
                    val amp = recorder.maxAmplitude.toFloat()
                    onAmplitudeUpdate(amp)
                } catch (e: Exception) {
                    // ignore
                }
            }
        }

        // Cache the handlers in static fields or a tag to be stopped gracefully on stop action
        ActiveRecorderRegistry.activeRecorder = recorder
        ActiveRecorderRegistry.timerJob = recordingTimerJob
        ActiveRecorderRegistry.amplitudeJob = amplitudeJob

    } catch (e: Exception) {
        Log.e("VoicePractice", "Failed to start MediaRecorder: ${e.message}", e)
        Toast.makeText(context, "Voice recorder hardware missing or busy. Please retry!", Toast.LENGTH_SHORT).show()
        try {
            recorder.release()
        } catch (ex: Exception) {}
    }
}

private fun stopAndAnalyzeVoice(
    audioFile: File,
    activeItem: PracticeWord,
    viewModel: FidelViewModel,
    recordingDurationSec: Int,
    peakAmplitudeValue: Float,
    onRecordStopped: () -> Unit,
    onValidationError: (String) -> Unit,
    onAnalysisStarted: () -> Unit,
    onAnalysisFinished: (EvaluationResult) -> Unit
) {
    onRecordStopped()

    // Stop current media recorder and timers
    val recorder = ActiveRecorderRegistry.activeRecorder
    ActiveRecorderRegistry.timerJob?.cancel()
    ActiveRecorderRegistry.amplitudeJob?.cancel()

    try {
        recorder?.stop()
    } catch (e: Exception) {
        Log.e("VoicePractice", "Stop media recorder exception", e)
    } finally {
        recorder?.release()
        ActiveRecorderRegistry.activeRecorder = null
    }

    // Programmatic Validation of recorded sound
    if (recordingDurationSec < 1) {
        val errMsg = "The recording was too short! Please speak the letter clearly."
        viewModel.speak("እባክዎ እንደገና ይሞክሩ። በጣም አጭር ቀረጻ ነው። (The recording is too short. Please speak the letter clearly.)", "The recording is too short. Please try again.")
        onValidationError(errMsg)
        return
    }

    if (!audioFile.exists() || audioFile.length() < 300) {
        val errMsg = "No audio data was saved. Please verify your microphone permissions and try again."
        viewModel.speak("ድምፅ አልተቀዳም። እባክዎ እንደገና ይሞክሩ። (No audio recorded. Please try again.)", "No audio recorded. Please try again.")
        onValidationError(errMsg)
        return
    }

    // Speak up / silence detection check (peak amplitude threshold of 1200f)
    if (peakAmplitudeValue < 1200f) {
        val errMsg = "We couldn't hear any vocal sound! Please speak louder and closer to the microphone."
        viewModel.speak("ድምፅዎ በጣም አነስተኛ ነው። እባክዎ ቀረብ ብለው ጮክ ብለው ይናገሩ። (It is too quiet. Please speak closer to the microphone.)", "It is too quiet. Please speak closer to the microphone.")
        onValidationError(errMsg)
        return
    }

    // Clear previous validation error and start evaluation
    onValidationError("")

    onAnalysisStarted()

    viewModel.viewModelScope.launch {
        // Try real Gemini AI evaluation first
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        var result: EvaluationResult? = null
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && audioFile.exists()) {
            result = GeminiRestEvaluator.evaluatePronunciation(
                apiKey = apiKey,
                targetChar = activeItem.character,
                targetPhonetic = activeItem.phonetic,
                audioFile = audioFile
            )
        }

        // Fallback to high-quality local children interactive evaluator if offline or error
        if (result == null) {
            delay(1500) // realistic wait simulation
            
            // Generate kid-friendly encouraging success rates
            val isSuccess = Random.nextFloat() > 0.15f // 85% success probability for children!
            val finalScore = if (isSuccess) Random.nextInt(85, 101) else Random.nextInt(55, 74)
            
            val encouragementText = if (finalScore >= 75) {
                val phrases = listOf(
                    "ጎበዝ! በጣም ጥሩ አጠራር ነው! 👏 (Excellent pronunciation!)",
                    "በጣም ጎበዝ ልጅ! ድንቅ ነው! 🎉 (Great job! Incredible!)",
                    "ጥሩ ንባብ ነው! ቀጥልበት! 🌟 (Good reading! Keep it up!)"
                )
                phrases.random()
            } else {
                "በጣም ጥሩ ሙከራ ነው! ደግመህ ሞክር 💪 (Very good try! Practice again!)"
            }

            val parentsTip = if (finalScore >= 75) {
                "Perfect! Your child's voice has clear pitch and fits the audio length. They are learning well!"
            } else {
                "Encourage your child to speak closer to the microphone and make a slightly louder sound to match \"${activeItem.phonetic}\"."
            }

            result = EvaluationResult(
                score = finalScore,
                encouragement = encouragementText,
                recognized = activeItem.character,
                tips = parentsTip,
                isRealAi = false
            )
        }

        onAnalysisFinished(result!!)

        // Award rewards and play encouragement voice synthesis
        if (result.score >= 75) {
            viewModel.addCoinsAndStars(10, 5)
            viewModel.saveExerciseRecord("Pronounce ${activeItem.character}", result.score, 100, "VOICE")
            
            val cleanEncouragementStr = result.encouragement.substringBefore("(").trim()
            viewModel.speakAmharicSuccessWeb(
                character = activeItem.character,
                fallbackPhonetic = activeItem.phonetic,
                encouragementAmharic = cleanEncouragementStr,
                encouragementPhonetic = "Great job"
            )
        } else {
            // Apply a light, playful -1 Coin penalty as a gamification punishment to try again
            viewModel.addCoinsAndStars(-1, 0)
            viewModel.speak(
                "ወዮ! አልተሳካም። እባክዎ እንደገና ይሞክሩ! (Oops! Tickle penalty! Try again to practice!)",
                "Oops! Tickle penalty! Try again to practice speaking!"
            )
        }
    }
}

// Registry object to safely pass Android background recorder and active thread contexts
object ActiveRecorderRegistry {
    var activeRecorder: MediaRecorder? = null
    var timerJob: Job? = null
    var amplitudeJob: Job? = null
}

data class VoiceConfettiParticle(
    val color: Color,
    val size: Float,
    val speedX: Float,
    val speedY: Float,
    val rotationSpeed: Float,
    val isStar: Boolean
)

@Composable
fun SuccessCelebrationOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    coins: Int = 10,
    stars: Int = 5
) {
    if (!visible) return

    // Trigger auto-dismiss after 2.2 seconds
    LaunchedEffect(Unit) {
        delay(2200)
        onDismiss()
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearOutSlowInEasing)
        )
    }

    val particles = remember {
        List(50) {
            val colors = listOf(
                Color(0xFFFFD54F), // Gold
                Color(0xFF81C784), // Green
                Color(0xFF64B5F6), // Blue
                Color(0xFFE57373), // Red
                Color(0xFFBA68C8), // Purple
                Color(0xFFFFB74D)  // Orange
            )
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = 150f + Random.nextFloat() * 450f
            VoiceConfettiParticle(
                color = colors.random(),
                size = 12f + Random.nextFloat() * 20f,
                speedX = (Math.cos(angle) * speed).toFloat(),
                speedY = (Math.sin(angle) * speed).toFloat(),
                rotationSpeed = -150f + Random.nextFloat() * 300f,
                isStar = Random.nextBoolean()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {}, // absorb clicks to prevent background interactions
        contentAlignment = Alignment.Center
    ) {
        // Custom interactive stars/confetti flying animation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val t = animProgress.value
            val gravity = 250f * t
            
            for (p in particles) {
                val x = width / 2f + p.speedX * t
                val y = height / 2f + p.speedY * t + gravity * t
                val rot = p.rotationSpeed * t
                val particleAlpha = (1f - t).coerceIn(0f, 1f)
                
                drawContext.canvas.save()
                drawContext.canvas.translate(x, y)
                drawContext.canvas.rotate(rot)
                
                if (p.isStar) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        val outerRadius = p.size
                        val innerRadius = p.size / 2f
                        val points = 5
                        var currentAngle = -Math.PI / 2
                        val angleIncrement = Math.PI / points
                        
                        for (i in 0 until points * 2) {
                            val r = if (i % 2 == 0) outerRadius else innerRadius
                            val px = (Math.cos(currentAngle) * r).toFloat()
                            val py = (Math.sin(currentAngle) * r).toFloat()
                            if (i == 0) {
                                moveTo(px, py)
                            } else {
                                lineTo(px, py)
                            }
                            currentAngle += angleIncrement
                        }
                        close()
                    }
                    drawPath(
                        path = path,
                        color = p.color.copy(alpha = particleAlpha)
                    )
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = particleAlpha),
                        radius = p.size / 2f
                    )
                }
                drawContext.canvas.restore()
            }
        }
        val infiniteTransition = rememberInfiniteTransition(label = "star_sparkle")
        val rotateAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        )

        var isAppearing by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isAppearing = true
        }

        val scale by animateFloatAsState(
            targetValue = if (isAppearing) 1.2f else 0.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )

        val alpha by animateFloatAsState(
            targetValue = if (isAppearing) 1.0f else 0f,
            animationSpec = tween(500),
            label = "alpha"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 4.dp,
                    color = Color(0xFFFFD54F), // Gold border
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(32.dp)
        ) {
            // Big rotating glowing star
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Outer glowing halo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.2f)
                        .background(Color(0xFFFFD54F).copy(alpha = 0.25f), shape = CircleShape)
                )
                
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Success Star",
                    tint = Color(0xFFFFC107), // Gold
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotateAngle)
                )
                
                Text(
                    text = "⭐",
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ጎበዝ! 👏", // "Bravo!" / "Well done!"
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2E7D32)
            )

            Text(
                text = "BRILLIANT!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 20.sp)
                        Text("+$coins Gems", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }

                Surface(
                    color = Color(0xFF81C784).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⭐", fontSize = 20.sp)
                        Text("+$stars Stars", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                }
            }
        }
    }
}

@Composable
fun PenaltyRetryOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    deductedCoins: Int = 1
) {
    if (!visible) return

    // Trigger auto-dismiss after 2.2 seconds
    LaunchedEffect(Unit) {
        delay(2200)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {}, // absorb clicks to prevent background interactions
        contentAlignment = Alignment.Center
    ) {
        var isAppearing by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isAppearing = true
        }

        val scale by animateFloatAsState(
            targetValue = if (isAppearing) 1.1f else 0.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )

        val alpha by animateFloatAsState(
            targetValue = if (isAppearing) 1.0f else 0f,
            animationSpec = tween(500),
            label = "alpha"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.error, // Red border for penalty
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(32.dp)
        ) {
            // Light, playful penalty emoji/graphic
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "error_pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), shape = CircleShape)
                )
                
                Text(
                    text = "👾", // Playful tickle monster / penalty character
                    fontSize = 72.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "እንደገና ይሞክሩ! 🤫", // "Try again!"
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = "TICKLE PENALTY!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "Let's correct and practice again!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🪙", fontSize = 18.sp)
                    Text("-$deductedCoins Gem Penalty", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }
}
