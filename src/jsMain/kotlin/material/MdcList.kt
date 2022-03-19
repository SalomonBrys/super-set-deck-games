package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.*


@Suppress("unused")
@JsModule("@material/list/dist/mdc.list.css")
private external val MdcListStyle: dynamic


class MdcListListItemContext(scope: DOMScope<HTMLSpanElement>) : DOMScope<HTMLSpanElement> by scope {
    @Composable
    fun TwoLines(
        primary: ContentBuilder<HTMLSpanElement>,
        secondary: ContentBuilder<HTMLSpanElement>
    ) {
        Span({ classes("mdc-deprecated-list-item__primary-text") }) { primary() }
        Span({ classes("mdc-deprecated-list-item__secondary-text") }) { secondary() }
    }
}

abstract class AbstractMdcListContext<S : HTMLElement, E : HTMLElement>(scope: DOMScope<S>) : DOMScope<S> by scope {
    private var first = true

    @Composable
    protected abstract fun Element(attrs: AttrBuilderContext<E>?, content: ContentBuilder<E>?)

    @Composable
    protected fun listItem(
        attrs: AttrBuilderContext<E>,
        content: @Composable MdcListListItemContext.() -> Unit
    ) {
        Element({
            classes("mdc-deprecated-list-item")
            if (first) { tabIndex(0) }
            first = false
            attrs(this)
        }) {
            Span({ classes("mdc-deprecated-list-item__ripple") })
            MdcRipple()
            Span({ classes("mdc-deprecated-list-item__text") }) {
                MdcListListItemContext(this).content()
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

class MdcListContext(private val callbacks: MutableList<() -> Unit>, scope: DOMScope<HTMLUListElement>) : AbstractMdcListContext<HTMLUListElement, HTMLLIElement>(scope) {

    @Composable
    override fun Element(attrs: AttrBuilderContext<HTMLLIElement>?, content: ContentBuilder<HTMLLIElement>?) =
        Li(attrs, content)

    @Composable
    fun MdcListItem(
        onSelect: (() -> Unit)? = null,
        attrs: AttrBuilderContext<HTMLLIElement>? = null,
        content: @Composable MdcListListItemContext.() -> Unit
    ) = listItem({
        callbacks.add(onSelect ?: {})
        attrs?.invoke(this)
    }, content)
}

@Composable
fun MdcList(
    attrs: AttrBuilderContext<HTMLUListElement>? = null,
    content: @Composable MdcListContext.() -> Unit
) {
    val callbacks by rememberUpdatedState(ArrayList<() -> Unit>())

    Ul({
        classes("mdc-deprecated-list")
        attrs?.invoke(this)
    }) {
        DisposableEffect(null) {
            val list = _Internal_MDCList(scopeElement)
            list.listen("MDCList:action") {
                callbacks[it.detail.unsafeCast<_Internal_MDCList.ActionDetails>().index].invoke()
            }
            onDispose {}
        }

        MdcListContext(callbacks, this).content()
    }
}

class MdcNavListContext(scope: DOMScope<HTMLElement>) : AbstractMdcListContext<HTMLElement, HTMLAnchorElement>(scope) {

    @Composable
    override fun Element(attrs: AttrBuilderContext<HTMLAnchorElement>?, content: ContentBuilder<HTMLAnchorElement>?) =
        A(null, attrs, content)

    @Composable
    fun MdcNavListItem(
        href: String,
        attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
        content: @Composable MdcListListItemContext.() -> Unit
    ) = listItem({
        href(href)
        attrs?.invoke(this)
    }, content)
}

@Composable
fun MdcNavList(
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: @Composable MdcNavListContext.() -> Unit
) {
    Nav({
        classes("mdc-deprecated-list")
        attrs?.invoke(this)
    }) {
        DisposableEffect(null) {
            _Internal_MDCList(scopeElement)
            onDispose {}
        }

        MdcNavListContext(this).content()
    }
}