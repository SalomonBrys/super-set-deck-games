@file:JsModule("@material/dialog")
@file:Suppress("ClassName")

package material

import material.utils._Internal_MdcWidget
import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCDialog")
internal external class _Internal_MDCDialog(el: HTMLElement) : _Internal_MdcWidget {

    fun open()
    fun close()

    interface ClosingDetails {
        val action: String
    }

}
