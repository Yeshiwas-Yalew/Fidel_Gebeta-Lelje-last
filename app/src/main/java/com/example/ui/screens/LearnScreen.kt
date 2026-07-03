package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items as lazyRowItems
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.FidelViewModel

enum class FidelViewMode {
    FAMILIES,
    ALL_LETTERS
}

fun getOrderColor(order: Int): Color {
    return when (order) {
        1 -> Color(0xFFFFF3E0) // soft orange / peach
        2 -> Color(0xFFE3F2FD) // soft blue
        3 -> Color(0xFFE8F5E9) // soft green
        4 -> Color(0xFFFCE4EC) // soft pink
        5 -> Color(0xFFEDE7F6) // soft purple
        6 -> Color(0xFFFFFDE7) // soft yellow
        7 -> Color(0xFFE0F2F1) // soft teal
        else -> Color(0xFFF5F5F5)
    }
}

fun getOrderTextAccent(order: Int): Color {
    return when (order) {
        1 -> Color(0xFFE65100) // dark orange
        2 -> Color(0xFF1565C0) // dark blue
        3 -> Color(0xFF2E7D32) // dark green
        4 -> Color(0xFFC2185B) // dark pink
        5 -> Color(0xFF4527A0) // dark purple
        6 -> Color(0xFFF57F17) // dark yellow
        7 -> Color(0xFF00695C) // dark teal
        else -> Color(0xFF616161)
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: FidelViewModel,
    userProgress: UserProgress,
    onBack: () -> Unit
) {
    val selectedFamily by viewModel.selectedFamily.collectAsState()
    var viewMode by remember { mutableStateOf(FidelViewMode.FAMILIES) }
    var selectedFamilyFilter by remember { mutableStateOf<String?>("ALL") }
    var explorerLetter by remember { mutableStateOf<Pair<AmharicLetter, FidelFamily>?>(null) }

    val allLettersAndFamilies = remember {
        FidelData.families.flatMap { family ->
            family.letters.map { letter -> letter to family }
        }
    }

    val filteredLetters = remember(selectedFamilyFilter) {
        if (selectedFamilyFilter == "ALL") {
            allLettersAndFamilies
        } else {
            allLettersAndFamilies.filter { it.second.mainConsonant == selectedFamilyFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedFamily == null) "Fidel Board ፊደል" else "Family ${selectedFamily!!.mainConsonant}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedFamily != null) {
                                viewModel.selectFamily(null)
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.testTag("learn_back_button")
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedFamily,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState != null) width else -width } with
                            slideOutHorizontally { width -> if (targetState != null) -width else width }
                },
                label = "fidel_navigation"
            ) { family ->
                if (family == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Modern, kid-friendly Bubble Segmented Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Tab 1: Families
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (viewMode == FidelViewMode.FAMILIES) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewMode = FidelViewMode.FAMILIES
                                        viewModel.speak("Browse alphabet families", "Browse alphabet families")
                                    }
                                    .testTag("tab_families"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "የፊደል መደቦች 📂",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (viewMode == FidelViewMode.FAMILIES) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Tab 2: All Letters Grid
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (viewMode == FidelViewMode.ALL_LETTERS) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewMode = FidelViewMode.ALL_LETTERS
                                        viewModel.speak("Choose any Amharic letters to play", "Choose any Amharic letters to play")
                                    }
                                    .testTag("tab_all_letters"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ሁሉም 231 ፊደላት 🔠",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (viewMode == FidelViewMode.ALL_LETTERS) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (viewMode == FidelViewMode.FAMILIES) {
                            Text(
                                text = "Tap a letter to see its full family! ✨",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(110.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(FidelData.families) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .clickable {
                                                viewModel.selectFamily(item)
                                                viewModel.speak(
                                                    "This is family ${item.letters[0].phonetic}",
                                                    "This is family ${item.letters[0].phonetic}"
                                                )
                                            }
                                            .testTag("family_card_${item.mainConsonant}"),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = item.mainConsonant,
                                                fontSize = 38.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = item.letters[0].phonetic,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = item.exampleEmoji,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // ALL LETTERS NAVIGATION GRID
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Horizon filter row for families
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    item {
                                        FilterChip(
                                            selected = selectedFamilyFilter == "ALL",
                                            onClick = {
                                                selectedFamilyFilter = "ALL"
                                                viewModel.speak("Showing all characters", "Showing all characters")
                                            },
                                            label = { Text("Show All ⭐", fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier.testTag("family_filter_all_letters")
                                        )
                                    }

                                    lazyRowItems(FidelData.families) { fam ->
                                        FilterChip(
                                            selected = selectedFamilyFilter == fam.mainConsonant,
                                            onClick = {
                                                selectedFamilyFilter = fam.mainConsonant
                                                viewModel.speak("Family ${fam.letters[0].phonetic}", "Family ${fam.letters[0].phonetic}")
                                            },
                                            label = {
                                                Text(
                                                    text = "${fam.mainConsonant} ${fam.exampleEmoji}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier.testTag("family_filter_chip_${fam.mainConsonant}")
                                        )
                                    }
                                }

                                Text(
                                    text = "Tap any letter below! 🔠🎧",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(72.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    items(filteredLetters) { (letter, family) ->
                                        val cardColor = getOrderColor(letter.order)
                                        val textAccentColor = getOrderTextAccent(letter.order)

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1.0f)
                                                .clickable {
                                                    explorerLetter = letter to family
                                                    viewModel.speak(
                                                        "${letter.character} ፦ ${letter.word}",
                                                        if (letter.translit.isNotEmpty()) "${letter.ttsPhonetic}... as in... ${letter.translit}" else letter.ttsPhonetic
                                                    )
                                                }
                                                .testTag("letter_card_${letter.character}"),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = cardColor
                                            ),
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(6.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = letter.character,
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = textAccentColor,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = letter.phonetic,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textAccentColor.copy(alpha = 0.8f),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 2. EXCEL FAMILY DETAILS SHEET - Tap orders to learn audio and spelling
                    var activeLetter by remember { mutableStateOf<AmharicLetter?>(null) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Word illustration card top (super high depth!)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = family.exampleEmoji,
                                        fontSize = 42.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "${family.exampleWord} (${family.exampleTranslit})",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Meaning: ${family.exampleEnglish}",
                                        fontSize = 16.sp,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        // Big central active speaking card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (activeLetter != null) {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            viewModel.speakAmharicLetterWeb(activeLetter!!.character, activeLetter!!.ttsPhonetic)
                                        }
                                        .testTag("learn_active_letter_card")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = activeLetter!!.emoji,
                                                    fontSize = 38.sp
                                                )
                                            }
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = activeLetter!!.character,
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.speakAmharicLetterWeb(activeLetter!!.character, activeLetter!!.ttsPhonetic)
                                                        },
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .testTag("replay_active_letter_learn_icon"),
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
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Sounds like: \"${activeLetter!!.phonetic}\"",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                                Text(
                                                    text = "Word: ${activeLetter!!.word} (${activeLetter!!.translit})",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                                Text(
                                                    text = "Means: ${activeLetter!!.english}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                                )
                                            }
                                        }

                                        FilledIconButton(
                                            onClick = {
                                                viewModel.speakAmharicLetterWeb(activeLetter!!.character, activeLetter!!.ttsPhonetic)
                                            },
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.testTag("speak_pronunciation_learn_card_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Hear native pronunciation"
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Tap any letter below to hear its sound! 🎧",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 7 Orders display list in raw rows
                        Text(
                            text = "7 Vowel Sounds (Orders):",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(vertical = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            family.letters.forEach { letter ->
                                val isSelected = activeLetter?.character == letter.character
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable {
                                            activeLetter = letter
                                            viewModel.speak(
                                                "${letter.character} ፦ ${letter.word}",
                                                if (letter.translit.isNotEmpty()) "${letter.ttsPhonetic}... as in... ${letter.translit}" else letter.ttsPhonetic
                                            )
                                        }
                                        .padding(vertical = 4.dp)
                                        .testTag("letter_order_${letter.character}")
                                ) {
                                    Text(
                                        text = letter.character,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = letter.phonetic,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "H${letter.order}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Triggering a trace shortcut
                        Button(
                            onClick = {
                                viewModel.speak("Let's trace ${family.mainConsonant}", "Let's trace")
                                // Go back to dashboard first to switch modes
                                onBack()
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen guide"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ቃላትን ተማር- ${if (activeLetter != null) "${activeLetter!!.word} ${activeLetter!!.emoji}" else "${family.exampleWord} ${family.exampleEmoji}"}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    explorerLetter?.let { (letter, family) ->
        Dialog(onDismissRequest = { explorerLetter = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("letter_explorer_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { explorerLetter = null },
                            modifier = Modifier.testTag("explorer_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close description"
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(family.exampleEmoji, fontSize = 24.sp)
                            Text(
                                text = "Hi! I am from the ${family.familyName} family!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(getOrderColor(letter.order))
                            .clickable {
                                viewModel.speak(
                                    "${letter.character} ፦ ${letter.word}",
                                    if (letter.translit.isNotEmpty()) "${letter.ttsPhonetic}... as in... ${letter.translit}" else letter.ttsPhonetic
                                )
                            }
                            .testTag("explorer_sound_board_trigger"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.character,
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Black,
                            color = getOrderTextAccent(letter.order)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Letter Sounds: \"${letter.phonetic}\"",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Vowel Order: ${letter.orderName} (${letter.order})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(letter.emoji, fontSize = 32.sp)
                            }
                            Column {
                                Text(
                                    text = "Fidel Word Association:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${letter.word} (${letter.translit})",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Means: ${letter.english}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Scribble Handwriting Tray ✏️",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val sandboxPoints = remember { mutableStateListOf<Offset>() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    sandboxPoints.add(change.position)
                                }
                            }
                            .testTag("mini_sandbox"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.character,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (sandboxPoints.size > 1) {
                                val path = Path().apply {
                                    moveTo(sandboxPoints[0].x, sandboxPoints[0].y)
                                    for (i in 1 until sandboxPoints.size) {
                                        lineTo(sandboxPoints[i].x, sandboxPoints[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0xFF29B6F6),
                                    style = Stroke(
                                        width = 6.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }

                        if (sandboxPoints.isNotEmpty()) {
                            IconButton(
                                onClick = { sandboxPoints.clear() },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .testTag("clear_sandbox")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Clear scribble",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.speak(
                                    "${letter.character} ፦ ${letter.word}",
                                    if (letter.translit.isNotEmpty()) "${letter.ttsPhonetic}... as in... ${letter.translit}" else letter.ttsPhonetic
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("explorer_hear_speech")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Hear sound"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("አድምጥ", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                explorerLetter = null
                                viewModel.selectFamily(family)
                                viewModel.speak(
                                    "Let's practice the complete group for ${family.familyName}",
                                    "Let's practice"
                                )
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("explorer_go_to_family")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Trace group"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ወደ መደቡ ሂድ", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
