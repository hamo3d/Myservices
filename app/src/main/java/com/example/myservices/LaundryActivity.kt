package com.example.myservices

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myservices.Adapter.DataSerViewAdapter
import com.example.myservices.databinding.ActivityLaundryBinding
import com.example.myservices.interfaces.ClickAction
import com.example.myservices.model.Services
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.threeten.bp.LocalDate

class LaundryActivity : AppCompatActivity(),ClickAction {
    private lateinit var binding: ActivityLaundryBinding
    private lateinit var adapter: DataSerViewAdapter
    private val services = ArrayList<Services>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaundryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        loading(true)
        setupRecyclerView()
        loadData()
    }


    private fun setupRecyclerView() {
        adapter = DataSerViewAdapter(services,this)
        binding.rc.apply {
            adapter = this@LaundryActivity.adapter
            layoutManager = LinearLayoutManager(this@LaundryActivity)
        }
    }

    private fun loadData() {
        val database = FirebaseFirestore.getInstance()
        database.collection("services")
            .whereEqualTo("typeService", "laundry")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                services.clear()
                for (documentSnapshot in queryDocumentSnapshots) {
                    val service = documentSnapshot.toObject(Services::class.java)
                    services.add(service)
                }
                adapter.notifyDataSetChanged()
                loading(false)
            }
            .addOnFailureListener { e ->
                loading(false)
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReservationClick(services: Services?) {
        val dialog = createDatePickerDialog()

        dialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val currentDate = LocalDate.now()

            if (selectedDate.isBefore(currentDate) || selectedDate == currentDate) {
                // التاريخ الذي تم اختياره ما قبل التاريخ الحالي أو هو نفسه التاريخ الحالي
                Toast.makeText(this, "لا يمكن حجز تاريخ ما قبل أو يساوي التاريخ الحالي", Toast.LENGTH_LONG).show()
            } else {
                val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formattedSelectedDate = selectedDate.format(formatter)
                val reservationData = createReservationData(services, formattedSelectedDate)
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                user?.uid?.let { userId ->
                    db.collection("users").document(userId).collection("reservations")
                        .add(reservationData)
                        .addOnSuccessListener {
                            showSuccessMessage()
                        }
                        .addOnFailureListener { e ->
                            handleReservationError(e)
                        }
                }
            }
        }

        dialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createDatePickerDialog(): DatePickerDialog {
        return DatePickerDialog(this@LaundryActivity)
    }

    private fun createReservationData(
        services: Services?,
        selectedDate: String
    ): HashMap<String, Any?> {
        return hashMapOf(
            "nameUser" to services?.nameUser,
            "serviceName" to services?.serviceName,
            "servicePrice" to services?.servicePrice,
            "typeService" to services?.typeService,
            "image" to services?.image,
            "bookingDate" to selectedDate
        )
    }

    private fun showSuccessMessage() {
        Toast.makeText(this, "Reservation successful", Toast.LENGTH_LONG).show()
    }

    private fun handleReservationError(e: Exception) {
        Log.d("TAG", "onReservationClick: ${e.message}")
        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
    }
}