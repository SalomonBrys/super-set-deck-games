package material_custom

import androidx.compose.runtime.Composable
import material.LocalAdditionalClasses
import material.MdcRipple
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement


@Composable
fun MdcTextIconButton(
    label: String,
    unbounded: Boolean? = true,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    onClick: () -> Unit
) {
    Div({
        classes("mdc-touch-target-wrapper")
    }) {
        val additionalClasses = LocalAdditionalClasses.current
        Button({
            classes("mdc-icon-button", "mdc-text-icon-button", "mdc-icon-button--touch")
            classes(*additionalClasses)
            onClick { onClick() }
            attrs?.invoke(this)
        }) {
            Div({ classes("mdc-icon-button__ripple") })
            MdcRipple(unbounded)
            Span({ classes("mdc-icon-button__focus-ring") })
            Text(label)
            Div({ classes("mdc-icon-button__touch") })
        }
    }
}
