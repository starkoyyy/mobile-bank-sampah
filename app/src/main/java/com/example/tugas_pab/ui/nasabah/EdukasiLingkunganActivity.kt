package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.data.model.NewsResponse

import com.example.tugas_pab.R

import com.example.tugas_pab.ui.adapter.ArtikelAdapter
import com.example.tugas_pab.network.NewsApiClient
import com.example.tugas_pab.utils.NavigationUtils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EdukasiLingkunganActivity : AppCompatActivity() {
    private lateinit var rvArticles: androidx.recyclerview.widget.RecyclerView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var adapter: ArtikelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_lingkungan)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_education
        NavigationUtils.setupBottomNavigation(this, bottomNav)
        
        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            val intent = android.content.Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        rvArticles = findViewById(R.id.rv_articles)
        progressBar = findViewById(R.id.progress_bar)
        
        rvArticles.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter = ArtikelAdapter(emptyList()) { article ->
            val intent = android.content.Intent(this, DetailArtikelActivity::class.java)
            intent.putExtra("EXTRA_ARTICLE", article)
            startActivity(intent)
        }
        rvArticles.adapter = adapter

        fetchArticles()
    }

    private fun fetchArticles() {
        progressBar.visibility = android.view.View.VISIBLE
        
        val query = "\"daur ulang\" OR \"pengolahan sampah\" OR \"bank sampah\""
        NewsApiClient.instance.getArticles(query = query, apiKey = NewsApiClient.API_KEY)
            .enqueue(object : retrofit2.Callback<NewsResponse> {
                override fun onResponse(
                    call: retrofit2.Call<NewsResponse>,
                    response: retrofit2.Response<NewsResponse>
                ) {
                    progressBar.visibility = android.view.View.GONE
                    if (response.isSuccessful) {
                        response.body()?.articles?.let { articles ->
                            // Filter out articles with "[Removed]" title
                            val validArticles = articles.filter { it.title != "[Removed]" }
                            adapter.updateData(validArticles)
                        }
                    } else {
                        android.widget.Toast.makeText(this@EdukasiLingkunganActivity, "Gagal mengambil data berita", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<NewsResponse>, t: Throwable) {
                    progressBar.visibility = android.view.View.GONE
                    android.widget.Toast.makeText(this@EdukasiLingkunganActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
    }
}
