package com.coverlabs.tictactoe.view

import android.content.Intent
import android.os.Bundle
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private val RC_SIGN_IN: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btSignIn.setOnClickListener { _ ->
            signIn()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                val account = result.signInAccount

                account.let {
                    fireBaseAuthWithGoogle(it!!)
                }
            } else {
                showErrorDialog()
            }
        }
    }

    private fun fireBaseAuthWithGoogle(account: GoogleSignInAccount) {
        if (isOnline()) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    if (firebaseUser != null) {
                        checkUserRegistered(firebaseUser.uid)
                    } else {
                        showErrorDialog()
                    }
                } else {
                    showErrorDialog()
                }
            }
        } else {
            showErrorDialog()
        }
    }

    private fun signIn() {
        if (mGoogleApiClient!!.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
        }

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
                        redirectToActivity(NameActivity::class.java, false)
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
                        redirectToActivity(NameActivity::class.java, false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorDialog()
                }
            })
        }
    }
}
