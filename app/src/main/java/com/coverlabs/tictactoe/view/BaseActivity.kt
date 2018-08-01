package com.coverlabs.tictactoe.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.coverlabs.tictactoe.R

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    protected fun redirectToActivity(nextClass: Class<*>, finish: Boolean) {
        val intent = Intent(this, nextClass)
        startActivity(intent)

        if (finish) {
            finish()
        }
    }

    protected fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_attention))
                .setMessage(getString(R.string.label_error))
                .setPositiveButton(getString(R.string.action_ok), null)

        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    protected fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}