package data

import androidx.compose.runtime.compositionLocalOf

data class Lang(
    val id: String,
    val Games: String,
    val Cards: String,
    val game: String,
    val player: String,
    val players: String,
    val AllTypes: String,
    val Rules: String,
    val References: String,
    val Add_game: String,
    val Variants: String,
    val Packer: String,
    val Favorites_only: String,
    val GameCards: String,
    val ReferenceCards: String,
    val Cancel: String,
    val PlayerCount: String,
    val GameType: String,
    val Add: String,
    val Edit: String,

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
        AllTypes = "All types",
        Rules = "Rules",
        References = "References",
        Add_game = "Add game",
        Variants = "Variants",
        Packer = "Packer",
        Favorites_only = "Favorites only",
        GameCards = "Game cards",
        ReferenceCards = "Reference cards",
        Cancel = "Cancel",
        PlayerCount = "Player Count",
        GameType = "Game Type",
        Add = "Add",
        Edit = "Edit",
    ),

    "fr" to Lang(
        id = "fr",
        Games = "Jeux",
        Cards = "Cartes",
        game = "jeu",
        player = "joueur·euse",
        players = "joueur·euse·s",
        AllTypes = "Tous types",
        Rules = "Règles",
        References = "Références",
        Add_game = "Ajouter un jeu",
        Variants = "Variantes",
        Packer = "Packer",
        Favorites_only = "Favoris seulement",
        GameCards = "cartes de Jeu",
        ReferenceCards = "cartes de Référence",
        Cancel = "Annuler",
        PlayerCount = "Nombre de joueur·euse·s",
        GameType = "Type de jeu",
        Add = "Ajouter",
        Edit = "Modifier",

        gameTypes = mapOf(
            "Auction" to "Enchères",
            "Bluffing" to "Bluff",
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
