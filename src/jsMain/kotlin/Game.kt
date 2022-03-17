import androidx.compose.runtime.*
import app.softwork.routingcompose.NavBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.Lang
import data.LocalLang
import kotlinx.browser.window
import kotlinx.coroutines.await
import material.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement
import utils.FlexColumn
import utils.decodeURIComponent
import utils.forEach


private fun Game.hasReference() = playerReferences.isNotEmpty() || gameReferences.isNotEmpty()

@Composable
private fun GameTopBar(game: Game?, langMenu: LangMenu) {
    MdcTopAppBar {
        Row {
            Section(SectionAlign.Start) {
                val router = Router.current
                NavigationIcon("arrow_back", "Back") {
                    router.navigate("/")
                }
                Title {
                    Text(game?.names?.get(LocalLang.current.id) ?: game?.names?.get("en") ?: game?.names?.values?.firstOrNull() ?: "")
                }
            }
            Section(SectionAlign.End) {
                langMenu()
            }
        }
        if (game?.hasReference() == true) {
            var selectedTab by remember { mutableStateOf(0) }
            MdcTabBar(
                selected = selectedTab,
                onSelected = { selectedTab = it }
            ) {
                MdcTab("gavel") { Text(LocalLang.current.rules) }
                MdcTab("lightbulb") { Text(LocalLang.current.references) }
            }
        }
    }
}

@Composable
private fun GameRules(game: Game, section: String?) {
    var html: String? by remember { mutableStateOf(null) }

    val langId = LocalLang.current.id.takeIf { it in game.names } ?: "en".takeIf { it in game.names } ?: game.names.values.first()
    LaunchedEffect(null) {
        html = window.fetch("games/${game.id}/${langId}.inc.html").await().text().await()
    }

    MdcCard({
        style {
            width(100.percent)
            maxWidth(60.cssRem)
        }
        classes("adoc", "adoc-fragment")
    }) {
        DisposableEffect(html) {
            if (html != null) {
                scopeElement.innerHTML = html!!
                scopeElement.getElementsByTagName("a").forEach {
                    it as HTMLAnchorElement
                    if ('#' in it.href) {
                        val anchor = it.href.split("#")[1]
                        it.href = "#/game/${game.id}?section=$anchor"
                    }
                }
            }
            onDispose {}
        }

        DisposableEffect(html, section) {
            if (html != null) {
                console.log(scopeElement.querySelector("#$section")?.scrollIntoView())
            }
            onDispose {}
        }

        if (html == null) {
            Loader()
        }
    }
}

@Composable
fun NavBuilder.Game(game: Game?, langMenu: LangMenu) {
    GameTopBar(game, langMenu)

    MdcTopAppBarMain(withTabs = game?.hasReference() == true) {
        FlexColumn(JustifyContent.Center, AlignItems.Center) {
            if (game == null) {
                Loader()
            } else {
                GameRules(game, parameters?.map?.get("section")?.firstOrNull()?.let { decodeURIComponent(it) })
            }
        }
    }
}