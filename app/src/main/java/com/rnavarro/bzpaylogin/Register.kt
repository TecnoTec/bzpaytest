package com.rnavarro.bzpaylogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Register : AppCompatActivity() {
    var btn_register: Button? = null
    var name: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var mFirestore: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

       // supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mFirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        name = findViewById(R.id.Edt_nombre)
        email = findViewById(R.id.correo)
        password = findViewById(R.id.contrasena)
        btn_register = findViewById(R.id.btn_registro)

        btn_register!!.setOnClickListener(View.OnClickListener {
            val nameUser = name!!.getText().toString().trim { it <= ' ' }
            val emailUser = email!!.getText().toString().trim { it <= ' ' }
            val passUser = password!!.getText().toString().trim { it <= ' ' }
            if (nameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty()) {
                Toast.makeText(this, "Complete los datos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                registerUser(nameUser, emailUser, passUser)
            }
        })
    }

    private fun registerUser(nameUser: String, emailUser: String, passUser: String) {
        mAuth!!.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener {
            val id = mAuth!!.currentUser!!.uid
            val map: MutableMap<String, Any> =
                HashMap()
            map["id"] = id
            map["name"] = nameUser
            map["email"] = emailUser
            map["password"] = passUser
            mFirestore!!.collection("user").document(id).set(map).addOnSuccessListener {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(
                    this,
                    "Usuario registrado con éxito",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al guardar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Error al registrar",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }
}