package com.example.myservices

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myservices.Adapter.ReservationsViewAdapter
import com.example.myservices.databinding.ActivityReservationBinding
import com.example.myservices.interfaces.OnDeleteClickListener
import com.example.myservices.model.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservationActivity : AppCompatActivity() ,OnDeleteClickListener{
    private lateinit var binding: ActivityReservationBinding
    private lateinit var adapter: ReservationsViewAdapter
    private val reservations = ArrayList<Reservation>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading(true)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = ReservationsViewAdapter(reservations,this)
        binding.rc.apply {
            adapter = this@ReservationActivity.adapter
            layoutManager = LinearLayoutManager(this@ReservationActivity)
        }
    }


    private fun loadData() {
        val database = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { userId ->
            database.collection("users").document(userId).collection("reservations")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            reservations.clear()
                            for (document in querySnapshot) {
                                val reservation = document.toObject(Reservation::class.java)
                                reservation.documentId = document.id // تعيين معرّف المستند
                                reservations.add(reservation)
                            }
                            adapter.notifyDataSetChanged()
                        } else {

                            Log.d("TAG", "No reservations found.")
                        }
                    } else {

                        val exception = task.exception
                        if (exception != null) {
                            Log.e("TAG", "loadData: " + exception.message)
                        }
                    }
                    loading(false)
                }
        }
        }

            private fun loading(isLoading: Boolean) {
        binding.rc.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressParIcon.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeleteClick(reservation: Reservation?) {
        val database = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { userId ->
            database.collection("users").document(userId).collection("reservations")
                .document(reservation?.documentId!!)
                ?.delete()
                ?.addOnSuccessListener {

                    // بعد حذف الحجز بنجاح
                    reservations.remove(reservation)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@ReservationActivity, "تم حذف الحجز بنجاح", Toast.LENGTH_SHORT).show()

                }
                ?.addOnFailureListener { e ->
                    // في حالة فشل عملية الحذف
                    Log.e("TAG", "onDeleteClick: " + e.message)
                }
        }
        }

    }

