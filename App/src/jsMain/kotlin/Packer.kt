import androidx.compose.runtime.*
import data.Game
import data.LocalLang
import data.name
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.button.MDCButtonType
import dev.petuska.kmdc.card.MDCCard
import dev.petuska.kmdc.checkbox.MDCCheckbox
import dev.petuska.kmdc.dialog.*
import dev.petuska.kmdc.form.field.MDCFormField
import dev.petuska.kmdc.list.Divider
import dev.petuska.kmdc.select.MDCSelect
import dev.petuska.kmdc.select.anchor.Anchor
import dev.petuska.kmdc.select.menu.Menu
import dev.petuska.kmdc.select.menu.SelectItem
import dev.petuska.kmdc.select.onChange
import dev.petuska.kmdcx.icons.MDCIcon
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import utils.*

@Composable
private fun <T : Comparable<T>> CheckList(
    title: String,
    all: Set<T>,
    set: Set<T>,
    onChange: (Set<T>) -> Unit
) {
    MDCFormField(attrs = {
        style {
            marginTop(2.em)
            fontSize(1.2.em)
            fontWeight("bold")
        }
    }) {
        MDCCheckbox(
            checked = when {
                set.size == all.size -> true
                set.isEmpty() -> false
                else -> null
            },
            touch = true,
            label = title,
            attrs = {
                onChange {
                    onChange(if (it.value) all else emptySet())
                }
            }
        )
    }
    FlexRow(JustifyContent.Center, AlignItems.Center, {
        style {
            flexWrap(FlexWrap.Wrap)
        }
    }) {
        all.toList().sorted().forEach { value ->
            MDCFormField(attrs = {
                style { marginRight(2.em) }
            }) {
                MDCCheckbox(
                    checked = value in set,
                    touch = true,
                    label = value.toString(),
                    attrs = {
                        onChange {
                            onChange(if (it.value) set + value else set - value)
                        }
                    }
                )
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
private fun PackerDialog(gamesList: List<Game>, trigger: SharedFlow<Pack?>, addPack: (Pack) -> Unit) {
    var pack: Pack? by remember { mutableStateOf(null) }
    var count by remember { mutableStateOf(0) }
    var edit: Game? by remember { mutableStateOf(null) }
    var open by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        trigger.collect {
            pack = it
            edit = it?.game
            open = true
            ++count
        }
    }

    val lang = LocalLang.current

    MDCDialog(
        open = open,
        fullscreen = true,
        stacked = false,
        attrs = {
            onOpened { open = true }
            onClosed { open = false }
            onClosing {
                if (it.detail.action == "ok") {
                    pack?.let(addPack)
                    ++count
                }
            }
        }
    ) {
        Title(
            title = if (edit == null) lang.Add_game else edit!!.name,
            attrs = {
                style { marginLeft(1.em) }
            }
        )
        Content {
            FlexColumn(JustifyContent.Center, AlignItems.Center, {
                style {
                    padding(1.em)
                }
            }) {
                if (edit == null) {
                    var favs: Set<String>? by remember { mutableStateOf(null) }
                    MDCFormField {
                        MDCCheckbox(
                            checked = favs != null,
                            touch = true,
                            label = lang.Favorites_only,
                            attrs = {
                                onChange {
                                    favs = if (it.value) Cookies["favs"]?.split(",")?.map { decodeURIComponent(it) }?.toSet() else null
                                    pack = null
                                    ++count
                                }
                            }
                        )
                    }

                    key(count) {
                        val games by rememberUpdatedState(gamesList.sortedBy { it.name(lang) })
                        MDCSelect(
                            attrs = {
                                onChange {
                                    val gameId = it.detail.value
                                    pack = games.first { it.id == gameId }.toPack()
                                }
                                style { width(18.cssRem) }
                            }
                        ) {
                            Anchor(lang.Games)
                            Menu(
                                fixed = true,
                                attrs = {
                                    style { width(18.cssRem) }
                                }
                            ) {
                                SelectItem("", selected = true)
                                Divider()
                                games
                                    .filter { favs?.contains(it.id) ?: true }
                                    .forEach {
                                        SelectItem(text = it.name, value = it.id)
                                    }
                            }
                        }
                    }
                }

                val p = pack
                if (p != null) {
                    key(p.game.id) {
                        CheckList(
                            title = LocalLang.current.players.replaceFirstChar { it.uppercase() },
                            all = p.game.cards.flatMap { (_, g) -> g.flatMap { (_, p) -> p.players } }.toSet(),
                            set = p.players,
                            onChange = {
                                pack = p.copy(players = it)
                            }
                        )

                        if (p.game.cards.size > 1) {
                            CheckList(
                                title = LocalLang.current.Variants,
                                all = (p.game.cards.keys - "Base").toSet(),
                                set = p.variants,
                                onChange = { pack = p.copy(variants = it) }
                            )
                        }

                    }
                }
            }
        }

        Actions {
            Action("close", lang.Cancel)
            Action("ok", if (edit == null) lang.Add else lang.Edit, true)
        }
    }
}

@Composable
private fun PackerGamesList(packs: List<Pack>, trigger: FlowCollector<Pack?>, onDelete: (Pack) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    FlexColumn(JustifyContent.Center, AlignItems.Center, {
        style {
            width(100.percent)
            maxWidth(25.cssRem)
        }
    }) {
        packs.forEach { pack ->
            MDCCard(attrs = {
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
                    MDCIconButton(
                        icon = MDCIcon.Edit,
                        attrs = {
                            onClick {
                                coroutineScope.launch {
                                    trigger.emit(pack)
                                }
                            }
                        }
                    )
                    MDCIconButton(
                        icon = MDCIcon.Delete,
                        attrs = {
                            onClick {
                                onDelete(pack)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardSpan(content: @Composable () -> Unit) {
    Span({
        style {
            border(1.px, LineStyle.Solid, Color.black)
            borderRadius(3.px)
            padding(0.4.em, 0.2.em)
        }
    }) {
        content()
    }
}

@Composable
private fun PackerGameCards(packs: List<Pack>) {
    MDCCard(attrs = {
        style {
            width(100.percent)
            maxWidth(50.cssRem)
            marginTop(1.em)
            marginBottom(2.cssRem)
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

        H3({
            style {
                textAlign("center")
            }
        }) {
            val allCount = allSuits.values.sumOf { it.values.sum() }
            Text("$allCount ${LocalLang.current.GameCards}")
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

                P({
                    style { margin(.6.em, 0.em) }
                }) {
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
                            CardSpan { Text(card) }
                        }
                    }
                }
            }
    }
}

@Composable
private fun PackerRefCards(packs: List<Pack>) {
    val allRefs = HashMap<String, Pair<Int, Int>>()
    packs.forEach {
        val pair = it.game.gameReferences.size to it.game.playerReferences.refs.size * minOf(it.players.maxOf { it }, it.game.playerReferences.max)
        if (pair.first > 0 || pair.second > 0) {
            allRefs[it.game.name] = pair
        }
    }

    if (allRefs.isNotEmpty()) {
        MDCCard(attrs = {
            style {
                width(100.percent)
                maxWidth(50.cssRem)
                marginTop(1.em)
                marginBottom(2.cssRem)
            }
        }) {
            H3({
                style {
                    textAlign("center")
                }
            }) {
                val allCount = allRefs.values.sumOf { it.first + it.second }
                Text("$allCount ${LocalLang.current.ReferenceCards}")
            }

            allRefs.entries
                .sortedBy { (name, _) -> name }
                .forEach { (name, refs) ->
                    val (gameRefs, playerRefs) = refs
                    P({
                        style {
                            margin(.6.em, 1.2.em)
                        }
                    }) {
                        B { Text("$name: ") }
                        if (gameRefs > 0) {
                            B { Text("$gameRefs×") }
                            CardSpan { Text(LocalLang.current.game) }
                        }
                        if (gameRefs > 0 && playerRefs > 0) {
                            Text(", ")
                        }
                        if (playerRefs > 0) {
                            B { Text("$playerRefs×") }
                            CardSpan { Text(LocalLang.current.player) }
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

        MDCButton(
            text = LocalLang.current.Add_game,
            type = MDCButtonType.Raised,
            touch = true,
            attrs = {
                style {
                    backgroundColor(Color("var(--mdc-theme-secondary)"))
                }
                onClick {
                    coroutineScope.launch {
                        trigger.emit(null)
                    }
                }
            }
        )

        PackerDialog(games - packs.map { it.game }.toSet(), trigger.asSharedFlow()) { pack ->
            val index = packs.indexOfFirst { it.game.id == pack.game.id }

            packs = if (index == -1) {
                packs + pack
            } else {
                buildList {
                    addAll(packs)
                    set(index, pack)
                }
            }
        }

        if (packs.isNotEmpty()) {
            PackerGameCards(packs)
            PackerRefCards(packs)
        }
    }
}
