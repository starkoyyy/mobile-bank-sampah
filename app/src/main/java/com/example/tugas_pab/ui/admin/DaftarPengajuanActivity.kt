package com.example.tugas_pab.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tugas_pab.R
import com.example.tugas_pab.network.AdminDashboardResponse
import com.example.tugas_pab.network.RetrofitClient
import com.example.tugas_pab.ui.adapter.PengajuanAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarPengajuanActivity : AppCompatActivity() {
    private lateinit var rvPengajuan: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: PengajuanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_pengajuan)

        findViewById<View>(R.id.iv_back).setOnClickListener { finish() }

        rvPengajuan = findViewById(R.id.rv_pengajuan)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        tvEmpty = findViewById(R.id.tv_empty)

        rvPengajuan.layoutManager = LinearLayoutManager(this)
        adapter = PengajuanAdapter(emptyList())
        rvPengajuan.adapter = adapter

        swipeRefresh.setOnRefreshListener { fetchPengajuan() }
    }
    
    override fun onResume() {
        super.onResume()
        swipeRefresh.isRefreshing = true
        fetchPengajuan()
    }

    private fun fetchPengajuan() {
        RetrofitClient.instance.getAdminDashboard().enqueue(object : Callback<AdminDashboardResponse> {
            override fun onResponse(call: Call<AdminDashboardResponse>, response: Response<AdminDashboardResponse>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val persetujuanList = response.body()!!.data.persetujuan
                    adapter.updateData(persetujuanList)
                    if (persetujuanList.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvPengajuan.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvPengajuan.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@DaftarPengajuanActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdminDashboardResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@DaftarPengajuanActivity, "Koneksi Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
