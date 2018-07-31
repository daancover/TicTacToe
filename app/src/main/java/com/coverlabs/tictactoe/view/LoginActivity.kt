package com.coverlabs.tictactoe.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.coverlabs.tictactoe.R
import com.coverlabs.tictactoe.model.Player
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 101

    private var mAuth: FirebaseAuth? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, null).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

        btSignIn.setOnClickListener { _ ->
            signIn()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser

        currentUser?.let {
            redirectToActivity()
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
                        makePlayer(firebaseUser.uid)
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

    private fun redirectToActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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

    private fun makePlayer(id: String) {
        val player = Player(id, "Daniel", 0, 0, 0)

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
                    redirectToActivity()
                } else {
                    showErrorDialog()
                }
            }
        } else {
            showErrorDialog()
        }
    }
}
