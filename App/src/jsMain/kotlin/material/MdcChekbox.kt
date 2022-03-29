package material

import androidx.compose.runtime.*
import kotlinx.coroutines.yield
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.svg.Path
import org.jetbrains.compose.web.svg.Svg
import org.w3c.dom.HTMLDivElement


@Suppress("unused")
@JsModule("@material/checkbox/dist/mdc.checkbox.css")
private external val MdcCheckboxStyle: dynamic


@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun MdcCheckbox(
    id: String,
    checked: Boolean?,
    onChange: (Boolean) -> Unit,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Div({ classes("mdc-touch-target-wrapper") }) {
        Div({
            classes("mdc-checkbox", "mdc-checkbox--touch")
            attrs?.invoke(this)
        }) {
            var count by remember { mutableStateOf(0) }
            var checkbox by remember { mutableStateOf<_Internal_MDCCheckbox?>(null) }

            DisposableEffect(null) {
                checkbox = _Internal_MDCCheckbox(scopeElement)
                onDispose {}
            }

            LaunchedEffect(checkbox, checked, count) {
                if (checkbox == null) return@LaunchedEffect
                if (checked != null) {
                    checkbox!!.indeterminate = false
                    checkbox!!.checked = checked
                } else {
                    checkbox!!.indeterminate = true
                }
            }

            Input(InputType.Checkbox) {
                classes("mdc-checkbox__native-control")
                id(id)
                onChange {
                    onChange(it.value)
                    ++count
                }
            }
            Div({ classes("mdc-checkbox__background") }) {
                Svg("0 0 24 24", { classes("mdc-checkbox__checkmark") }) {
                    Path("M1.73,12.91 8.1,19.28 22.79,4.59", {
                        classes("mdc-checkbox__checkmark-path")
                        attr("fill", "none")
                    })
                }
                Div({ classes("mdc-checkbox__mixedmark") })
            }
            Div({ classes("mdc-checkbox__ripple") })
            MdcRipple()
            Div({ classes("mdc-checkbox__focus-ring") })
        }
    }
}