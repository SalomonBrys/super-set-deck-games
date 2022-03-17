package material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLElement


@Composable
fun ElementScope<HTMLElement>.MdcRipple(unbounded: Boolean? = null) {
    DisposableEffect(null) {
        val ripple = _Internal_MDCRipple(scopeElement)
        if (unbounded != null) {
            ripple.unbounded = unbounded
        }
        onDispose {}
    }
}
