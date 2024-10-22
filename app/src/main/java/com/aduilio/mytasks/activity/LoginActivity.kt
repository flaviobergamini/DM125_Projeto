package com.aduilio.mytasks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ActivityLoginBinding
import com.aduilio.mytasks.extension.value
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleSignIn()
        initComponents()
    }

    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initComponents() {
        binding.btLogin.setOnClickListener { login() }
        binding.btCreateAccount.setOnClickListener { createAccount() }
        binding.btGoogleSignIn.setOnClickListener { signInWithGoogle() }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(this, "Falha ao fazer login com o Google.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Falha na autenticação com o Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login() {
        Firebase.auth.signInWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    binding.tilEmail.error = task.exception?.message
                    Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createAccount() {
        Firebase.auth.createUserWithEmailAndPassword(binding.etEmail.value(), binding.etPassword.value())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    login()
                } else {
                    binding.tilEmail.error = task.exception?.message
                    Toast.makeText(this, "Falha ao criar conta.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
