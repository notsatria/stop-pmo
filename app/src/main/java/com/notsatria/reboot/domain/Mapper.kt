package com.notsatria.reboot.domain

import com.notsatria.reboot.data.local.entity.RelapseEventEntity
import com.notsatria.reboot.domain.model.RelapseEvent

fun RelapseEventEntity.toDomainModel() = RelapseEvent(
    id = this.id,
    occurredAt = this.occurredAt,
    streak = this.streak,
    note = this.note
)