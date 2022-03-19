package material

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement


@Suppress("unused")
@JsModule("@material/drawer/dist/mdc.drawer.css")
private external val MdcDrawerStyle: dynamic


@Composable
fun MdcDrawer(
    trigger: Flow<Boolean>,
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: ContentBuilder<HTMLDivElement>
) {
    Aside({
        classes("mdc-drawer", "mdc-drawer--modal")
        attrs?.invoke(this)
    }) {
        var drawer by remember { mutableStateOf<_Internal_MDCDrawer?>(null) }

        DisposableEffect(null) {
            drawer = _Internal_MDCDrawer(scopeElement)
            onDispose {}
        }

        LaunchedEffect(trigger, drawer) {
            val d = drawer ?: return@LaunchedEffect
            trigger.collect {
                d.open = it
            }
        }

        Div({ classes("mdc-drawer__content") }) {
            content()
        }
    }
    Div({ classes("mdc-drawer-scrim") })
}
