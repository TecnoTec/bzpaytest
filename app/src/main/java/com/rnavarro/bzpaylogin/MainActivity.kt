package com.rnavarro.bzpaylogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rnavarro.bzpaylogin.adapter.PetAdapter
import com.rnavarro.bzpaylogin.model.Pet

class MainActivity : AppCompatActivity() {

    var btn_add: Button? = null
    var btn_add_fragment: Button? = null
    var btn_exit: Button? = null
    var mAdapter: PetAdapter? = null
    var mRecycler: RecyclerView? = null
    var mFirestore: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var search_view: SearchView? = null
    var query: Query? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        search_view = findViewById(R.id.search)
        btn_add = findViewById(R.id.btn_add)
        btn_add_fragment = findViewById(R.id.btn_add_fragment)
        btn_exit = findViewById(R.id.btn_close)

        btn_add!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(this, Create_pet::class.java
                )
            )
        })
        btn_add_fragment!!.setOnClickListener(View.OnClickListener {
            val fm = CreatePetFragment()
            fm.show(supportFragmentManager, "Navegar a fragment")
        })

        btn_exit!!.setOnClickListener(View.OnClickListener {
            mAuth!!.signOut()
            finish()
            startActivity(Intent(this, Login::class.java))
        })
        setUpRecyclerView()
        search_view()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle)
        mRecycler!!.setLayoutManager(LinearLayoutManager(this))
        query = mFirestore!!.collection("pet")
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Pet> =
            FirestoreRecyclerOptions.Builder<Pet>().setQuery(
                query!!,
                Pet::class.java
            ).build()
        mAdapter = PetAdapter(firestoreRecyclerOptions, this, supportFragmentManager)
        mAdapter!!.notifyDataSetChanged()
        mRecycler!!.setAdapter(mAdapter)
    }

    private fun search_view() {
        search_view!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                textSearch(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                textSearch(s)
                return false
            }
        })
    }

    fun textSearch(s: String) {
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Pet> =
            FirestoreRecyclerOptions.Builder<Pet>()
                .setQuery(
                    query!!.orderBy("name")
                        .startAt(s).endAt("$s~"), Pet::class.java
                ).build()
        mAdapter = PetAdapter(firestoreRecyclerOptions, this, supportFragmentManager)
        mAdapter!!.startListening()
        mRecycler!!.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }
}