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
import com.example.myservices.databinding.ActivityCleaningBinding
import com.example.myservices.interfaces.ClickAction
import com.example.myservices.model.Services
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.threeten.bp.LocalDate

class CleaningActivity : AppCompatActivity(), ClickAction {

    private lateinit var binding: ActivityCleaningBinding
    private lateinit var adapter: DataSerViewAdapter
    private val services = ArrayList<Services>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ربط عناصر واجهة المستخدم بمتغيرات الكود
        binding = ActivityCleaningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // إعداد شريط الأدوات وعرض الزر الرجوع
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // إعداد RecyclerView ومهيأة البيانات
        setupRecyclerView()
        loadData()
    }

    // إعداد RecyclerView ومهيأة محول البيانات
    private fun setupRecyclerView() {
        adapter = DataSerViewAdapter(services, this@CleaningActivity)
        binding.rc.apply {
            adapter = this@CleaningActivity.adapter
            layoutManager = LinearLayoutManager(this@CleaningActivity)
        }
    }

    // تحميل بيانات الخدمات من قاعدة البيانات
    private fun loadData() {
        val database = FirebaseFirestore.getInstance()
        database.collection("services")
            .whereEqualTo("typeService", "cleaning")
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

    // إظهار أو إخفاء عناصر الواجهة أثناء التحميل
    private fun loading(isLoading: Boolean) {
        binding.rc.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressParIcon.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    // التعامل مع اختيار زر العودة في شريط الأدوات
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // تفاصيل الإجراء الذي يتم تنفيذه عند النقر على زر الحجز
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReservationClick(services: Services?) {
        // إنشاء مربع حوار اختيار التاريخ
        val dialog = createDatePickerDialog()

        dialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val currentDate = LocalDate.now()

            if (selectedDate.isBefore(currentDate) || selectedDate == currentDate) {
                // التحقق من أن التاريخ المختار ليس قبل أو يساوي التاريخ الحالي
                Toast.makeText(this, "لا يمكن حجز تاريخ ما قبل أو يساوي التاريخ الحالي", Toast.LENGTH_LONG).show()
            } else {
                // تنفيذ الحجز إذا كان التاريخ صالحًا
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

    // إنشاء مربع حوار اختيار التاريخ
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createDatePickerDialog(): DatePickerDialog {
        return DatePickerDialog(this@CleaningActivity)
    }

    // إعداد بيانات الحجز للتخزين في قاعدة البيانات
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

    // عرض رسالة نجاح عند إتمام الحجز بنجاح
    private fun showSuccessMessage() {
        Toast.makeText(this, "تم إجراء الحجز بنجاح", Toast.LENGTH_LONG).show()
    }

    // التعامل مع خطأ في عملية الحجز
    private fun handleReservationError(e: Exception) {
        Log.d("TAG", "onReservationClick: ${e.message}")
        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
    }
}
