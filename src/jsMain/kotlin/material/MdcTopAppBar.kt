package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement


@Suppress("unused")
@JsModule("@material/top-app-bar/dist/mdc.top-app-bar.css")
private external val MdcTopAppBarStyle: dynamic


enum class SectionAlign { Start, End }

class MdcTopAppBarSectionContext(scope: DOMScope<HTMLElement>) : DOMScope<HTMLElement> by scope {

    @Composable
    fun Title(
        attrs: AttrBuilderContext<HTMLSpanElement>? = null,
        content: ContentBuilder<HTMLSpanElement>
    ) {
        Span({
            classes("mdc-top-app-bar__title")
            attrs?.invoke(this)
        }) {
            content()
        }
    }

    @Composable
    fun NavigationIcon(
        icon: String,
        label: String? = null,
        attrs: AttrBuilderContext<HTMLButtonElement>? = null,
        onClick: () -> Unit
    ) {
        MdcAdditionalButtonClasses("mdc-top-app-bar__navigation-icon") {
            MdcIconButton(
                icon = icon,
                label = label,
                onClick = onClick,
                attrs = attrs
            )
        }
    }

    @Composable
    fun Action(
        content: @Composable () -> Unit
    ) {
        MdcAdditionalButtonClasses("mdc-top-app-bar__action-item") {
            content()
        }
    }
}

class MdcTopAppBarRowContext(scope: DOMScope<HTMLDivElement>) : DOMScope<HTMLDivElement> by scope {

    @Composable
    fun Section(
        align: SectionAlign,
        attrs: AttrBuilderContext<HTMLElement>? = null,
        content: @Composable MdcTopAppBarSectionContext.() -> Unit
    ) {
        Section({
            classes(
                "mdc-top-app-bar__section",
                when (align) {
                    SectionAlign.Start -> "mdc-top-app-bar__section--align-start"
                    SectionAlign.End -> "mdc-top-app-bar__section--align-end"
                }
            )
            attrs?.invoke(this)
        }) {
            MdcTopAppBarSectionContext(this).content()
        }
    }
}

class MdcTopAppBarContext(scope: DOMScope<HTMLElement>) : DOMScope<HTMLElement> by scope {

    @Composable
    fun Row(
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: @Composable MdcTopAppBarRowContext.() -> Unit
    ) {
        Div({
            classes("mdc-top-app-bar__row")
            attrs?.invoke(this)
        }) {
            MdcTopAppBarRowContext(this).content()
        }
    }
}

@Composable
fun MdcTopAppBar(
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: @Composable MdcTopAppBarContext.() -> Unit
) {
    Header({
        classes("mdc-top-app-bar")
        attrs?.invoke(this)
    }) {
        DisposableEffect(null) {
            _Internal_MDCTopAppBar(scopeElement)
            onDispose {}
        }

        MdcTopAppBarContext(this).content()
    }
}

@Composable
fun MdcTopAppBarMain(
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: ContentBuilder<HTMLElement>
) {
    Main({
        classes("mdc-top-app-bar--fixed-adjust")
        attrs?.invoke(this)
    }) {
        content()
    }
}
