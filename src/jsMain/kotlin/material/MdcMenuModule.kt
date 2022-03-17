@file:JsModule("@material/menu")
@file:Suppress("ClassName")

package material

import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCMenu")
internal external class _Internal_MDCMenu(el: HTMLElement) {
    var open: Boolean

    fun listen(event: String, listener: (CustomEvent) -> Unit)

    interface SelectedDetails {
        val index: Int
    }
}
