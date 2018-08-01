package com.coverlabs.tictactoe.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.coverlabs.tictactoe.R
import kotlinx.android.synthetic.main.activity_game_mode.*

class GameModeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        setSupportActionBar(toolbar)

        btSinglePlayer.setOnClickListener {
            redirectToActivity(MainActivity::class.java, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game_mode, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logoff -> {
                logoff()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
