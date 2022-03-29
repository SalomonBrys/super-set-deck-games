@file:JsModule("@material/tab-bar")
@file:Suppress("ClassName")

package material

import material.utils._Internal_MdcWidget
import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCTabBar")
internal external class _Internal_MDCTabBar(el: HTMLElement) : _Internal_MdcWidget {

    var focusOnActivate: Boolean

    fun activateTab(index: Int)

    interface ActivatedDetails {
        val index: Int
    }
}
