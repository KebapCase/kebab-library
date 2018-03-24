package com.kebab.core.config.listener

import com.kebab.core.model.BaseEntity
import java.util.UUID.randomUUID
import javax.persistence.PrePersist

class UuidListener {

    @PrePersist
    fun touchForCreate(target: BaseEntity) {
        target.guid = randomUUID()
    }
}