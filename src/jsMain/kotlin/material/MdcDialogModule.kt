@file:JsModule("@material/dialog")
@file:Suppress("ClassName")

package material

import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCDialog")
internal external class _Internal_MDCDialog(el: HTMLElement) {

    fun open()

    fun listen(event: String, listener: (CustomEvent) -> Unit)

    interface ClosingDetails {
        val action: String
    }

}
