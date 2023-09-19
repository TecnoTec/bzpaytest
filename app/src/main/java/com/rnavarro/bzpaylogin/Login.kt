package com.rnavarro.bzpaylogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Login : AppCompatActivity() {
    var btn_login: Button? = null
    var btn_register: Button? = null
    var btn_login_anonymous: Button? = null
    var email: EditText? = null
    var password: EditText? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.title = "Login"
        mAuth = FirebaseAuth.getInstance()
        email = findViewById(R.id.correo)
        password = findViewById(R.id.contrasena)
        btn_login = findViewById(R.id.btn_ingresar)
        btn_register = findViewById(R.id.btn_register)
        btn_login_anonymous = findViewById(R.id.btn_anonymous)

        btn_login!!.setOnClickListener(View.OnClickListener {
            val emailUser = email!!.getText().toString().trim { it <= ' ' }
            val passUser = password!!.getText().toString().trim { it <= ' ' }
            if (emailUser.isEmpty() && passUser.isEmpty()) {
                Toast.makeText(this, "Ingresar los datos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(emailUser, passUser)
            }
        })

        btn_register!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)

        })
        btn_login_anonymous!!.setOnClickListener(View.OnClickListener { loginAnonymous() })
    }

    private fun loginAnonymous() {
        mAuth!!.signInAnonymously()
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al acceder",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loginUser(emailUser: String, passUser: String) {
        mAuth!!.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Error al inciar sesión",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}