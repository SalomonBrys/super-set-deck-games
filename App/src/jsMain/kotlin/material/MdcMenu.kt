package material

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.*


@Suppress("unused")
@JsModule("@material/menu/dist/mdc.menu.css")
private external val MdcMenuStyle: dynamic

@Suppress("unused")
@JsModule("@material/menu-surface/dist/mdc.menu-surface.css")
private external val MdcMenuSurfaceStyle: dynamic


class MdcMenuContext(private val callbacks: MutableList<() -> Unit>, scope: DOMScope<HTMLUListElement>) : DOMScope<HTMLUListElement> by scope {

    private var first = false

    @Composable
    fun MdcMenuItem(
        onSelect: () -> Unit,
        attrs: AttrBuilderContext<HTMLLIElement>? = null,
        content: ContentBuilder<HTMLSpanElement>
    ) {
        callbacks.add(onSelect)
        Li({
            classes("mdc-deprecated-list-item")
            attr("role", "menuitem")
            if (first) {
                tabIndex(0)
            }
            first = false
            attrs?.invoke(this)
        }) {
            Span({ classes("mdc-deprecated-list-item__ripple") })
            Span({ classes("mdc-deprecated-list-item__text") }) {
                content()
            }
        }

    }
}

@Composable
fun MdcMenu(
    trigger: Flow<Boolean>,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable MdcMenuContext.() -> Unit
) {
    val callbacks by rememberUpdatedState(ArrayList<() -> Unit>())

    Div({
        classes("mdc-menu", "mdc-menu-surface")
        attrs?.invoke(this)
    }) {
        var menu by remember { mutableStateOf<_Internal_MDCMenu?>(null) }

        DisposableEffect(null) {
            val m = _Internal_MDCMenu(scopeElement)
            m.listen("MDCMenu:selected") {
                callbacks[it.detail.unsafeCast<_Internal_MDCMenu.SelectedDetails>().index].invoke()
            }
            menu = m
            onDispose {}
        }

        LaunchedEffect(trigger, menu) {
            val m = menu ?: return@LaunchedEffect
            trigger.collect {
                m.open = true
            }
        }

        Ul({
            classes("mdc-deprecated-list")
            attr("role", "menu")
            attr("aria-hidden", "true")
            attr("aria-orientation", "vertical")
            tabIndex(-1)
        }) {
            MdcMenuContext(callbacks, this).content()
        }
    }

}

@Composable
fun MdcMenuAnchor(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>
) {
    Div({
        classes("mdc-menu-surface--anchor")
        attrs?.invoke(this)
    }) {
        content()
    }
}
