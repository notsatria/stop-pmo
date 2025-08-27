package dev.notsatria.stop_pmo.data.repository

import dev.notsatria.stop_pmo.data.local.dao.RelapseDao
import dev.notsatria.stop_pmo.data.local.entity.RelapseEventEntity
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.domain.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RelapseRepositoryImpl(private val dao: RelapseDao) : RelapseRepository {
    override suspend fun logRelapse(occurredAt: String, note: String?) {
        RelapseEventEntity(occurredAt = occurredAt, note = note).let {
            dao.insert(it)
        }
    }

    override suspend fun lastRelapse(): RelapseEvent? {
        return dao.getLastRelapse()?.toDomainModel()

    }

    override fun allRelapseFlow(): Flow<List<RelapseEvent>> {
        return dao.getAllRelapsesFlow().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}