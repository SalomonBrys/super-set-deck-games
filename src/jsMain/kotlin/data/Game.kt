package data

import kotlinx.serialization.Serializable

typealias ValueToCount = Map<String, Int>
typealias SuitToValues = Map<String, ValueToCount>
typealias PlayerToCards = Map<String, Game.P2C>
typealias GameToPlayers = Map<String, PlayerToCards>

@Serializable
data class Game(
    val id: String,
    val names: Map<String, String>,
    val types: List<String>,
    val playerCount: List<Int>,
    val cards: GameToPlayers,
    val playerReferences: List<ReferenceCard>,
    val gameReferences: List<ReferenceCard>
) {
    @Serializable
    data class ReferenceCard(val recto: String, val verso: String? = null)

    @Serializable
    data class P2C(val players: List<Int>, val cards: SuitToValues)
}
