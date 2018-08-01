package com.coverlabs.tictactoe.view

import android.os.Bundle

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirectToActivity(LoginActivity::class.java, true)
    }
}
