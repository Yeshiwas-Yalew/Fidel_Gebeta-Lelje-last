package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FidelDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)

    @Query("SELECT * FROM exercise_records ORDER BY timestamp DESC")
    fun getExerciseRecords(): Flow<List<ExerciseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseRecord(record: ExerciseRecord)

    @Query("DELETE FROM exercise_records")
    suspend fun clearAllExerciseRecords()

    @Query("SELECT * FROM learning_sessions ORDER BY startTime DESC")
    fun getLearningSessions(): Flow<List<LearningSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningSession(session: LearningSession): Long

    @Query("UPDATE learning_sessions SET endTime = :endTime, durationSeconds = :duration WHERE id = :id")
    suspend fun updateLearningSession(id: Int, endTime: Long, duration: Long)

    @Query("DELETE FROM learning_sessions")
    suspend fun clearAllLearningSessions()
}
