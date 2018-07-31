package com.coverlabs.tictactoe.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent



class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start home activity
        startActivity(Intent(this, LoginActivity::class.java))

        // close splash activity
        finish()
    }
}
