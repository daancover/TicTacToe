package com.coverlabs.tictactoe.view

import com.coverlabs.tictactoe.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : BaseActivity() {

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user: FirebaseUser ->
            checkUserRegistered(user.uid)
        } ?: run {
            redirectToActivity(LoginActivity::class.java, true)
        }
    }

    private fun checkUserRegistered(id: String) {
        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("players").child(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val player = dataSnapshot.getValue(Player::class.java)

                    if (player != null) {
                        checkUserMapped(player.id!!, player.name!!)
                    } else {
                        logoff()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorDialog()
                }
            })
        }
    }

    private fun checkUserMapped(id: String, name: String) {
        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("playerMapper").child(name)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val mappedId = dataSnapshot.getValue(String::class.java)

                    if (mappedId != null && mappedId == id) {
                        redirectToActivity(GameModeActivity::class.java, true)
                    } else {
                        logoff()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorDialog()
                }
            })
        }
    }
}
