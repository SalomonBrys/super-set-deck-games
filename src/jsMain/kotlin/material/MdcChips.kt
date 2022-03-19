package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement


@Suppress("unused")
@JsModule("@material/chips/dist/mdc.chips.css")
private external val MdcChipsStyle: dynamic


class MdcChipSetContext(private val callbacks: MutableMap<String, () -> Unit>, scope: DOMScope<HTMLSpanElement>) : DOMScope<HTMLSpanElement> by scope {
    @Composable
    fun MdcChip(
        id: String,
        onInteract: () -> Unit,
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: ContentBuilder<HTMLSpanElement>
    ) {
        callbacks[id] = onInteract
        Div({ classes("mdc-touch-target-wrapper") }) {
            Div({
                classes("mdc-chip", "mdc-chip--touch")
                attr("role", "row")
                attrs?.invoke(this)
                id(id)
            }) {
                Div({ classes("mdc-chip__ripple") })
                MdcRipple()
                Span({ attr("role", "gridcell") }) {
                    Span({
                        classes("mdc-chip__primary-action")
                        attr("role", "button")
                        tabIndex(0)
                    }) {
                        Div({ classes("mdc-chip__touch") })
                        Span({ classes("mdc-chip__text") }) { content() }
                    }
                }
            }
        }
    }
}

@Composable
fun MdcChipSet(
    attrs: AttrBuilderContext<HTMLSpanElement>? = null,
    content: @Composable MdcChipSetContext.() -> Unit
) {
    val callbacks by rememberUpdatedState(HashMap<String, () -> Unit>())

    Span({
        classes("mdc-chip-set")
        attr("role", "grid")
        attrs?.invoke(this)
    }) {
        DisposableEffect(null) {
            val cs = _Internal_MDCChipSet(scopeElement)
            cs.listen("MDCChip:interaction") {
                callbacks[it.detail.unsafeCast<_Internal_MDCChipSet.InteractionDetail>().chipId]?.invoke()
            }
            onDispose {}
        }

        MdcChipSetContext(callbacks, this).content()
    }
}
