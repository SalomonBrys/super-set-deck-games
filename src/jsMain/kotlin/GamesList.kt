import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import data.name
import material.*
import material.utils.rememberMdcTrigger
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import utils.*


private fun Router.applyFilter(playerCount: Int, gameType: String?, favorites: Boolean) {
    val params = buildList {
        if (playerCount != 0) add("playerCount=$playerCount")
        if (gameType != null) add("gameType=${encodeURIComponent(gameType)}")
        if (favorites) add("favorites=1")
    }
    navigate(
        if (params.isNotEmpty()) "/games?${params.joinToString("&")}"
        else "/games"
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
private fun GamesListFilters(games: List<Game>, playerCount: Int, gameType: String?, favorites: Boolean) {
    val router = Router.current

    @Suppress("NAME_SHADOWING") val playerCount by rememberUpdatedState(playerCount)
    @Suppress("NAME_SHADOWING") val gameType by rememberUpdatedState(gameType)
    @Suppress("NAME_SHADOWING") val favorites by rememberUpdatedState(favorites)

    val allCounts = games.playerCounts(null)

    MdcChipSet {
        val playerCountTrigger = rememberMdcTrigger()
        MdcChip(
            id = "playerCount",
            onInteract = { playerCountTrigger.open() }
        ) {
            Text(when (playerCount) {
                0 -> "${allCounts.first()}-${allCounts.last()} ${LocalLang.current.players}"
                1 -> "1 ${LocalLang.current.player}"
                else -> "$playerCount ${LocalLang.current.players}"
            })
        }
        MdcDialog(
            trigger = playerCountTrigger.flow,
            onAction = {
                if (it.startsWith("s:")) {
                    router.applyFilter(
                        playerCount = if (it == "s:all") 0 else it.removePrefix("s:").toInt(),
                        gameType = gameType,
                        favorites = favorites
                    )
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

        val gameTypeTrigger = rememberMdcTrigger()
        MdcChip(
            id = "gameType",
            onInteract = { gameTypeTrigger.open() }
        ) {
            Text(gameType?.let { LocalLang.current.gameTypes[it] ?: it } ?: LocalLang.current.allTypes)
        }
        MdcDialog(
            trigger = gameTypeTrigger.flow,
            onAction = {
                if (it.startsWith("s:")) {
                    router.applyFilter(
                        playerCount = playerCount,
                        gameType = if (it == "s:all") null else it.removePrefix("s:"),
                        favorites = favorites
                    )
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

    MdcFormField {
        MdcCheckbox(
            id = "favorites",
            checked = favorites,
            onChange = { checked ->
                router.applyFilter(
                    playerCount = playerCount,
                    gameType = gameType,
                    favorites = checked
                )
            }
        )
        Label(forId = "favorites") {
            Text(LocalLang.current.Favorites_only)
        }
    }
}

@Composable
fun GamesList(games: List<Game>, playerCount: Int, gameType: String?, favorites: Boolean) {
    GamesListFilters(games, playerCount, gameType, favorites)

    MdcList({
        style {
            width(100.percent)
            maxWidth(26.cssRem)
        }
    }) {
        val router = Router.current

        val favs = Cookies["favs"]?.split(",")?.map { decodeURIComponent(it) }?.toSet() ?: emptySet()

        games
            .filter {
                if (playerCount == 0) true else playerCount in it.playerCount
            }
            .filter {
                if (gameType == null) true else gameType in it.types
            }
            .filter {
                if (favorites) it.id in favs else true
            }
            .sortedBy { it.name }
            .forEach {
                MdcListItem(
                    onSelect = {
                        router.navigate("/game/${it.id}")
                    },
                    attrs = {
                        style {
                            paddingTop(0.2.cssRem)
                            paddingBottom(0.6.cssRem)
                        }
                    }
                ) {
                    FlexRow(alignItems = AlignItems.Center) {
                        if (it.id in favs) {
                            I({
                                classes("material-icons")
                                style { width(2.cssRem) }
                            }) { Text("star") }
                        } else {
                            Div({
                                style { width(2.cssRem) }
                            })
                        }
                        Div {
                            TwoLines(
                                primary = { Text(it.name) },
                                secondary = { Text("${it.playerCount.toShortStrings().joinToString()} ${LocalLang.current.players}") }
                            )
                        }
                    }
                }
            }
    }
}
