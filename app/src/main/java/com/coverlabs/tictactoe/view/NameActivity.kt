package com.coverlabs.tictactoe.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user: FirebaseUser ->
            btSubmit.setOnClickListener {
                if (!etName.text.toString().isEmpty()) {
                    makePlayer(user.uid)
                }
            }
        }
    }

    private fun makePlayer(id: String) {
        val player = Player(id, etName.text.toString(), 0, 0, 0)

        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("players")
            ref.child(id).setValue(player).addOnCompleteListener {
                if (it.isSuccessful) {
                    makePlayerMapper(player.id!!, player.name!!)
                } else {
                    showErrorDialog()
                }
            }
        } else {
            showErrorDialog()
        }
    }

    private fun makePlayerMapper(id: String, name: String) {
        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("playerMapper")
            ref.child(name).setValue(id).addOnCompleteListener {
                if (it.isSuccessful) {
                    redirectToMain()
                } else {
                    showErrorDialog()
                }
            }
        } else {
            showErrorDialog()
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_attention))
                .setMessage(getString(R.string.label_error))
                .setPositiveButton(getString(R.string.action_ok), null)

        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
