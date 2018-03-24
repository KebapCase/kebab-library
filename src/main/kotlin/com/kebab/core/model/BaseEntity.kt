package com.kebab.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.kebab.core.config.listener.UuidListener
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(UuidListener::class)
abstract class BaseEntity(

        @Id
        @GeneratedValue
        @JsonIgnore
        open var id: Long? = null,

        @Column(unique = true, updatable = false, nullable = false, columnDefinition = "BINARY(16)")
        @set:JsonProperty(access = READ_ONLY)
        @ApiModelProperty(value = "Guid", example = "f647e1fa-ad24-4f7e-a18e-4dd197feb66b", readOnly = true)
        open var guid: UUID? = null

) : Serializable
