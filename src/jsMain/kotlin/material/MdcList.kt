package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement


@Suppress("unused")
@JsModule("@material/list/dist/mdc.list.css")
private external val MdcListStyle: dynamic


class MdcListListItemContext {
    @Composable
    fun TwoLines(
        primary: @Composable () -> Unit,
        secondary: @Composable () -> Unit
    ) {
        Span({ classes("mdc-deprecated-list-item__primary-text") }) { primary() }
        Span({ classes("mdc-deprecated-list-item__secondary-text") }) { secondary() }
    }
}

class MdcListContext {
    @Composable
    fun MdcListItem(
        onClick: (() -> Unit)? = null,
        attrs: AttrBuilderContext<HTMLLIElement>? = null,
        content: @Composable MdcListListItemContext.() -> Unit
    ) {
        Li({
            classes("mdc-deprecated-list-item")
            attrs?.invoke(this)
            if (onClick != null) onClick { onClick() }
        }) {
            Span({ classes("mdc-deprecated-list-item__ripple") })
            MdcRipple()
            Span({ classes("mdc-deprecated-list-item__text") }) {
                MdcListListItemContext().content()
            }
        }
    }

    @Composable
    fun MdcListDivider() {
        Li({
            classes("mdc-deprecated-list-divider")
        })
    }
}

@Composable
fun MdcList(
    attrs: AttrBuilderContext<HTMLUListElement>? = null,
    content: @Composable MdcListContext.() -> Unit
) {
    Ul({
        classes("mdc-deprecated-list")
        attrs?.invoke(this)
    }) {
        DisposableEffect(null) {
            _Internal_MDCList(scopeElement)
            onDispose {}
        }

        MdcListContext().content()
    }
}