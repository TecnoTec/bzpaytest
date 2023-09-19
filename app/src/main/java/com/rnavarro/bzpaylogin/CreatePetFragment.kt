package com.rnavarro.bzpaylogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class CreatePetFragment : DialogFragment() {
    var id_pet: String? = null
    var btn_add: Button? = null
    var name: EditText? = null
    var age: EditText? = null
    var color: EditText? = null
    var precio_vacuna: EditText? = null
    private var mfirestore: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            id_pet = arguments!!.getString("id_pet")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_create_pet, container, false)
        mfirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        name = v.findViewById(R.id.Edt_nombre)
        age = v.findViewById(R.id.Edt_date)
        color = v.findViewById(R.id.Edt_plantel)
        precio_vacuna = v.findViewById(R.id.Edt_grado)
        btn_add = v.findViewById(R.id.btn_add)
        if (id_pet == null || id_pet === "") {
            btn_add!!.setOnClickListener(View.OnClickListener {
                val namepet = name!!.getText().toString().trim { it <= ' ' }
                val agepet = age!!.getText().toString().trim { it <= ' ' }
                val colorpet = color!!.getText().toString().trim { it <= ' ' }
                val precio_vacunapet = precio_vacuna!!.getText().toString().trim { it <= ' ' }
                    .toDouble()
                if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()) {
                    Toast.makeText(context, "Ingresar los datos", Toast.LENGTH_SHORT).show()
                } else {
                    postPet(namepet, agepet, colorpet, precio_vacunapet)
                }
            })
        } else {
            pet
            btn_add!!.setText("update")
            btn_add!!.setOnClickListener(View.OnClickListener {
                val namepet = name!!.getText().toString().trim { it <= ' ' }
                val agepet = age!!.getText().toString().trim { it <= ' ' }
                val colorpet = color!!.getText().toString().trim { it <= ' ' }
                val precio_vacunapet = precio_vacuna!!.getText().toString().trim { it <= ' ' }
                    .toDouble()
                if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()) {
                    Toast.makeText(context, "Ingresar los datos", Toast.LENGTH_SHORT).show()
                } else {
                    updatePet(namepet, agepet, colorpet, precio_vacunapet)
                }
            })
        }
        return v
    }

    private fun updatePet(
        namepet: String,
        agepet: String,
        colorpet: String,
        precio_vacunapet: Double
    ) {
        val map: MutableMap<String, Any> = HashMap()
        map["name"] = namepet
        map["age"] = agepet
        map["color"] = colorpet
        map["vaccine_price"] = precio_vacunapet
        mfirestore!!.collection("pet").document(id_pet!!).update(map).addOnSuccessListener {
            Toast.makeText(context, "Actualizado exitosamente", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "Creado exitosamente", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al ingresar", Toast.LENGTH_SHORT).show()
        }
    }

    private val pet: Unit
        private get() {
            mfirestore!!.collection("pet").document(id_pet!!).get()
                .addOnSuccessListener { documentSnapshot ->
                    val format = DecimalFormat("0.00")
                    //            format.setMaximumFractionDigits(2);
                    val namePet = documentSnapshot.getString("name")
                    val agePet = documentSnapshot.getString("age")
                    val colorPet = documentSnapshot.getString("color")
                    val precio_vacunapet = documentSnapshot.getDouble("vaccine_price")
                    name!!.setText(namePet)
                    age!!.setText(agePet)
                    color!!.setText(colorPet)
                    precio_vacuna!!.setText(format.format(precio_vacunapet))
                }.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error al obtener los datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
}