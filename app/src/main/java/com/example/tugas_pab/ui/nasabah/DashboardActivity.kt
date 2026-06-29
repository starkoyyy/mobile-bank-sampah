package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.R
import com.example.tugas_pab.network.UserProfileResponse
import com.example.tugas_pab.data.model.TransactionResponse

import com.example.tugas_pab.ui.auth.LoginActivity
import com.example.tugas_pab.ui.adapter.RiwayatAdapter
import com.example.tugas_pab.network.RetrofitClient
import com.example.tugas_pab.utils.AvatarUtil
import com.example.tugas_pab.utils.NavigationUtils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.ImageView

class DashboardActivity : AppCompatActivity() {
    private lateinit var rvRecentHistory: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val tvSeeAll = findViewById<TextView>(R.id.tv_see_all)
        val cvSetorSampah = findViewById<CardView>(R.id.cv_setor_sampah)
        val cvTarikSaldo = findViewById<CardView>(R.id.cv_tarik_saldo)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        rvRecentHistory = findViewById(R.id.rv_recent_history)
        rvRecentHistory.layoutManager = LinearLayoutManager(this)

        findViewById<android.view.View>(R.id.fl_notification).setOnClickListener {
            startActivity(Intent(this, NotifikasiActivity::class.java))
        }

        cvSetorSampah.setOnClickListener {
            startActivity(Intent(this, SetorSampahActivity::class.java))
        }

        cvTarikSaldo.setOnClickListener {
            startActivity(Intent(this, TarikSaldoActivity::class.java))
        }

        tvSeeAll.setOnClickListener {
            startActivity(Intent(this, RiwayatTransaksiActivity::class.java))
            bottomNav.selectedItemId = R.id.navigation_history
        }

        bottomNav.selectedItemId = R.id.navigation_home
        NavigationUtils.setupBottomNavigation(this, bottomNav)
        
        swipeRefresh = findViewById(R.id.swipe_refresh_user)
        swipeRefresh.setOnRefreshListener {
            loadUserData()
        }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                android.app.AlertDialog.Builder(this@DashboardActivity)
                    .setTitle("Keluar Aplikasi")
                    .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Iya") { _, _ -> finishAffinity() }
                    .setNegativeButton("Tidak", null)
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        swipeRefresh.isRefreshing = true
        loadUserData()
    }

    private fun loadUserData() {
        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val tvSaldoAmount = findViewById<TextView>(R.id.tv_saldo_amount)
        val ivProfileUser = findViewById<ImageView>(R.id.iv_profile_user)
        
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", null)
        
        if (userDataString != null) {
            try {
                val jsonObject = JSONObject(userDataString)
                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                
                val name = if (userObj.has("nama_lengkap") && !userObj.isNull("nama_lengkap") && userObj.getString("nama_lengkap").isNotBlank()) {
                    userObj.getString("nama_lengkap")
                } else if (userObj.has("username") && !userObj.isNull("username")) {
                    userObj.getString("username")
                } else {
                    "Pengguna"
                }
                
                val userId = userObj.optString("id", "")
                val firstName = name.split(" ").firstOrNull() ?: name
                tvGreeting.text = "Halo, $firstName"
                
                val avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=1B5E20&color=fff&size=200"
                com.bumptech.glide.Glide.with(this).load(avatarUrl).circleCrop().into(ivProfileUser)
                
                // Render fallback saldo immediately
                val fallbackSaldo = if (userObj.has("saldo")) userObj.optDouble("saldo", 0.0).toInt() else 0
                val formatRp = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
                tvSaldoAmount.text = formatRp.format(fallbackSaldo).replace("Rp", "Rp ").substringBefore(",")

                if (userId.isNotEmpty() && userId != "-1") {
                    fetchRecentHistory(userId)
                    
                    // Fetch latest profile data
                    RetrofitClient.instance.getUserProfile(userId).enqueue(object : Callback<UserProfileResponse> {
                        override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                val userProfile = response.body()!!.data
                                val formatRp = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
                                tvSaldoAmount.text = formatRp.format(userProfile.saldo).replace("Rp", "Rp ").substringBefore(",")
                                
                                // Update SharedPreferences
                                userObj.put("saldo", userProfile.saldo)
                                val editor = sharedPreferences.edit()
                                if (jsonObject.has("user")) {
                                    jsonObject.put("user", userObj)
                                    editor.putString("user_data", jsonObject.toString())
                                } else {
                                    editor.putString("user_data", userObj.toString())
                                }
                                editor.apply()
                            }
                            swipeRefresh.isRefreshing = false
                        }
                        override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                            val fallbackSaldo = if (userObj.has("saldo")) userObj.optDouble("saldo", 0.0).toInt() else 0
                            val formatRp = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
                            tvSaldoAmount.text = formatRp.format(fallbackSaldo).replace("Rp", "Rp ").substringBefore(",")
                            swipeRefresh.isRefreshing = false
                        }
                    })
                } else {
                    // Force logout if session is invalid (id is empty or -1)
                    forceLogout()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tvGreeting.text = "Halo, Pengguna"
                forceLogout()
            }
        } else {
            tvGreeting.text = "Halo, Pengguna"
            forceLogout()
        }
    }

    private fun forceLogout() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("user_data").remove("USER_ID").apply()
        Toast.makeText(this, "Sesi kedaluwarsa. Silakan login kembali.", Toast.LENGTH_LONG).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun fetchRecentHistory(userId: String) {
        RetrofitClient.instance.getRiwayat(userId).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val transactions = response.body()!!.data.take(3)
                    rvRecentHistory.adapter = RiwayatAdapter(transactions)
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
            }
        })

        RetrofitClient.instance.getNotifikasi(userId).enqueue(object : Callback<com.example.tugas_pab.data.model.NotifikasiResponse> {
            override fun onResponse(call: Call<com.example.tugas_pab.data.model.NotifikasiResponse>, response: Response<com.example.tugas_pab.data.model.NotifikasiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val count = response.body()!!.data.size
                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val lastSeenCount = sharedPref.getInt("last_seen_notif_count_$userId", 0)
                    val vDot = findViewById<android.view.View>(R.id.v_notification_dot)
                    if (count > lastSeenCount) {
                        vDot.visibility = android.view.View.VISIBLE
                    } else {
                        vDot.visibility = android.view.View.GONE
                    }
                }
            }
            override fun onFailure(call: Call<com.example.tugas_pab.data.model.NotifikasiResponse>, t: Throwable) {}
        })
    }
}
