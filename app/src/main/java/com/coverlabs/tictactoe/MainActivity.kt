package com.coverlabs.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mBoard = Array(9, { -1 })
    private var computerMove: Point? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (index in 0..mBoard.size - 1) {
            mBoard[index] = -1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_restart -> {
                restartGame()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onButtonClick(view: View) {
        if (!isGameOver()) {
            when (view.id) {
                R.id.bt1 -> makePlay(bt1, 1)
                R.id.bt2 -> makePlay(bt2, 2)
                R.id.bt3 -> makePlay(bt3, 3)
                R.id.bt4 -> makePlay(bt4, 4)
                R.id.bt5 -> makePlay(bt5, 5)
                R.id.bt6 -> makePlay(bt6, 6)
                R.id.bt7 -> makePlay(bt7, 7)
                R.id.bt8 -> makePlay(bt8, 8)
                R.id.bt9 -> makePlay(bt9, 9)
            }
        }
    }

    fun makePlay(button: Button, buttonIndex: Int) {
        placeAMove(buttonIndex - 1, 0, button)

        if (!isGameOver()) {
            minimax(0, 1)

            val index: Int? = Point.getIndexByPoint(computerMove!!)
            val computerButton = getComputerButton(index)
            placeAMove(index, 1, computerButton)
        }
    }


    fun restartGame() {
        restoreButton(bt1)
        restoreButton(bt2)
        restoreButton(bt3)
        restoreButton(bt4)
        restoreButton(bt5)
        restoreButton(bt6)
        restoreButton(bt7)
        restoreButton(bt8)
        restoreButton(bt9)

        for (index in 0..mBoard.size - 1) {
            mBoard[index] = -1
        }
    }

    fun restoreButton(button: Button) {
        button.isEnabled = true
        button.text = ""
    }

    fun getComputerButton(index: Int?): Button {
        return when (index) {
            0 -> bt1
            1 -> bt2
            2 -> bt3
            3 -> bt4
            4 -> bt5
            5 -> bt6
            6 -> bt7
            7 -> bt8
            8 -> bt9
            else -> null!!
        }
    }

    fun isGameOver(): Boolean {
        return hasPlayerWon(0) || hasPlayerWon(1) || noAvailableCells()
    }

    fun hasPlayerWon(player: Int): Boolean {
        var success = false

        if (mBoard[0] == player && mBoard[1] == player && mBoard[2] == player) { // Row 1
            success = true
        } else if (mBoard[3] == player && mBoard[4] == player && mBoard[5] == player) { // Row 2
            success = true
        } else if (mBoard[6] == player && mBoard[7] == player && mBoard[8] == player) { // Row 3
            success = true
        } else if (mBoard[0] == player && mBoard[3] == player && mBoard[6] == player) { // Column 1
            success = true
        } else if (mBoard[1] == player && mBoard[4] == player && mBoard[7] == player) { // Column 2
            success = true
        } else if (mBoard[2] == player && mBoard[5] == player && mBoard[8] == player) { // Column 3
            success = true
        } else if (mBoard[0] == player && mBoard[4] == player && mBoard[8] == player) { // Diagonal 1
            success = true
        } else if (mBoard[2] == player && mBoard[4] == player && mBoard[6] == player) { // Diagonal 2
            success = true
        }

        return success
    }

    fun noAvailableCells(): Boolean {
        for (index in 0..mBoard.size - 1) {
            if (mBoard[index] == -1) {
                return false
            }
        }

        return true
    }

    fun getAvailableCells(): List<Point> {
        var points: MutableList<Point> = arrayListOf()

        if (mBoard[0] == -1) {
            points.add(Point(0, 0))
        }

        if (mBoard[1] == -1) {
            points.add(Point(0, 1))
        }

        if (mBoard[2] == -1) {
            points.add(Point(0, 2))
        }

        if (mBoard[3] == -1) {
            points.add(Point(1, 0))
        }

        if (mBoard[4] == -1) {
            points.add(Point(1, 1))
        }

        if (mBoard[5] == -1) {
            points.add(Point(1, 2))
        }

        if (mBoard[6] == -1) {
            points.add(Point(2, 0))
        }

        if (mBoard[7] == -1) {
            points.add(Point(2, 1))
        }

        if (mBoard[8] == -1) {
            points.add(Point(2, 2))
        }

        return points
    }

    fun placeAMove(index: Int?, player: Int, button: Button?): Boolean {
        if (mBoard[index!!] != -1) {
            return false
        }

        mBoard[index!!] = player

        if (button != null) {
            button.isEnabled = false

            if (player == 0) {
                button.setTextColor(Color.RED)
                button.text = "X"
            } else {
                button.setTextColor(Color.BLUE)
                button.text = "O"
            }
        }

        return true
    }

    fun minimax(depth: Int, turn: Int): Int {
        if (hasPlayerWon(0)) {
            return -1
        } else if (hasPlayerWon(1)) {
            return 1
        } else if (noAvailableCells()) {
            return 0
        }

        var availableCells: List<Point> = getAvailableCells()

        var min: Int = Integer.MAX_VALUE
        var max: Int = Int.MIN_VALUE

        for (i in 0..availableCells.size - 1) {
            val point = availableCells.get(i)

            if (turn == 1) {
                val index = Point.getIndexByPoint(point)
                placeAMove(index, 1, null)
                val currentScore: Int = minimax(depth, 0)
                max = Math.max(currentScore, max)

                if (depth == 0) {

                }

                if (currentScore >= 0) {
                    if (depth == 0) {
                        computerMove = point
                    }
                }

                if (currentScore == 1) {
                    val index = Point.getIndexByPoint(point)
                    mBoard[index!!] = -1
                    break
                }

                if (i == availableCells.size - 1 && max < 0) {
                    if (depth == 0) {
                        computerMove = point
                    }
                }
            } else if (turn == 0) {
                val index = Point.getIndexByPoint(point)
                placeAMove(index, 0, null)
                val currentScore = minimax(depth + 1, 1)
                min = Math.min(currentScore, min)

                if (min == -1) {
                    val index = Point.getIndexByPoint(point)
                    mBoard[index!!] = -1
                    break
                }
            }

            var index = Point.getIndexByPoint(point)
            mBoard[index!!] = -1
        }

        return if (turn == 1) max else min
    }
}

