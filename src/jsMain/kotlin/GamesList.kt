import androidx.compose.runtime.*
import app.softwork.routingcompose.NavBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import data.name
import material.*
import material.utils.MdcTrigger
import material.utils.rememberMdcTrigger
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import utils.FlexColumn
import utils.encodeURIComponent


private fun Router.applyFilter(playerCount: Int, gameType: String?) {
    val params = buildList {
        if (playerCount != 0) add("playerCount=$playerCount")
        if (gameType != null) add("gameType=${encodeURIComponent(gameType)}")
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
private fun GamesListFilters(games: List<Game>, playerCount: Int, gameType: String?) {
    val router = Router.current

    @Suppress("NAME_SHADOWING") val playerCount by rememberUpdatedState(playerCount)
    @Suppress("NAME_SHADOWING") val gameType by rememberUpdatedState(gameType)

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
                        gameType = gameType
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
                        gameType = if (it == "s:all") null else it.removePrefix("s:")
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

}

@Composable
fun GamesList(games: List<Game>, playerCount: Int, gameType: String?) {
    GamesListFilters(games, playerCount, gameType)

    MdcList({
        style {
            width(100.percent)
            maxWidth(26.cssRem)
        }
    }) {
        val router = Router.current

        games
            .filter {
                if (playerCount == 0) true else playerCount in it.playerCount
            }
            .filter {
                if (gameType == null) true else gameType in it.types
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
                    TwoLines(
                        primary = { Text(it.name) },
                        secondary = { Text("${it.playerCount.joinToString()} ${LocalLang.current.players}") }
                    )
                }
            }
    }
}
