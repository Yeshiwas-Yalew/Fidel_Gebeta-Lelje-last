package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.FidelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentScreen(
    viewModel: FidelViewModel,
    progress: UserProgress,
    records: List<ExerciseRecord>,
    onBack: () -> Unit
) {
    var isVerified by remember { mutableStateOf(false) }
    var pinEntry by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (!isVerified) {
        // Simple secure parental PIN entry screen (Child-gate)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Parent lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Parental Verification Gate",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )

                    Text(
                        text = "Enter your Parent PIN to view progress reports and settings (Default PIN: 1234)",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pinEntry,
                        onValueChange = {
                            pinEntry = it
                            errorMessage = ""
                        },
                        label = { Text("Parent PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_pin_input")
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ተመለስ")
                        }

                        Button(
                            onClick = {
                                if (pinEntry == progress.parentPin || (pinEntry == "1234" && progress.parentPin.isEmpty())) {
                                    isVerified = true
                                    viewModel.speak("PIN authenticated", "PIN authenticated")
                                } else {
                                    errorMessage = "Invalid PIN! Please try again."
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("validate_pin_button")
                        ) {
                            Text("አረጋግጥ")
                        }
                    }
                }
            }
        }
    } else {
        // ENTIRE PARENTAL REPORT & SETTINGS HUB
        val sessions by viewModel.learningSessions.collectAsState()
        var newPin by remember { mutableStateOf(progress.parentPin) }
        val scrollState = rememberScrollState()

        var showWeeklyReportOverlay by remember { mutableStateOf(false) }

        val letterInfo = remember {
            FidelData.families.flatMap { it.letters }.associateBy { it.character }
        }

        val letterToFamilyMap = remember {
            val map = mutableMapOf<String, FidelFamily>()
            FidelData.families.forEach { family ->
                family.letters.forEach { letter ->
                    map[letter.character] = family
                }
            }
            map
        }

        val weeklyRecords = remember(records) {
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
            val last7Days = records.filter { it.timestamp >= sevenDaysAgo }
            if (last7Days.isNotEmpty()) last7Days else records
        }

        val characterInsights = remember(weeklyRecords, letterInfo, letterToFamilyMap) {
            val charRecords = mutableMapOf<String, MutableList<ExerciseRecord>>()
            weeklyRecords.forEach { record ->
                val chars = record.exerciseName.filter { it.code in 0x1200..0x137F }.map { it.toString() }
                chars.forEach { char ->
                    charRecords.getOrPut(char) { mutableListOf() }.add(record)
                }
            }
            charRecords.map { (char, recordsForChar) ->
                val practiceCount = recordsForChar.size
                val avgAccuracy = recordsForChar.map { 
                    if (it.totalQuestions > 0) it.score.toFloat() / it.totalQuestions else 1f
                }.average().toFloat()
                val lastPracticed = recordsForChar.maxOf { it.timestamp }
                val letter = letterInfo[char]
                val family = letterToFamilyMap[char]
                CharacterInsight(
                    character = char,
                    phonetic = letter?.phonetic ?: "",
                    familyName = family?.familyName?.substringBefore(" (") ?: "General",
                    practiceCount = practiceCount,
                    averageAccuracy = avgAccuracy,
                    lastPracticed = lastPracticed
                )
            }
        }

        val mostPracticed = remember(characterInsights) {
            characterInsights.sortedByDescending { it.practiceCount }
        }

        val struggledWith = remember(characterInsights) {
            characterInsights
                .filter { it.averageAccuracy < 0.85f }
                .sortedBy { it.averageAccuracy }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Parent Control Hub 🛡️", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("parent_back_button")) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // PART 1: OFFLINE MODE & CLOUD SYNC CONTROL RIPPLE
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Cloud Status",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Offline-First Storage Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Children can play without an internet connection. Data automatically buffers in SQLite locally, and can sync when online.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(if (progress.isSyncing) Color.Yellow else Color(0xFF4CAF50), RoundedCornerShape(5.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (progress.isSyncing) "Syncing with Cloud..." else "Synced: ${if (progress.lastSyncedTime > 0) SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(progress.lastSyncedTime)) else "Local Only"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { viewModel.triggerCloudSync() },
                                enabled = !progress.isSyncing,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.testTag("cloud_sync_button")
                            ) {
                                if (progress.isSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                                } else {
                                    Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("አሁኑኑ አመሳስል", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PART 2: THE DETAILED PROGRESS REPORT CARDS
                var selectedFilter by remember { mutableStateOf("ALL") }
                val filteredRecords = remember(records, selectedFilter) {
                    when (selectedFilter) {
                        "QUIZ" -> records.filter { it.type == "QUIZ" }
                        "TRACING" -> records.filter { it.type == "TRACING" }
                        else -> records
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visual Learning Progress Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (records.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("እንደገና አስጀምር", fontSize = 12.sp)
                        }
                    }
                }

                Text(
                    text = "Track your child's frequency of practice, accuracy trends, and mastery milestones over time.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Weekly Insights Report Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { showWeeklyReportOverlay = true }
                        .testTag("weekly_report_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Weekly Report",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weekly Insights Report 📊",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "See which characters your child practiced most & struggled with this week.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Weekly Insights Dialog Overlay
                if (showWeeklyReportOverlay) {
                    AlertDialog(
                        onDismissRequest = { showWeeklyReportOverlay = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .wrapContentHeight()
                            .testTag("weekly_report_dialog"),
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Analytics,
                                        contentDescription = "Weekly Insights",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Weekly Insights Report 📊",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { showWeeklyReportOverlay = false }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close overlay")
                                }
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Weekly analytical breakdown of your child's Amharic practice and struggle areas.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                if (characterInsights.isEmpty()) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Inbox,
                                            contentDescription = "No data yet",
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No character practice records found.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Please complete some tracing or voice exercises first, or inject demo data to preview this report.",
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                viewModel.injectDemoData()
                                                viewModel.speak("Demo progress data generated", "Demo charts populated")
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Demo data", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Inject Practice History", fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    // 1. MOST PRACTICED CHARACTERS SECTION
                                    Text(
                                        text = "🔥 Most Practiced Characters",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    
                                    Text(
                                        text = "Characters with the highest practice volume. Keep up the high repetition to lock in recognition!",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        mostPracticed.take(3).forEach { insight ->
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("practiced_char_${insight.character}"),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(42.dp)
                                                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = insight.character,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = insight.phonetic,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "${insight.practiceCount} attempts",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    val pct = (insight.averageAccuracy * 100).toInt()
                                                    Text(
                                                        text = "$pct% accuracy",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (pct >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 2. STRUGGLED WITH CHARACTERS SECTION
                                    Text(
                                        text = "⚠️ Struggle Points & Guidance",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Text(
                                        text = "Characters with lower scores where the child might need guidance. Tap speaker to hear correct pronunciation.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    if (struggledWith.isEmpty()) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFE8F5E9)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Perfect score",
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(36.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = "Flawless Performance! 🎉",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color(0xFF1B5E20)
                                                    )
                                                    Text(
                                                        text = "Your child has maintained high accuracy (85%+) across all practiced characters this week. Outstanding job!",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            struggledWith.take(3).forEach { insight ->
                                                val pct = (insight.averageAccuracy * 100).toInt()
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("struggled_char_${insight.character}"),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                                    )
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(38.dp)
                                                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                                                        .clickable {
                                                                            viewModel.speakAmharicLetterWeb(insight.character, insight.phonetic)
                                                                        },
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text(
                                                                        text = insight.character,
                                                                        fontSize = 18.sp,
                                                                        fontWeight = FontWeight.Black,
                                                                        color = MaterialTheme.colorScheme.onError
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Column {
                                                                    Text(
                                                                        text = "Letter ${insight.character} (${insight.phonetic})",
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 13.sp
                                                                    )
                                                                    Text(
                                                                        text = "Family: ${insight.familyName}",
                                                                        fontSize = 11.sp,
                                                                        color = MaterialTheme.colorScheme.outline
                                                                    )
                                                                }
                                                            }
                                                            
                                                            Column(horizontalAlignment = Alignment.End) {
                                                                Text(
                                                                    text = "$pct% accuracy",
                                                                    fontWeight = FontWeight.Black,
                                                                    fontSize = 13.sp,
                                                                    color = MaterialTheme.colorScheme.error
                                                                )
                                                                Text(
                                                                    text = "${insight.practiceCount} attempts",
                                                                    fontSize = 10.sp,
                                                                    color = MaterialTheme.colorScheme.outline
                                                                )
                                                            }
                                                        }
                                                        
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Divider(color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.AutoAwesome,
                                                                contentDescription = "Tip",
                                                                tint = Color(0xFFF57C00),
                                                                modifier = Modifier
                                                                    .size(16.dp)
                                                                    .offset(y = 2.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = getParentTipForCharacter(insight.character, insight.familyName, insight.phonetic),
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                lineHeight = 15.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 3. ACTIONABLE PARENTAL ADVICE
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.MenuBook,
                                                    contentDescription = "Engagement advice",
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Actionable Home Practice Tips 💡",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "1. Active voice modeling helps children connect letters to speech. Try clicking the red/orange letters to hear their native audio and repeat them with your child.\n\n" +
                                                       "2. Keep sessions short! 5 to 10 minutes of active tracing daily is significantly more effective than a single weekly session.\n\n" +
                                                       "3. Reward consistency rather than speed or perfect scores to encourage a positive growth mindset.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { showWeeklyReportOverlay = false },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Got it, thank you!")
                            }
                        }
                    )
                }

                // Tab-style Filters for Dashboard
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("ALL" to "All Play", "QUIZ" to "Quizzes 🏆", "TRACING" to "Tracing ✍️").forEach { (filterType, label) ->
                        val isSelected = selectedFilter == filterType
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedFilter = filterType }
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (records.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "No charts yet",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Welcome to the Analytics Portal!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Play character games, trace letters or complete Amharic vocabulary quizzes to automatically plot progress trends here.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.outline
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Button(
                                onClick = { 
                                    viewModel.injectDemoData() 
                                    viewModel.speak("Demo progress data generated", "Demo charts populated")
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Demo data")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("የሙከራ ታሪክ አስገባ")
                            }
                        }
                    }
                } else {
                    val successfullyTracedLetters = remember(records) {
                        records
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

                    // 1. STATS METRIC WIDGETS
                    val totalSessions = filteredRecords.size
                    val totalQuestions = filteredRecords.sumOf { it.totalQuestions }
                    val totalCorrect = filteredRecords.sumOf { it.score }
                    val averageAccuracy = if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions * 100).toInt() else 0
                    
                    // Streak calculation inside the last 7 calendar days
                    val activeDaysCount = remember(records) {
                        val formatDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        records.map { formatDay.format(Date(it.timestamp)) }.distinct().size
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sessions Widget
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Practiced", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$totalSessions sessions", fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Mastery Score Accuracy Widget
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Mastery Level", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$averageAccuracy% Accuracy", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                            }
                        }

                        // Active Streak Widget
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Active Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$activeDaysCount total days", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFFD84315))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // NEW: VISUAL ACHIEVEMENT SUMMARY VIEW (STARS, BADGES, AND LETTER GROUPS BAR CHART)
                    val totalStars = progress.stars
                    val unlockedBadgeIds = remember(progress.unlockedStickers) {
                        progress.unlockedStickers.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    }
                    val totalBadgesCount = unlockedBadgeIds.size

                    val familyProgressList = remember(FidelData.families, successfullyTracedLetters) {
                        FidelData.families.map { family ->
                            val masteredCount = family.letters.count { successfullyTracedLetters.contains(it.character) }
                            val pct = if (family.letters.isNotEmpty()) masteredCount.toFloat() / family.letters.size else 0f
                            Triple(family, masteredCount, pct)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("visual_summary_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Title row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Achievements",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Achievement & Mastery Summary",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Visual dashboard of collected stars, active badges, and letter mastery.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // High fidelity Stars and Badges Count Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Star count container
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color(0xFFFFD54F).copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .background(Color(0xFFFFF9C4), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Stars Icon",
                                                tint = Color(0xFFFBC02D),
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "$totalStars",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFF57F17)
                                            )
                                            Text(
                                                text = "Total Stars",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }

                                // Badge count container
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color(0xFF64B5F6).copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .background(Color(0xFFE3F2FD), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CardMembership,
                                                contentDescription = "Badge Icon",
                                                tint = Color(0xFF1976D2),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "$totalBadgesCount",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF0D47A1)
                                            )
                                            Text(
                                                text = "Badges Earned",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // BADGES SHOWCASE ROW (HORIZONTAL CAROUSEL OF BADGES)
                            Text(
                                text = "Badges Showcase (የሜዳሊያ ሰሌዳ) 🏅",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(StickerData.stickers) { sticker ->
                                    val isUnlocked = unlockedBadgeIds.contains(sticker.id)
                                    val backgroundColor = if (isUnlocked) Color(sticker.colorHex) else Color.LightGray.copy(alpha = 0.15f)
                                    val borderStrokeColor = if (isUnlocked) Color(sticker.colorHex) else Color.LightGray.copy(alpha = 0.3f)

                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .width(135.dp)
                                            .height(68.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = backgroundColor
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, borderStrokeColor)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isUnlocked) sticker.emoji else "🔒",
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(end = 4.dp)
                                            )
                                            Column(
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = sticker.name,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Start
                                                )
                                                Text(
                                                    text = if (isUnlocked) "Unlocked" else sticker.conditionText,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isUnlocked) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.outline,
                                                    lineHeight = 9.sp,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // THE PROGRESS BAR CHART ACROSS LETTER GROUPS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress Across Amharic Letter Groups 📊",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "Slide left/right ➡️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Text(
                                text = "Shows the number of characters traced perfectly (score >= 4/5) in each Amharic letter family.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Horizontally scrollable row containing custom styled vertical bars representing letter families
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                items(familyProgressList) { (family, count, pct) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(42.dp)
                                    ) {
                                        // Mastery count label above the bar
                                        Text(
                                            text = "$count/7",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (count == 7) Color(0xFF2E7D32) else if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )

                                        // Bar container block
                                        Box(
                                            modifier = Modifier
                                                .width(22.dp)
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            // Animated filled bar portion using spring physics
                                            val animatedBarHeight = animateFloatAsState(
                                                targetValue = pct,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                ),
                                                label = "bar_height"
                                            )

                                            val barColor = when (count) {
                                                7 -> Brush.verticalGradient(colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))) // Golden Green
                                                in 4..6 -> Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))) // Indigo Blue
                                                in 1..3 -> Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f))) 
                                                else -> Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Transparent))
                                            }

                                            if (pct > 0f) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .fillMaxHeight(animatedBarHeight.value)
                                                    .background(barColor, RoundedCornerShape(12.dp))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Base letter group circular button
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                if (count == 7) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                                CircleShape
                                            )
                                            .border(
                                                1.1.dp,
                                                if (count == 7) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                            .clickable {
                                                viewModel.speakAmharicLetterWeb(family.mainConsonant, family.letters.firstOrNull()?.ttsPhonetic ?: "")
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = family.mainConsonant,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (count == 7) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                        )
                                        
                                        // Small golden star if family is completely completed (7/7)
                                        if (count == 7) {
                                            Box(
                                                modifier = Modifier
                                                    .size(11.dp)
                                                    .background(Color(0xFFFFD54F), CircleShape)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 2.dp, y = (-2).dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Mastered Star",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(8.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Short description helper label
                                    Text(
                                        text = family.familyName.substringBefore(" ("),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. FREQUENCY OF PRACTICE HEATMAP / TIMELINE
                    // Let's count sessions for the last 7 calendar days
                    val counts = remember(records) {
                        val currentMillis = System.currentTimeMillis()
                        val oneDayMillis = 24 * 60 * 60 * 1000L
                        val resultList = mutableListOf<Pair<String, Int>>()
                        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                        val formatDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        
                        for (i in 6 downTo 0) {
                            val targetTime = currentMillis - (i * oneDayMillis)
                            val dayLabel = sdf.format(Date(targetTime))
                            val dayKey = formatDay.format(Date(targetTime))
                            val countOnDay = records.count { formatDay.format(Date(it.timestamp)) == dayKey }
                            resultList.add(dayLabel to countOnDay)
                        }
                        resultList
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Weekly Practice Frequency Heat Grid 🗓️",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Shows practice consistency over the last 7 calendar days.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                counts.forEach { (dayName, count) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // GitHub contribution-like block color mapping
                                        val blockColor = when {
                                            count == 0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                            count == 1 -> Color(0xFFC8E6C9) // Pastel Mint L1
                                            count == 2 -> Color(0xFF81C784) // Green L2
                                            count >= 3 -> Color(0xFF2E7D32) // Deep Forrest L3
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                        
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(blockColor, RoundedCornerShape(8.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = if (count > 0) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (count > 0) count.toString() else "",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (count >= 3) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = dayName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. MASTERY LEVEL OVER TIME GRID (BEZIER TREND CURVE OVER TIME)
                    val timeline = remember(filteredRecords) {
                        val sorted = filteredRecords.sortedBy { it.timestamp }
                        val masteryList = mutableListOf<Float>()
                        var totalPoints = 0
                        var earnedPoints = 0
                        sorted.forEach { record ->
                            totalPoints += record.totalQuestions
                            earnedPoints += record.score
                            val accuracy = if (totalPoints > 0) earnedPoints.toFloat() / totalPoints else 0f
                            masteryList.add(accuracy)
                        }
                        masteryList
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Mastery Level Over Time Trend 📈",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cumulative mastery progress as chronological exercises are completed.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            
                            Spacer(modifier = Modifier.height(18.dp))

                            if (timeline.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No activities in this filter. Keep studying!",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                                    // Plot grid lines for tiers: Starter, Novice, Explorer, Achiever, Expert
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        listOf("100% Specialist 👑", "75% Achiever 🎓", "50% Explorer 🌟", "25% Novice 🌱", "0% Starter").forEach { tier ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = tier,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(1.dp)
                                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                                                )
                                            }
                                        }
                                    }

                                    // Canvas drawing smooth curve
                                    val primaryColor = MaterialTheme.colorScheme.primary
                                    val secondaryColor = MaterialTheme.colorScheme.secondary
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        val width = size.width
                                        val height = size.height

                                        if (timeline.size == 1) {
                                            val y = height * (1f - timeline[0])
                                            drawCircle(
                                                color = primaryColor,
                                                radius = 6.dp.toPx(),
                                                center = Offset(width / 2f, y)
                                            )
                                        } else {
                                            val path = Path()
                                            val fillPath = Path()

                                            val points = timeline.mapIndexed { index, valPct ->
                                                val x = (index.toFloat() / (timeline.size - 1)) * width
                                                val y = height * (1f - valPct)
                                                Offset(x, y)
                                            }

                                            // Draw smoothly
                                            points.forEachIndexed { idx, point ->
                                                if (idx == 0) {
                                                    path.moveTo(point.x, point.y)
                                                    fillPath.moveTo(point.x, height)
                                                    fillPath.lineTo(point.x, point.y)
                                                } else {
                                                    val prev = points[idx - 1]
                                                    val cx1 = prev.x + (point.x - prev.x) / 2f
                                                    val cy1 = prev.y
                                                    val cx2 = prev.x + (point.x - prev.x) / 2f
                                                    val cy2 = point.y
                                                    path.cubicTo(cx1, cy1, cx2, cy2, point.x, point.y)
                                                    fillPath.cubicTo(cx1, cy1, cx2, cy2, point.x, point.y)
                                                }
                                            }

                                            fillPath.lineTo(width, height)
                                            fillPath.close()

                                            // Fill under the curve with beautiful gradient
                                            drawPath(
                                                path = fillPath,
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        primaryColor.copy(alpha = 0.4f),
                                                        primaryColor.copy(alpha = 0.01f)
                                                    )
                                                )
                                            )

                                            // Stroke curve line
                                            drawPath(
                                                path = path,
                                                color = primaryColor,
                                                style = Stroke(
                                                    width = 3.dp.toPx(),
                                                    cap = StrokeCap.Round
                                                )
                                            )

                                            // Points
                                            points.forEach { pt ->
                                                drawCircle(
                                                    color = secondaryColor,
                                                    radius = 4.dp.toPx(),
                                                    center = pt
                                                )
                                                drawCircle(
                                                    color = Color.White,
                                                    radius = 2.dp.toPx(),
                                                    center = pt
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                // AMHARIC LETTER TRACING MASTERY MAP CARD
                var searchQuery by remember { mutableStateOf("") }
                val filteredFamilies = remember(searchQuery) {
                    FidelData.families.filter { family ->
                        searchQuery.isEmpty() ||
                        family.familyName.contains(searchQuery, ignoreCase = true) ||
                        family.mainConsonant.contains(searchQuery) ||
                        family.letters.any { it.character.contains(searchQuery) }
                    }
                }
                val totalLettersCount = FidelData.families.sumOf { it.letters.size }
                val tracedCount = FidelData.families.flatMap { it.letters }.count { successfullyTracedLetters.contains(it.character) }
                val completionPct = if (totalLettersCount > 0) (tracedCount.toFloat() / totalLettersCount * 100).toInt() else 0

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("mastery_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Amharic Tracing Mastery Map ✍️🗺️",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "Full syllabary completion & letter tracing tracker.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                text = "$tracedCount / $totalLettersCount mastered",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                                )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        LinearProgressIndicator(
                            progress = { tracedCount.toFloat() / totalLettersCount },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Total Completion: $completionPct% • Click on families below to view letters.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by letter or phonetic (e.g., ሀ, Ha)...", fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("parent_mastery_search")
                )

                // Families mastery list
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredFamilies.take(12).forEach { family ->
                        val familyTracedCount = family.letters.count { successfullyTracedLetters.contains(it.character) }
                        val isFullMastery = familyTracedCount == family.letters.size
                        var isExpanded by remember { mutableStateOf(false) }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                                .testTag("mastery_family_card_${family.familyName}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFullMastery) {
                                    Color(0xFFE8F5E9)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    if (isFullMastery) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = family.mainConsonant,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isFullMastery) Color(0xFF1B5E20) else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(10.dp))
                                        
                                        Column {
                                            Text(
                                                text = "Family: ${family.familyName}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isFullMastery) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Pronunciation: ${family.letters.firstOrNull()?.phonetic ?: ""}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$familyTracedCount / ${family.letters.size} Mastered",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFullMastery) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Expand details",
                                            tint = if (isFullMastery) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                                
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                    ) {
                                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            family.letters.forEach { subLetter ->
                                                val subTraced = successfullyTracedLetters.contains(subLetter.character)
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(
                                                                if (subTraced) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                                                                RoundedCornerShape(8.dp)
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = subLetter.character,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (subTraced) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = subLetter.phonetic,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                    Text(
                                                        text = if (subTraced) "⭐ Mastered" else "⏳ Pending",
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (subTraced) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (filteredFamilies.size > 12) {
                        Text(
                            text = "Showing 12 of ${filteredFamilies.size} families. Refine your search to explore other families.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    } else if (filteredFamilies.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Amharic families match your search query.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. CHRONOLOGICAL PLAY AUDIT TRAIL LIST
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Activity Audit Logs:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            filteredRecords.sortedByDescending { it.timestamp }.take(4).forEach { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = record.exerciseName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Category: ${record.type} • ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(record.timestamp))}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    
                                    val percent = if (record.totalQuestions > 0) (record.score.toFloat() / record.totalQuestions * 100).toInt() else 0
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${record.score} / ${record.totalQuestions}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (percent >= 80) Color(0xFF2E7D32) else if (percent >= 50) Color(0xFFF57C00) else MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "${percent}% correct",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PART 2.5: PARENTAL SESSION DURATION LOGS ⏱️
                Text(
                    text = "Active Learning Session Logs ⏱️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Observe when and how long your child has spent practicing Amharic.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Sessions Summary & Log Stats
                val totalTimeSeconds = sessions.sumOf { it.durationSeconds }
                val formattedTotalTime = remember(totalTimeSeconds) {
                    if (totalTimeSeconds < 60) {
                        "$totalTimeSeconds secs"
                    } else {
                        val mins = totalTimeSeconds / 60
                        val secs = totalTimeSeconds % 60
                        "${mins}m ${secs}s"
                    }
                }
                
                val avgTimeSeconds = if (sessions.isNotEmpty()) totalTimeSeconds / sessions.size else 0L
                val formattedAvgTime = remember(avgTimeSeconds) {
                    if (avgTimeSeconds < 60) {
                        "$avgTimeSeconds secs"
                    } else {
                        val mins = avgTimeSeconds / 60
                        val secs = avgTimeSeconds % 60
                        "${mins}m ${secs}s"
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Time Spent
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HourglassEmpty,
                                    contentDescription = "Total Practice Time",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Total Screen Time", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formattedTotalTime, fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }

                    // Average Session Time
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Average Session Time",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Avg. Session", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formattedAvgTime, fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    if (sessions.isEmpty()) {
                        Text(
                            text = "No learning sessions recorded yet. Start practicing to begin logs!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    } else {
                        Column {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Date 📅",
                                    modifier = Modifier.weight(1.3f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Start ⏱️",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "End 🏁",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Duration ⌛",
                                    modifier = Modifier.weight(1.1f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Table Rows with alternate backgrounds (Zebra stripes)
                            sessions.take(15).forEachIndexed { index, session ->
                                val dateStr = remember(session.startTime) {
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(session.startTime))
                                }
                                val startStr = remember(session.startTime) {
                                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(session.startTime))
                                }
                                val endStr = remember(session.endTime) {
                                    if (session.endTime > 0) {
                                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(session.endTime))
                                    } else {
                                        "Active"
                                    }
                                }
                                val durationText = remember(session.durationSeconds) {
                                    if (session.durationSeconds < 60) {
                                        "${session.durationSeconds}s"
                                    } else {
                                        val m = session.durationSeconds / 60
                                        val s = session.durationSeconds % 60
                                        "${m}m ${s}s"
                                    }
                                }

                                val rowBackground = if (index % 2 == 0) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBackground)
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateStr,
                                        modifier = Modifier.weight(1.3f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = startStr,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = endStr,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp,
                                        color = if (endStr == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (endStr == "Active") FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = durationText,
                                        modifier = Modifier.weight(1.1f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (index < sessions.size - 1 && index < 14) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PART 3: ACCESSIBILITY CONFIGURATION SWITCHERS
                Text(
                    text = "Accessibility & Guardian Choices",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // High Contrast Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = "Eye icon")
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("High-Contrast Themes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Optimal outlines & stark shades for visibility", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                            Switch(
                                checked = progress.highContrast,
                                onCheckedChange = {
                                    viewModel.updateParentSettings(
                                        pin = progress.parentPin,
                                        highContrast = it,
                                        ttsEnabled = progress.textToSpeechEnabled
                                    )
                                },
                                modifier = Modifier.testTag("high_contrast_switch")
                            )
                        }

                        // Global Sound Toggle in Settings Menu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (progress.textToSpeechEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Global Sound Icon",
                                    tint = if (progress.textToSpeechEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Global Sound Toggle 🔊", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "Mute or unmute Amharic letter pronunciation guide audio in games & learning activities",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Switch(
                                checked = progress.textToSpeechEnabled,
                                onCheckedChange = {
                                    viewModel.updateParentSettings(
                                        pin = progress.parentPin,
                                        highContrast = progress.highContrast,
                                        ttsEnabled = it
                                    )
                                },
                                modifier = Modifier.testTag("tts_switch")
                            )
                        }

                        Divider()

                        // Background Music Toggle in Parent Settings
                        val bgMusicEnabled by viewModel.bgMusicEnabled.collectAsState()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (bgMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                                    contentDescription = "Background Music Icon",
                                    tint = if (bgMusicEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Background Music 🎵", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "Play soft, educational instrumental music box tunes to keep children focused & engaged",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Switch(
                                checked = bgMusicEnabled,
                                onCheckedChange = { viewModel.toggleBgMusic() },
                                modifier = Modifier.testTag("bg_music_parent_switch")
                            )
                        }

                        Divider()

                        // Amharic Teaching Voice Selector
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "Voice icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Amharic Teacher Voice 🎙️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "Select a tutor's voice style & speed for all pronunciations",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val currentVoice = progress.teachingVoice
                                val row1 = listOf(
                                    Triple("DEFAULT", "Aster 👩‍🏫", "Standard"),
                                    Triple("KID", "Mimi 👧", "Energetic"),
                                    Triple("ELDER", "Yeneta 👴", "Elderly")
                                )
                                val row2 = listOf(
                                    Triple("TEACHER", "Almaz 👩", "Native Voice"),
                                    Triple("BABA", "Baba 👦", "Boy Voice")
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        row1.forEach { (id, name, desc) ->
                                            val isSelected = currentVoice == id
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                    .border(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { viewModel.updateTeachingVoice(id) }
                                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                                    .testTag("voice_chip_$id"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                                    Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        row2.forEach { (id, name, desc) ->
                                            val isSelected = currentVoice == id || (id == "BABA" && currentVoice == "CHUNI")
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                    .border(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { viewModel.updateTeachingVoice(id) }
                                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                                    .testTag("voice_chip_$id"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                                    Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline, maxLines = 1)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Divider()

                        // Difficulty Level Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = "Speed/Level icon")
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Learning Level Mode 🎮", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = if (progress.difficultyMode == "EXPERT")
                                            "Expert (Full alphabet random sequence)"
                                            else "Easy (Fewer letters per session)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isExpert = progress.difficultyMode == "EXPERT"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!isExpert) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .clickable { viewModel.updateDifficultyMode("EASY") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("difficulty_easy_chip"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Easy",
                                        color = if (!isExpert) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isExpert) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .clickable { viewModel.updateDifficultyMode("EXPERT") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("difficulty_expert_chip"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Expert",
                                        color = if (isExpert) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Divider()

                        // Parent PIN custom update controls
                        Column {
                            Text(
                                text = "Modify Guardian Lock PIN:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newPin,
                                    onValueChange = { if (it.length <= 6) newPin = it },
                                    label = { Text("New PIN (Max 6 digits)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = {
                                        if (newPin.isNotEmpty()) {
                                            viewModel.updateParentSettings(
                                                pin = newPin,
                                                highContrast = progress.highContrast,
                                                ttsEnabled = progress.textToSpeechEnabled
                                            )
                                            viewModel.speak("PIN changed successfully", "PIN changed")
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("አስቀምጥ")
                                }
                            }
                        }

                        Divider()

                        // Erase Metrics DB cache
                        Button(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eraser")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ዳታቤዙን አጽዳ")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "App Information & Developer Details ℹ️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: App Description
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "App Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Amharic Learn",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Amharic Learn is an interactive, gamified learning platform designed to help children master the Ge'ez alphabet (Fidel), vocabulary words, and sentence structures. Featuring native voice feedback, an intuitive drawing tracing canvas, sound matching puzzles, and custom progress tracking tools, this app bridges culture and technology for young minds.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Section 2: Developer details
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Developer Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Developer Profiles",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Crafted with passion to preserve and celebrate Ethiopian heritage through educational technology, prioritizing offline responsiveness and accessibility.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Section 3: Contact/Email
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Contact Email",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Contact Support & Feedback",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "yeshiwas2014@gmail.com",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Section 4: App Version
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "App Version",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "App Version",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "v1.2.0 (Stable release)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

data class CharacterInsight(
    val character: String,
    val phonetic: String,
    val familyName: String,
    val practiceCount: Int,
    val averageAccuracy: Float,
    val lastPracticed: Long
)

fun getParentTipForCharacter(character: String, familyName: String, phonetic: String): String {
    return when {
        familyName.contains("H", ignoreCase = true) || phonetic.contains("h", ignoreCase = true) || phonetic.contains("ሀ", ignoreCase = true) -> {
            "Make a soft puffing 'H' sound game together. Say 'Hoo!' and have them mimic you while writing $character."
        }
        familyName.contains("R", ignoreCase = true) || phonetic.contains("r", ignoreCase = true) -> {
            "Practice rolling the 'R' sound. Point out words starting with '$character' (like 'ርግብ' - pigeon) to build recognition."
        }
        familyName.contains("M", ignoreCase = true) || phonetic.contains("m", ignoreCase = true) -> {
            "Draw $character in sand or flour while humming the 'Mmm' sound. Associate it with 'መኪና' (Mekina - car)."
        }
        familyName.contains("T", ignoreCase = true) || phonetic.contains("t", ignoreCase = true) -> {
            "Clap or tap for each '$phonetic' sound to build rhythmic phonetic awareness. Draw $character together slowly."
        }
        familyName.contains("S", ignoreCase = true) || phonetic.contains("s", ignoreCase = true) -> {
            "Practice making a hissing snake 'Sss' sound while tracing the curves of $character together."
        }
        else -> {
            "Focus on the visual shape of $character. Break down the stroke sequence together, and pronounce the '$phonetic' sound aloud."
        }
    }
}
