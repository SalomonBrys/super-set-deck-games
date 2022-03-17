package material

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Ul


@Suppress("unused")
@JsModule("@material/menu/dist/mdc.menu.css")
private external val MdcMenuStyle: dynamic

class MdcMenuContext {

    internal val list = ArrayList<Pair<() -> Unit, @Composable () -> Unit>>()

    fun menuItem(onSelect: () -> Unit, content: @Composable () -> Unit) {
        list.add(onSelect to content)
    }
}

@Composable
fun MdcMenu(
    anchorContent: @Composable (() -> Unit) -> Unit,
    menuContent: MdcMenuContext.() -> Unit
) {
    val list by rememberUpdatedState(MdcMenuContext().apply(menuContent).list)

    Div({ classes("mdc-menu-surface--anchor") }) {
        var menu by remember { mutableStateOf<_Internal_MDCMenu?>(null) }
        anchorContent {
            menu?.open = true
        }
        Div({ classes("mdc-menu", "mdc-menu-surface") }) {
            DisposableEffect(null) {
                menu = _Internal_MDCMenu(scopeElement)
                menu!!.listen("MDCMenu:selected") {
                    val (callback, _) =
                        list[it.detail.unsafeCast<_Internal_MDCMenu.SelectedDetails>().index]
                    callback.invoke()
                }
                onDispose {}
            }

            Ul({
                classes("mdc-deprecated-list")
                attr("role", "menu")
                attr("aria-hidden", "true")
                attr("aria-orientation", "vertical")
                tabIndex(-1)
            }) {
                list.forEach { (_, content) ->
                    Li({
                        classes("mdc-deprecated-list-item")
                        attr("role", "menuitem")
                    }) {
                        Span({ classes("mdc-deprecated-list-item__ripple") })
                        Span({ classes("mdc-deprecated-list-item__text") }) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
