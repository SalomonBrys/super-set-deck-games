import androidx.compose.runtime.*
import data.Game
import data.LocalLang
import data.name
import material.*
import material.utils.rememberMdcTrigger
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import utils.FlexColumn
import utils.FlexRow


@Composable
private fun <T> checkList(
    title: String,
    id: String,
    map: Map<T, Boolean>,
    onChange: (Map<T, Boolean>) -> Unit
) {
    MdcFormField({
        style {
            marginTop(2.em)
        }
    }) {
        MdcCheckbox(
            id = "all-$id",
            checked = when {
                map.all { it.value } -> true
                map.none { it.value } -> false
                else -> null
            },
            onChange = { checked ->
                onChange(map.keys.associateWith { checked })
            }
        )
        Label("all-$id") {
            H2 {
                Text(title)
            }
        }
    }
    FlexRow(JustifyContent.Center, AlignItems.Center, {
        style {
            flexWrap(FlexWrap.Wrap)
        }
    }) {
        map.keys.forEach { value ->
            MdcFormField {
                MdcCheckbox("$id-$value", map[value]!!, { onChange(map + (value to it)) })
                Label("$id-$value", {
                    style {
                        marginRight(2.em)
                    }
                }) {
                    Text(value.toString())
                }
            }
        }
    }
}


@Composable
fun Packer(games: List<Game>) {

    var counts: Map<Int, Boolean> by remember { mutableStateOf(emptyMap()) }
    var variants: Map<String, Boolean> by remember { mutableStateOf(emptyMap()) }
    var gameId by remember { mutableStateOf("") }

    val game = games.firstOrNull { it.id == gameId }

    LaunchedEffect(game) {
        if (game == null) return@LaunchedEffect
        counts = game.cards.flatMap { (_, g) -> g.flatMap { (_, p) -> p.players } } .distinct().sorted().associateWith { true }
        variants = (game.cards.keys - "Base").sorted().associateWith { true }
    }

    FlexColumn(JustifyContent.Center, AlignItems.Center) {
        MdcChipSet {

        }

        val trigger = rememberMdcTrigger()

        MdcButton(
            variant = MdcButtonVariant.Elevated,
            onClick = {
                trigger.open()
            },
            attrs = {
                style {
                    backgroundColor(Color("var(--mdc-theme-secondary)"))
                }
            }
        ) {
            Text(LocalLang.current.addGame)
        }

        MdcDialog(
            trigger = trigger.flow,
            fullScreen = true,
            onAction = {

            },
            surfaceAttrs = {
                style {
                    maxWidth(40.cssRem)
                }
            }
        ) {
            MdcDialogTitle { Text(LocalLang.current.addGame) }
            MdcDialogContent {
                FlexColumn(JustifyContent.Center, AlignItems.Center, {
                    style {
                        padding(1.em)
                    }
                }) {
                    MdcSelect(
                        selected = gameId,
                        onSelected = { gameId = it },
                        label = LocalLang.current.games,
                        fixed = true,
                        selectAttrs = {
                            style {
                                width(18.cssRem)
                            }
                        },
                        menuAttrs = {
                            style {
                                width(18.cssRem)
                            }
                        }
                    ) {
                        MdcSelectOption("")
                        games.forEach {
                            MdcSelectOption(it.id) {
                                Text(it.name)
                            }
                        }
                    }
                    if (game != null) {
                        key(gameId) {
                            checkList(
                                title = LocalLang.current.players.replaceFirstChar { it.uppercase() },
                                id = "count",
                                map = counts,
                                onChange = { counts = it }
                            )
                            if (game.cards.size > 1) {
                                checkList(
                                    title = LocalLang.current.variants,
                                    id = "variants",
                                    map = variants,
                                    onChange = { variants = it }
                                )
                            }
                        }
                    }
                }
            }
            MdcDialogActions {
                MdcDialogAction("close") { Text("Cancel") }
                MdcDialogAction("add") { Text("Add") }
            }
        }
    }
}
