package com.kebab.core.paging.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.hateoas.Link
import java.io.Serializable

@ApiModel("Link")
data class LinkWrapper(

        @ApiModelProperty("The actual URI the `Link` is pointing to")
        var href: String? = null

) : Serializable {

    companion object {

        private const val serialVersionUID = -9082849452897183609L

        fun parse(link: Link?): LinkWrapper? {
            link ?: return null

            return LinkWrapper(href = link.href)
        }
    }
}
