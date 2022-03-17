package material

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*


@Suppress("unused")
@JsModule("@material/icon-button/dist/mdc.icon-button.css")
private external val MdcIconButtonStyle: dynamic


@Composable
fun MdcIconButton(
    icon: String,
    label: String? = null,
    unbounded: Boolean? = true,
    onClick: () -> Unit
) {
    Div({
        classes("mdc-touch-target-wrapper")
    }) {
        val additionalClasses = LocalAdditionalClasses.current
        Button({
            classes("mdc-icon-button", "mdc-icon-button--touch", "material-icons")
            classes(*additionalClasses)
            if (label != null) {
                attr("aria-label", label)
            }
            onClick { onClick() }
        }) {
            Div({ classes("mdc-icon-button__ripple") })
            MdcRipple(unbounded)
            Span({ classes("mdc-icon-button__focus-ring") })
            Text(icon)
            Div({ classes("mdc-icon-button__touch") })
        }
    }
}

@Composable
fun MdcTextIconButton(
    label: String,
    unbounded: Boolean? = true,
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
        }) {
            Div({ classes("mdc-icon-button__ripple") })
            MdcRipple(unbounded)
            Span({ classes("mdc-icon-button__focus-ring") })
            Text(label)
            Div({ classes("mdc-icon-button__touch") })
        }
    }
}
