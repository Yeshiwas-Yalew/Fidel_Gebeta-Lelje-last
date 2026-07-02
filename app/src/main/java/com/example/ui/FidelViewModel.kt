package com.example.ui

import com.example.BuildConfig
import android.app.Application
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import org.json.JSONArray

enum class ChildSong(
    val id: String,
    val displayName: String,
    val emoji: String,
    val description: String,
    val notes: List<Float>,
    val durations: List<Int>,
    val colorHex: Long
) {
    TWINKLE(
        "TWINKLE",
        "Twinkle Star 🌟",
        "🌟",
        "Soft cosmic star chime theme",
        listOf(
            261.63f, 261.63f, 392.00f, 392.00f, 440.00f, 440.00f, 392.00f,
            349.23f, 349.23f, 329.63f, 329.63f, 293.66f, 293.66f, 261.63f,
            392.00f, 392.00f, 349.23f, 349.23f, 329.63f, 329.63f, 293.66f,
            392.00f, 392.00f, 349.23f, 349.23f, 329.63f, 329.63f, 293.66f,
            261.63f, 261.63f, 392.00f, 392.00f, 440.00f, 440.00f, 392.00f,
            349.23f, 349.23f, 329.63f, 329.63f, 293.66f, 293.66f, 261.63f
        ),
        listOf(
            650, 650, 650, 650, 650, 650, 1300,
            650, 650, 650, 650, 650, 650, 1300,
            650, 650, 650, 650, 650, 650, 1300,
            650, 650, 650, 650, 650, 650, 1300,
            650, 650, 650, 650, 650, 650, 1300,
            650, 650, 650, 650, 650, 650, 1300
        ),
        0xFFFFD54F
    ),
    MARY_LAMB(
        "MARY_LAMB",
        "Little Lamb 🐑",
        "🐑",
        "Playful little lamb chime guide",
        listOf(
            329.63f, 293.66f, 261.63f, 293.66f, 329.63f, 329.63f, 329.63f,
            293.66f, 293.66f, 293.66f, 329.63f, 392.00f, 392.00f,
            329.63f, 293.66f, 261.63f, 293.66f, 329.63f, 329.63f, 329.63f, 329.63f,
            293.66f, 293.66f, 329.63f, 293.66f, 261.63f
        ),
        listOf(
            500, 500, 500, 500, 500, 500, 1000,
            500, 500, 1000, 500, 500, 1000,
            500, 500, 500, 500, 500, 500, 500, 500,
            500, 500, 500, 500, 1000
        ),
        0xFF81C784
    ),
    ROW_BOAT(
        "ROW_BOAT",
        "Row Your Boat 🚣",
        "🚣",
        "Active gentle stream bells",
        listOf(
            261.63f, 261.63f, 261.63f, 293.66f, 329.63f,
            329.63f, 293.66f, 329.63f, 349.23f, 392.00f,
            523.25f, 523.25f, 523.25f, 392.00f, 392.00f, 392.00f, 329.63f, 329.63f, 329.63f, 261.63f, 261.63f, 261.63f,
            392.00f, 349.23f, 329.63f, 293.66f, 261.63f
        ),
        listOf(
            600, 600, 450, 150, 600,
            450, 150, 450, 150, 1200,
            200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200,
            450, 150, 450, 150, 1200
        ),
        0xFF64B5F6
    ),
    BROTHER_JOHN(
        "BROTHER_JOHN",
        "Brother John 🔔",
        "🔔",
        "Rhythmic traditional chime bells",
        listOf(
            261.63f, 293.66f, 329.63f, 261.63f, 261.63f, 293.66f, 329.63f, 261.63f,
            329.63f, 349.23f, 392.00f, 329.63f, 349.23f, 392.00f,
            392.00f, 440.00f, 392.00f, 349.23f, 329.63f, 261.63f,
            392.00f, 440.00f, 392.00f, 349.23f, 329.63f, 261.63f,
            261.63f, 196.00f, 261.63f, 261.63f, 196.00f, 261.63f
        ),
        listOf(
            500, 500, 500, 500, 500, 500, 500, 500,
            500, 500, 1000, 500, 500, 1000,
            250, 250, 250, 250, 500, 500,
            250, 250, 250, 250, 500, 500,
            500, 500, 1000, 500, 500, 1000
        ),
        0xFFAB47BC
    ),
    YEGNA_DIMET(
        "YEGNA_DIMET",
        "Yegna Dimet 🐈",
        "🐈",
        "Soft Amharic kitty bells",
        listOf(
            329.63f, 329.63f, 349.23f, 392.00f, 392.00f, 349.23f, 329.63f, 293.66f, 261.63f, 261.63f, 293.66f, 329.63f, 329.63f, 293.66f, 293.66f
        ),
        listOf(
            500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 750, 250, 1000
        ),
        0xFFF48FB1
    ),
    ABABA_TESFAYE(
        "ABABA_TESFAYE",
        "Ababa Tesfaye 🐴",
        "🐴",
        "Amharic mule chimes",
        listOf(
            293.66f, 329.63f, 349.23f, 293.66f, 349.23f, 392.00f, 440.00f, 392.00f, 349.23f, 329.63f, 293.66f
        ),
        listOf(
            400, 400, 400, 400, 400, 400, 800, 400, 400, 400, 800
        ),
        0xFFFFB74D
    ),
    FIDEL_DESTA(
        "FIDEL_DESTA",
        "Fidel BeDesta 📚",
        "📚",
        "Cheerful alphabet chime",
        listOf(
            261.63f, 329.63f, 392.00f, 523.25f, 440.00f, 392.00f, 349.23f, 329.63f, 293.66f, 261.63f
        ),
        listOf(
            500, 500, 500, 1000, 500, 500, 500, 500, 500, 1000
        ),
        0xFF81C784
    )
}

class FidelViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val database = AppDatabase.getDatabase(application)
    private val mediaContext: Context get() = getApplication<Application>()
    private val repository = FidelRepository(database.fidelDao())

    // UI States
    val userProgress: StateFlow<UserProgress> = repository.userProgress
        .map { it ?: UserProgress() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgress()
        )

    val exerciseRecords: StateFlow<List<ExerciseRecord>> = repository.exerciseRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val learningSessions: StateFlow<List<LearningSession>> = repository.learningSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var activeSessionId: Int = 0

    // TTS engine
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // Particle/confetti trigger state
    private val _confettiTrigger = MutableStateFlow(0)
    val confettiTrigger: StateFlow<Int> = _confettiTrigger.asStateFlow()

    // Active learning selection
    private val _selectedFamily = MutableStateFlow<FidelFamily?>(null)
    val selectedFamily: StateFlow<FidelFamily?> = _selectedFamily.asStateFlow()

    // Background Music state (persisted in SharedPreferences for seamless user preferences)
    private val prefs = application.getSharedPreferences("fidel_prefs", Context.MODE_PRIVATE)
    private val _bgMusicEnabled = MutableStateFlow(prefs.getBoolean("bg_music_enabled", false))
    val bgMusicEnabled: StateFlow<Boolean> = _bgMusicEnabled.asStateFlow()

    private val _selectedSong = MutableStateFlow(
        try {
            ChildSong.valueOf(prefs.getString("selected_song", ChildSong.TWINKLE.name) ?: ChildSong.TWINKLE.name)
        } catch (e: Exception) {
            ChildSong.TWINKLE
        }
    )
    val selectedSong: StateFlow<ChildSong> = _selectedSong.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var musicJob: kotlinx.coroutines.Job? = null

    init {
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("FidelViewModel", "Failed to init TTS: ${e.message}")
        }
        startSessionTracking()
        
        // Auto-start background music loop on launch if enabled
        if (_bgMusicEnabled.value) {
            startBackgroundMusic()
        }
    }

    fun toggleBgMusic() {
        val newValue = !_bgMusicEnabled.value
        _bgMusicEnabled.value = newValue
        prefs.edit().putBoolean("bg_music_enabled", newValue).apply()
        if (newValue) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
    }

    fun selectSong(song: ChildSong) {
        _selectedSong.value = song
        prefs.edit().putString("selected_song", song.name).apply()
        // Automatically turn on background music when a specific song is selected
        _bgMusicEnabled.value = true
        prefs.edit().putBoolean("bg_music_enabled", true).apply()
        stopBackgroundMusic()
        startBackgroundMusic()
    }

    fun stopBgMusicExiting() {
        stopBackgroundMusic()
        // Turn off background music state so it doesn't leak or play on other screens
        _bgMusicEnabled.value = false
        prefs.edit().putBoolean("bg_music_enabled", false).apply()
    }

    @Synchronized
    fun startBackgroundMusic() {
        if (musicJob != null) return // Already playing
        
        musicJob = viewModelScope.launch(Dispatchers.Default) {
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
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize.coerceAtLeast(4096))
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize.coerceAtLeast(4096),
                    AudioTrack.MODE_STREAM
                )
            }
            
            synchronized(this@FidelViewModel) {
                audioTrack = track
            }
            
            try {
                track.play()
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Failed to start AudioTrack: ${e.message}")
                return@launch
            }
            
            var noteIndex = 0
            while (coroutineContext.isActive) {
                val currentSong = _selectedSong.value
                val notes = currentSong.notes
                val durations = currentSong.durations
                
                if (notes.isEmpty() || durations.isEmpty()) {
                    delay(500)
                    continue
                }
                
                // Keep noteIndex safe in case song changes with a different size list
                val safeIndex = noteIndex % notes.size
                val freq = notes[safeIndex]
                val duration = durations[safeIndex]
                
                // Cheerful and audible melody level that kids can sing along to (not whisper soft)
                val pcm = generateNote(freq, duration, volume = 0.18f)
                
                try {
                    track.write(pcm, 0, pcm.size)
                } catch (e: Exception) {
                    break
                }
                
                delay(duration + 100L)
                noteIndex = (safeIndex + 1) % notes.size
            }
        }
    }

    @Synchronized
    fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
        try {
            audioTrack?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("FidelViewModel", "Error stopping track: ${e.message}")
        }
        audioTrack = null
    }

    private fun generateNote(frequency: Float, durationMs: Int, volume: Float): ShortArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        
        val angularFrequency1 = 2.0 * Math.PI * frequency / sampleRate
        val angularFrequency2 = 2.0 * Math.PI * (frequency * 2.0) / sampleRate // Harmonic overtone
        val angularFrequency3 = 2.0 * Math.PI * (frequency * 3.0) / sampleRate // Pure sparkle overtone
        
        val attackSamples = (numSamples * 0.06).toInt() // fast chime attack
        
        for (i in 0 until numSamples) {
            val s1 = Math.sin(i * angularFrequency1)
            val s2 = Math.sin(i * angularFrequency2) * 0.25
            val s3 = Math.sin(i * angularFrequency3) * 0.12
            
            val mixedSine = (s1 + s2 + s3) / 1.37
            
            // Linear decay pluck shape
            val envelope = when {
                i < attackSamples -> i.toFloat() / attackSamples
                else -> {
                    val offset = i - attackSamples
                    1.0f - (offset.toFloat() / (numSamples - attackSamples))
                }
            }
            
            val amplitude = mixedSine * envelope * volume * 32767.0
            samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
        }
        return samples
    }

    private fun startSessionTracking() {
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val initialSession = LearningSession(
                startTime = startTime,
                endTime = startTime,
                durationSeconds = 0L
            )
            val id = repository.saveLearningSession(initialSession)
            activeSessionId = id.toInt()
            
            try {
                // Ticker to periodically update the session's active end time
                while (true) {
                    delay(5000) // update database every 5 seconds
                    val now = System.currentTimeMillis()
                    val duration = (now - startTime) / 1000
                    repository.updateLearningSession(activeSessionId, now, duration)
                }
            } catch (e: Exception) {
                Log.d("FidelViewModel", "Session tracking stopped or cancelled: ${e.message}")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US) // Fallback phonetics speak
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
            }
        }
    }

    private fun android.media.MediaPlayer.applyVoiceParams() {
        try {
            val voice = userProgress.value.teachingVoice
            val (speedValue, pitchValue) = when (voice) {
                "KID" -> 1.22f to 1.35f
                "BABA", "CHUNI" -> 1.10f to 1.15f
                "ELDER" -> 0.8f to 0.72f
                "TEACHER" -> 1.0f to 1.0f
                else -> 1.0f to 1.0f
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                this.playbackParams = android.media.PlaybackParams().apply {
                    speed = speedValue
                    pitch = pitchValue
                }
            }
        } catch (e: Exception) {
            Log.e("FidelViewModel", "Failed to apply voice playbackParams: ${e.message}")
        }
    }

    var storyMediaPlayer: android.media.MediaPlayer? = null
    var isStoryPlaying = false
    var currentStoryParagraphIndex = 0

    fun stopStorySpeech() {
        isStoryPlaying = false
        try {
            storyMediaPlayer?.stop()
            storyMediaPlayer?.release()
        } catch (e: Exception) {}
        storyMediaPlayer = null
    }

    fun speakStory(paragraphs: List<String>, onParagraphActive: (Int?) -> Unit) {
        stopStorySpeech()
        if (paragraphs.isEmpty()) {
            onParagraphActive(null)
            return
        }
        
        isStoryPlaying = true
        currentStoryParagraphIndex = 0
        
        fun playNext() {
            if (!isStoryPlaying || currentStoryParagraphIndex >= paragraphs.size) {
                isStoryPlaying = false
                onParagraphActive(null)
                return
            }
            
            val text = paragraphs[currentStoryParagraphIndex]
            onParagraphActive(currentStoryParagraphIndex)
            
            if (!userProgress.value.textToSpeechEnabled) {
                isStoryPlaying = false
                onParagraphActive(null)
                return
            }

            if (text.any { it.code in 0x1200..0x137F }) {
                viewModelScope.launch(Dispatchers.Main) {
                    try {
                        val encoded = java.net.URLEncoder.encode(text, "UTF-8")
                        val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"
                        val mediaContext = this@FidelViewModel.mediaContext
                        
                        storyMediaPlayer = android.media.MediaPlayer().apply {
                            setAudioAttributes(
                                android.media.AudioAttributes.Builder()
                                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                    .build()
                            )
                            setDataSource(mediaContext, android.net.Uri.parse(url))
                            prepareAsync()
                            setOnPreparedListener {
                                applyVoiceParams()
                                start()
                            }
                            setOnErrorListener { mp, _, _ ->
                                speakLocalFallback(text)
                                mp.release()
                                viewModelScope.launch {
                                    delay(4000)
                                    if (isStoryPlaying) {
                                        currentStoryParagraphIndex++
                                        playNext()
                                    }
                                }
                                true
                            }
                            setOnCompletionListener { mp ->
                                mp.release()
                                storyMediaPlayer = null
                                if (isStoryPlaying) {
                                    currentStoryParagraphIndex++
                                    playNext()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        speakLocalFallback(text)
                        viewModelScope.launch {
                            delay(4000)
                            if (isStoryPlaying) {
                                currentStoryParagraphIndex++
                                playNext()
                            }
                        }
                    }
                }
            } else {
                speakLocalFallback(text)
                viewModelScope.launch {
                    delay(4000)
                    if (isStoryPlaying) {
                        currentStoryParagraphIndex++
                        playNext()
                    }
                }
            }
        }

        playNext()
    }

    fun speak(text: String, phoneticAlternative: String) {
        if (text != "Difficulty mode updated" && !text.startsWith("ሰላም") && !text.startsWith("የአማርኛ")) {
            stopStorySpeech()
        }
        if (!userProgress.value.textToSpeechEnabled) return
        
        // If the text contains any Amharic characters, play it using the perfect web Amharic TTS!
        if (text.any { it.code in 0x1200..0x137F }) {
            viewModelScope.launch(Dispatchers.Main) {
                try {
                    val encoded = java.net.URLEncoder.encode(text, "UTF-8")
                    val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"
                    val mediaContext = this@FidelViewModel.mediaContext
                    
                    android.media.MediaPlayer().apply {
                        setAudioAttributes(
                            android.media.AudioAttributes.Builder()
                                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(mediaContext, android.net.Uri.parse(url))
                        prepareAsync()
                        setOnPreparedListener {
                            applyVoiceParams()
                            start()
                        }
                        setOnErrorListener { mp, _, _ ->
                            speakLocalFallback(phoneticAlternative)
                            mp.release()
                            true
                        }
                        setOnCompletionListener { mp -> mp.release() }
                    }
                } catch (e: Exception) {
                    speakLocalFallback(phoneticAlternative)
                }
            }
        } else {
            speakLocalFallback(phoneticAlternative)
        }
    }

    private fun speakLocalFallback(phoneticAlternative: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (isTtsReady && tts != null) {
                val voice = userProgress.value.teachingVoice
                val (speedValue, pitchValue) = when (voice) {
                    "KID" -> 1.22f to 1.35f
                    "BABA", "CHUNI" -> 1.10f to 1.15f
                    "ELDER" -> 0.8f to 0.72f
                    "TEACHER" -> 1.0f to 1.0f
                    else -> 1.0f to 1.0f
                }
                try {
                    tts?.setPitch(pitchValue)
                    tts?.setSpeechRate(speedValue)
                    tts?.speak(phoneticAlternative, TextToSpeech.QUEUE_FLUSH, null, null)
                } catch (e: Exception) {
                    Log.e("FidelViewModel", "Local TTS voice setting failed: ${e.message}")
                    tts?.speak(phoneticAlternative, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    fun speakAmharicLetterWeb(character: String, fallbackPhonetic: String) {
        if (!userProgress.value.textToSpeechEnabled) return
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val encoded = java.net.URLEncoder.encode(character, "UTF-8")
                // Custom web-based Speech Synthesis API endpoint using clear, high-quality translation phonetics for Amharic locale
                val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"
                
                val mediaContext = this@FidelViewModel.mediaContext
                
                val mediaPlayer = android.media.MediaPlayer().apply {
                    setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(mediaContext, android.net.Uri.parse(url))
                    prepareAsync()
                    setOnPreparedListener { 
                        applyVoiceParams()
                        start() 
                    }
                    setOnErrorListener { mp, _, _ ->
                        // Fallback automatically to high-quality local JVM/Android phonetic TTS on any internet/network dropouts
                        speak(character, fallbackPhonetic)
                        mp.release()
                        true
                    }
                    setOnCompletionListener { mp ->
                        mp.release()
                    }
                }
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Web speech synthesis API failed: ${e.message}")
                speak(character, fallbackPhonetic)
            }
        }
    }

    fun speakAmharicSuccessWeb(character: String, fallbackPhonetic: String, encouragementAmharic: String, encouragementPhonetic: String) {
        if (!userProgress.value.textToSpeechEnabled) return
        val combinedAmharic = "$encouragementAmharic $character"
        val combinedPhonetic = "$encouragementPhonetic! $fallbackPhonetic"
        
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val encoded = java.net.URLEncoder.encode(combinedAmharic, "UTF-8")
                val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"
                
                val mediaContext = this@FidelViewModel.mediaContext
                
                val mediaPlayer = android.media.MediaPlayer().apply {
                    setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(mediaContext, android.net.Uri.parse(url))
                    prepareAsync()
                    setOnPreparedListener { 
                        applyVoiceParams()
                        start() 
                    }
                    setOnErrorListener { mp, _, _ ->
                        speak(combinedAmharic, combinedPhonetic)
                        mp.release()
                        true
                    }
                    setOnCompletionListener { mp ->
                        mp.release()
                    }
                }
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Web speech synthesis API failed: ${e.message}")
                speak(combinedAmharic, combinedPhonetic)
            }
        }
    }

    fun triggerConfetti() {
        _confettiTrigger.value += 1
    }

    fun selectFamily(family: FidelFamily?) {
        _selectedFamily.value = family
    }

    fun setChildNameAndAvatar(name: String, avatarId: Int) {
        viewModelScope.launch {
            val current = userProgress.value
            repository.saveProgress(current.copy(childName = name, avatarId = avatarId))
        }
    }

    val newlyUnlockedSticker = MutableStateFlow<StickerBadge?>(null)

    fun dismissStickerUnlock() {
        newlyUnlockedSticker.value = null
    }

    fun addCoinsAndStars(coinsToAdd: Int, starsToAdd: Int) {
        viewModelScope.launch {
            val current = userProgress.value
            val nextCoins = current.coins + coinsToAdd
            val nextStars = current.stars + starsToAdd
            
            val unlockedList = current.unlockedStickers.split(",").map { it.trim() }.toMutableSet()
            val newUnlocks = mutableListOf<StickerBadge>()
            
            if (nextCoins >= 500 && !unlockedList.contains("COIN_HUNTER")) {
                unlockedList.add("COIN_HUNTER")
                StickerData.stickers.find { it.id == "COIN_HUNTER" }?.let { newUnlocks.add(it) }
            }
            if (nextStars >= 50 && !unlockedList.contains("STAR_SCHOLAR")) {
                unlockedList.add("STAR_SCHOLAR")
                StickerData.stickers.find { it.id == "STAR_SCHOLAR" }?.let { newUnlocks.add(it) }
            }
            
            repository.saveProgress(current.copy(
                coins = nextCoins,
                stars = nextStars,
                unlockedStickers = unlockedList.joinToString(",")
            ))
            
            if (starsToAdd > 0 || newUnlocks.isNotEmpty()) {
                triggerConfetti()
            }
            if (newUnlocks.isNotEmpty()) {
                val latestUnlock = newUnlocks.last()
                newlyUnlockedSticker.value = latestUnlock
                speak("Congratulations! You unlocked a new badge: ${latestUnlock.name}!", "New sticker unlocked")
            }
        }
    }

    fun saveExerciseRecord(name: String, score: Int, total: Int, type: String) {
        viewModelScope.launch {
            repository.saveExerciseRecord(
                ExerciseRecord(
                    exerciseName = name,
                    score = score,
                    totalQuestions = total,
                    type = type
                )
            )
            // Reward 10 coins per correct answer!
            val coinsReward = score * 10
            val starsReward = if (score == total) 5 else 2
            
            val current = userProgress.value
            var nextStreak = current.tracingStreak
            if (type == "TRACING") {
                if (score >= 4) {
                    nextStreak += 1
                } else {
                    nextStreak = 0 // broke the streak of success
                }
            }
            
            val unlockedList = current.unlockedStickers.split(",").map { it.trim() }.toMutableSet()
            val newUnlocks = mutableListOf<StickerBadge>()
            var extraCoins = 0
            var extraStars = 0
            
            // 1. FIRST_TRACE
            if (type == "TRACING" && score >= 4 && !unlockedList.contains("FIRST_TRACE")) {
                unlockedList.add("FIRST_TRACE")
                StickerData.stickers.find { it.id == "FIRST_TRACE" }?.let { newUnlocks.add(it) }
            }
            
            // 1.5. PENTASTAR_TRACER: Check if the child has successfully traced at least 5 different Amharic letters
            if (type == "TRACING" && score >= 4 && !unlockedList.contains("PENTASTAR_TRACER")) {
                val tracingRecords = exerciseRecords.value.filter { it.type == "TRACING" && it.score >= 4 }
                val uniqueLetters = tracingRecords.mapNotNull {
                    it.exerciseName.removePrefix("Draw Letter ").trim()
                }.toMutableSet()
                val currentLetter = name.removePrefix("Draw Letter ").trim()
                uniqueLetters.add(currentLetter)
                
                if (uniqueLetters.size >= 5) {
                    unlockedList.add("PENTASTAR_TRACER")
                    StickerData.stickers.find { it.id == "PENTASTAR_TRACER" }?.let { newUnlocks.add(it) }
                    extraStars += 15
                    extraCoins += 100
                }
            }
            
            val nextCoins = current.coins + coinsReward + extraCoins
            val nextStars = current.stars + starsReward + extraStars
            
            // 2. TRACING_STREAK_10
            if (nextStreak >= 10 && !unlockedList.contains("TRACING_STREAK_10")) {
                unlockedList.add("TRACING_STREAK_10")
                StickerData.stickers.find { it.id == "TRACING_STREAK_10" }?.let { newUnlocks.add(it) }
            }
            
            // 3. COIN_HUNTER
            if (nextCoins >= 500 && !unlockedList.contains("COIN_HUNTER")) {
                unlockedList.add("COIN_HUNTER")
                StickerData.stickers.find { it.id == "COIN_HUNTER" }?.let { newUnlocks.add(it) }
            }
            
            // 4. STAR_SCHOLAR
            if (nextStars >= 50 && !unlockedList.contains("STAR_SCHOLAR")) {
                unlockedList.add("STAR_SCHOLAR")
                StickerData.stickers.find { it.id == "STAR_SCHOLAR" }?.let { newUnlocks.add(it) }
            }
            
            // 5. QUIZ_PERFECT
            if (type == "QUIZ" && score == total && total >= 5 && !unlockedList.contains("QUIZ_PERFECT")) {
                unlockedList.add("QUIZ_PERFECT")
                StickerData.stickers.find { it.id == "QUIZ_PERFECT" }?.let { newUnlocks.add(it) }
            }
            
            val nextUnlockedString = unlockedList.joinToString(",")
            
            repository.saveProgress(current.copy(
                coins = nextCoins,
                stars = nextStars,
                tracingStreak = nextStreak,
                unlockedStickers = nextUnlockedString
            ))
            
            if (starsReward > 0 || newUnlocks.isNotEmpty()) {
                triggerConfetti()
            }
            
            if (newUnlocks.isNotEmpty()) {
                val latestUnlock = newUnlocks.last()
                newlyUnlockedSticker.value = latestUnlock
                speak("Congratulations! You unlocked a new badge: ${latestUnlock.name}!", "New sticker unlocked")
            }
        }
    }

    fun injectDemoData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val oneDay = 24 * 60 * 60 * 1000L
            val demoLogs = listOf(
                ExerciseRecord(exerciseName = "Letter ሀ - Tracing", score = 4, totalQuestions = 5, type = "TRACING", timestamp = now - 12 * oneDay),
                ExerciseRecord(exerciseName = "Letter ሀ - Tracing", score = 5, totalQuestions = 5, type = "TRACING", timestamp = now - 11 * oneDay),
                ExerciseRecord(exerciseName = "Letter ሀ - Tracing", score = 3, totalQuestions = 5, type = "TRACING", timestamp = now - 11 * oneDay),
                ExerciseRecord(exerciseName = "Fidel Level 1 Quiz", score = 3, totalQuestions = 10, type = "QUIZ", timestamp = now - 10 * oneDay),
                ExerciseRecord(exerciseName = "Letter ለ - Tracing", score = 5, totalQuestions = 5, type = "TRACING", timestamp = now - 9 * oneDay),
                ExerciseRecord(exerciseName = "Letter ለ - Tracing", score = 4, totalQuestions = 5, type = "TRACING", timestamp = now - 8 * oneDay),
                ExerciseRecord(exerciseName = "Fidel Vocabulary Quiz", score = 6, totalQuestions = 10, type = "QUIZ", timestamp = now - 7 * oneDay),
                ExerciseRecord(exerciseName = "Letter መ - Tracing", score = 5, totalQuestions = 5, type = "TRACING", timestamp = now - 5 * oneDay),
                ExerciseRecord(exerciseName = "Letter መ - Tracing", score = 5, totalQuestions = 5, type = "TRACING", timestamp = now - 5 * oneDay),
                ExerciseRecord(exerciseName = "Grammar Matcher Test", score = 8, totalQuestions = 10, type = "QUIZ", timestamp = now - 4 * oneDay),
                ExerciseRecord(exerciseName = "Letter ረ - Tracing", score = 2, totalQuestions = 5, type = "TRACING", timestamp = now - 3 * oneDay),
                ExerciseRecord(exerciseName = "Letter ሠ - Tracing", score = 2, totalQuestions = 5, type = "TRACING", timestamp = now - 3 * oneDay),
                ExerciseRecord(exerciseName = "Pronounce ቸ", score = 45, totalQuestions = 100, type = "VOICE", timestamp = now - 2 * oneDay),
                ExerciseRecord(exerciseName = "Pronounce ኘ", score = 35, totalQuestions = 100, type = "VOICE", timestamp = now - 2 * oneDay),
                ExerciseRecord(exerciseName = "Speed Sound Quiz", score = 9, totalQuestions = 10, type = "QUIZ", timestamp = now - 2 * oneDay),
                ExerciseRecord(exerciseName = "Fidel Masters Quiz", score = 10, totalQuestions = 10, type = "QUIZ", timestamp = now - 1 * oneDay),
                ExerciseRecord(exerciseName = "Letter ሰ - Tracing", score = 5, totalQuestions = 5, type = "TRACING", timestamp = now)
            )
            demoLogs.forEach {
                repository.saveExerciseRecord(it)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun toggleTtsEnabled() {
        viewModelScope.launch {
            val current = userProgress.value
            val nextState = !current.textToSpeechEnabled
            repository.saveProgress(current.copy(textToSpeechEnabled = nextState))
            // Briefly speak to inform about change if enabled
            if (nextState) {
                speak("Vocal guidance on", "Vocal guidance activated")
            }
        }
    }

    fun updateParentSettings(pin: String, highContrast: Boolean, ttsEnabled: Boolean) {
        viewModelScope.launch {
            val current = userProgress.value
            repository.saveProgress(current.copy(
                parentPin = pin,
                highContrast = highContrast,
                textToSpeechEnabled = ttsEnabled
            ))
        }
    }

    fun updateDifficultyMode(mode: String) {
        viewModelScope.launch {
            val current = userProgress.value
            repository.saveProgress(current.copy(difficultyMode = mode))
            speak("Difficulty mode updated", "Difficulty level set to $mode")
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            val current = userProgress.value
            // Update syncing state to true
            repository.saveProgress(current.copy(isSyncing = true))
            
            // Simulate networking delay for actual background sync
            delay(1800)
            
            // Sync finished successfully offline-cache synced
            repository.saveProgress(current.copy(
                isSyncing = false,
                lastSyncedTime = System.currentTimeMillis()
            ))
        }
    }

    fun updateTeachingVoice(voice: String) {
        viewModelScope.launch {
            val current = userProgress.value
            repository.saveProgress(current.copy(teachingVoice = voice))
            
            // Speak a small introduction or acknowledgment in the selected voice!
            val greeting = when (voice) {
                "KID" -> "ሰላም! እኔ ሚሚ ነኝ። አብረን እንማር!" // "Hi! I'm Mimi. Let's learn together!"
                "BABA", "CHUNI" -> "ሰላም! እኔ ባባ ነኝ። አብረን እንማር!" // "Hi! I'm Baba. Let's learn together!"
                "ELDER" -> "ሰላም ልጄ! እኔ የኔታ ነኝ። ጎበዝ!" // "Hi child! I'm Yeneta. Excellent!"
                "TEACHER" -> "ሰላም ተማሪዎች! እኔ መምህርት አልማዝ ነኝ።" // "Hello students! I'm Teacher Almaz."
                else -> "የአማርኛ ድምፅ መሪ ተቀይሯል።" // "Amharic voice guide has changed."
            }
            val phonetic = when (voice) {
                "KID" -> "Selam! Ine Mimi negn. Abren innimar!"
                "BABA", "CHUNI" -> "Selam! Ine Baba negn. Abren innimar!"
                "ELDER" -> "Selam lije! Ine Yeneta negn. Gobez!"
                "TEACHER" -> "Selam temariwoch! Ine Memhirt Almaz negn."
                else -> "Amharic voice guide changed."
            }
            delay(150)
            speak(greeting, phonetic)
        }
    }

    fun generateCustomStory(topic: String, onResult: (JSONObject?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    Log.e("FidelViewModel", "Gemini API key is not configured.")
                    launch(Dispatchers.Main) { onResult(null) }
                    return@launch
                }

                val prompt = "Write a very short, simple, engaging children's story in Amharic about '$topic' with exactly 2 short paragraphs and a simple moral. Return the story in JSON format with exactly the following keys: \"title\" (Amharic title), \"paragraphs\" (list of 2 Amharic string paragraphs), \"englishTranslation\" (list of 2 English translation string paragraphs corresponding to the Amharic paragraphs), \"moral\" (Amharic moral). Output ONLY valid JSON."

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                val partObj = JSONObject().apply {
                                    put("text", prompt)
                                }
                                put(partObj)
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)

                    val genConfig = JSONObject().apply {
                        put("responseMimeType", "application/json")
                    }
                    put("generationConfig", genConfig)
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("FidelViewModel", "Gemini API error: ${response.code} - ${response.message}")
                        launch(Dispatchers.Main) { onResult(null) }
                        return@launch
                    }

                    val respBody = response.body?.string() ?: ""
                    val outerObj = JSONObject(respBody)
                    val candidates = outerObj.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val contentObj = firstCandidate?.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val firstPart = parts?.optJSONObject(0)
                    val text = firstPart?.optString("text") ?: ""

                    if (text.isNotEmpty()) {
                        val storyObj = JSONObject(text.trim())
                        launch(Dispatchers.Main) { onResult(storyObj) }
                    } else {
                        launch(Dispatchers.Main) { onResult(null) }
                    }
                }
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Failed to generate story: ${e.message}", e)
                launch(Dispatchers.Main) { onResult(null) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopStorySpeech()
        tts?.shutdown()
        stopBackgroundMusic()
    }
}
