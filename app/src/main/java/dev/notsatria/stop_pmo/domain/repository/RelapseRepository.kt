package dev.notsatria.stop_pmo.domain.repository

import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import kotlinx.coroutines.flow.Flow

interface RelapseRepository {
    suspend fun logRelapse(occurredAt: String, note: String?)
    suspend fun lastRelapse(): RelapseEvent?
    fun allRelapseFlow(): Flow<List<RelapseEvent>>
    suspend fun clearAll()
}