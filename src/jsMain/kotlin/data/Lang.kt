package data

import androidx.compose.runtime.compositionLocalOf

data class Lang(
    val id: String,
    val games: String,
    val player: String,
    val players: String,
    val allTypes: String,
    val rules: String,
    val references: String,

    val gameTypes: Map<String, String> = emptyMap()
)

val langs = mapOf(

    "en" to Lang(
        id = "en",
        games = "Games",
        player = "player",
        players = "players",
        allTypes = "all types",
        rules = "Rules",
        references = "References",
    ),

    "fr" to Lang(
        id = "fr",
        games = "Jeux",
        player = "joueur·euse",
        players = "joueur·euse·s",
        allTypes = "tous types",
        rules = "Règles",
        references = "Références",

        gameTypes = mapOf(
            "Cooperative" to "Coopératif",
            "Hand Management" to "Gestion de Main",
            "Ladder Climbing" to "Montée en Puissance",
            "Player Elimination" to "Élimination de joueur·euse·s",
            "Predictive Bid" to "Pari Prédictif",
            "Push Your Luck" to "Pousse ta Chance",
            "Score and Reset" to "Score et Recommence",
            "Set Collection" to "Collection d'ensembles",
            "Team Based" to "En Équipe",
            "Trick Taking" to "Prise de pli"
        )
    )

)

val LocalLang = compositionLocalOf { langs["en"]!! }
