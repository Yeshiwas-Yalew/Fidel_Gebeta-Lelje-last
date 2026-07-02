package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val childName: String = "Kiddo",
    val avatarId: Int = 0, // Icon drawable index for chosen avatar
    val coins: Int = 0,
    val stars: Int = 0,
    val currentLevel: Int = 1,
    val isSyncing: Boolean = false,
    val lastSyncedTime: Long = 0L,
    val parentPin: String = "1234",
    val highContrast: Boolean = false,
    val textToSpeechEnabled: Boolean = true,
    val difficultyMode: String = "EASY",
    val tracingStreak: Int = 0,
    val unlockedStickers: String = "FIRST_STEPS",
    val teachingVoice: String = "DEFAULT"
)

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val exerciseName: String,
    val score: Int,
    val totalQuestions: Int,
    val type: String // "QUIZ" or "TRACING"
)

@Entity(tableName = "learning_sessions")
data class LearningSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long
)

data class StickerBadge(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val colorHex: Long,
    val conditionText: String
)

object StickerData {
    val stickers = listOf(
        StickerBadge(
            id = "FIRST_STEPS",
            name = "Explorer 🦁",
            description = "Welcome to Amharic Adventure!",
            emoji = "🦁",
            colorHex = 0xFFFFD54F,
            conditionText = "Join the adventure"
        ),
        StickerBadge(
            id = "FIRST_TRACE",
            name = "First Trace ✍️",
            description = "Traced your first Amharic letter!",
            emoji = "✍️",
            colorHex = 0xFF64B5F6,
            conditionText = "Trace 1 letter"
        ),
        StickerBadge(
            id = "PENTASTAR_TRACER",
            name = "Pentastar Tracer 🏅",
            description = "Successfully traced a set of 5 Amharic characters!",
            emoji = "🏅",
            colorHex = 0xFFFFD700,
            conditionText = "Trace 5 Amharic letters"
        ),
        StickerBadge(
            id = "TRACING_STREAK_10",
            name = "Master Tracer 👑",
            description = "Successfully traced 10 letters in a row!",
            emoji = "👑",
            colorHex = 0xFF81C784,
            conditionText = "Trace 10 letters in a row"
        ),
        StickerBadge(
            id = "COIN_HUNTER",
            name = "Gem Rich 💎",
            description = "Collected over 500 shiny gems!",
            emoji = "💎",
            colorHex = 0xFFE040FB,
            conditionText = "Earn 500 Gems"
        ),
        StickerBadge(
            id = "STAR_SCHOLAR",
            name = "Star Scholar 🌟",
            description = "Collected 50 bright learning stars!",
            emoji = "🌟",
            colorHex = 0xFFFF9100,
            conditionText = "Earn 50 Stars"
        ),
        StickerBadge(
            id = "QUIZ_PERFECT",
            name = "Brainiac 🧠",
            description = "Scored a perfect 10/10 in the quiz!",
            emoji = "🧠",
            colorHex = 0xFF00E676,
            conditionText = "Get 10/10 in a quiz"
        )
    )
}
