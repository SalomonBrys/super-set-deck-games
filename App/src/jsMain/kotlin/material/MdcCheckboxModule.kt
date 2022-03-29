@file:JsModule("@material/checkbox")
@file:Suppress("ClassName")

package material

import material.utils._Internal_MdcWidget
import org.w3c.dom.HTMLElement


@JsName("MDCCheckbox")
internal external class _Internal_MDCCheckbox(el: HTMLElement) : _Internal_MdcWidget {

    var checked: Boolean
    var indeterminate: Boolean

}
