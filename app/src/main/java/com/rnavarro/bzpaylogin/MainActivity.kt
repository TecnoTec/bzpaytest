package com.rnavarro.bzpaylogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    val Usuario: String = ""
    val Password:String = ""

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun Registrar(view: View){
        Toast.makeText(this, "Mando a la pagina de Registro", Toast.LENGTH_SHORT).show()
    }
}