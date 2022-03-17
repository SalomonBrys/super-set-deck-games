package material

import androidx.compose.runtime.*
import kotlinx.coroutines.yield
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text


@Suppress("unused")
@JsModule("@material/tab-bar/dist/mdc.tab-bar.css")
private external val MdcTabBarStyle: dynamic

@Suppress("unused")
@JsModule("@material/tab-scroller/dist/mdc.tab-scroller.css")
private external val MdcTabScrollerStyle: dynamic

@Suppress("unused")
@JsModule("@material/tab-indicator/dist/mdc.tab-indicator.css")
private external val MdcTabIndicatorStyle: dynamic

@Suppress("unused")
@JsModule("@material/tab/dist/mdc.tab.css")
private external val MdcTabStyle: dynamic


class MdcTabBarContext {
    @Composable
    fun MdcTab(icon: String? = null, content: @Composable () -> Unit) {
        Button({
            classes("mdc-tab")
            attr("role", "tab")
            tabIndex(0)
        }) {
            Span({ classes("mdc-tab__content") }) {
                if (icon != null) {
                    Span({
                        classes("mdc-tab__icon", "material-icons")
                        attr("aria-hidden", "true")
                    }) { Text(icon) }
                }
                Span({ classes("mdc-tab__text-label") }) { content() }
            }
            Span({ classes("mdc-tab-indicator") }) {
                Span({ classes("mdc-tab-indicator__content", "mdc-tab-indicator__content--underline") })
            }
            Span({ classes("mdc-tab__ripple") })
            MdcRipple()
            Div({ classes("mdc-tab__focus-ring") })
        }
    }
}

@Composable
fun MdcTabBar(
    selected: Int,
    onSelected: (Int) -> Unit,
    content: @Composable MdcTabBarContext.() -> Unit
) {
    var count by remember { mutableStateOf(0) }
    var tabBar by remember { mutableStateOf<_Internal_MDCTabBar?>(null) }

    Div({
        classes("mdc-tab-bar")
        attr("role", "tablist")
    }) {
        @Suppress("NAME_SHADOWING") val selected by rememberUpdatedState(selected)

        DisposableEffect(null) {
            tabBar = _Internal_MDCTabBar(scopeElement)
            tabBar!!.focusOnActivate = false
//            tabBar!!.activateTab(selected)

            tabBar!!.listen("MDCTabBar:activated") {
                val newSelected = it.detail.unsafeCast<_Internal_MDCTabBar.ActivatedDetails>().index
                if (newSelected != selected) {
                    onSelected(newSelected)
                }
                ++count
            }

            onDispose {}
        }

        LaunchedEffect(tabBar, selected, count) {
            if (tabBar == null) return@LaunchedEffect
//            if (tabBar!!.value == selected) return@LaunchedEffect
            yield()
            tabBar!!.activateTab(selected)
        }

        Div({ classes("mdc-tab-scroller") }) {
            Div({ classes("mdc-tab-scroller__scroll-area") }) {
                Div({ classes("mdc-tab-scroller__scroll-content") }) {
                    MdcTabBarContext().content()
                }
            }
        }
    }
}
