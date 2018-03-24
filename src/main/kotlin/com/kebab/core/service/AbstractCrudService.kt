package com.kebab.core.service

import com.kebab.core.exception.EntityNotFoundException
import com.kebab.core.model.BaseEntity
import com.kebab.core.repository.BaseEntityRepository
import com.kebab.core.util.mergeWith
import com.kebab.core.util.validate
import org.springframework.core.GenericTypeResolver.resolveTypeArguments
import org.springframework.data.domain.PageRequest
import java.util.UUID

/**
 * @author Valentin Trusevich
 */
abstract class AbstractCrudService<T : BaseEntity>(
        private val repository: BaseEntityRepository<T>
) {

    private val entityClass by lazy { resolveTypeArguments(javaClass, AbstractCrudService::class.java)[0] as Class<T> }

    open fun create(model: T, accountGuid: UUID) = repository.save(model.validate())!!

    open fun update(guid: UUID, model: T) =
            repository.save(model.mergeWith(repository.findByGuid(guid)!!).validate())!!

    fun find(guid: UUID) = repository.findByGuid(guid) ?: EntityNotFoundException()

    fun findByGuidIn(guids: Collection<UUID>) = repository.findAllByGuidIn(guids)

    open fun delete(guid: UUID) = repository.deleteByGuid(guid)

    fun findAll(page: Int, size: Int) = repository.findAll(PageRequest(page, size))

}