package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FidelViewModel
import com.example.ui.ChildSong

enum class LearnAppScreen {
    SPLASH_ONBOARDING,
    MAIN_MAP,
    LEARN_BOARD,
    PRACTICE_DRAWING,
    QUIZ_ARENA,
    SOUND_MATCHING_GAME,
    PARENT_REPORT,
    ALPHABET_REFERENCE,
    SENTENCE_FORMATION,
    VOICE_PRACTICE,
    AMHARIC_SONGS_STORIES
}

data class TutorVoice(
    val id: String,
    val avatar: String,
    val name: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: FidelViewModel) {
    var currentScreen by remember { mutableStateOf(LearnAppScreen.SPLASH_ONBOARDING) }

    val userProgress by viewModel.userProgress.collectAsState()
    val exerciseRecords by viewModel.exerciseRecords.collectAsState()
    val confettiTriggered by viewModel.confettiTrigger.collectAsState()
    val bgMusicEnabled by viewModel.bgMusicEnabled.collectAsState()

    var showStickerAlbumDialog by remember { mutableStateOf(false) }
    var showVoiceSelectionDialog by remember { mutableStateOf(false) }
    var showSongSelectionDialog by remember { mutableStateOf(false) }
    val newlyUnlockedSticker by viewModel.newlyUnlockedSticker.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            LearnAppScreen.SPLASH_ONBOARDING -> {
                SplashScreen(
                    viewModel = viewModel,
                    progress = userProgress,
                    onOnboardingCompleted = {
                        currentScreen = LearnAppScreen.MAIN_MAP
                    }
                )
            }

            LearnAppScreen.MAIN_MAP -> {
                // EXTREMELY INTUITIVE, COLORFUL MAIN MAP & DASHBOARD FOR CHILDREN (NON-READERS)
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Kids selected avatar
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = AVATARS.firstOrNull { it.id == userProgress.avatarId }?.emoji ?: "🦁",
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hello, ${userProgress.childName}! 🌟",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            },
                            actions = {
                                // Mute/Unmute toggle for children audio guidance
                                val isMuted = !userProgress.textToSpeechEnabled
                                IconButton(
                                    onClick = { viewModel.toggleTtsEnabled() },
                                    modifier = Modifier.testTag("mute_unmute_button")
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = if (isMuted) "Unmute Audio" else "Mute Audio",
                                        tint = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Soft instrumental background music toggle and selector
                                IconButton(
                                    onClick = {
                                        viewModel.speak("Choose your background music", "Choose your background music!")
                                        showSongSelectionDialog = true
                                    },
                                    modifier = Modifier.testTag("bg_music_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (bgMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                                        contentDescription = if (bgMusicEnabled) "Choose Background Music" else "Enable Background Music",
                                        tint = if (bgMusicEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }

                                // Tutor Voice selector button
                                val currentVoiceEmoji = when (userProgress.teachingVoice) {
                                    "KID" -> "👧"
                                    "BABA", "CHUNI" -> "👦"
                                    "ELDER" -> "👴"
                                    "TEACHER" -> "👩"
                                    else -> "👩‍🏫"
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.speak("Choose your Amharic teacher voice", "Choose your teacher voice!")
                                        showVoiceSelectionDialog = true
                                    },
                                    modifier = Modifier.testTag("voice_selector_button")
                                ) {
                                    Text(text = currentVoiceEmoji, fontSize = 22.sp)
                                }

                                // Sticker Book Album button
                                IconButton(
                                    onClick = {
                                        viewModel.speak("Opening sticker album", "Stickers and Badges Album Opened")
                                        showStickerAlbumDialog = true
                                    },
                                    modifier = Modifier.testTag("sticker_book_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Sticker Album",
                                        tint = Color(0xFFFFD54F)
                                    )
                                }

                                // Parental Gate entry button
                                IconButton(
                                    onClick = {
                                        viewModel.speak("Entering parents corner", "Entering parents corner")
                                        currentScreen = LearnAppScreen.PARENT_REPORT
                                    },
                                    modifier = Modifier.testTag("parent_gate_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Parent Gate",
                                        tint = MaterialTheme.colorScheme.primary
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Stat Rewards Bar for Children motivation (Coins, Stars)
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Gems/Coins",
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${userProgress.coins} Gems",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = "Stars Awarded",
                                        tint = Color(0xFFFF9100),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${userProgress.stars} Stars",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // High Contrast visual hero image (Cow & Amharic alphabet board)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = android.R.drawable.ic_menu_gallery), // safe fallback for preview
                                    contentDescription = "Amharic learning companion illustration",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f))
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Amharic Adventure! 🦁",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Learn, trace and play to earn shiny gems!",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Choose a Game Spot Below! 👇",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 12.dp)
                        )

                        // LARGE COLORFUL AND HIGHLY GRAPHICAL ADVENTURE MODULES FOR CHILDREN
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Spot 1: Alphabet Fidel Board
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clickable {
                                        viewModel.speak("Let's learn letters!", "Let's learn letters")
                                        currentScreen = LearnAppScreen.LEARN_BOARD
                                    }
                                    .testTag("map_spot_learn"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFB74D) // Kid friendly soft sun-colored orange
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📚", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Fidel Board ፊደል",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Listen to pronunciation guides!",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            // Spot 2: Handwriting Arena
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clickable {
                                        viewModel.speak("Time to practice writing!", "Time to practice writing")
                                        currentScreen = LearnAppScreen.PRACTICE_DRAWING
                                    }
                                    .testTag("map_spot_practice"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF64B5F6) // Kid friendly soft sky-blue
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✏️", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Handwriting Arena",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Trace characters to unlock stars!",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            // Spot 3: Quiz Arena
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clickable {
                                        viewModel.speak("Play Quiz and get Gems!", "Play Quiz and get Gems")
                                        currentScreen = LearnAppScreen.QUIZ_ARENA
                                    }
                                    .testTag("map_spot_quiz"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF81C784) // Kid friendly leaf-green
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🎮", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Fidel Master Quiz",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Answer simple word quizzes!",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            // Spot 4: Complete Alphabet Reference Chart
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clickable {
                                        viewModel.speak("Opening full alphabet reference guide", "Full Amharic Alphabet Chart!")
                                        currentScreen = LearnAppScreen.ALPHABET_REFERENCE
                                    }
                                    .testTag("map_spot_reference"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFAB47BC) // Kid friendly vibrant lavender/purple
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🔤", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Interactive Dictionary ፊደላት",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Pronunciation audio & stroke formation guides!",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            // Spot 5: Amharic Sound Matching Game
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(98.dp)
                                    .clickable {
                                        viewModel.speak("Let's play the Sound Matching game and earn extra stars!", "Sound Match Game!")
                                        currentScreen = LearnAppScreen.SOUND_MATCHING_GAME
                                    }
                                    .testTag("map_spot_sound_game"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF06292) // Playful strawberry pink
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🎵", fontSize = 28.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Sound Match ድምፅ",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Match Amharic audio sounds to earn extra stars!",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            // Spot 6: Amharic Sentence Builder Game
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(108.dp)
                                    .clickable {
                                        viewModel.speak("Let's build Amharic sentences! Fun grammar puzzle!", "Sentence Builder Game!")
                                        currentScreen = LearnAppScreen.SENTENCE_FORMATION
                                    }
                                    .testTag("map_spot_sentence_builder"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF26A69A) // Professional cheerful teal
                                )
                             ) {
                                  Row(
                                      modifier = Modifier
                                          .fillMaxSize()
                                          .padding(16.dp),
                                      verticalAlignment = Alignment.CenterVertically
                                  ) {
                                      Box(
                                          modifier = Modifier
                                              .size(52.dp)
                                              .clip(CircleShape)
                                              .background(Color.White),
                                          contentAlignment = Alignment.Center
                                      ) {
                                          Text("🧩", fontSize = 28.sp)
                                      }

                                      Spacer(modifier = Modifier.width(16.dp))

                                      Column {
                                          Text(
                                              text = "Sentence Builder ዐረፍተ ነገር",
                                              fontSize = 18.sp,
                                              fontWeight = FontWeight.Black,
                                              color = Color.White
                                          )
                                          Text(
                                              text = "Connect Amharic words into complete phrases and learn structure!",
                                              fontSize = 12.sp,
                                              color = Color.White.copy(alpha = 0.9f)
                                          )
                                      }
                                  }
                              }

                             // Spot 7: Voice Practice Game
                             Card(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .height(108.dp)
                                     .clickable {
                                         viewModel.speak("Let's practice Amharic pronunciation!", "Voice Practice!")
                                         currentScreen = LearnAppScreen.VOICE_PRACTICE
                                     }
                                     .testTag("map_spot_voice_practice"),
                                 shape = RoundedCornerShape(20.dp),
                                 colors = CardDefaults.cardColors(
                                     containerColor = Color(0xFF7E57C2) // Beautiful deep purple
                                 )
                              ) {
                                  Row(
                                      modifier = Modifier
                                          .fillMaxSize()
                                          .padding(16.dp),
                                      verticalAlignment = Alignment.CenterVertically
                                  ) {
                                      Box(
                                          modifier = Modifier
                                              .size(52.dp)
                                              .clip(CircleShape)
                                              .background(Color.White),
                                          contentAlignment = Alignment.Center
                                      ) {
                                          Text("🎙️", fontSize = 28.sp)
                                      }

                                      Spacer(modifier = Modifier.width(16.dp))

                                      Column {
                                          Text(
                                              text = "Voice Practice ድምፅ ልምምድ",
                                              fontSize = 18.sp,
                                              fontWeight = FontWeight.Black,
                                              color = Color.White
                                          )
                                          Text(
                                              text = "Speak into the microphone to practice your Amharic letters and words!",
                                              fontSize = 12.sp,
                                              color = Color.White.copy(alpha = 0.9f)
                                          )
                                      }
                                  }
                              }

                             // Spot 8: Amharic Songs & Stories
                             Card(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .height(108.dp)
                                     .clickable {
                                         viewModel.speak("Let's sing Amharic songs and read stories!", "Songs and Stories!")
                                         currentScreen = LearnAppScreen.AMHARIC_SONGS_STORIES
                                     }
                                     .testTag("map_spot_songs_stories"),
                                 shape = RoundedCornerShape(20.dp),
                                 colors = CardDefaults.cardColors(
                                     containerColor = Color(0xFFFF7043) // Warm golden-orange
                                 )
                              ) {
                                  Row(
                                      modifier = Modifier
                                          .fillMaxSize()
                                          .padding(16.dp),
                                      verticalAlignment = Alignment.CenterVertically
                                  ) {
                                      Box(
                                          modifier = Modifier
                                              .size(52.dp)
                                              .clip(CircleShape)
                                              .background(Color.White),
                                          contentAlignment = Alignment.Center
                                      ) {
                                          Text("🎶", fontSize = 28.sp)
                                      }

                                      Spacer(modifier = Modifier.width(16.dp))

                                      Column {
                                          Text(
                                              text = "Songs & Stories ዜማና ተረት",
                                              fontSize = 18.sp,
                                              fontWeight = FontWeight.Black,
                                              color = Color.White
                                          )
                                          Text(
                                              text = "Sing beautiful Amharic children's songs and read amazing moral tales!",
                                              fontSize = 12.sp,
                                              color = Color.White.copy(alpha = 0.9f)
                                          )
                                      }
                                  }
                              }

                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            LearnAppScreen.LEARN_BOARD -> {
                LearnScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.PRACTICE_DRAWING -> {
                PracticeScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.QUIZ_ARENA -> {
                QuizScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.SOUND_MATCHING_GAME -> {
                SoundMatchingScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.PARENT_REPORT -> {
                ParentScreen(
                    viewModel = viewModel,
                    progress = userProgress,
                    records = exerciseRecords,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.ALPHABET_REFERENCE -> {
                AlphabetReferenceScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.SENTENCE_FORMATION -> {
                SentenceFormationScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.VOICE_PRACTICE -> {
                VoicePracticeScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }

            LearnAppScreen.AMHARIC_SONGS_STORIES -> {
                SongsAndStoriesScreen(
                    viewModel = viewModel,
                    userProgress = userProgress,
                    onBack = { currentScreen = LearnAppScreen.MAIN_MAP }
                )
            }
        }

        // Floating gamified confetti trigger overlay
        StarConfettiEffect(trigger = confettiTriggered)

        if (showStickerAlbumDialog) {
            AlertDialog(
                onDismissRequest = { showStickerAlbumDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showStickerAlbumDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("sticker_album_close")
                    ) {
                        Text("አስደናቂ! 🚀")
                    }
                },
                title = {
                    Text("🏷️ Sticker Album", fontWeight = FontWeight.Black, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                },
                text = {
                    val unlockedIds = remember(userProgress.unlockedStickers) {
                        userProgress.unlockedStickers.split(",").map { it.trim() }.toSet()
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Trace 10 letters in a row to earn the Master Tracer badge! Click stickers to listen! 📢",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.height(300.dp)
                        ) {
                            items(StickerData.stickers.size) { index ->
                                val sticker = StickerData.stickers[index]
                                val isUnlocked = unlockedIds.contains(sticker.id)
                                
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUnlocked) Color(sticker.colorHex) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isUnlocked) {
                                                viewModel.speak(
                                                    "You unlocked ${sticker.name}! ${sticker.description}",
                                                    "You unlocked ${sticker.name}! ${sticker.description}"
                                                )
                                            } else {
                                                viewModel.speak(
                                                    "Locked badge. ${sticker.conditionText} to unlock!",
                                                    "Locked badge. Try hard to unlock!"
                                                )
                                            }
                                        }
                                        .border(
                                            width = if (isUnlocked) 2.dp else 1.dp,
                                            color = if (isUnlocked) Color.White else Color.Gray.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .testTag("sticker_item_${sticker.id.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(if (isUnlocked) Color.White.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isUnlocked) sticker.emoji else "🔒",
                                                fontSize = 32.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = sticker.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isUnlocked) Color.Black else MaterialTheme.colorScheme.outline,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = if (isUnlocked) sticker.description else sticker.conditionText,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            color = if (isUnlocked) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }

        newlyUnlockedSticker?.let { sticker ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissStickerUnlock() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissStickerUnlock() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.testTag("sticker_celebrate_dismiss")
                    ) {
                        Text("እንኳን ደስ አለህ! 🥳🎉", color = Color.White)
                    }
                },
                title = {
                    Text(
                        text = "🏆 NEW STICKER UNLOCKED!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFF9100)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(sticker.colorHex))
                                .border(width = 3.dp, color = Color.White, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sticker.emoji, fontSize = 54.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = sticker.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = sticker.description,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Condition: ${sticker.conditionText}",
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.testTag("new_sticker_dialog")
            )
        }

        if (showVoiceSelectionDialog) {
            AlertDialog(
                onDismissRequest = { showVoiceSelectionDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showVoiceSelectionDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("voice_selection_close")
                    ) {
                        Text("እሺ! 👍")
                    }
                },
                title = {
                    Text("🗣️ Choose Your Amharic Tutor!", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tap a teacher to change their voice in all learning spots! 🎙️",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        val currentVoice = userProgress.teachingVoice
                        val voiceTutors = listOf(
                            TutorVoice("DEFAULT", "👩‍🏫", "Aster (አስቴር)", "Standard pronunciation, clear & patient"),
                            TutorVoice("KID", "👧", "Mimi (ሚሚ)", "Playful kid voice, high pitch & high energy"),
                            TutorVoice("BABA", "👦", "Baba (ባባ)", "Playful baby boy voice, crystal clear & natural pacing"),
                            TutorVoice("ELDER", "👴", "Yeneta (የኔታ)", "Wise elder voice, slow, deep & caring"),
                            TutorVoice("TEACHER", "👩", "Teacher Almaz (አልማዝ)", "Crystal clear native speaker, natural pronunciation")
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            voiceTutors.forEach { tutor ->
                                val isSelected = currentVoice == tutor.id || (tutor.id == "BABA" && currentVoice == "CHUNI")
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateTeachingVoice(tutor.id)
                                        }
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .testTag("tutor_option_${tutor.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White.copy(alpha = 0.4f) else Color.Gray.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(tutor.avatar, fontSize = 28.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = tutor.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = tutor.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (showSongSelectionDialog) {
            AlertDialog(
                onDismissRequest = { showSongSelectionDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showSongSelectionDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("song_selection_close")
                    ) {
                        Text("እሺ! 👍")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎶 Music Station!",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (bgMusicEnabled) "ON" else "OFF",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bgMusicEnabled) Color(0xFF4CAF50) else Color.Gray
                            )
                            Switch(
                                checked = bgMusicEnabled,
                                onCheckedChange = { viewModel.toggleBgMusic() },
                                modifier = Modifier.testTag("dialog_music_switch")
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tap any cheerful song below to play in the background! 🌸",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        val currentSong by viewModel.selectedSong.collectAsState()
                        val songs = ChildSong.values()

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            songs.forEach { song ->
                                val isSelected = currentSong == song
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected && bgMusicEnabled) Color(song.colorHex).copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectSong(song)
                                            if (!bgMusicEnabled) {
                                                viewModel.toggleBgMusic()
                                            }
                                            viewModel.speak("Playing ${song.displayName}", "Playing ${song.displayName}")
                                        }
                                        .border(
                                            width = if (isSelected && bgMusicEnabled) 3.dp else 1.dp,
                                            color = if (isSelected && bgMusicEnabled) Color(song.colorHex) else Color.Gray.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .testTag("song_option_${song.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(song.colorHex).copy(alpha = 0.4f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(song.emoji, fontSize = 28.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = song.displayName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isSelected && bgMusicEnabled) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("🎵", fontSize = 12.sp)
                                                }
                                            }
                                            Text(
                                                text = song.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

// Help method to handle responsive sizes inside standard ratios
fun Modifier.fillHorizontalPercent(percent: Float): Modifier = this.then(
    fillMaxWidth(percent)
)
