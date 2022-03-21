import androidx.compose.runtime.*
import data.Game
import data.LocalLang
import data.name
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import material.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import utils.*


@Composable
private fun <T : Comparable<T>> checkList(
    title: String,
    id: String,
    all: Set<T>,
    set: Set<T>,
    onChange: (Set<T>) -> Unit
) {
    MdcFormField({
        style {
            marginTop(2.em)
        }
    }) {
        MdcCheckbox(
            id = "all-$id",
            checked = when {
                set.size == all.size -> true
                set.isEmpty() -> false
                else -> null
            },
            onChange = { checked ->
                onChange(if (checked) all else emptySet())
            }
        )
        Label(forId = "all-$id") {
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
        all.toList().sorted().forEach { value ->
            MdcFormField {
                MdcCheckbox(
                    id = "$id-$value",
                    checked = value in set,
                    onChange = {
                        onChange(if (it) set + value else set - value)
                    }
                )
                Label(forId = "$id-$value", {
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

private data class Pack(
    val game: Game,
    val players: Set<Int>,
    val variants: Set<String>
)

private fun Game.toPack() = Pack(
    game = this,
    players = cards.flatMap { (_, g) -> g.flatMap { (_, p) -> p.players } }.toSet(),
    variants = (cards.keys - "Base").toSet()
)

private fun Pack.toSuits(): Map<String, MutableMap<String, Int>> {
    val suits = HashMap<String, MutableMap<String, Int>>()
    (variants + "Base").forEach { variant ->
        game.cards[variant]!!.values
            .filter { p2c -> players.any { it in p2c.players } }
            .forEach { p2c ->
                p2c.cards.forEach { (suit, cards) ->
                    val packCards = suits.getOrPut(suit) { HashMap() }
                    cards.forEach { (card, count) ->
                        packCards[card] = (packCards[card] ?: 0) + count
                    }
                }
            }
    }
    return suits
}

@Composable
private fun PackerDialog(gamesList: List<Game>, trigger: Flow<Pack?>, addPack: (Pack) -> Unit) {
    var pack: Pack? by remember { mutableStateOf(null) }
    var count by remember { mutableStateOf(0) }
    var edit: Game? by remember { mutableStateOf(null) }

    LaunchedEffect(trigger) {
        trigger.collect {
            pack = it
            edit = it?.game
            ++count
        }
    }

    MdcDialog(
        trigger = trigger.map { true },
        fullScreen = true,
        onAction = {
            if (it == "add" && pack != null && pack!!.players.isNotEmpty()) {
                addPack(pack!!)
            }
        },
        surfaceAttrs = {
            style {
                maxWidth(40.cssRem)
            }
        }
    ) {
        MdcDialogTitle { Text(if (edit == null) LocalLang.current.Add_game else edit!!.name) }
        MdcDialogContent {
            FlexColumn(JustifyContent.Center, AlignItems.Center, {
                style {
                    padding(1.em)
                }
            }) {
                if (edit == null) {
                    var favs: Set<String>? by remember { mutableStateOf(null) }

                    MdcFormField {
                        MdcCheckbox(
                            id = "favorites",
                            checked = favs != null,
                            onChange = { checked ->
                                favs = if (checked) Cookies["favs"]?.split(",")?.map { decodeURIComponent(it) }?.toSet() else null
                                pack = null
                                ++count
                            }
                        )
                        Label(forId = "favorites") {
                            Text("Favorites only")
                        }
                    }

                    key(count) {
                        val games by rememberUpdatedState(gamesList)
                        MdcSelect(
                            selected = pack?.game?.id ?: "",
                            onSelected = { gameId ->
                                println(gameId)
                                pack = games.first { it.id == gameId }.toPack()
                            },
                            label = LocalLang.current.Games,
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
                            games
                                .filter { favs?.contains(it.id) ?: true }
                                .forEach {
                                    MdcSelectOption(it.id) {
                                        Text(it.name)
                                    }
                                }
                        }
                    }
                }
                val p = pack
                if (p != null) {
                    key(p.game.id) {
                        checkList(
                            title = LocalLang.current.players.replaceFirstChar { it.uppercase() },
                            id = "count",
                            all = p.game.cards.flatMap { (_, g) -> g.flatMap { (_, p) -> p.players } }.toSet(),
                            set = p.players,
                            onChange = {
                                pack = p.copy(players = it)
                            }
                        )
                        if (p.game.cards.size > 1) {
                            checkList(
                                title = LocalLang.current.Variants,
                                id = "variants",
                                all = (p.game.cards.keys - "Base").toSet(),
                                set = p.variants,
                                onChange = { pack = p.copy(variants = it) }
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

@Composable
private fun PackerGamesList(packs: List<Pack>, trigger: FlowCollector<Pack>, onDelete: (Pack) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    FlexColumn(JustifyContent.Center, AlignItems.Center, {
        style {
            width(100.percent)
            maxWidth(25.cssRem)
        }
    }) {
        packs.forEach { pack ->
            MdcCard(attrs = {
                style {
                    width(100.percent)
                    margin(0.5.em)
                    padding(0.25.em, 0.25.em, 0.25.em, 0.75.em)
                }
            }) {
                FlexRow(JustifyContent.Center, AlignItems.Center) {
                    Span({
                        style { flex(1) }
                    }) {
                        Span({ classes("mdc-deprecated-list-item__primary-text") }) {
                            Text(pack.game.name)
                        }
                        Span({ classes("mdc-deprecated-list-item__secondary-text") }) {
                            Text("${pack.players.sorted().toShortStrings().joinToString()} ${LocalLang.current.players}")
                            if (pack.variants.isNotEmpty()) {
                                Text(" + ${pack.variants.joinToString()}")
                            }
                        }

                    }
                    MdcIconButton("edit", "Edit") {
                        coroutineScope.launch {
                            trigger.emit(pack)
                        }
                    }
                    MdcIconButton("delete", "Delete") { onDelete(pack) }
                }
            }
        }
    }
}

@Composable
private fun PackerCards(packs: List<Pack>) {
    MdcCard(attrs = {
        style {
            width(100.percent)
            maxWidth(50.cssRem)
            marginTop(1.em)
        }
    }) {
        val allSuits = HashMap<String, MutableMap<String, Int>>()
        packs.forEach { pack ->
            pack.toSuits().forEach { (suit, cards) ->
                val allCards = allSuits.getOrPut(suit) { HashMap() }
                cards.forEach { (card, count) ->
                    allCards[card] = maxOf(allCards[card] ?: 0, count)
                }
            }
        }

        allSuits
            .entries
            .sortedBy { (suit, _) ->
                when (suit) {
                    "spades" -> "!0"
                    "hearts" -> "!1"
                    "clubs" -> "!2"
                    "diamonds" -> "!3"
                    "florettes" -> "!4"
                    "wheels" -> "!5"
                    "stars" -> "!6"
                    else -> suit
                }
            }
            .forEach { (suit, cards) ->
                val numberCards = ArrayList<Pair<Int, Int>>()
                val headCards = ArrayList<Pair<String, Int>>()
                cards.forEach { (card, count) ->
                    val number = card.toIntOrNull()
                    if (number != null) numberCards += number to count
                    else headCards += (LocalLang.current.cardNames[card] ?: card) to count
                }

                val cardList = (
                        numberCards.groupBy { (_, count) -> count }
                            .map { (count, list) -> count to list.map { (value, _) -> value } .sorted().toShortStrings() }
                            .sortedBy { (count, _) -> count }
                            .flatMap { (count, list) -> list.map { it to count } }
                        ) + (
                        headCards.sortedBy { (value, _) ->
                            when (value) {
                                "J" -> "!0"
                                "S" -> "!1"
                                "Q" -> "!2"
                                "K" -> "!3"
                                else -> value
                            }
                        }
                        )

                P {
                    Span({
                        style {
                            fontWeight("bold")
                            display(DisplayStyle.LegacyInlineFlex)
                            flexDirection(FlexDirection.Row)
                            alignItems(AlignItems.Center)
                            justifyContent(JustifyContent.End)
                            width(8.em)
                            textAlign("right")
                        }
                    }) {
                        Text((LocalLang.current.cardNames[suit] ?: suit).replaceFirstChar { it.uppercase() })
                        Span({
                            style {
                                display(DisplayStyle.InlineBlock)
                                paddingLeft(0.1.em)
                                fontSize(2.5.em)
                            }
                        }) {
                            when (suit) {
                                "spades" -> Text(" ♠")
                                "hearts" -> Text(" ♥")
                                "clubs" -> Text(" ♣")
                                "diamonds" -> Text(" ♦")
                                "florettes" -> Text(" ✿")
                                "wheels" -> Text(" ⎈")
                                "stars" -> Text(" ★")
                                "specials" -> Text(" ‽")
                            }
                        }
                    }
                    Text(": ")
                    Span {
                        var coma = false
                        cardList.forEach { (card, count) ->
                            if (coma) Text(", ")
                            coma = true
                            if (count > 1) B { Text("${count}×") }
                            Span({
                                style {
                                    border(1.px, LineStyle.Solid, Color.black)
                                    borderRadius(3.px)
                                    padding(0.4.em, 0.2.em)
                                }
                            }) {
                                Text(card)
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun Packer(games: List<Game>) {

    var packs: List<Pack> by remember { mutableStateOf(emptyList()) }
//    var packs: List<Pack> by remember { mutableStateOf(listOf(
//        Pack(games.first { it.id == "Oh_7" }, setOf(3, 4), setOf("Advanced")),
//        Pack(games.first { it.id == "Fox_in_Forest" }, setOf(2), emptySet()),
//        Pack(games.first { it.id == "Yokai_Septet" }, setOf(3, 4), setOf("Seven-Suiters")),
//    )) }

    val trigger = remember { MutableSharedFlow<Pack?>() }
    val coroutineScope = rememberCoroutineScope()

    FlexColumn(JustifyContent.Center, AlignItems.Center, {
        style {
            width(100.percent)
        }
    }) {

        PackerGamesList(packs, trigger) { packs = packs - it }

        MdcButton(
            variant = MdcButtonVariant.Elevated,
            onClick = {
                coroutineScope.launch {
                    trigger.emit(null)
                }
            },
            attrs = {
                style {
                    backgroundColor(Color("var(--mdc-theme-secondary)"))
                }
            }
        ) {
            Text(LocalLang.current.Add_game)
        }

        PackerDialog(games - packs.map { it.game }.toSet(), trigger.asSharedFlow()) { pack ->
            val index = packs.indexOfFirst { it.game.id == pack.game.id }

            if (index == -1) {
                packs = packs + pack
            } else {
                packs = buildList {
                    addAll(packs)
                    set(index, pack)
                }
            }
        }

        if (packs.isNotEmpty()) {
            PackerCards(packs)
        }
    }
}
