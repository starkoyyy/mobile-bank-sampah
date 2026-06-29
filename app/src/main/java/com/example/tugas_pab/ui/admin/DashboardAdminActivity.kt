package com.example.tugas_pab.ui.admin

import com.example.tugas_pab.R
import com.example.tugas_pab.network.AdminDashboardResponse
import com.example.tugas_pab.network.PersetujuanItem

import com.example.tugas_pab.network.RetrofitClient
import com.example.tugas_pab.utils.AvatarUtil

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var swipeRefresh: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        setupBottomNavigation()
        
        swipeRefresh = findViewById(R.id.swipe_refresh_admin)
        swipeRefresh.setOnRefreshListener {
            fetchDashboardData()
        }
        
        // Initial fetch
        swipeRefresh.isRefreshing = true
        fetchDashboardData()
        
        // Setup Avatar
        val ivProfile = findViewById<ImageView>(R.id.iv_profile_admin)
        ivProfile.setImageDrawable(AvatarUtil.getAvatar("Admin"))

        findViewById<TextView>(R.id.tv_pending_see_all).setOnClickListener {
            startActivity(Intent(this, DaftarPengajuanActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                android.app.AlertDialog.Builder(this@DashboardAdminActivity)
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
        fetchDashboardData()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_admin)
        bottomNav.selectedItemId = R.id.navigation_admin_home
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_admin_home -> true
                R.id.navigation_submission -> {
                    startActivity(Intent(this, DaftarPengajuanActivity::class.java))
                    true
                }
                R.id.navigation_report -> {
                    startActivity(Intent(this, CetakLaporanActivity::class.java))
                    true
                }
                R.id.navigation_more -> {
                    startActivity(Intent(this, MenuAdminActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchDashboardData() {
        RetrofitClient.instance.getAdminDashboard().enqueue(object : Callback<AdminDashboardResponse> {
            override fun onResponse(call: Call<AdminDashboardResponse>, response: Response<AdminDashboardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    
                    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
                    val saldoRp = formatter.format(data.total_saldo.toInt()).replace(",00", "").replace("Rp", "Rp ")
                    
                    findViewById<TextView>(R.id.tv_admin_saldo_amount).text = saldoRp
                    findViewById<TextView>(R.id.tv_admin_nasabah_count).text = "${data.total_nasabah}"
                    findViewById<TextView>(R.id.tv_admin_sampah_count).text = "${data.total_sampah.toInt()} Kg"

                    val persetujuanList = data.persetujuan
                    
                    setupPersetujuanCard(1, persetujuanList.getOrNull(0))
                    setupPersetujuanCard(2, persetujuanList.getOrNull(1))
                    setupPersetujuanCard(3, persetujuanList.getOrNull(2))
                    
                    swipeRefresh.isRefreshing = false
                } else {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this@DashboardAdminActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdminDashboardResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@DashboardAdminActivity, "Koneksi Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun setupPersetujuanCard(index: Int, item: PersetujuanItem?) {
        val cvPersetujuan = findViewById<CardView>(resources.getIdentifier("cv_persetujuan_$index", "id", packageName))
        val tvName = findViewById<TextView>(resources.getIdentifier("tv_name_$index", "id", packageName))
        val tvDesc = findViewById<TextView>(resources.getIdentifier("tv_desc_$index", "id", packageName))
        val btnSetujui = findViewById<View>(resources.getIdentifier("btn_setujui_$index", "id", packageName))

        if (item != null) {
            cvPersetujuan.visibility = View.VISIBLE
            tvName.text = item.nama_lengkap
            
            if (item.jenis_transaksi == "setor") {
                tvDesc.text = "Setor Sampah ${item.jenis_sampah} ${item.berat_kg} Kg"
            } else {
                tvDesc.text = "Tarik Tunai Rp ${item.nominal_rp.toInt()} - ${item.metode_penarikan}"
            }
            
            btnSetujui.setOnClickListener {
                startActivity(Intent(this, DaftarPengajuanActivity::class.java))
            }
        } else {
            cvPersetujuan.visibility = View.GONE
        }
    }
}
