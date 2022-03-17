package utils

import org.w3c.dom.Element
import org.w3c.dom.HTMLCollection
import org.w3c.dom.get


inline fun HTMLCollection.forEach(block: (Element) -> Unit) {
    (0 until length).forEach { block(this.get(it)!!) }
}
