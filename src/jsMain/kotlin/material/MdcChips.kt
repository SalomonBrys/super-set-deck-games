package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement


@Suppress("unused")
@JsModule("@material/chips/dist/mdc.chips.css")
private external val MdcChipsStyle: dynamic


@Composable
fun MdcChip(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Span({
        classes("mdc-chip")
        attr("role", "button")
        onClick { onClick() }
    }) {
        Span({ classes("mdc-chip__ripple") })
        MdcRipple()
        Span({ classes("mdc-chip__text") }) { content() }
    }
}

@Composable
fun MdcChipSet(content: @Composable () -> Unit) {
    Span({
        classes("mdc-chip-set")
        attr("role", "row")
    }) {
        content()
    }
}
