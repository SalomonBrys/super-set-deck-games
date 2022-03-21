package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLSpanElement


@Suppress("unused")
@JsModule("@material/button/dist/mdc.button.css")
private external val MdcButtonStyle: dynamic


internal val LocalButtonAdditionalClasses = compositionLocalOf<Array<String>> { emptyArray() }

@Composable
fun MdcAdditionalButtonClasses(vararg classes: String, content: @Composable () -> Unit) {
    val current = LocalButtonAdditionalClasses.current
    CompositionLocalProvider(LocalButtonAdditionalClasses provides (current + classes)) {
        content()
    }
}

enum class MdcButtonVariant { Text, Outlined, Elevated, Unelevated }

@Composable
fun MdcButton(
    onClick: () -> Unit,
    variant: MdcButtonVariant = MdcButtonVariant.Text,
    icon: String? = null,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    content: ContentBuilder<HTMLSpanElement>
) {
    Div({
        classes("mdc-touch-target-wrapper")
    }) {
        val additionalClasses = LocalButtonAdditionalClasses.current
        Button({
            classes("mdc-button", "mdc-button--touch")
            classes(*additionalClasses)
            onClick { onClick() }
            attrs?.invoke(this)
            when (variant) {
                MdcButtonVariant.Text -> {}
                MdcButtonVariant.Outlined -> classes("mdc-button--outlined")
                MdcButtonVariant.Elevated -> classes("mdc-button--raised")
                MdcButtonVariant.Unelevated -> classes("mdc-button--unelevated")
            }
        }) {
            Span({ classes("mdc-button__ripple") })
            MdcRipple()
            Span({ classes("mdc-button__touch") })
            if (icon != null) {
                I({ classes("material-icons", "mdc-button__icon") ; attr("aria-hidden", "true") }) { Text("bookmark") }
            }
            Span({ classes("mdc-button__label") }) { content() }
        }
    }
}
