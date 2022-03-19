package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement


@Suppress("unused")
@JsModule("@material/card/dist/mdc.card.css")
private external val MdcCardStyle: dynamic


@Composable
fun MdcCard(
    onClick: (() -> Unit)? = null,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>
) {
    Div({
        classes("mdc-card")
        attrs?.invoke(this)
        if (onClick != null) onClick { onClick() }
    }) {
        if (onClick != null) {
            Div({
                classes("mdc-card__primary-action")
                tabIndex(0)
            }) {
                content()
                Div({ classes("mdc-card__ripple") })
                MdcRipple()
            }
        } else {
            content()
        }
    }
}
