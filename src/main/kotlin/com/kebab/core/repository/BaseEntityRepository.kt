package com.kebab.core.repository

import com.kebab.core.model.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * @author Alexander Yugov
 */
@NoRepositoryBean
@Transactional(readOnly = true)
interface BaseEntityRepository<T : BaseEntity>
    : JpaRepository<T, Long>, JpaSpecificationExecutor<T>, PagingAndSortingRepository<T, Long> {

    fun findByGuid(guid: UUID): T?

    fun findAllByGuidIn(guids: Iterable<UUID>): List<T>

    @Modifying
    @Transactional
    fun deleteByGuid(guid: UUID): T?
}