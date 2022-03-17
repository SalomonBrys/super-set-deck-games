package material

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*


@Suppress("unused")
@JsModule("@material/dialog/dist/mdc.dialog.css")
private external val MdcDialogStyle: dynamic


class MdcDialogActionsContext {
    @Composable
    fun MdcDialogAction(action: String, content: @Composable () -> Unit) {
        MdcButton(
            onClick = {},
            attrs = {
                attr("data-mdc-dialog-action", action)
            }
        ) {
            content()
        }
    }
}

class MdcDialogContentContext {
    @Composable
    fun MdcListContext.MdcDialogListItem(action: String, content: @Composable MdcListListItemContext.() -> Unit) {
        MdcListItem(
            attrs = {
                attr("data-mdc-dialog-action", action)
            },
            content = content
        )
    }
}

class MdcDialogContext {
    @Composable
    fun MdcDialogTitle(content: @Composable () -> Unit) {
        H2({ classes("mdc-dialog__title") }) { content() }
    }

    @Composable
    fun MdcDialogContent(content: @Composable MdcDialogContentContext.() -> Unit) {
        Div({ classes("mdc-dialog__content") }) {
            MdcDialogContentContext().content()
        }
    }

    @Composable
    fun MdcDialogActions(content: @Composable MdcDialogActionsContext.() -> Unit) {
        Div({ classes("mdc-dialog__actions") }) {
            MdcDialogActionsContext().content()
        }
    }
}

@Composable
fun MdcDialog(
    onAction: (String) -> Unit,
    anchorContent: @Composable (() -> Unit) -> Unit,
    content: @Composable MdcDialogContext.() -> Unit
) {
    var dialog by remember { mutableStateOf<_Internal_MDCDialog?>(null) }
    anchorContent { dialog?.open() }

    Div({
        classes("mdc-dialog")
    }) {
        DisposableEffect(null) {
            dialog = _Internal_MDCDialog(scopeElement)
            dialog!!.listen("MDCDialog:closing") {
                onAction(it.detail.unsafeCast<_Internal_MDCDialog.ClosingDetails>().action)
            }
            onDispose {}
        }

        Div({
            classes("mdc-dialog__container")
        }) {
            Div({
                classes("mdc-dialog__surface")
                attr("role", "alertdialog")
                attr("aria-modal", "true")
            }) {
                MdcDialogContext().content()
            }
        }
        Div({ classes("mdc-dialog__scrim") })
    }
}
