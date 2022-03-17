package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.jetbrains.compose.web.dom.*


@Suppress("unused")
@JsModule("@material/top-app-bar/dist/mdc.top-app-bar.css")
private external val MdcTopAppBarStyle: dynamic


enum class SectionAlign { Start, End }

class MdcTopAppBarSectionContext {
    @Composable
    fun Title(content: @Composable () -> Unit) {
        Span({ classes("mdc-top-app-bar__title") }) { content() }
    }

    @Composable
    fun NavigationIcon(icon: String, label: String? = null, onClick: () -> Unit) {
        MdcAdditionalButtonClasses("mdc-top-app-bar__navigation-icon") {
            MdcIconButton(icon, label, onClick = onClick)
        }
    }

    @Composable
    fun Action(content: @Composable () -> Unit) {
        MdcAdditionalButtonClasses("mdc-top-app-bar__action-item") {
            content()
        }
    }
}

class MdcTopAppBarRowContext {
    @Composable
    fun Section(align: SectionAlign, content: @Composable MdcTopAppBarSectionContext.() -> Unit) {
        Section({
            classes(
                "mdc-top-app-bar__section",
                when (align) {
                    SectionAlign.Start -> "mdc-top-app-bar__section--align-start"
                    SectionAlign.End -> "mdc-top-app-bar__section--align-end"
                }
            )
        }) {
            MdcTopAppBarSectionContext().content()
        }
    }
}

class MdcTopAppBarContext {
    @Composable
    fun Row(content: @Composable MdcTopAppBarRowContext.() -> Unit) {
        Div({ classes("mdc-top-app-bar__row") }) {
            MdcTopAppBarRowContext().content()
        }
    }
}

@Composable
fun MdcTopAppBar(content: @Composable MdcTopAppBarContext.() -> Unit) {
    Header({ classes("mdc-top-app-bar") }) {
        DisposableEffect(null) {
            _Internal_MDCTopAppBar(scopeElement)
            onDispose {}
        }

        MdcTopAppBarContext().content()
    }
}

@Composable
fun MdcTopAppBarMain(withTabs: Boolean = false, content: @Composable () -> Unit) {
    Main({
        classes(if (withTabs) "mdc-top-app-bar--fixed-adjust-with-tabs" else "mdc-top-app-bar--fixed-adjust")
    }) {
        content()
    }
}
