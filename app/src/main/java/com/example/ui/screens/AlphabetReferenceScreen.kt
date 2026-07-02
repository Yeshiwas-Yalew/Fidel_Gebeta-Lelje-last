package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.*
import com.example.ui.FidelViewModel

enum class AlphabetViewFormat {
    POSTER_CHART,
    FAMILY_CARDS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetReferenceScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    var viewFormat by remember { mutableStateOf(AlphabetViewFormat.POSTER_CHART) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedLetterForDictionary by remember { mutableStateOf<AmharicLetter?>(null) }

    val filteredFamilies = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            FidelData.families
        } else {
            FidelData.families.filter { family ->
                family.familyName.contains(searchQuery, ignoreCase = true) ||
                family.mainConsonant.contains(searchQuery) ||
                family.letters.any { letter ->
                    letter.character.contains(searchQuery) || 
                    letter.phonetic.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reference Chart ፊደላት",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Comprehensive Amharic Matrix Guide",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("ref_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Visual Guide & Toggle Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab 1: Posters Chart Matrix
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (viewFormat == AlphabetViewFormat.POSTER_CHART) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable {
                            viewFormat = AlphabetViewFormat.POSTER_CHART
                            viewModel.speak("Alphabet Chart Mode", "Full alphabet reference chart poster")
                        }
                        .testTag("tab_chart_poster"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Interactive Matrix",
                            tint = if (viewFormat == AlphabetViewFormat.POSTER_CHART) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Poster Chart 📊",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (viewFormat == AlphabetViewFormat.POSTER_CHART) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tab 2: Family Deck Cards
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (viewFormat == AlphabetViewFormat.FAMILY_CARDS) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable {
                            viewFormat = AlphabetViewFormat.FAMILY_CARDS
                            viewModel.speak("Detailed Cards Mode", "Detailed alphabet family cards deck")
                        }
                        .testTag("tab_family_cards"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = "Detailed Cards",
                            tint = if (viewFormat == AlphabetViewFormat.FAMILY_CARDS) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Family Deck 🎴",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (viewFormat == AlphabetViewFormat.FAMILY_CARDS) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // High Contrast Quick Filter Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by letter, phonetic (e.g., hu, la)...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("alphabet_search_input")
            )

            // Info banner reminding kids to tape to hear sound
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡 Tap any letter cell to play real native audio guides via Web Speech Synthesis!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Core Layout Display
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (filteredFamilies.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No letters match \"$searchQuery\" 🔍",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching characters like ሀ, ሁ, ሂ or sound tags like ha, lo.",
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    when (viewFormat) {
                        AlphabetViewFormat.POSTER_CHART -> {
                            PosterGridView(
                                families = filteredFamilies,
                                onLetterClicked = { letter ->
                                    selectedLetterForDictionary = letter
                                }
                            )
                        }
                        AlphabetViewFormat.FAMILY_CARDS -> {
                            CardsListView(
                                families = filteredFamilies,
                                onLetterClicked = { letter ->
                                    selectedLetterForDictionary = letter
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedLetterForDictionary != null) {
        val letterRef = selectedLetterForDictionary!!
        val familyRef = remember(letterRef) {
            FidelData.families.firstOrNull { fam -> fam.letters.any { it.character == letterRef.character } }
        }
        InteractiveDictionaryDialog(
            letter = letterRef,
            family = familyRef,
            viewModel = viewModel,
            onDismiss = { selectedLetterForDictionary = null }
        )
    }
}

@Composable
fun PosterGridView(
    families: List<FidelFamily>,
    onLetterClicked: (AmharicLetter) -> Unit
) {
    // Scrollable 2D matrix layout replicating traditional Amharic Fidel charts elegantly.
    val horizontalScrollState = rememberScrollState()
    
    val orders = listOf(
        "1st (ግዕዝ)" to "ä / a",
        "2nd (ካዕብ)" to "u",
        "3rd (ሣልስ)" to "i",
        "4th (ራብዕ)" to "a",
        "5th (ኃምስ)" to "e",
        "6th (ሳድስ)" to "ə / (con)",
        "7th (ሳብዕ)" to "o"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 10.dp)
            ) {
                // Family Column spacer cell
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Family",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Vowel Header columns
                orders.forEachIndexed { index, (orderLabel, vowelSound) ->
                    Column(
                        modifier = Modifier
                            .width(68.dp)
                            .padding(horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = orderLabel,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "($vowelSound)",
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Scrollable Data Rows
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(families) { family ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Consonant Family badge indicator
                        Card(
                            modifier = Modifier
                                .width(76.dp)
                                .height(58.dp)
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = family.mainConsonant,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = family.familyName.substringBefore(" ").uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        // Order Letter buttons
                        family.letters.forEach { letter ->
                            val isUnlocked = true // reference chart accessible fully
                            val cellColor = getOrderColor(letter.order)
                            val accentColor = getOrderTextAccent(letter.order)

                            Card(
                                modifier = Modifier
                                    .width(68.dp)
                                    .height(58.dp)
                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                                    .clickable { onLetterClicked(letter) }
                                    .testTag("ref_chart_cell_${letter.character}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = cellColor
                                ),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = letter.character,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = accentColor,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = letter.phonetic,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor.copy(alpha = 0.82f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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

@Composable
fun CardsListView(
    families: List<FidelFamily>,
    onLetterClicked: (AmharicLetter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(families) { family ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ref_family_card_${family.mainConsonant}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    // Header of Card: Family Title, Emoji Companion and words details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(family.exampleEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Family: ${family.familyName}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Example Word: ${family.exampleWord} (${family.exampleTranslit} - ${family.exampleEnglish})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chunky interactive pronunciation blocks for kids in a horizontal scroll card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        family.letters.forEach { letter ->
                            val backgroundOrderColor = getOrderColor(letter.order)
                            val accentTextColor = getOrderTextAccent(letter.order)

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = backgroundOrderColor
                                ),
                                modifier = Modifier
                                    .width(74.dp)
                                    .clickable { onLetterClicked(letter) }
                                    .border(
                                        width = 1.dp,
                                        color = accentTextColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .testTag("ref_card_item_${letter.character}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(accentTextColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter.character,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = accentTextColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = letter.phonetic,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = accentTextColor,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = letter.orderName,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = accentTextColor.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveDictionaryDialog(
    letter: AmharicLetter,
    family: FidelFamily?,
    viewModel: FidelViewModel,
    onDismiss: () -> Unit
) {
    // Canvas state for user tracing
    val points = remember { mutableStateListOf<Offset>() }
    
    // Animation trigger for showing stroke formation simulation/projection
    var isSimulatingFormation by remember { mutableStateOf(false) }
    val simulationProgress = remember { Animatable(0f) }
    
    // Play pronunciation immediately on load
    LaunchedEffect(letter) {
        viewModel.speakAmharicLetterWeb(letter.character, letter.ttsPhonetic)
    }
    
    // Whenever simulation runs, trigger animation
    LaunchedEffect(isSimulatingFormation) {
        if (isSimulatingFormation) {
            simulationProgress.snapTo(0f)
            simulationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
            )
            isSimulatingFormation = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 16.dp)
            .testTag("dictionary_explorer_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("dict_close_button")
            ) {
                Text("መዝገበ ቃላት ዝጋ 📖", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📖 Dictionary Explorer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ፊደል", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Visual representation & Association
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Large letter preview
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        color = getOrderColor(letter.order).copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .border(2.dp, getOrderTextAccent(letter.order).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .clickable {
                                        viewModel.speakAmharicLetterWeb(letter.character, letter.ttsPhonetic)
                                    }
                                    .testTag("dict_letter_speaker_anchor"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter.character,
                                    fontSize = 58.sp,
                                    fontWeight = FontWeight.Black,
                                    color = getOrderTextAccent(letter.order)
                                )
                                // Friendly floating speaker icon
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak pronunciation",
                                    tint = getOrderTextAccent(letter.order).copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Phonetic: \"${letter.phonetic}\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${letter.orderName} Order / ${getOrderAmharicLabel(letter.order)}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Visual word association
                        Column(
                            modifier = Modifier.weight(1.5f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter.emoji,
                                    fontSize = 44.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = letter.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "(${letter.translit} = ${letter.english})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Section 2: Formation Guide writing steps
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✍️ Stroke Formation Steps",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        family?.strokePathsHint?.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = step,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } ?: Text(
                            text = "Trace bottom-up and left-to-right to form this letter character cleanly.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Section 3: Interactive Chalkboard Practice Area!
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✏️ Try Forming/Drawing Me Below!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Watch formation simulation button
                            IconButton(
                                onClick = {
                                    isSimulatingFormation = true
                                    viewModel.speak("Watch how to form the letter", "Formation Projection!")
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Watch Formation",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Clean canvas button
                            IconButton(
                                onClick = { points.clear() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Clear Canvas",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Cozy slate blackboard container
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF263238)) // elegant carbon/chalkboard slate dark theme
                            .border(3.dp, Color(0xFF78909C), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        points.add(offset)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        points.add(change.position)
                                    }
                                )
                            }
                            .testTag("dict_mini_chalkboard"),
                        contentAlignment = Alignment.Center
                    ) {
                        // 1. Cozy Faint Reference guidelines
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Horizontal dashed center guides
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(0f, size.height / 2f),
                                end = Offset(size.width, size.height / 2f),
                                strokeWidth = 1.dp.toPx()
                            )
                            // Vertical dashed center guides
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(size.width / 2f, 0f),
                                end = Offset(size.width / 2f, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // 2. Faint gray reference letter
                        Text(
                            text = letter.character,
                            fontSize = 150.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.15f),
                            textAlign = TextAlign.Center
                        )

                        // 3. User finger chalk strokes
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (points.size > 1) {
                                for (i in 0 until points.size - 1) {
                                    val start = points[i]
                                    val end = points[i + 1]
                                    val distance = (end - start).getDistance()
                                    if (distance < 80f) {
                                        drawLine(
                                            color = Color(0xFF80DEEA), // neon bright sky blue chalk lines
                                            start = start,
                                            end = end,
                                            strokeWidth = 10.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }

                        // 4. Projection simulation overlay (a star tracing path!)
                        if (simulationProgress.value > 0f) {
                            val animVal = simulationProgress.value
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                
                                val startX = w * 0.5f
                                var cursorX = startX
                                var cursorY = h * 0.2f + (h * 0.6f * animVal)

                                if (letter.character.hashCode() % 3 == 0) {
                                    val angle = animVal * Math.PI * 2
                                    cursorX = (w * 0.5f + Math.sin(angle) * (w * 0.25f)).toFloat()
                                    cursorY = (h * 0.5f + Math.cos(angle) * (h * 0.25f)).toFloat()
                                } else if (letter.character.hashCode() % 3 == 1) {
                                    if (animVal < 0.4f) {
                                        cursorX = w * 0.3f
                                        cursorY = h * 0.2f + (h * 0.6f * (animVal / 0.4f))
                                    } else if (animVal < 0.7f) {
                                        val subFraction = (animVal - 0.4f) / 0.3f
                                        cursorX = w * 0.3f + (w * 0.4f * subFraction)
                                        cursorY = h * 0.2f
                                    } else {
                                        val subFraction = (animVal - 0.7f) / 0.3f
                                        cursorX = w * 0.7f
                                        cursorY = h * 0.2f + (h * 0.6f * subFraction)
                                    }
                                } else {
                                    val angle = animVal * Math.PI * 1.5
                                    cursorX = (w * 0.5f + Math.cos(angle) * (w * 0.25f)).toFloat()
                                    cursorY = (h * 0.5f + Math.sin(angle) * (h * 0.25f)).toFloat()
                                }

                                drawCircle(
                                    color = Color(0xFFFFD54F),
                                    radius = 12.dp.toPx(),
                                    center = Offset(cursorX, cursorY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 4.dp.toPx(),
                                    center = Offset(cursorX, cursorY)
                                )
                            }
                        }
                    }
                    
                    if (simulationProgress.value > 0f && simulationProgress.value < 1f) {
                        Text(
                            text = "👀 Look! Follow the golden cursor path to draw the letter!",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    )
}

fun getOrderAmharicLabel(order: Int): String {
    return when (order) {
        1 -> "ግዕዝ (Ge'ez)"
        2 -> "ካዕብ (Ka'ib)"
        3 -> "ሣልስ (Salis)"
        4 -> "ራብዕ (Rabi')"
        5 -> "ኃምስ (Hamis)"
        6 -> "ሳድስ (Sadis)"
        7 -> "ሳብዕ (Sab'i)"
        else -> ""
    }
}
