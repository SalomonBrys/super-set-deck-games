import androidx.compose.runtime.*
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.NavBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import data.langs
import material.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import utils.FlexColumn
import utils.encodeURIComponent


@Composable
private fun GamesListTopBar(langMenu: LangMenu) {
    MdcTopAppBar {
        Row {
            Section(SectionAlign.Start) {
                Title {
                    Text(LocalLang.current.games)
                }
            }
            Section(SectionAlign.End) {
                langMenu()
            }
        }
    }
}

private fun Router.applyFilter(playerCount: Int, gameType: String?) {
    val params = buildList {
        if (playerCount != 0) add("playerCount=$playerCount")
        if (gameType != null) add("gameType=${encodeURIComponent(gameType)}")
    }
    navigate(
        if (params.isNotEmpty()) "/?${params.joinToString("&")}"
        else "/"
    )
}

private fun List<Game>.playerCounts(gameType: String?): List<Int> =
    filter { if (gameType == null) true else gameType in it.types }
        .flatMap { it.playerCount }
        .distinct().sorted()

private fun List<Game>.gameTypes(playerCount: Int): List<String> =
    filter { if (playerCount == 0) true else playerCount in it.playerCount }
        .flatMap { it.types }
        .distinct().sorted()

@Composable
private fun GamesListFilters(games: List<Game>, playerCount: Int, gameType: String?) {
    val router = Router.current

    @Suppress("NAME_SHADOWING") val playerCount by rememberUpdatedState(playerCount)
    @Suppress("NAME_SHADOWING") val gameType by rememberUpdatedState(gameType)

    val allCounts = games.playerCounts(null)

    MdcChipSet {
        MdcDialog(
            onAction = {
                if (it.startsWith("s:")) {
                    router.applyFilter(
                        playerCount = if (it == "s:all") 0 else it.removePrefix("s:").toInt(),
                        gameType = gameType
                    )
                }
            },
            anchorContent = { openDialog ->
                MdcChip(
                    onClick = {
                        openDialog()
                    }
                ) {
                    Text(when (playerCount) {
                        0 -> "${allCounts.first()}-${allCounts.last()} ${LocalLang.current.players}"
                        1 -> "1 ${LocalLang.current.player}"
                        else -> "$playerCount ${LocalLang.current.players}"
                    })
                }
            }
        ) {
            MdcDialogTitle { Text("Select player count") }
            MdcDialogContent {
                val playerCounts = games.playerCounts(gameType)
                MdcList {
                    MdcDialogListItem("s:all") { Text("${allCounts.first()}-${allCounts.last()}") }
                    MdcListDivider()
                    playerCounts.forEach {
                        MdcDialogListItem("s:$it") { Text("$it") }
                    }
                }
            }
            MdcDialogActions {
                MdcDialogAction("close") { Text("Cancel") }
            }
        }

        MdcDialog(
            onAction = {
                if (it.startsWith("s:")) {
                    router.applyFilter(
                        playerCount = playerCount,
                        gameType = if (it == "s:all") null else it.removePrefix("s:")
                    )
                }
            },
            anchorContent = { openDialog ->
                MdcChip(
                    onClick = {
                        openDialog()
                    }
                ) {
                    Text(gameType?.let { LocalLang.current.gameTypes[it] ?: it } ?: LocalLang.current.allTypes)
                }
            }
        ) {
            MdcDialogTitle { Text("Select game type") }
            MdcDialogContent {
                MdcList {
                    MdcDialogListItem("s:all") { Text(LocalLang.current.allTypes) }
                    MdcListDivider()
                    val gameTypes = games.gameTypes(playerCount)
                    gameTypes.forEach {
                        MdcDialogListItem("s:$it") { Text(LocalLang.current.gameTypes[it] ?: it) }
                    }
                }
            }
            MdcDialogActions {
                MdcDialogAction("close") { Text("Cancel") }
            }
        }
    }

}

@Composable
private fun GamesListBody(games: List<Game>, playerCount: Int, gameType: String?) {
    GamesListFilters(games, playerCount, gameType)

    MdcList({
        style {
            width(100.percent)
            maxWidth(26.cssRem)
        }
    }) {
        val router = Router.current

        @Composable fun Game.name() = names[LocalLang.current.id] ?: names["en"] ?: names.values.first()
        games
            .filter {
                if (playerCount == 0) true else playerCount in it.playerCount
            }
            .filter {
                if (gameType == null) true else gameType in it.types
            }
            .sortedBy { it.name() }
            .forEach {
                MdcListItem(
                    onClick = {
                        router.navigate("/game/${it.id}")
                    },
                    attrs = {
                        style {
                            paddingTop(0.2.cssRem)
                            paddingBottom(0.6.cssRem)
                        }
                    }
                ) {
                    TwoLines(
                        primary = { Text(it.name()) },
                        secondary = { Text("${it.playerCount.joinToString()} ${LocalLang.current.players}") }
                    )
                }
            }
    }
}

@Composable
fun NavBuilder.GamesList(games: List<Game>?, langMenu: LangMenu) {
    GamesListTopBar(langMenu)

    MdcTopAppBarMain {
        FlexColumn(JustifyContent.Center, AlignItems.Center) {
            if (games == null) {
                Loader()
            } else {
                GamesListBody(
                    games = games,
                    playerCount = parameters?.map?.get("playerCount")?.firstOrNull()?.toIntOrNull() ?: 0,
                    gameType = parameters?.map?.get("gameType")?.firstOrNull()
                )
            }
        }
    }
}
