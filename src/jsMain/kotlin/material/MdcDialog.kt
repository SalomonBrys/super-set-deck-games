package material

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*


@Suppress("unused")
@JsModule("@material/dialog/dist/mdc.dialog.css")
private external val MdcDialogStyle: dynamic


class MdcDialogActionsContext(scope: DOMScope<HTMLDivElement>) : DOMScope<HTMLDivElement> by scope {
    @Composable
    fun MdcDialogAction(
        action: String,
        attrs: AttrBuilderContext<HTMLButtonElement>? = null,
        content: ContentBuilder<HTMLSpanElement>
    ) {
        MdcButton(
            onClick = {},
            attrs = {
                attr("data-mdc-dialog-action", action)
                attrs?.invoke(this)
            }
        ) {
            content()
        }
    }
}

class MdcDialogContentContext(scope: DOMScope<HTMLDivElement>) : DOMScope<HTMLDivElement> by scope {
    @Composable
    fun MdcListContext.MdcDialogListItem(
        action: String,
        attrs: AttrBuilderContext<HTMLLIElement>? = null,
        content: @Composable MdcListListItemContext.() -> Unit
    ) {
        MdcListItem(
            attrs = {
                attr("data-mdc-dialog-action", action)
                attrs?.invoke(this)
            },
            content = content
        )
    }
}

class MdcDialogContext(private val fullScreen: Boolean, scope: DOMScope<HTMLDivElement>) : DOMScope<HTMLDivElement> by scope {
    @Composable
    fun MdcDialogTitle(
        attrs: AttrBuilderContext<HTMLHeadingElement>? = null,
        content: ContentBuilder<HTMLHeadingElement>
    ) {
        if (fullScreen) {
            Div({
                classes("mdc-dialog__header")
            }) {
                H2({
                    classes("mdc-dialog__title")
                    attrs?.invoke(this)
                }) { content() }
                Button({
                    classes("mdc-icon-button", "material-icons", "mdc-dialog__close")
                    attr("data-mdc-dialog-action", "close")
                }) { Text("close") }
            }
        } else {
            H2({
                classes("mdc-dialog__title")
                attrs?.invoke(this)
            }) { content() }
        }
    }

    @Composable
    fun MdcDialogContent(
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: @Composable MdcDialogContentContext.() -> Unit
    ) {
        Div({
            classes("mdc-dialog__content")
            attrs?.invoke(this)
        }) {
            MdcDialogContentContext(this).content()
        }
    }

    @Composable
    fun MdcDialogActions(
        attrs: AttrBuilderContext<HTMLDivElement>? = null,
        content: @Composable MdcDialogActionsContext.() -> Unit
    ) {
        Div({
            classes("mdc-dialog__actions")
            attrs?.invoke(this)
        }) {
            MdcDialogActionsContext(this).content()
        }
    }
}

@Composable
fun MdcDialog(
    trigger: Flow<Boolean>,
    onAction: (String) -> Unit,
    fullScreen: Boolean = false,
    dialogAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    surfaceAttrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: @Composable MdcDialogContext.() -> Unit
) {
    Div({
        classes("mdc-dialog")
        if (fullScreen) {
            classes("mdc-dialog--fullscreen")
        }
        dialogAttrs?.invoke(this)
    }) {
        var dialog by remember { mutableStateOf<_Internal_MDCDialog?>(null) }

        DisposableEffect(null) {
            dialog = _Internal_MDCDialog(scopeElement)
            dialog!!.listen("MDCDialog:closing") {
                onAction(it.detail.unsafeCast<_Internal_MDCDialog.ClosingDetails>().action)
            }
            onDispose {}
        }

        LaunchedEffect(trigger, dialog) {
            val d = dialog ?: return@LaunchedEffect
            trigger.collect {
                if (it) d.open()
                else d.close()
            }
        }

        Div({
            classes("mdc-dialog__container")
        }) {
            Div({
                classes("mdc-dialog__surface")
                attr("role", if (fullScreen) "dialog" else "alertdialog")
                attr("aria-modal", "true")
                surfaceAttrs?.invoke(this)
            }) {
                MdcDialogContext(fullScreen, this).content()
            }
        }
        Div({ classes("mdc-dialog__scrim") })
    }
}
