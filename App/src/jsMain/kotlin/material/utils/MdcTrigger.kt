package material.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class MdcTrigger(private val scope: CoroutineScope) {

    private val mFlow = MutableSharedFlow<Boolean>()

    val flow: Flow<Boolean> get() = mFlow.asSharedFlow()

    fun open() = emit(true)
    fun close() = emit(false)

    fun emit(v: Boolean) = scope.launch { mFlow.emit(v) }

}

@Composable
fun rememberMdcTrigger(): MdcTrigger {
    val scope = rememberCoroutineScope()
    return remember { MdcTrigger(scope) }
}
