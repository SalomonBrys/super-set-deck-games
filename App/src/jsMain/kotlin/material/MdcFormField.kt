package material

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement


@Suppress("unused")
@JsModule("@material/form-field/dist/mdc.form-field.css")
private external val MdcFormFieldStyle: dynamic


@Composable
fun MdcFormField(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null
) {
    Div({
        classes("mdc-form-field")
        attrs?.invoke(this)
    }, content)
}
