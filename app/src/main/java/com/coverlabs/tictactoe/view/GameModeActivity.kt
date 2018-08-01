package com.coverlabs.tictactoe.view

import android.os.Bundle
import com.coverlabs.tictactoe.R
import kotlinx.android.synthetic.main.activity_game_mode.*

class GameModeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        btSinglePlayer.setOnClickListener {
            redirectToActivity(MainActivity::class.java, false)
        }
    }
}
