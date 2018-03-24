package com.kebab.core.model

import com.kebab.core.model.enumeration.Operation
import java.io.Serializable
import java.util.UUID

/**
 * @author Valentin Trusevich
 */
data class ChangeItemsInSmartContainer(

        var guid: UUID? = null,

        var operation: Operation? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 95452509300828736L
    }
}