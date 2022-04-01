import androidx.compose.runtime.*
import app.softwork.routingcompose.NavBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.LocalLang
import material.*
import material.utils.MdcTrigger
import material.utils.rememberMdcTrigger
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Text
import utils.FlexColumn


@Composable
private fun HomeTopBar(title: String, trigger: MdcTrigger, langMenu: LangMenu) {
    MdcTopAppBar {
        Row {
            Section(SectionAlign.Start) {
                NavigationIcon("menu", "Menu") { trigger.open() }
                Title { Text(title) }
            }
            Section(SectionAlign.End) {
                langMenu()
            }
        }
    }
}

enum class Page { Games, Packer }

@Composable
fun NavBuilder.Home(games: List<Game>?, langMenu: LangMenu) {

    val trigger = rememberMdcTrigger()

    var page: Page? by remember { mutableStateOf(null) }

    MdcDrawer(trigger.flow) {
        MdcDrawerHeader {
            MdcDrawerHeaderTitle {
                Text("Super-Set Deck‽")
            }
            MdcDrawerHeaderSubtitle {
                Text("198 ${LocalLang.current.Cards}")
                games?.size?.let { Text(", $it ${LocalLang.current.Games}") }
                Text("!")
            }
        }
        MdcDrawerContent {
            MdcNavList({
                onClick { trigger.close() }
            }) {
                MdcNavListItem("#/games", page == Page.Games) { Text(LocalLang.current.Games) }
                MdcNavListItem("#/games/packer", page == Page.Packer) { Text("Packer") }
            }
        }
    }

    HomeTopBar(
        when (page) {
            Page.Games -> LocalLang.current.Games
            Page.Packer -> "Packer"
            null -> ""
        },
        trigger, langMenu
    )

    MdcTopAppBarMain {
        FlexColumn(JustifyContent.Center, AlignItems.Center) {
            val router by rememberUpdatedState(Router.current)

            route("/packer") {
                SideEffect { page = Page.Packer }
                if (games == null) {
                    Loader()
                } else {
                    Packer(games)
                }
            }
            route("/") {
                SideEffect { page = Page.Games }
                if (games == null) {
                    Loader()
                } else {
                    GamesList(
                        games = games,
                        playerCount = parameters?.map?.get("playerCount")?.firstOrNull()?.toIntOrNull() ?: 0,
                        gameType = parameters?.map?.get("gameType")?.firstOrNull(),
                        favorites = parameters?.map?.get("favorites")?.firstOrNull() == "1"
                    )
                }
            }
            noMatch {
                SideEffect { router.navigate("/games") }
            }
        }
    }
}
