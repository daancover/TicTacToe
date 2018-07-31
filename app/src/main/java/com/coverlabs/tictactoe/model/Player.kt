package com.coverlabs.tictactoe.model

class Player(
        var id: String? = "",
        var name: String? = "",
        var singlePlayerGames: Int? = 0,
        var multiplayerGames: Int? = 0,
        var gamesWon: Int? = 0) {

    constructor() : this("", "", 0, 0, 0)
}