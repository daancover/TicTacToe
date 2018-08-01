package com.coverlabs.tictactoe.view

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.coverlabs.tictactoe.model.Point
import com.coverlabs.tictactoe.util.DialogUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private val mBoard = Array(9) { -1 }
    private var computerMove: Point? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, null).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

        for (index in 0 until mBoard.size) {
            mBoard[index] = -1
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setSupportActionBar(toolbar)

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user: FirebaseUser ->
            getUserInfo(user.uid)
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
            R.id.menu_logoff -> {
                logoff()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logoff() {
        FirebaseAuth.getInstance().signOut()

        if (mGoogleApiClient!!.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

    private fun makePlay(button: Button, buttonIndex: Int) {
        placeAMove(buttonIndex - 1, 0, button)

        if (!isGameOver()) {
            minimax(0, 1)

            val index: Int? = Point.getIndexByPoint(computerMove!!)
            val computerButton = getComputerButton(index)
            placeAMove(index, 1, computerButton)
        }

        if (isGameOver()) {
            val positiveClick = DialogInterface.OnClickListener { _, _ ->
                restartGame()
            }

            if (hasPlayerWon(0)) {
                DialogUtils.showDialog(this, getString(R.string.title_congrats), getString(R.string.label_you_win), positiveClick)
            } else if (hasPlayerWon(1)) {
                DialogUtils.showDialog(this, getString(R.string.title_oh_no), getString(R.string.label_you_lose), positiveClick)
            } else {
                DialogUtils.showDialog(this, getString(R.string.title_oh_no), getString(R.string.label_draw), positiveClick)
            }
        }
    }

    private fun restartGame() {
        restoreButton(bt1)
        restoreButton(bt2)
        restoreButton(bt3)
        restoreButton(bt4)
        restoreButton(bt5)
        restoreButton(bt6)
        restoreButton(bt7)
        restoreButton(bt8)
        restoreButton(bt9)

        for (index in 0 until mBoard.size) {
            mBoard[index] = -1
        }
    }

    private fun restoreButton(button: Button) {
        button.isEnabled = true
        button.text = ""
    }

    private fun getComputerButton(index: Int?): Button {
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

    private fun isGameOver(): Boolean {
        return hasPlayerWon(0) || hasPlayerWon(1) || noAvailableCells()
    }

    private fun hasPlayerWon(player: Int): Boolean {
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

    private fun noAvailableCells(): Boolean {
        for (index in 0 until mBoard.size) {
            if (mBoard[index] == -1) {
                return false
            }
        }

        return true
    }

    private fun getAvailableCells(): List<Point> {
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

    private fun placeAMove(index: Int?, player: Int, button: Button?): Boolean {
        if (mBoard[index!!] != -1) {
            return false
        }

        mBoard[index] = player

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

    private fun minimax(depth: Int, turn: Int): Int {
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

        for (i in 0 until availableCells.size) {
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
                    mBoard[index] = -1
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
                    mBoard[index] = -1
                    break
                }
            }

            var index = Point.getIndexByPoint(point)
            mBoard[index] = -1
        }

        return if (turn == 1) max else min
    }

    private fun getUserInfo(id: String) {
        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("players").child(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val player = dataSnapshot.getValue(Player::class.java)

                    if (player != null) {
                        mPlayer = player
                        supportActionBar?.title = mPlayer?.name
                    } else {
                        showErrorDialog()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorDialog()
                }
            })
        }
    }
}

