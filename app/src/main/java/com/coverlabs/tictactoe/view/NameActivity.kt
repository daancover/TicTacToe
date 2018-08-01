package com.coverlabs.tictactoe.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        val currentUser = FirebaseAuth.getInstance().currentUser

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tlName.error = null
                tlName.isErrorEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        currentUser?.let { user: FirebaseUser ->
            btSubmit.setOnClickListener {
                if (!etName.text.toString().isEmpty()) {
                    checkUserMapped(user.uid, etName.text.toString())
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

    private fun checkUserMapped(id: String, name: String) {
        if (isOnline()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("playerMapper").child(name)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val mappedId = dataSnapshot.getValue(String::class.java)

                    if (mappedId == null || mappedId == id) {
                        makePlayer(id)
                    } else {
                        tlName.error = "This nickname is already being used."
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorDialog()
                }
            })
        }
    }
}
