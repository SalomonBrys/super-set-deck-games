@file:JsModule("@material/tab-bar")
@file:Suppress("ClassName")

package material

import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCTabBar")
internal external class _Internal_MDCTabBar(el: HTMLElement) {

    var focusOnActivate: Boolean

    fun activateTab(index: Int)

    fun listen(event: String, listener: (CustomEvent) -> Unit)

    interface ActivatedDetails {
        val index: Int
    }
}
