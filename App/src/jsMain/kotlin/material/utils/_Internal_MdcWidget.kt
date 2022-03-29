package material.utils

import org.w3c.dom.CustomEvent


abstract external class _Internal_MdcWidget {
    fun listen(event: String, listener: (CustomEvent) -> Unit)
}
