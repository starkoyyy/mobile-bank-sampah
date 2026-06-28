package com.example.tugas_pab

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatTransaksiActivity : AppCompatActivity() {
    private lateinit var rvRiwayat: RecyclerView
    private var allTransactions: List<TransactionItem> = emptyList()
    
    private lateinit var tvSemua: TextView
    private lateinit var tvSetoran: TextView
    private lateinit var tvPenarikan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_transaksi)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_history
        NavigationUtils.setupBottomNavigation(this, bottomNav)

        rvRiwayat = findViewById(R.id.rv_riwayat)
        rvRiwayat.layoutManager = LinearLayoutManager(this)

        tvSemua = findViewById(R.id.tv_filter_semua)
        tvSetoran = findViewById(R.id.tv_filter_setoran)
        tvPenarikan = findViewById(R.id.tv_filter_penarikan)

        tvSemua.setOnClickListener { updateFilter("semua") }
        tvSetoran.setOnClickListener { updateFilter("setor") }
        tvPenarikan.setOnClickListener { updateFilter("tarik") }

        fetchRiwayat()
    }

    private fun updateFilter(filterType: String) {
        // Reset all styles
        tvSemua.setBackgroundResource(R.drawable.bg_chip_unselected)
        tvSemua.setTextColor(Color.parseColor("#333333"))
        tvSetoran.setBackgroundResource(R.drawable.bg_chip_unselected)
        tvSetoran.setTextColor(Color.parseColor("#333333"))
        tvPenarikan.setBackgroundResource(R.drawable.bg_chip_unselected)
        tvPenarikan.setTextColor(Color.parseColor("#333333"))

        // Apply active style & filter data
        val filteredList = when (filterType) {
            "setor" -> {
                tvSetoran.setBackgroundResource(R.drawable.bg_chip_selected)
                tvSetoran.setTextColor(Color.WHITE)
                allTransactions.filter { it.jenis_transaksi == "setor" }
            }
            "tarik" -> {
                tvPenarikan.setBackgroundResource(R.drawable.bg_chip_selected)
                tvPenarikan.setTextColor(Color.WHITE)
                allTransactions.filter { it.jenis_transaksi == "tarik" }
            }
            else -> {
                tvSemua.setBackgroundResource(R.drawable.bg_chip_selected)
                tvSemua.setTextColor(Color.WHITE)
                allTransactions
            }
        }

        rvRiwayat.adapter = RiwayatAdapter(filteredList)
    }

    private fun fetchRiwayat() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPref.getString("user_data", null)

        if (userDataString != null) {
            try {
                val jsonObject = JSONObject(userDataString)
                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                val userId = userObj.optString("id", "")

                if (userId.isNotEmpty() && userId != "-1") {
                    RetrofitClient.instance.getRiwayat(userId).enqueue(object : Callback<TransactionResponse> {
                        override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                allTransactions = response.body()!!.data
                                // Terapkan filter "semua" secara default
                                updateFilter("semua")
                            } else {
                                Toast.makeText(this@RiwayatTransaksiActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                            Toast.makeText(this@RiwayatTransaksiActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
