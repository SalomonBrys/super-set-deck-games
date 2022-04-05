package data

import androidx.compose.runtime.compositionLocalOf

data class Lang(
    val id: String,
    val Games: String,
    val Cards: String,
    val game: String,
    val player: String,
    val players: String,
    val allTypes: String,
    val Rules: String,
    val References: String,
    val Add_game: String,
    val Variants: String,
    val Packer: String,
    val Favorites_only: String,
    val GameCards: String,
    val ReferenceCards: String,

    val gameTypes: Map<String, String> = emptyMap(),
    val cardNames: Map<String, String> = emptyMap()

)

val langs = mapOf(

    "en" to Lang(
        id = "en",
        Games = "Games",
        Cards = "Cards",
        game = "game",
        player = "player",
        players = "players",
        allTypes = "all types",
        Rules = "Rules",
        References = "References",
        Add_game = "Add game",
        Variants = "Variants",
        Packer = "Packer",
        Favorites_only = "Favorites only",
        GameCards = "Game cards",
        ReferenceCards = "Reference cards",
    ),

    "fr" to Lang(
        id = "fr",
        Games = "Jeux",
        Cards = "Cartes",
        game = "jeu",
        player = "joueur·euse",
        players = "joueur·euse·s",
        allTypes = "tous types",
        Rules = "Règles",
        References = "Références",
        Add_game = "Ajouter un jeu",
        Variants = "Variantes",
        Packer = "Packer",
        Favorites_only = "Favoris seulement",
        GameCards = "cartes de Jeu",
        ReferenceCards = "cartes de Référence",

        gameTypes = mapOf(
            "Auction" to "Enchères",
            "Cooperative" to "Coopératif",
            "Drafting" to "Draft",
            "Hand Management" to "Gestion de Main",
            "Ladder Climbing" to "Montée en Puissance",
            "Player Elimination" to "Élimination de joueur·euse·s",
            "Predictive Bid" to "Pari Prédictif",
            "Push Your Luck" to "Pousse ta Chance",
            "Score and Reset" to "Score et Recommence",
            "Set Collection" to "Collection d'ensembles",
            "Single Loser" to "Perdant Unique",
            "Team Based" to "En Équipe",
            "Trick Taking" to "Prise de pli"
        ),

        cardNames = mapOf(
            "spades" to "piques",
            "hearts" to "coeurs",
            "clubs" to "trèfles",
            "diamonds" to "carreaux",
            "florettes" to "fleurettes",
            "wheels" to "roues",
            "stars" to "étoiles",
            "specials" to "spéciales",

            "Butterfly" to "Papillon",
            "Wolf" to "Loup",
            "Owl" to "Chouette",
            "Turtle" to "Tortue",
            "Toad" to "Crapaud",
            "Monkey" to "Singe"
        )
    )

)

val LocalLang = compositionLocalOf { langs["en"]!! }
