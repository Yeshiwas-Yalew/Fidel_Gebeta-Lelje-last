package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers

class FidelRepository(private val fidelDao: FidelDao) {
    val userProgress: Flow<UserProgress?> = fidelDao.getUserProgress()
        .flowOn(Dispatchers.IO)

    val exerciseRecords: Flow<List<ExerciseRecord>> = fidelDao.getExerciseRecords()
        .flowOn(Dispatchers.IO)

    val learningSessions: Flow<List<LearningSession>> = fidelDao.getLearningSessions()
        .flowOn(Dispatchers.IO)

    suspend fun saveProgress(progress: UserProgress) {
        fidelDao.insertProgress(progress)
    }

    suspend fun saveExerciseRecord(record: ExerciseRecord) {
        fidelDao.insertExerciseRecord(record)
    }

    suspend fun saveLearningSession(session: LearningSession): Long {
        return fidelDao.insertLearningSession(session)
    }

    suspend fun updateLearningSession(id: Int, endTime: Long, duration: Long) {
        fidelDao.updateLearningSession(id, endTime, duration)
    }

    suspend fun clearHistory() {
        fidelDao.clearAllExerciseRecords()
        fidelDao.clearAllLearningSessions()
    }
}
