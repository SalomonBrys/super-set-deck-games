@file:JsModule("@material/select")
@file:Suppress("ClassName")

package material

import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLElement


@JsName("MDCSelect")
internal external class _Internal_MDCSelect(el: HTMLElement) {

    var value: String

    fun listen(event: String, listener: (CustomEvent) -> Unit)

}
