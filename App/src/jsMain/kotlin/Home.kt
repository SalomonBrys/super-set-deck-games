import androidx.compose.runtime.*
import app.softwork.routingcompose.RouteBuilder
import app.softwork.routingcompose.Router
import data.Game
import data.Lang
import data.LocalLang
import dev.petuska.kmdc.drawer.*
import dev.petuska.kmdc.list.MDCNavList
import dev.petuska.kmdc.list.item.ListItem
import dev.petuska.kmdc.list.onAction
import dev.petuska.kmdc.top.app.bar.*
import dev.petuska.kmdcx.icons.MDCIcon
import dev.petuska.kmdcx.icons.mdcIcon
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Text
import utils.FlexColumn


enum class Page(val fragment: String, val title: Lang.() -> String) { Games("", Lang::Games), Packer("packer", Lang::Packer) }

@Composable
fun RouteBuilder.Home(games: List<Game>?, langMenu: LangMenu) {

    val router = Router.current

    var isDrawerOpen: Boolean by remember { mutableStateOf(false) }
    var page: Page? by remember { mutableStateOf(null) }

    MDCDrawer(
        open = isDrawerOpen,
        type = MDCDrawerType.Modal,
        attrs = {
            onOpened { isDrawerOpen = true }
            onClosed { isDrawerOpen = false }
        }
    ) {
        Header {
            Title("Super-Set Deck‽")
            Subtitle {
                Text("198 ${LocalLang.current.Cards}")
                games?.size?.let { Text(", $it ${LocalLang.current.Games}") }
                Text("!")
            }
        }
        Content {
            MDCNavList(
                attrs = {
                    onAction {
                        val nextPage = Page.values()[it.detail.index]
                        router.navigate("/games/${nextPage.fragment}")
                        isDrawerOpen = false
                    }
                }
            ) {
                ListItem(
                    text = LocalLang.current.Games,
                    selected = page == Page.Games
                )
                ListItem(
                    text = LocalLang.current.Packer,
                    selected = page == Page.Packer
                )
            }
        }
    }

    MDCDrawerAppContent {
        MDCTopAppBar {
            TopAppBar(
                attrs = {
                    onNav { isDrawerOpen = true }
                }
            ) {
                Row {
                    Section(MDCTopAppBarSectionAlign.Start) {
                        NavButton(
                            touch = true,
                            attrs = { mdcIcon() }
                        ) { Text(MDCIcon.Menu.type) }
                        page?.let {
                            Title(it.title(LocalLang.current))
                        }
                    }
                    Section(MDCTopAppBarSectionAlign.End) {
                        langMenu()
                    }
                }
            }
            Main {
                FlexColumn(JustifyContent.Center, AlignItems.Center) {
                    route("/packer") {
                        SideEffect { page = Page.Packer }
                        if (games == null) {
                            Loader()
                        } else {
                            Packer(games)
                        }
                    }
                    string {
                        SideEffect { router.navigate("/games") }
                    }
                    noMatch {
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
                }

            }
        }
    }
}
