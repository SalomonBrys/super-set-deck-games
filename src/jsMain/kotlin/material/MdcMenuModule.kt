@file:JsModule("@material/menu")
@file:Suppress("ClassName")

package material

import material.utils._Internal_MdcWidget
import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCMenu")
internal external class _Internal_MDCMenu(el: HTMLElement) : _Internal_MdcWidget {
    var open: Boolean

    interface SelectedDetails {
        val index: Int
    }
}
