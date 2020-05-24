package com.wwjz.watsplan


class Model {
    var storedCards: MutableList<Card> = mutableListOf<Card>()
    var cards: MutableList<Card> = mutableListOf<Card>()

    companion object {
        val mInstance = Model()
    }

}