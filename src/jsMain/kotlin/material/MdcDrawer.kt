package material

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement


@Suppress("unused")
@JsModule("@material/drawer/dist/mdc.drawer.css")
private external val MdcDrawerStyle: dynamic


class MdcDrawerHeaderContext(scope: DOMScope<HTMLDivElement>): DOMScope<HTMLDivElement> by scope {
    @Composable
    fun MdcDrawerHeaderTitle(
        attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
        content: ContentBuilder<HTMLHeadingElement>
    ) {
        H3({
            classes("mdc-drawer__title")
            attrs?.invoke(this)
        }) {
            content()
        }
    }

    @Composable
    fun MdcDrawerHeaderSubtitle(
        attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
        content: ContentBuilder<HTMLHeadingElement>
    ) {
        H6({
            classes("mdc-drawer__subtitle")
            attrs?.invoke(this)
        }) {
            content()
        }
    }
}

class MdcDrawerContext(scope: DOMScope<HTMLElement>): DOMScope<HTMLElement> by scope {

    @Composable
    fun MdcDrawerHeader(
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: @Composable MdcDrawerHeaderContext.() -> Unit
    ) {
        Div({
            classes("mdc-drawer__header")
            attrs?.invoke(this)
        }) {
            MdcDrawerHeaderContext(this).content()
        }
    }

    @Composable
    fun MdcDrawerContent(
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: ContentBuilder<HTMLDivElement>
    ) {
        Div({
            classes("mdc-drawer__content")
            attrs?.invoke(this)
        }) {
            content()
        }
    }

}

@Composable
fun MdcDrawer(
    trigger: Flow<Boolean>,
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: @Composable MdcDrawerContext.() -> Unit
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

        MdcDrawerContext(this).content()
    }
    Div({ classes("mdc-drawer-scrim") })
}
