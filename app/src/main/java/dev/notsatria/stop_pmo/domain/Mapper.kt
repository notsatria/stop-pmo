package dev.notsatria.stop_pmo.domain

import dev.notsatria.stop_pmo.data.local.entity.RelapseEventEntity
import dev.notsatria.stop_pmo.domain.model.RelapseEvent

fun RelapseEventEntity.toDomainModel() = RelapseEvent(
    id = this.id,
    occurredAt = this.occurredAt,
    note = this.note
)