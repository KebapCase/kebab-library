package com.kebab.core.paging.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.NullHandling
import org.springframework.data.domain.Sort.Order
import java.io.Serializable

@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderBy(

        @ApiModelProperty(allowableValues = "asc,desc", example = "asc", value = "Sort direction")
        var direction: Direction? = null,

        @ApiModelProperty(example = "id", value = "Property name to sort by")
        var property: String? = null,

        @ApiModelProperty(hidden = true)
        var ignoreCase: Boolean? = null,

        @ApiModelProperty(allowableValues = "NATIVE, NULLS_FIRST, NULLS_LAST", example = "id",
                value = "A hint how to sort result set in case if it contains `null` values (defaults to `NATIVE`)")
        var nullHandling: NullHandling? = null

) : Serializable {

    fun toOrder(): Order? {
        property?.isBlank() ?: return null

        return Order(direction, property, nullHandling).apply {
            if (ignoreCase == true) {
                ignoreCase()
            }
        }
    }

    companion object {

        private const val serialVersionUID = 4313866551482081369L

        fun parse(order: Order?): OrderBy? {
            order ?: return null

            return OrderBy(
                    direction = order.direction,
                    ignoreCase = if (order.isIgnoreCase) true else null,
                    property = order.property,
                    nullHandling = order.nullHandling
            )
        }
    }
}
