package com.coverlabs.tictactoe

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.FragmentActivity




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
                // Fail show message
            }
        }
    }

    private fun fireBaseAuthWithGoogle(account: GoogleSignInAccount) {
        //if (FirebaseUtils.isOnline(this) && FirebaseAuth.getInstance() != null) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                if (firebaseUser != null) {
                    redirectToActivity()
                } else {

                }
            } else {

            }
        }
        /*} else {
            DialogUtils.hideProgressDialog()
            DialogUtils.showSimpleDialog(this, getString(R.string.error_no_internet_connection))
        }*/
    }

    private fun signIn() {
        if (mGoogleApiClient!!.isConnected) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
        }

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /*private fun signIn() {
        val signInIntent = mGoogleApiClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }*/

    private fun redirectToActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
