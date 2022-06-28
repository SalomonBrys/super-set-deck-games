import androidx.compose.runtime.*
import app.softwork.routingcompose.RouteBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import data.name
import dev.petuska.kmdc.card.Content
import dev.petuska.kmdc.card.MDCCard
import dev.petuska.kmdc.card.PrimaryAction
import dev.petuska.kmdc.chips.grid.ActionChip
import dev.petuska.kmdc.chips.grid.MDCChipsGrid
import dev.petuska.kmdc.chips.onInteraction
import dev.petuska.kmdc.icon.button.MDCIconButton
import dev.petuska.kmdc.tab.Content
import dev.petuska.kmdc.tab.Label
import dev.petuska.kmdc.tab.Tab
import dev.petuska.kmdc.tab.bar.MDCTabBar
import dev.petuska.kmdc.tab.bar.onActivated
import dev.petuska.kmdc.tab.indicator.Content
import dev.petuska.kmdc.tab.indicator.Indicator
import dev.petuska.kmdc.tab.indicator.MDCTabIndicatorType
import dev.petuska.kmdc.tab.scroller.Scroller
import dev.petuska.kmdc.top.app.bar.*
import dev.petuska.kmdcx.icons.MDCIcon
import dev.petuska.kmdcx.icons.mdcIcon
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLHeadingElement
import utils.*
import kotlin.time.Duration.Companion.days

private fun Game.hasReference() = playerReferences.refs.isNotEmpty() || gameReferences.isNotEmpty()

@Composable
private fun GameRules(game: Game, section: String?) {
    val router = Router.current

    var html: String? by remember { mutableStateOf(null) }
    val langId = LocalLang.current.id.takeIf { it in game.names } ?: "en".takeIf { it in game.names } ?: game.names.values.first()
    LaunchedEffect(langId) {
        html = null
        try {
            html = window.fetch("games/${game.id}/${langId}.inc.html").await().text().await()
        } catch (_: Throwable) {
            html = "<h1>Error :(</h1>"
        }
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
    }) {
        var isFav by remember { mutableStateOf(Cookies["favs"]?.split(",")?.contains(encodeURIComponent(game.id)) ?: false) }

        MDCIconButton(
            icon = if (isFav) MDCIcon.Star else MDCIcon.StarBorder,
            touch = true,
            attrs = {
                onClick {
                    val newFav = !isFav
                    val favs = Cookies["favs"]?.split(",")?.toSet() ?: emptySet()
                    Cookies.set(
                        "favs",
                        (if (newFav) favs + encodeURIComponent(game.id) else favs - encodeURIComponent(game.id)).joinToString(","),
                        (5 * 365).days
                    )
                    isFav = newFav
                }
                mdcIcon()
            }
        )
        Text(game.names[LocalLang.current.id] ?: game.names["en"] ?: game.names.values.firstOrNull() ?: "")
    }

    MDCChipsGrid(attrs = {
        onInteraction {
            router.navigate("/games?gameType=${it.detail.chipID}")
        }
    }) {
        game.types.forEach {
            val encoded = encodeURIComponent(it)
            ActionChip(
                id = encoded,
            ) {
                Text(LocalLang.current.gameTypes[it] ?: it)
            }
        }
    }

    P {
        Text("${game.playerCount.toShortStrings().joinToString()} ${LocalLang.current.players}")
    }

    MDCCard(attrs = {
        style {
            width(98.percent)
            maxWidth(60.cssRem)
        }
    }) {
        Content(attrs = {
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
}

@Composable
fun GameReferences(game: Game) {
    val references = remember(game) {
        listOf("G" to game.gameReferences, "P" to game.playerReferences.refs)
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
    }

    var focusRefId: String? by remember { mutableStateOf(null) }
    var focusRefShown by remember { mutableStateOf(false) }

    val noSleep = remember { NoSleep() }

    FlexRow(JustifyContent.Center, AlignItems.Center, {
        style {
            flexWrap(FlexWrap.Wrap)
        }
    }) {
        references.forEach { ref ->
            MDCCard(
                attrs = {
                    style {
                        margin(1.em)
                        overflow("hidden")
                    }
                    onClick {
                        noSleep.enable()
                        focusRefId = ref
                        focusRefShown = true
                    }
                }
            ) {
                PrimaryAction {
                    Img(src = "games/${game.id}/$ref.png") {
                        style {
                            when (ref.last()) {
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
            if (focusRefShown) {
                opacity(1)
            } else {
                opacity(0)
                property("pointer-events", "none")
            }
            property("transition", "opacity 150ms")
            overflow("hidden")
        }
        onClick {
            noSleep.disable()
            focusRefShown = false
        }
    }) {
        if (focusRefId != null) {
            Img(src = "games/${game.id}/$focusRefId.png") {
                style {
                    width(100.percent)
                    height(100.percent)
                    property("object-fit", "contain")
                    transform { scale(1.06) }
                }
            }
        }
    }
}

@Composable
fun RouteBuilder.Game(game: Game?, langMenu: LangMenu) {
    var selectedTab by remember { mutableStateOf(-1) }

    val router = Router.current

    MDCTopAppBar {
        TopAppBar({
            onNav { router.navigate("/games") }
        }) {
            Row {
                Section(MDCTopAppBarSectionAlign.Start) {
                    NavButton(
                        touch = true,
                        attrs = { mdcIcon() }
                    ) { Text(MDCIcon.ArrowBack.type) }
                    Title(game?.name ?: "")
                }
                Section(MDCTopAppBarSectionAlign.End) {
                    langMenu()
                }
            }
        }
        Main {
            if (game?.hasReference() == true) {
                MDCTabBar({
                    onActivated {
                        when (it.detail.index) {
                            0 -> router.navigate("/game/${game.id}")
                            1 -> router.navigate("/game/${game.id}/references")
                        }
                    }
                }) {
                    Scroller {
                        Tab(active = selectedTab == 0) {
                            Content {
                                Icon(MDCIcon.Gavel)
                                Label(LocalLang.current.Rules)
                            }
                            Indicator(active = selectedTab == 0) { Content(MDCTabIndicatorType.Underline) }
                        }
                        Tab(active = selectedTab == 1) {
                            Content {
                                Icon(MDCIcon.Lightbulb)
                                Label(LocalLang.current.References)
                            }
                            Indicator(active = selectedTab == 1) { Content(MDCTabIndicatorType.Underline) }
                        }
                    }
                }
            }

            FlexColumn(JustifyContent.Center, AlignItems.Center, {
                style {
                    marginBottom(2.cssRem)
                }
            }) {
                if (game == null) {
                    Loader()
                } else {
                    route("/references") {
                        SideEffect { selectedTab = 1 }
                        GameReferences(game)
                    }
                    string {
                        LaunchedEffect(null) { router.navigate("/game/${game.id}") }
                    }
                    noMatch {
                        SideEffect { selectedTab = 0 }
                        GameRules(game, parameters?.map?.get("section")?.firstOrNull()?.let { decodeURIComponent(it) })
                    }
                }
            }
        }
    }
}