package material_custom

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLElement

@Composable
fun MdcTopAppBarMain(
    withTabs: Boolean = false,
    attrs: AttrBuilderContext<HTMLElement>? = null,
    content: ContentBuilder<HTMLElement>
) = material.MdcTopAppBarMain(
    attrs = {
        if (withTabs) classes("mdc-top-app-bar--fixed-adjust-with-tabs")
        attrs?.invoke(this)
    },
    content = content
)
