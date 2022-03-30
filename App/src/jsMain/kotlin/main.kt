import androidx.compose.runtime.*
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import data.langs
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import material.MdcMenu
import material.MdcMenuAnchor
import material.MdcTopAppBarSectionContext
import material.utils.rememberMdcTrigger
import material_custom.MdcTextIconButton
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposableInBody
import utils.Cookies
import kotlin.time.Duration.Companion.days


private fun getLanguage(): String {
    val cookie = Cookies["lang"]
    if (cookie != null && cookie in langs) {
        return cookie
    }

    val navs = when {
        window.navigator.languages.isNotEmpty() -> {
            window.navigator.languages.map { it.split("-")[0] } .distinct()
        }
        window.navigator.language.isNotEmpty() -> {
            listOf(window.navigator.language.split("-")[0])
        }
        else -> listOf("en")
    }

    val lang = navs.firstOrNull { it in langs } ?: "en"
    return lang
}

typealias LangMenu = @Composable MdcTopAppBarSectionContext.() -> Unit

@Composable
private fun WithLang(content: @Composable (LangMenu) -> Unit) {
    var langId by remember { mutableStateOf(getLanguage()) }

    LaunchedEffect(langId) {
        Cookies.set("lang", langId, 365.days)
    }

    val lang = langs.getValue(langId)

    CompositionLocalProvider(LocalLang provides lang) {
        content {
            Action {
                MdcMenuAnchor {
                    val trigger = rememberMdcTrigger()

                    MdcTextIconButton(LocalLang.current.id.uppercase()) { trigger.open() }

                    MdcMenu(trigger.flow) {
                        langs.keys.sorted().forEach {
                            MdcMenuItem(onSelect = { langId = it }) { Text(it.uppercase()) }
                        }
                    }
                }
            }
        }
    }
}

fun homePath() = Cookies["lastFilterHash"] ?: "/"

@Composable
fun App() {
    var games by remember { mutableStateOf<List<Game>?>(null) }

    LaunchedEffect(null) {
        try {
            val response = window.fetch("games/games.json").await()
            if (!response.ok) error("${response.status} ${response.statusText}")
            games = Json.decodeFromString(response.text().await())
        } catch (e: Throwable) {
            window.alert("Error loading games: ${e.message ?: e.toString()}")
        }
    }

    WithLang { langMenu ->
        HashRouter(initRoute = "/") {
            val router by rememberUpdatedState(Router.current)

            route("/game") {
                string { gameId ->
                    if (games == null) {
                        Game(null, langMenu)
                    } else {
                        val game = games!!.firstOrNull { it.id == gameId }
                        if (game != null) {
                            Game(game, langMenu)
                        } else {
                            SideEffect { router.navigate("/") }
                        }
                    }
                }
                noMatch {
                    SideEffect { router.navigate("/") }
                }
            }
            route("/games") {
                LaunchedEffect(window.location.hash) {
                    Cookies.set("lastFilterHash", window.location.hash.removePrefix("#"), 14.days)
                }
                Home(games, langMenu)
            }
            noMatch {
                SideEffect { router.navigate("/games") }
            }
        }
    }
}

fun main() {
    window.addEventListener("load", {
        MainScope().launch {
            try {
                window.navigator.serviceWorker.register("ServiceWorker.js").await()
                console.log("Service worker registered")
            } catch (ex: Throwable) {
                console.error("Could not register service worker: ${ex.message}")
            }
        }
    })


    renderComposableInBody { App() }
}
