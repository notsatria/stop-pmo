package com.notsatria.reboot.domain.repository

import com.notsatria.reboot.domain.model.RelapseEvent
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface RelapseRepository {
    suspend fun logRelapse(occurredAt: String, streak: Int, note: String?)
    suspend fun lastRelapse(): RelapseEvent?
    fun allRelapseFlow(): Flow<List<RelapseEvent>>
    suspend fun clearAll()

    @OptIn(ExperimentalTime::class)
    fun lastRelapseTimeFlow(): Flow<Instant?>
    fun recentRelapses(count: Int): Flow<List<RelapseEvent>>
    fun getRelapseHistory(count: Int, offset: Int): Flow<List<RelapseEvent>>
}