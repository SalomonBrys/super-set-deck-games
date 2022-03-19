import androidx.compose.runtime.*
import app.softwork.routingcompose.NavBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import kotlinx.browser.window
import kotlinx.coroutines.await
import material.*
import material_custom.MdcTopAppBarMain
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*
import utils.*


private fun Game.hasReference() = playerReferences.isNotEmpty() || gameReferences.isNotEmpty()

@Composable
private fun GameTopBar(game: Game?, langMenu: LangMenu, selectedTab: Int, selectTab: (Int) -> Unit) {
    val router by rememberUpdatedState(Router.current)

    MdcTopAppBar {
        Row {
            Section(SectionAlign.Start) {
                NavigationIcon("arrow_back", "Back") {
                    router.navigate(homePath())
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

            MdcTabBar(
                selected = selectedTab,
                onSelected = selectTab,
            ) {
                MdcTab("gavel", {
                    onClick {
                        if (selectedTab == 0) {
                            router.navigate("/game/${game.id}")
                            window.scrollTo(ScrollToOptions(top = 0.0, behavior = ScrollBehavior.SMOOTH))
                        }
                    }
                }) { Text(LocalLang.current.rules) }
                MdcTab("lightbulb") { Text(LocalLang.current.references) }
            }
        }
    }
}

@Composable
private fun GameRules(game: Game, section: String?) {
    var html: String? by remember { mutableStateOf(null) }

    val langId = LocalLang.current.id.takeIf { it in game.names } ?: "en".takeIf { it in game.names } ?: game.names.values.first()
    LaunchedEffect(langId) {
        html = null
        html = window.fetch("games/${game.id}/${langId}.inc.html").await().text().await()
    }

    H1({
        style {
            fontFamily("Picon-Extended", "sans-serif")
            color(Color("var(--mdc-theme-primary)"))
            padding(0.5.cssRem)
            textAlign("center")
            fontSize(2.5.em)
            marginBottom(0.5.cssRem)
        }
    }) { Text(game.names.get(LocalLang.current.id) ?: game.names.get("en") ?: game.names.values.firstOrNull() ?: "") }

    MdcChipSet({
        style {
            marginBottom(1.cssRem)
            justifyContent(JustifyContent.Center)
        }
    }) {
        val router by rememberUpdatedState(Router.current)
        game.types.forEach {
            val encoded = encodeURIComponent(it)
            MdcChip(
                id = encoded,
                onInteract = { router.navigate("/games?gameType=$encoded") },
            ) {
                Text(it)
            }
        }
    }

    MdcCard(attrs = {
        style {
            width(100.percent)
            maxWidth(60.cssRem)
        }
        classes("adoc", "adoc-fragment")
    }) {
        val router by rememberUpdatedState(Router.current)
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
                scopeElement.querySelectorAll("h2, h3, h4").forEach { heading ->
                    heading as HTMLHeadingElement
                    if (heading.id.isNotEmpty()) {
                        heading.style.cursor = "pointer"
                        heading.onclick = {
                            router.navigate("/game/${game.id}?section=${heading.id}")
                        }
                    }
                }
            }
            onDispose {}
        }

        var hasLoaded by remember { mutableStateOf(false) }
        DisposableEffect(html, section) {
            if (html != null) {
                if (section != null) {
                    scopeElement.querySelector("#$section")?.scrollIntoView(if (hasLoaded) js("{ behavior: \"smooth\" }") else null)
                }
                hasLoaded = true
            }
            onDispose {}
        }

        if (html == null) {
            Loader()
        }
    }
}

@Composable
fun GameReferences(game: Game) {
    val references = listOf("G" to game.gameReferences, "P" to game.playerReferences)
        .flatMap { (prefix, list) ->
            list.flatMapIndexed { index, card ->
                buildList {
                    add("R-$prefix$index-R${card.recto}")
                    if (card.verso != null) {
                        add("R-$prefix$index-V${card.verso}")
                    }
                }
            }
        }

    var shown by remember { mutableStateOf<String?>(null) }

    val noSleep = remember { NoSleep() }

    FlexRow(JustifyContent.Center, AlignItems.Center, {
        style {
            flexWrap(FlexWrap.Wrap)
        }
    }) {
        references.forEach {
            MdcCard(
                onClick = {
                    noSleep.enable()
                    shown = it
                },
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.Center)
                        alignItems(AlignItems.Center)
                        margin(1.em)
                    }
                }
            ) {
                Img(src = "games/${game.id}/$it.png") {
                    style {
                        when (it.last()) {
                            'P' -> {
                                height(400.px)
                                maxHeight(80.vw)
                            }
                            'L' -> {
                                width(400.px)
                                maxWidth(80.vw)
                            }
                        }
                    }
                }
            }
        }
    }

    FlexColumn(JustifyContent.Center, AlignItems.Center, {
        style {
            position(Position.Absolute)
            width(100.percent)
            height(100.percent)
            top(0.percent)
            left(0.percent)
            backgroundColor(Color.black)
            property("z-index", "10")
            if (shown != null) {
                opacity(1)
            } else {
                opacity(0)
                property("pointer-events", "none")
            }
            property("transition", "opacity 150ms")
        }
        onClick {
            noSleep.disable()
            shown = null
        }
    }) {
        if (shown != null) {
            Img(src = "games/${game.id}/$shown.png") {
                style {
                    width(100.percent)
                    height(100.percent)
                    property("object-fit", "contain")
                }
            }
        }
    }

}

@Composable
fun NavBuilder.Game(game: Game?, langMenu: LangMenu) {
    var tab by remember { mutableStateOf(-1) }

    val router by rememberUpdatedState(Router.current)

    GameTopBar(game, langMenu, tab) {
        when (it) {
            0 -> router.navigate("/game/${game?.id}")
            1 -> router.navigate("/game/${game?.id}/references")
        }
    }

    MdcTopAppBarMain(withTabs = game?.hasReference() == true) {
        FlexColumn(JustifyContent.Center, AlignItems.Center) {
            if (game == null) {
                Loader()
            } else {
                route("/references") {
                    SideEffect { tab = 1 }
                    GameReferences(game)
                }
                route("/") {
                    SideEffect { tab = 0 }
                    GameRules(game, parameters?.map?.get("section")?.firstOrNull()?.let { decodeURIComponent(it) })
                }
                noMatch {
                    SideEffect { router.navigate("/game/${game.id}") }
                }
            }
        }
    }
}