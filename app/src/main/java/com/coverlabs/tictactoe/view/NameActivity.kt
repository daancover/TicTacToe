package com.coverlabs.tictactoe.view

import android.os.Bundle
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : BaseActivity() {

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
                    redirectToActivity(GameModeActivity::class.java, true)
                } else {
                    showErrorDialog()
                }
            }
        } else {
            showErrorDialog()
        }
    }
}
