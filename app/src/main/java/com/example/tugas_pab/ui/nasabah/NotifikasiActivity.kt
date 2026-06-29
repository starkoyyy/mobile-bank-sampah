package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.R
import com.example.tugas_pab.data.model.NotifikasiResponse
import com.example.tugas_pab.data.model.NotifikasiItem

import com.example.tugas_pab.ui.adapter.NotifikasiAdapter
import com.example.tugas_pab.network.RetrofitClient

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var rvNotifikasi: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NotifikasiAdapter
    private val notifikasiList = mutableListOf<NotifikasiItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi)
        
        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        rvNotifikasi = findViewById(R.id.rv_notifikasi)
        tvEmptyState = findViewById(R.id.tv_empty_state)
        progressBar = findViewById(R.id.progressBar)

        rvNotifikasi.layoutManager = LinearLayoutManager(this)
        adapter = NotifikasiAdapter(notifikasiList)
        rvNotifikasi.adapter = adapter

        fetchNotifikasi()
    }

    private fun fetchNotifikasi() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", null)

        var userId = ""
        if (userDataString != null) {
            try {
                val jsonObject = org.json.JSONObject(userDataString)
                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                userId = userObj.optString("id", "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (userId.isEmpty()) {
            Toast.makeText(this, "Sesi pengguna tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        rvNotifikasi.visibility = View.GONE

        RetrofitClient.instance.getNotifikasi(userId).enqueue(object : Callback<NotifikasiResponse> {
            override fun onResponse(
                call: Call<NotifikasiResponse>,
                response: Response<NotifikasiResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    notifikasiList.clear()
                    notifikasiList.addAll(data)
                    adapter.notifyDataSetChanged()

                    if (notifikasiList.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        rvNotifikasi.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvNotifikasi.visibility = View.VISIBLE
                    }
                    
                    val editor = sharedPreferences.edit()
                    editor.putInt("last_seen_notif_count_$userId", data.size)
                    editor.apply()
                } else {
                    Toast.makeText(this@NotifikasiActivity, "Gagal memuat notifikasi", Toast.LENGTH_SHORT).show()
                    tvEmptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<NotifikasiResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@NotifikasiActivity, "Koneksi error", Toast.LENGTH_SHORT).show()
                tvEmptyState.visibility = View.VISIBLE
            }
        })
    }
}
