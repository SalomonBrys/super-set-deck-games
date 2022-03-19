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


@Composable
fun NavBuilder.Home(games: List<Game>?, langMenu: LangMenu) {

    val trigger = rememberMdcTrigger()

    var title by remember { mutableStateOf("") }

    MdcDrawer(trigger.flow) {
        MdcNavList({
            onClick { trigger.close() }
        }) {
            MdcNavListItem("#/games") { Text(LocalLang.current.games) }
            MdcNavListItem("#/games/packer") { Text("Packer") }
        }
    }

    HomeTopBar(title, trigger, langMenu)

    MdcTopAppBarMain {
        FlexColumn(JustifyContent.Center, AlignItems.Center) {
            val router by rememberUpdatedState(Router.current)
            val lang by rememberUpdatedState(LocalLang.current)

            route("/packer") {
                SideEffect { title = "Packer" }
                if (games == null) {
                    Loader()
                } else {
                    Packer(games)
                }
            }
            route("/") {
                SideEffect { title = lang.games }
                if (games == null) {
                    Loader()
                } else {
                    GamesList(
                        games = games,
                        playerCount = parameters?.map?.get("playerCount")?.firstOrNull()?.toIntOrNull() ?: 0,
                        gameType = parameters?.map?.get("gameType")?.firstOrNull()
                    )
                }
            }
            noMatch {
                SideEffect { router.navigate("/games") }
            }
        }
    }
}
