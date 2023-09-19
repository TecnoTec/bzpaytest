package com.rnavarro.bzpaylogin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rnavarro.bzpaylogin.adapter.StudentAdapter
import com.rnavarro.bzpaylogin.model.Student
import java.util.Locale


class Student : AppCompatActivity() {
    var studentAdapter: StudentAdapter? = null
    var studentArrayList: ArrayList<Student?>? = null
    var mRecyclerView: RecyclerView? = null
    var mFirestore: FirebaseFirestore? = null
    var searchView: SearchView? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        title = "Students"
        mFirestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
        mRecyclerView = findViewById(R.id.recyclerStudent)
        searchView = findViewById(R.id.search)
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        studentArrayList = ArrayList<Student?>()
        studentAdapter = StudentAdapter(studentArrayList)
        mRecyclerView!!.setAdapter(studentAdapter)
        students
        searchStudent()
    }

    private val students: Unit
        private get() {
            progressDialog!!.setMessage("Obteniendo estudiantes")
            progressDialog!!.show()
            mFirestore!!.collection("student").orderBy("name").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("zzzzzz", document.id + " => " + document.data)
                            val student: Student = document.toObject(Student::class.java)
                            studentArrayList!!.add(student)
                        }
                        studentAdapter!!.notifyDataSetChanged()
                        progressDialog!!.dismiss()
                    }
                }
        }

    private fun searchStudent() {
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                searchStudent(s)
                return true
            }
        })
    }

    private fun searchStudent(s: String) {
        val listStudent: ArrayList<Student> = ArrayList<Student>()
        for (student in studentArrayList!!) {
            if (student!!.getName().toLowerCase().contains(s.lowercase(Locale.getDefault()))) {
                student?.let { listStudent.add(it) }
            }
        }
        studentAdapter = StudentAdapter(listStudent)
        mRecyclerView!!.adapter = studentAdapter
    }
}