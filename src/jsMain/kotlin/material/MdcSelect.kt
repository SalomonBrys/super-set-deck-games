package material

import androidx.compose.runtime.*
import kotlinx.coroutines.yield
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.svg.Polygon
import org.jetbrains.compose.web.svg.Svg
import org.w3c.dom.*


@Suppress("unused")
@JsModule("@material/select/dist/mdc.select.css")
private external val MdcSelectStyle: dynamic



class MdcSelectContext(scope: DOMScope<HTMLUListElement>) : DOMScope<HTMLUListElement> by scope {

    @Composable
    fun MdcSelectOption(
        value: String,
        disabled: Boolean = false,
        attrs: AttrBuilderContext<HTMLLIElement>? = null,
        content: ContentBuilder<HTMLSpanElement>? = null
    ) {
        Li({
            classes("mdc-deprecated-list-item")
            if (disabled) {
                classes("mdc-deprecated-list-item--disabled")
            }
            if (disabled) {
                attr("data-disabled", "true")
            }
            attr("data-value", value)
            attr("role", "option")
            attrs?.invoke(this)
        }) {
            Span({ classes("mdc-deprecated-list-item__ripple") })
            MdcRipple()
            if (content != null) {
                Span({ classes("mdc-deprecated-list-item__text") }) { content() }
            }
        }
    }
}

enum class MdcSelectVariant { Outlined, Filled }

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun MdcSelect(
    selected: String,
    onSelected: (String) -> Unit,
    label: String,
    variant: MdcSelectVariant = MdcSelectVariant.Outlined,
    listAriaLabel: String? = null,
    fixed: Boolean = false,
    disabled: Boolean = false,
    selectAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    menuAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable MdcSelectContext.() -> Unit
) {
    Div({
        classes(
            "mdc-select",
            when (variant) {
                MdcSelectVariant.Filled -> "mdc-select--filled"
                MdcSelectVariant.Outlined -> "mdc-select--outlined"
            }
        )
        if (disabled) {
            classes("mdc-select--disabled")
        }
        selectAttrs?.invoke(this)
    }) {
        var count by remember { mutableStateOf(0) }
        var select by remember { mutableStateOf<_Internal_MDCSelect?>(null) }

        @Suppress("NAME_SHADOWING") val selected by rememberUpdatedState(selected)

        DisposableEffect(null) {
            select = _Internal_MDCSelect(scopeElement)
            select!!.listen("MDCSelect:change") {
                if (select!!.value != selected) {
                    onSelected(select!!.value)
                }
                ++count
            }
            onDispose {}
        }

        LaunchedEffect(select, selected, count) {
            if (select == null) return@LaunchedEffect
            if (select!!.value == selected) return@LaunchedEffect
            yield()
            select!!.value = selected
        }

        Div({
            classes("mdc-select__anchor")
            attr("role", "button")
            attr("aria-haspopup", "listbox")
            attr("aria-expanded", "false")
            attr("aria-labelledby", "floating-label selected-text")
            if (disabled) {
                attr("aria-disabled", "true")
            }
        }) {
            when (variant) {
                MdcSelectVariant.Filled -> {
                    Span({ classes("mdc-select__ripple") })
                    Span({
                        classes("mdc-floating-label")
                        id("floating-label")
                    }) {
                        Text(label)
                    }
                }
                MdcSelectVariant.Outlined -> {
                    Span({ classes("mdc-notched-outline") }) {
                        Span({ classes("mdc-notched-outline__leading") })
                        Span({ classes("mdc-notched-outline__notch") }) {
                            Span({
                                classes("mdc-floating-label")
                                id("floating-label")
                            }) {
                                Text(label)
                            }
                        }
                        Span({ classes("mdc-notched-outline__trailing") })
                    }
                }
            }
            Span({ classes("mdc-select__selected-text-container") }) {
                Span({
                    classes("mdc-select__selected-text")
                    id("selected-text")
                })
            }
            Span({ classes("mdc-select__dropdown-icon") }) {
                Svg(
                    viewBox = "7 10 10 5",
                    attrs = {
                        classes("mdc-select__dropdown-icon-graphic")
                        attr("focusable", "false")
                    }
                ) {
                    Polygon(7, 10, 12, 15, 17, 10, attrs = {
                        classes("mdc-select__dropdown-icon-inactive")
                        attr("stroke", "none")
                        attr("fill-rule", "evenodd")
                    })
                    Polygon(7, 15, 12, 10, 17, 15, attrs = {
                        classes("mdc-select__dropdown-icon-active")
                        attr("stroke", "none")
                        attr("fill-rule", "evenodd")
                    })
                }
            }
            when (variant) {
                MdcSelectVariant.Filled -> {
                    Span({ classes("mdc-line-ripple") })
                }
                MdcSelectVariant.Outlined -> {}
            }
            MdcRipple()
        }

        Div({
            classes("mdc-select__menu", "mdc-menu", "mdc-menu-surface")
            if (fixed) {
                classes("mdc-menu-surface--fixed")
            } else {
                classes("mdc-menu-surface--fullwidth")
            }
            menuAttrs?.invoke(this)
        }) {
            Ul({
                classes("mdc-deprecated-list")
                attr("role", "listbox")
                if (listAriaLabel != null) {
                    attr("aria-label", listAriaLabel)
                }
            }) {
                MdcSelectContext(this).content()
            }
        }
    }
}
