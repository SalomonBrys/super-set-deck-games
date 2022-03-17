package material

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.Div


@Suppress("unused")
@JsModule("@material/menu-surface/dist/mdc.menu-surface.css")
private external val MdcMenuSurfaceStyle: dynamic


@Composable
fun MdcMenuSurface(
    anchorContent: @Composable (() -> Unit) -> Unit,
    menuContent: @Composable () -> Unit
) {
    Div({ classes("mdc-menu-surface--anchor") }) {
        var surface by remember { mutableStateOf<_Internal_MDCMenuSurface?>(null) }
        anchorContent { surface?.open() }
        Div({ classes("mdc-menu-surface") }) {
            DisposableEffect(null) {
                surface = _Internal_MDCMenuSurface(scopeElement)
                onDispose {}
            }

            menuContent()
        }
    }
}
