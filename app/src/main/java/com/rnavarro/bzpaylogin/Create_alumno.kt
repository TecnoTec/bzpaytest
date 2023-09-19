package com.rnavarro.bzpaylogin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DecimalFormat


class Create_pet : AppCompatActivity() {
    var photo_pet: ImageView? = null
    var btn_add: Button? = null
    var linearLayout_image_btn: LinearLayout? = null
    var name: EditText? = null
    var age: EditText? = null
    var color: EditText? = null
    var precio_vacuna: EditText? = null
    private var mfirestore: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    var storageReference: StorageReference? = null
    var storage_path = "pet/*"
    private var image_url: Uri? = null
    var idd: String? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_alumno)

        this.title = "Alumnos"


        progressDialog = ProgressDialog(this)
        val id = intent.getStringExtra("id_pet")
        mfirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        linearLayout_image_btn = findViewById(R.id.images_btn)
        name = findViewById(R.id.Edt_nombre)
        age = findViewById(R.id.Edt_date)
        color = findViewById(R.id.Edt_plantel)
        precio_vacuna = findViewById(R.id.Edt_grado)
        photo_pet = findViewById(R.id.pet_photo)
        btn_add = findViewById(R.id.btn_add)

        if (id == null || id === "") {
            linearLayout_image_btn!!.setVisibility(View.GONE)
            btn_add!!.setOnClickListener(View.OnClickListener {
                val namepet = name!!.getText().toString().trim { it <= ' ' }
                val agepet = age!!.getText().toString().trim { it <= ' ' }
                val colorpet = color!!.getText().toString().trim { it <= ' ' }
                val precio_vacunapet = precio_vacuna!!.getText().toString().trim { it <= ' ' }
                    .toDouble()
                if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()) {
                    Toast.makeText(applicationContext, "Ingresar los datos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    postPet(namepet, agepet, colorpet, precio_vacunapet)
                }
            })
        } else {
            idd = id
            btn_add!!.setText("Update")
            getPet(id)
            btn_add!!.setOnClickListener(View.OnClickListener {
                val namepet = name!!.getText().toString().trim { it <= ' ' }
                val agepet = age!!.getText().toString().trim { it <= ' ' }
                val colorpet = color!!.getText().toString().trim { it <= ' ' }
                val precio_vacunapet = precio_vacuna!!.getText().toString().trim { it <= ' ' }
                    .toDouble()
                if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()) {
                    Toast.makeText(applicationContext, "Ingresar los datos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    updatePet(namepet, agepet, colorpet, precio_vacunapet, id)
                }
            })
        }
    }

    private fun uploadPhoto() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        startActivityForResult(i, COD_SEL_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                image_url = data!!.data
                subirPhoto(image_url)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun subirPhoto(image_url: Uri?) {
        progressDialog!!.setMessage("Actualizando foto")
        progressDialog!!.show()
        val rute_storage_photo = storage_path + "" + mAuth!!.uid + "" + idd
        val reference = storageReference!!.child(rute_storage_photo)
        reference.putFile(image_url!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            if (uriTask.isSuccessful) {
                uriTask.addOnSuccessListener { uri ->
                    val download_uri = uri.toString()
                    val map = HashMap<String, Any>()
                    map["photo"] = download_uri
                    mfirestore!!.collection("pet").document(idd!!).update(map)
                    Toast.makeText(
                        this,
                        "Foto actualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog!!.dismiss()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Error al cargar foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updatePet(
        namepet: String,
        agepet: String,
        colorpet: String,
        precio_vacunapet: Double,
        id: String
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map["name"] = namepet
        map["age"] = agepet
        map["color"] = colorpet
        map["vaccine_price"] = precio_vacunapet
        mfirestore!!.collection("pet").document(id).update(map).addOnSuccessListener {
            Toast.makeText(applicationContext, "Actualizado exitosamente", Toast.LENGTH_SHORT)
                .show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Error al actualizar",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun postPet(
        namepet: String,
        agepet: String,
        colorpet: String,
        precio_vacunapet: Double
    ) {
        val idUser = mAuth!!.currentUser!!.uid
        val id = mfirestore!!.collection("pet").document()
        val map: MutableMap<String, Any> = HashMap()
        map["id_user"] = idUser
        map["id"] = id.id
        map["name"] = namepet
        map["age"] = agepet
        map["color"] = colorpet
        map["vaccine_price"] = precio_vacunapet
        mfirestore!!.collection("pet").document(id.id).set(map).addOnSuccessListener {
            Toast.makeText(applicationContext, "Creado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Error al ingresar",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getPet(id: String) {
        mfirestore!!.collection("pet").document(id).get().addOnSuccessListener { documentSnapshot ->
            val format = DecimalFormat("0.00")
            val namePet = documentSnapshot.getString("name")
            val agePet = documentSnapshot.getString("age")
            val colorPet = documentSnapshot.getString("color")
            val precio_vacunapet = documentSnapshot.getDouble("vaccine_price")
            val photoPet = documentSnapshot.getString("photo")
            name!!.setText(namePet)
            age!!.setText(agePet)
            color!!.setText(colorPet)
            precio_vacuna!!.setText(format.format(precio_vacunapet))

        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Error al obtener los datos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    companion object {
        private const val COD_SEL_STORAGE = 200
        private const val COD_SEL_IMAGE = 300
    }
}