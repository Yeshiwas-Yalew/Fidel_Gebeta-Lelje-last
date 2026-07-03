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
import java.io.File
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

enum class InstrumentStyle(
    val id: String,
    val displayName: String,
    val emoji: String,
    val description: String
) {
    CHIME_BELLS("CHIME_BELLS", "Chime Bells 🔔", "🔔", "Warm toy chime bells"),
    SWEET_FLUTE("SWEET_FLUTE", "Sweet Flute 🌬️", "🌬️", "Soft, breathy wind instrument"),
    RETRO_SYNTH("RETRO_SYNTH", "8-Bit Retro 👾", "👾", "Playful arcade square wave"),
    MUSIC_BOX("MUSIC_BOX", "Music Box 🎵", "🎵", "High-pitched sweet chime box"),
    CALM_HARP("CALM_HARP", "Calm Harp 🪕", "🪕", "Soft, gentle plucked strings")
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

    private val _selectedInstrument = MutableStateFlow(
        try {
            InstrumentStyle.valueOf(prefs.getString("selected_instrument", InstrumentStyle.CHIME_BELLS.name) ?: InstrumentStyle.CHIME_BELLS.name)
        } catch (e: Exception) {
            InstrumentStyle.CHIME_BELLS
        }
    )
    val selectedInstrument: StateFlow<InstrumentStyle> = _selectedInstrument.asStateFlow()

    fun selectInstrument(instrument: InstrumentStyle) {
        _selectedInstrument.value = instrument
        prefs.edit().putString("selected_instrument", instrument.name).apply()
        if (_bgMusicEnabled.value) {
            stopBackgroundMusic()
            startBackgroundMusic()
        }
    }

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
                
                val startTime = System.currentTimeMillis()
                
                // Cheerful and audible melody level that kids can sing along to (not whisper soft)
                val pcm = generateNote(freq, duration, volume = 0.18f)
                
                try {
                    track.write(pcm, 0, pcm.size)
                } catch (e: Exception) {
                    break
                }
                
                val elapsed = System.currentTimeMillis() - startTime
                // If write() took less time than the note duration, delay for the remaining duration
                val remainingDelay = duration - elapsed
                if (remainingDelay > 0) {
                    delay(remainingDelay)
                } else {
                    delay(10) // Brief pause to allow cancellation
                }
                
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
        val style = _selectedInstrument.value

        when (style) {
            InstrumentStyle.CHIME_BELLS -> {
                val angularFrequency1 = 2.0 * Math.PI * frequency / sampleRate
                val angularFrequency2 = 2.0 * Math.PI * (frequency * 2.0) / sampleRate
                val angularFrequency3 = 2.0 * Math.PI * (frequency * 3.0) / sampleRate
                val attackSamples = (numSamples * 0.06).toInt()
                for (i in 0 until numSamples) {
                    val s1 = Math.sin(i * angularFrequency1)
                    val s2 = Math.sin(i * angularFrequency2) * 0.25
                    val s3 = Math.sin(i * angularFrequency3) * 0.12
                    val mixedSine = (s1 + s2 + s3) / 1.37
                    val envelope = if (i < attackSamples) {
                        i.toFloat() / attackSamples
                    } else {
                        val offset = i - attackSamples
                        1.0f - (offset.toFloat() / (numSamples - attackSamples))
                    }
                    val amplitude = mixedSine * envelope * volume * 32767.0
                    samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
            }
            InstrumentStyle.SWEET_FLUTE -> {
                val angularFrequency = 2.0 * Math.PI * frequency / sampleRate
                val attackSamples = (numSamples * 0.15).toInt()
                val decaySamples = (numSamples * 0.15).toInt()
                for (i in 0 until numSamples) {
                    val sine = Math.sin(i * angularFrequency)
                    val windNoise = (Math.random() * 2.0 - 1.0) * 0.04
                    val mixed = sine + windNoise
                    val envelope = when {
                        i < attackSamples -> i.toFloat() / attackSamples
                        i > numSamples - decaySamples -> {
                            val offset = i - (numSamples - decaySamples)
                            1.0f - (offset.toFloat() / decaySamples)
                        }
                        else -> 1.0f
                    }
                    val amplitude = mixed * envelope * volume * 0.9f * 32767.0
                    samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
            }
            InstrumentStyle.RETRO_SYNTH -> {
                val period = sampleRate / frequency
                val attackSamples = (numSamples * 0.02).toInt()
                for (i in 0 until numSamples) {
                    val phase = i % period
                    val square = if (phase < period / 2.0) 0.4 else -0.4
                    val envelope = if (i < attackSamples) {
                        i.toFloat() / attackSamples
                    } else {
                        val offset = i - attackSamples
                        1.0f - (offset.toFloat() / (numSamples - attackSamples))
                    }
                    val amplitude = square * envelope * volume * 32767.0
                    samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
            }
            InstrumentStyle.MUSIC_BOX -> {
                val transposedFreq = frequency * 2.0f
                val angularFrequency1 = 2.0 * Math.PI * transposedFreq / sampleRate
                val angularFrequency2 = 2.0 * Math.PI * (transposedFreq * 2.0) / sampleRate
                val angularFrequency3 = 2.0 * Math.PI * (transposedFreq * 4.0) / sampleRate
                val attackSamples = (numSamples * 0.02).toInt()
                for (i in 0 until numSamples) {
                    val s1 = Math.sin(i * angularFrequency1)
                    val s2 = Math.sin(i * angularFrequency2) * 0.3
                    val s3 = Math.sin(i * angularFrequency3) * 0.15
                    val mixedSine = (s1 + s2 + s3) / 1.45
                    val envelope = if (i < attackSamples) {
                        i.toFloat() / attackSamples
                    } else {
                        val offset = i - attackSamples
                        val ratio = offset.toFloat() / (numSamples - attackSamples)
                        Math.pow(1.0 - ratio, 2.0).toFloat()
                    }
                    val amplitude = mixedSine * envelope * volume * 0.75f * 32767.0
                    samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
            }
            InstrumentStyle.CALM_HARP -> {
                val angularFrequency1 = 2.0 * Math.PI * frequency / sampleRate
                val angularFrequency2 = 2.0 * Math.PI * (frequency * 1.5) / sampleRate
                val angularFrequency3 = 2.0 * Math.PI * (frequency * 2.5) / sampleRate
                val attackSamples = (numSamples * 0.08).toInt()
                for (i in 0 until numSamples) {
                    val s1 = Math.sin(i * angularFrequency1)
                    val s2 = Math.sin(i * angularFrequency2) * 0.2
                    val s3 = Math.sin(i * angularFrequency3) * 0.08
                    val mixedSine = (s1 + s2 + s3) / 1.28
                    val envelope = if (i < attackSamples) {
                        i.toFloat() / attackSamples
                    } else {
                        val offset = i - attackSamples
                        val ratio = offset.toFloat() / (numSamples - attackSamples)
                        (1.0f - ratio) * (1.0f - ratio)
                    }
                    val amplitude = mixedSine * envelope * volume * 1.1f * 32767.0
                    samples[i] = amplitude.coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
            }
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

    private fun getAudioContext(): android.content.Context {
        return if (android.os.Build.VERSION.SDK_INT >= 30) {
            try {
                mediaContext.createAttributionContext("audioPlayback")
            } catch (e: Exception) {
                mediaContext
            }
        } else {
            mediaContext
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

    fun getGeminiVoiceSettings(): Pair<String, String> {
        val voice = userProgress.value.teachingVoice
        return when (voice) {
            "KID" -> "Puck" to "with a playful, high-pitched, child-friendly, extremely enthusiastic native Amharic voice"
            "BABA" -> "Fenrir" to "with a warm, friendly, loving, fatherly and comforting native Amharic voice"
            "ELDER" -> "Charon" to "with a wise, gentle, grandfatherly, slow, and highly respected elder's native Amharic voice"
            "TEACHER" -> "Kore" to "with a clear, instructional, articulate, professional, and patient native Amharic teacher's voice"
            "CHUNI" -> "Aoede" to "with a sweet, extremely joyful, cute, and lively native Amharic voice"
            else -> "Kore" to "with a warm, friendly, and natural native Amharic voice"
        }
    }

    private fun fetchGeminiTtsAudio(text: String, voiceName: String, voiceDescription: String, callback: (File?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    launch(Dispatchers.Main) { callback(null) }
                    return@launch
                }

                // Construct prompt to read clearly as native
                val prompt = "Pronounce this Amharic text extremely clearly, with a perfect, native Amharic accent, phrasing, and emotional feeling, configured as a speech tool for teaching students. Speak $voiceDescription. Read only this Amharic text, do not translate, do not add anything else: $text"

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-tts:generateContent?key=$apiKey"

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
                        val modalities = JSONArray().apply {
                            put("AUDIO")
                        }
                        put("responseModalities", modalities)

                        val prebuiltVoiceConfig = JSONObject().apply {
                            put("voiceName", voiceName)
                        }
                        val voiceConfig = JSONObject().apply {
                            put("prebuiltVoiceConfig", prebuiltVoiceConfig)
                        }
                        val speechConfig = JSONObject().apply {
                            put("voiceConfig", voiceConfig)
                        }
                        put("speechConfig", speechConfig)
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
                        Log.e("FidelViewModel", "Gemini TTS API error: ${response.code} - ${response.message}")
                        launch(Dispatchers.Main) { callback(null) }
                        return@launch
                    }

                    val respBody = response.body?.string() ?: ""
                    val outerObj = JSONObject(respBody)
                    val candidates = outerObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null) {
                                var base64Data: String? = null
                                for (i in 0 until parts.length()) {
                                    val part = parts.getJSONObject(i)
                                    val inlineData = part.optJSONObject("inlineData")
                                    if (inlineData != null) {
                                        base64Data = inlineData.optString("data")
                                        break
                                    }
                                }

                                if (!base64Data.isNullOrEmpty()) {
                                    val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                                    val tempFile = File.createTempFile("gemini_tts_", ".mp3", mediaContext.cacheDir)
                                    tempFile.writeBytes(bytes)
                                    launch(Dispatchers.Main) { callback(tempFile) }
                                    return@launch
                                }
                            }
                        }
                    }
                    launch(Dispatchers.Main) { callback(null) }
                }
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Error fetching Gemini TTS", e)
                launch(Dispatchers.Main) { callback(null) }
            }
        }
    }

    private fun playFallbackStoryAudio(text: String, playNext: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val encoded = java.net.URLEncoder.encode(text, "UTF-8")
                val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"

                storyMediaPlayer = android.media.MediaPlayer().apply {
                    setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(getAudioContext(), android.net.Uri.parse(url))
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
    }

    private fun playFallbackGeneralAudio(text: String, phoneticAlternative: String) {
         viewModelScope.launch(Dispatchers.Main) {
             try {
                 val encoded = java.net.URLEncoder.encode(text, "UTF-8")
                 val url = "https://translate.google.com/translate_tts?ie=UTF-8&tl=am&client=tw-ob&q=$encoded"

                 android.media.MediaPlayer().apply {
                     setAudioAttributes(
                         android.media.AudioAttributes.Builder()
                             .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                             .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                             .build()
                     )
                     setDataSource(getAudioContext(), android.net.Uri.parse(url))
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
                val (voiceName, voiceDesc) = getGeminiVoiceSettings()
                fetchGeminiTtsAudio(text, voiceName, voiceDesc) { tempFile ->
                    if (tempFile != null) {
                        viewModelScope.launch(Dispatchers.Main) {
                            try {
                                if (!isStoryPlaying) {
                                    try { tempFile.delete() } catch (e: Exception) {}
                                    return@launch
                                }
                                storyMediaPlayer = android.media.MediaPlayer().apply {
                                    setAudioAttributes(
                                        android.media.AudioAttributes.Builder()
                                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                    setDataSource(getAudioContext(), android.net.Uri.fromFile(tempFile))
                                    prepareAsync()
                                    setOnPreparedListener {
                                        start()
                                    }
                                    setOnErrorListener { mp, _, _ ->
                                        speakLocalFallback(text)
                                        mp.release()
                                        try { tempFile.delete() } catch (e: Exception) {}
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
                                        try { tempFile.delete() } catch (e: Exception) {}
                                        if (isStoryPlaying) {
                                            currentStoryParagraphIndex++
                                            playNext()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                try { tempFile.delete() } catch (e2: Exception) {}
                                playFallbackStoryAudio(text, ::playNext)
                            }
                        }
                    } else {
                        playFallbackStoryAudio(text, ::playNext)
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
            val (voiceName, voiceDesc) = getGeminiVoiceSettings()
            fetchGeminiTtsAudio(text, voiceName, voiceDesc) { tempFile ->
                if (tempFile != null) {
                    viewModelScope.launch(Dispatchers.Main) {
                        try {
                            android.media.MediaPlayer().apply {
                                setAudioAttributes(
                                    android.media.AudioAttributes.Builder()
                                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                        .build()
                                )
                                setDataSource(getAudioContext(), android.net.Uri.fromFile(tempFile))
                                prepareAsync()
                                setOnPreparedListener {
                                    start()
                                }
                                setOnErrorListener { mp, _, _ ->
                                    speakLocalFallback(phoneticAlternative)
                                    mp.release()
                                    try { tempFile.delete() } catch (e: Exception) {}
                                    true
                                }
                                setOnCompletionListener { mp ->
                                    mp.release()
                                    try { tempFile.delete() } catch (e: Exception) {}
                                }
                            }
                        } catch (e: Exception) {
                            try { tempFile.delete() } catch (e2: Exception) {}
                            playFallbackGeneralAudio(text, phoneticAlternative)
                        }
                    }
                } else {
                    playFallbackGeneralAudio(text, phoneticAlternative)
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
        speak(character, fallbackPhonetic)
    }

    fun speakAmharicSuccessWeb(character: String, fallbackPhonetic: String, encouragementAmharic: String, encouragementPhonetic: String) {
        val combinedAmharic = "$encouragementAmharic $character"
        val combinedPhonetic = "$encouragementPhonetic! $fallbackPhonetic"
        speak(combinedAmharic, combinedPhonetic)
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

    fun toggleHighContrast() {
        viewModelScope.launch {
            val current = userProgress.value
            val nextState = !current.highContrast
            repository.saveProgress(current.copy(highContrast = nextState))
            if (nextState) {
                speak("High contrast mode on", "High contrast mode activated")
            } else {
                speak("High contrast mode off", "Standard colorful theme activated")
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

    private fun generateLocalFallbackStory(topic: String): JSONObject {
        val cleanTopic = topic.trim().lowercase()
        val json = JSONObject()
        
        when {
            cleanTopic.contains("dog") || cleanTopic.contains("ውሻ") -> {
                json.put("title", "የቀልደኛው ውሻ ቡቢ አስቂኝ ጀብዱ")
                json.put("paragraphs", JSONArray().apply {
                    put("በአንድ ትንሽ መንደር ውስጥ ቡቢ የሚባል በጣም ቀልደኛና አስቂኝ ውሻ ይኖር ነበር። ቡቢ እንደ ሌሎቹ ውሾች መጮህ አይወድም ነበር፤ ይልቁንም ጠዋት ጠዋት ዶሮዎች ከመጮሃቸው በፊት እሱ ራሱ 'ኩኩሉኡኡ!' እያለ በመጮህ ሰዎችን ይቀሰቅስ ነበር። በተጨማሪም ባለቀለም ኮፍያ ማድረግና በሁለት እግሮቹ መደነስ ዋነኛ የትርፍ ጊዜ ማሳለፊያው ነበረ።")
                    put("አንድ ቀን እረኛው አንዲት ትንሽ በግ ጠፍታበት ሲጨነቅ አየው። ቡቢ ወዲያውኑ ባለቀለም ኮፍያውን አድርጎ በጎን ለመፈለግ ወደ ጫካው ሮጠ። ጫካ ውስጥ ሲገባ አንዲት ተኩላ በጓን ልትበላ ሲያኮረኩር አገኛት፤ ቡቢ ግን በመጮህ ፈንታ ፊቷ ቆሞ በጣም የሚያስቅ የቀልድ ጭፈራና ፊቱን የማጣመም ጨዋታ አሳያት። ተኩላዋ በሳቅ ብዛት ሆዷን ይዛ መሬት ላይ ተንከባለለች፤ በጉም በሳቅ እየፈነዳች አመለጠች!")
                    put("ቡቢ በጓን በትከሻው ላይ አዝሎ በኩራትና በደስታ ወደ መንደሩ ተመለሰ። ከመንደሩ ሰዎች ሁሉ በስጦታ የተለያየ ቀለም ያላቸው ኮፍያዎች ተበረከቱለት። ከዚያን ቀን ጀምሮ ቡቢ ታማኝ ጠባቂ ብቻ ሳይሆን የመንደሩ ታላቅ የቀልድ ንጉሥ ሆኖ በደስታ ኖረ።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("In a small village lived a very comical and funny dog named Bubi. Bubi didn't like to bark like other dogs; instead, he woke up the villagers in the morning by shouting 'Kookooloo!' even before the roosters. Additionally, wearing a colorful hat and dancing on his two hind legs was his main hobby.")
                    put("One day, he saw the shepherd worried about a lost little lamb. Bubi immediately put on his colorful hat and ran into the forest to find the lamb. In the forest, he found a wolf preparing to eat the lamb; instead of barking, Bubi stood in front of the wolf and performed a hilarious, funny dance with a goofy face. The wolf rolled on the ground laughing so hard, and the lamb escaped while giggling!")
                    put("Bubi carried the lamb back on his shoulders proudly. The villagers rewarded him with many colorful hats of different shapes. From that day on, Bubi became not only a loyal guardian but also the village's supreme comedy king.")
                })
                json.put("moral", "ደግነትና አስቂኝ ቀልድ ትልቁን ጠላት እንኳ ማሸነፍ ይችላል። (Kindness and a funny joke can conquer even the greatest enemy.)")
            }
            cleanTopic.contains("elephant") || cleanTopic.contains("ዝሆን") || cleanTopic.contains("ሃርማ") -> {
                json.put("title", "ደንበኛውና ተወዛዋዡ ዝሆን ቶቶ")
                json.put("paragraphs", JSONArray().apply {
                    put("ቶቶ የሚባል እጅግ በጣም ትልቅ ነገር ግን ቀልደኛ ዝሆን በጥልቁ ጫካ ውስጥ ይኖር ነበር። ቶቶ በረጅሙ አፍንጫው ውኃ ለመጠጣት ሲሞክር ሁልጊዜ ፊኛዎችና አረፋዎችን በመስራት የጫካውን እንስሳት ያጠጣ ነበር። ትልቁ ምኞቱ ታዋቂ የዳንስ አሰልጣኝ መሆን ነበር፤ ስለዚህም በትላልቅ ዛፎች መካከል በጫጫታ እየጨፈረ እንስሳቱን ያስቅ ነበር።")
                    put("አንድ ወቅት በጫካው ውስጥ ከባድ የድርቅ ዘመን መጣና ሁሉም ኩሬዎች ደረቁ። እንስሳቱ በሙሉ ተጠምተው ሳለ፥ ቶቶ ድንገት የዳንስ እንቅስቃሴውን ጀመረና በኃይል መሬቱን በእግሮቹ መርገጥ (ስታምፕ ማድረግ) ጀመረ። እንስሳቱ 'ቶቶ በዚህ በጭንቅ ሰዓት ምን ያስፈንጥዝሃል!' ብለው ሲቆጡት፥ እሱ ግን 'ይህ አዲስ የዝናብ ዳንስ ነው!' እያለ መዝለሉን ቀጠለ።")
                    put("በመጨረሻም ቶቶ በዳንሱ መሬቱን በጣም ከመታው የተነሳ፥ መሬቱ ተከፍቶ አንድ ትልቅና ጣፋጭ የከርሰ ምድር ውኃ ምንጭ ፈነዳ! እንስሳቱ በሙሉ በደስታ እየጮሁና እያሸበሸቡ የቶቶን የዳንስ ስልት እየተከተሉ ውኃውን ጠጡ። ቶቶም ታላቅ የቀልድና የዳንስ ጀግና ተባለ።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("A very huge but comical elephant named Toto lived in the deep forest. When Toto tried to drink water with his long trunk, he always ended up blowing bubbles and splashing the forest animals instead. His biggest dream was to be a famous dance coach; thus, he danced clumsily among the big trees, making everyone laugh.")
                    put("Once, a severe drought came to the forest, and all the ponds dried up. While the animals were thirsty, Toto suddenly started his crazy dance moves and stomped the ground heavily with his feet. When the animals got angry, saying, 'Toto, why are you dancing in this crisis!', he replied, 'This is my new rain dance!' and kept jumping.")
                    put("Finally, because Toto stomped the ground so hard during his dance, the ground cracked open and a huge, delicious underground water spring gushed out! All the animals cheered, danced Toto's style, and drank the water. Toto was declared the ultimate hero of comedy and dance.")
                })
                json.put("moral", "ደስታንና ፈገግታን ማጋራት ሁልጊዜም አስቸጋሪ ጊዜን ያቃልላል። (Sharing joy and laughter always makes difficult times easier.)")
            }
            cleanTopic.contains("monkey") || cleanTopic.contains("ጦጣ") || cleanTopic.contains("ዝንጀሮ") -> {
                json.put("title", "አስቂኙና ቀልደኛው ጦጣ ሚኪ")
                json.put("paragraphs", JSONArray().apply {
                    put("ሚኪ የሚባል ተንኮለኛ፣ ብልጥና እጅግ አስቂኝ ጦጣ በትልቅ ዛፍ ላይ ይኖር ነበር። ሚኪ ሙዝ መብላት ብቻ ሳይሆን ሙዝን እንደ ስልክ በመጠቀም 'ሃሎ! የጫካው ንጉስ አንበሳ እባክህ ዛሬ እራት ሙዝ ጋብዘኝ' እያለ በሙዝ ስልክ ማውራትና እንስሳቱን ማሳቅ ይወድ ነበር።")
                    put("አንድ ቀን አንድ ስግብግብ አዞ ከሚኪ ጋር ጓደኛ ሆኖ ወደ ወንዙ መሃል ከወሰደው በኋላ፥ 'ሚስቴ የጦጣ ልብ መብላት ስለምትፈልግ ልብህን ልወስደው ነው' አለው። ሚኪ ፈገግ ብሎ 'ወንድሜ አዞ፥ ልቤን እኮ ዛፍ ላይ ጥዬው መጣሁ! ያውም ሙዝ ውስጥ ነው የደበቅኩት፤ ዛሬ ደግሞ የልቤን ቀልድ የምትነግረው ከሆነ ራሱ በሳቅ ይፈነዳል' አለው።")
                    put("አዞው ሞኝ ስለነበር እውነት መስሎት ወደ ዛፉ መለሰው። ሚኪም ወዲያው ዛፍ ላይ ወጥቶ አንድ ሙዝ ወረወረለትና 'ይኸውልህ ልቤ! ቀልዴ ደግሞ፡ አዞዎች ዛፍ መውጣት ቢሞክሩ ኖሮ ምን ይሆናሉ? ይወድቃሉ!' ብሎት በሳቅ ፈነዳ። አዞው በሞኝነቱ እያፈረ ወደ ወንዙ ተመለሰ።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("A mischievous, clever, and hilarious monkey named Miki lived on a big tree. Miki loved not only eating bananas but also using a banana as a phone, talking into it, 'Hello! King lion of the forest, please invite me for banana dinner tonight', making all the animals laugh.")
                    put("One day, a greedy alligator befriended Miki, took him to the middle of the river, and said, 'My wife wants to eat a monkey's heart, so I am taking yours.' Miki smiled and said, 'Brother alligator, I left my heart on the tree! Hidden inside a banana. If you tell it a joke, it will literally burst with laughter!'")
                    put("The alligator was foolish and believed him, returning Miki to the tree. Miki immediately climbed up, threw a banana down, and yelled, 'Here is my heart! And the joke is: what happens when alligators try to climb trees? They fall!' and laughed hysterically. The alligator returned to the river, embarrassed by his foolishness.")
                })
                json.put("moral", "በጭንቅ ጊዜ ቀልድና ብልሃት ካጋጠመን አደጋ ያድነናል። (In difficult times, humor and intelligence will save us from danger.)")
            }
            cleanTopic.contains("frog") || cleanTopic.contains("ጓጉር") || cleanTopic.contains("ጓጉንቸር") -> {
                json.put("title", "የቀልደኛዋና ዝላይ አፍቃሪዋ ጓጉንቸር ፊፊ አስቂኝ ጉዞ")
                json.put("paragraphs", JSONArray().apply {
                    put("ፊፊ የምትባል አስቂኝ ጓጉንቸር በጥልቅ ጉድጓድ ውስጥ ትኖር ነበር። ፊፊ ሌሊት ሌሊት እንደ ኦፔራ ዘፋኝ 'ክሮአክ! ክሮአክ!' እያለች ጮክ ብላ በመዘመር የጉድጓዱን ነዋሪዎች እንቅልፍ ትነሳ ነበር። ሁሉም 'ፊፊ ድምፅሽ አይሰማም! ውጭውም ዓለም አደገኛ ነው አትውጪ!' እያሉ ይመክሯት ነበር።")
                    put("ፊፊ ግን መስማት ስለማትችል፥ ሁሉም በዜማዋ ተገርመው 'እባክሽን ጨምሪበት!' እያሉ የሚያበረቷት ይመስላት ነበር። ስለዚህም በደስታና በአስቂኝ ሁኔታ የፊት እግሮቿን ወደ ላይ ዘርግታ በአንድ እግሯ እየዘለለች ከጉድጓዱ መውጫ ላይ ወጣች!")
                    put("ውጭ እንደወጣች፥ ትልቁን ሜዳና ወንዝ አየች። እዚያም ለነበሩት ወፎችና ቢራቢሮዎች የኦፔራ ዘፈኗንና የኮሜዲ ትዕይንቷን አቀረበችላቸው። ሁሉም በሳቅና በደስታ አጨበጨቡላት፤ ፊፊም የጫካው ታላቅ የኦፔራ ኮሜዲያን ሆነች!")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("A comical frog named Fifi lived in a deep well. Every night, she used to sing loudly like an opera singer, shouting 'Croak! Croak!' and waking up all the well's residents. Everyone advised her, 'Fifi, you are out of tune! And the outside world is dangerous, don't go there!'")
                    put("But since Fifi was deaf, she thought everyone was amazed by her song and cheering her on to sing more. So she happily stretched her front legs up and jumped high out of the well on one foot!")
                    put("Once outside, she saw the big meadow and river. She performed her opera song and stand-up comedy show for the birds and butterflies there. Everyone clapped and laughed; Fifi became the forest's greatest opera comedian!")
                })
                json.put("moral", "የሰዎችን አሉታዊ ተስፋ አስቆራጭ ወሬዎች ባለመስማት አስቂኝ ህልማችንን ማሳካት እንችላለን። (By ignoring people's discouraging words, we can achieve our funniest dreams.)")
            }
            cleanTopic.contains("bird") || cleanTopic.contains("ወፍ") || cleanTopic.contains("ቆቅ") -> {
                json.put("title", "ቀልደኛዋና ድምፅ አስመሳይዋ ወፍ ቺቺ")
                json.put("paragraphs", JSONArray().apply {
                    put("ቺቺ የምትባል በጣም ቀልደኛና ድምፅ አስመሳይ ወፍ በጥንታዊ ዛፍ ላይ ትኖር ነበር። ቺቺ እንደ ሌሎቹ ወፎች በጣፋጭ ድምፅ መዘመር አሰልቺ ሆኖባታል። ይልቁንም ጠዋት ጠዋት እንደ ፍየል ማስነጠስ ወይም እንደ ዝንጀሮ መሳቅ የመሳሰሉ አስቂኝ ድምፆችን በማስመሰል የጫካውን እንስሳት በሳቅ ትቀሰቅስ ነበር።")
                    put("አንድ ቀን ቺቺ በጣም ቀዝቃዛ አይስክሬም ስለበላች ድምፅዋ ሙሉ በሙሉ ተዘጋ። የጫካው እንስሳት ጠዋት ሲነቁ የቺቺን አስቂኝ ድምፅ ባለመስማታቸው በጣም አዘኑ። ያለ እሷ ቀልድና ሳቅ ማለዳው ባዶ ሆነባቸው።")
                    put("ስለዚህም ዝንጀሮውና ፍየሉ ማርና የሞቀ ሻይ በአንድነት አዘጋጅተው አጠጧት። ድምፅዋ ወዲያው ሲመለስ፥ ቺቺ በአመስጋኝነት 'የፍየል ሻይና የዝንጀሮ ማር!' የሚል አዲስ አስቂኝ ዘፈን በመዘመር እንስሳቱን በሳቅ አፈነዳቻቸው።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("A very funny and mimic bird named Chichi lived on an ancient tree. Chichi found singing sweet songs like other birds boring. Instead, she woke up the forest animals in the morning with hilarious sound effects, like a sneezing goat or a laughing monkey.")
                    put("One day, because she ate extremely cold ice cream, Chichi lost her voice completely. When the animals woke up, they were sad not to hear Chichi's funny voices. Without her jokes and laughter, the morning felt empty.")
                    put("So, the monkey and the goat together prepared honey and warm tea for her. As soon as her voice returned, Chichi gratefully sang a new hilarious song called 'Goat's Tea and Monkey's Honey,' making them burst into laughter.")
                })
                json.put("moral", "የጋራ ፈገግታና ቀልድ ሁልጊዜም እርስ በርስ ፍቅርንና ደግነትን ያጠናክራል። (Shared laughter and humor always strengthen love and kindness between us.)")
            }
            cleanTopic.contains("star") || cleanTopic.contains("ኮከብ") || cleanTopic.contains("ጨረቃ") -> {
                json.put("title", "የሚያንኮራፋውና የሚያብለጨልጨው ኮከብ ሉሉ")
                json.put("paragraphs", JSONArray().apply {
                    put("ሉሉ የምትባል በጣም ትንሽና ቀልደኛ ኮከብ በሰማይ ላይ ትኖር ነበር። ሉሉ ስትተኛ በጣም በከፍተኛ ድምፅ 'ኮርርር! ኮርርር!' እያለች ታንኮራፋ ነበር። በምታንኮራፋበት ጊዜ ሁሉ ብርሃኗ በአስቂኝ ሁኔታ 'ብልጭ! ድርግም!' እያለ ይበራና ይጠፋ ነበር።")
                    put("አንድ ቀን አንድ መንገደኛ በጨለማ ጫካ ውስጥ መንገድ ጠፋው። ድንገት ከሰማይ የመጣውን ከፍተኛ የአንኮራፋ ድምፅና በዚያው ፍጥነት የሚበራውንና የሚጠፋውን የሉሉን ብርሃን አየ። መንገደኛው 'ይህ ሰማያዊ ዲስኮ ጭፈራ ምንድነው?' እያለ እየሳቀ ወደ ብርሃኑ አቅጣጫ በመሄድ መንገዱን አገኘ።")
                    put("ሉሉ ከእንቅልፏ ስትነቃ፥ አስቂኝ ማንኮራፋቷ ሰዎችን ከፈገግታ አልፎ መንገድ እንዲያገኙ እንደረዳቸው ተረዳች። ከዚያን ቀን ጀምሮ ሉሉ ማንኮራፋቷን አልተወችም፤ ይልቁንም 'የሚያንኮራፋው የዲስኮ ኮከብ' ተብላ በሰማይ ላይ በደስታ መብረቅረቋን ቀጠለች።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("A very small and funny star named Lulu lived in the sky. When she slept, she snored extremely loudly, sounding like 'Kooorrr! Kooorrr!' Every time she snored, her light hilariously flashed on and off.")
                    put("One day, a traveler got lost in a dark forest. Suddenly, he heard the loud snoring from the sky and saw Lulu's flashing light. The traveler laughed, wondering, 'What is this heavenly disco dance?', followed the light, and found his way.")
                    put("When Lulu woke up, she realized that her funny snoring had not only brought smiles but also helped people find their way. From that day on, she never stopped snoring; instead, she continued to shine happily as 'The Snoring Disco Star.'")
                })
                json.put("moral", "የእኛ ልዩና አስቂኝ ባህሪ እንኳ ለሌሎች ሰዎች ትልቅ ጥቅም ሊኖረው ይችላል። (Even our unique and funny quirks can be of great benefit to others.)")
            }
            else -> {
                val capitalizedTopic = topic.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                json.put("title", "ስለ $capitalizedTopic የተደረገው እጅግ አስቂኝና ረጅም ጉዞ")
                json.put("paragraphs", JSONArray().apply {
                    put("በአንድ ወቅት፥ በጥልቁና ሰፊው ጫካ ውስጥ የሚኖሩ እንስሳት በአንድነት ሆነው ስለ '$capitalizedTopic' እጅግ በጣም አስቂኝ የሆኑ ታሪኮችንና ሚስጥሮችን ለማወቅ ፈለጉ። ብልሁ ጥንቸልና ደጉ ዝሆን ተነስተው ረጅሙንና በሙዝ የተሞላውን ተራራ ለመውጣትና ምስጢሩን ለመፈለግ ተስማሙ። ነገር ግን በመንገዳቸው ላይ ተራራው በጣም የሚያንሸራትት የቅቤ ተራራ ሆኖ አገኙት!")
                    put("ዝሆኑ በቅቤው ላይ እየተንሸራተተ በሆዱ ሲወርድ፥ ጥንቸሉ በጀርባው ላይ ተቀምጦ እንደ መኪና ይነዳው ነበር። በመንገዳቸው ላይ ያገኟቸው ጦጣዎች በሙሉ ይህንን አስቂኝ ትዕይንት እያዩ ሆዳቸውን እስኪይዙ ድረስ በሳቅ ፈነዱ። እነሱም በሳቅ እየሳቁ የ'$capitalizedTopic' ምስጢር የት እንደሚገኝ የሚጠቁም ካርታ ወረወሩላቸው።")
                    put("በመጨረሻም፥ በጋራ በመሳቅና በመደጋገፍ የ'$capitalizedTopic' ዋነኛ ምስጢር በህይወት ውስጥ ሁልጊዜ መሳቅ፣ መደሰትና መልካምነትን ማጋራት መሆኑን አወቁ። እንስሳቱ በሙሉ ይህንን ታሪክ እየሰሙ በየቀኑ በደስታ እየሳቁ ይጫወታሉ፤ ከዚያን ጊዜ ጀምሮ በደስታና በሳቅ ኖሩ።")
                })
                json.put("englishTranslation", JSONArray().apply {
                    put("Once, the animals in the deep forest wanted to discover the most hilarious stories and secrets about '$capitalizedTopic'. The clever rabbit and the kind elephant agreed to climb the high mountain covered with bananas. However, they found that the mountain was actually a slippery butter mountain!")
                    put("As the elephant slid down the butter on his belly, the rabbit sat on his back, driving him like a car. All the monkeys they met on their path laughed hysterically at this funny sight. Giggling, they threw them a map pointing to the secret of '$capitalizedTopic'.")
                    put("Finally, by laughing together and supporting one another, they discovered that the true secret of '$capitalizedTopic' is to always laugh, be happy, and share goodness. All the animals live in joy and laughter to this day, sharing this funny story.")
                })
                json.put("moral", "ሳቅና ጨዋታ በጋራ ሲሆኑ አስቸጋሪውን የቅቤ ተራራ እንኳ በቀላሉ ለመሻገር ይረዳሉ። (Laughter and playfulness together help us easily cross even the most difficult butter mountain.)")
            }
        }
        return json
    }

    fun generateCustomStory(topic: String, onResult: (JSONObject?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    Log.e("FidelViewModel", "Gemini API key is not configured. Falling back to offline generator.")
                    val fallback = generateLocalFallbackStory(topic)
                    launch(Dispatchers.Main) { onResult(fallback) }
                    return@launch
                }

                val prompt = "Write a long, funny, and highly engaging children's story in Amharic about '$topic' with 3 to 4 paragraphs filled with humorous and funny situations that will make kids laugh out loud, and a simple moral. Return the story in JSON format with exactly the following keys: \"title\" (Amharic title), \"paragraphs\" (list of 3 or 4 Amharic string paragraphs), \"englishTranslation\" (list of 3 or 4 English translation string paragraphs corresponding to the Amharic paragraphs), \"moral\" (Amharic moral). Output ONLY valid JSON."

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
                        Log.e("FidelViewModel", "Gemini API error: ${response.code} - ${response.message}. Using offline generator.")
                        val fallback = generateLocalFallbackStory(topic)
                        launch(Dispatchers.Main) { onResult(fallback) }
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
                        val fallback = generateLocalFallbackStory(topic)
                        launch(Dispatchers.Main) { onResult(fallback) }
                    }
                }
            } catch (e: Exception) {
                Log.e("FidelViewModel", "Failed to generate story online: ${e.message}. Using offline generator.", e)
                val fallback = generateLocalFallbackStory(topic)
                launch(Dispatchers.Main) { onResult(fallback) }
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
