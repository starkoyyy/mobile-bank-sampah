package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.R
import com.example.tugas_pab.data.model.Article

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DetailArtikelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_artikel)
        
        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val article = intent.getSerializableExtra("EXTRA_ARTICLE") as? Article
        if (article != null) {
            val tvTitle = findViewById<android.widget.TextView>(R.id.tv_detail_title)
            val tvDate = findViewById<android.widget.TextView>(R.id.tv_detail_date)
            val tvAuthor = findViewById<android.widget.TextView>(R.id.tv_detail_author)
            val tvContent = findViewById<android.widget.TextView>(R.id.tv_detail_content)
            val ivImage = findViewById<android.widget.ImageView>(R.id.iv_detail_image)
            val btnReadMore = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_read_more)

            tvTitle.text = article.title ?: "Tanpa Judul"
            tvAuthor.text = article.author ?: article.source?.name ?: "Anonim"
            
            // NewsAPI sometimes returns [removed] or truncated content. Show description as fallback.
            tvContent.text = article.content ?: article.description ?: "Tidak ada konten tersedia."
            
            btnReadMore.setOnClickListener {
                val url = article.url
                if (!url.isNullOrEmpty()) {
                    val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    startActivity(browserIntent)
                } else {
                    android.widget.Toast.makeText(this, "Link artikel tidak tersedia", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            
            // Format date
            article.publishedAt?.let { rawDate ->
                try {
                    val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                    parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    val date = parser.parse(rawDate)
                    if (date != null) {
                        val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                        tvDate.text = formatter.format(date)
                    } else {
                        tvDate.text = rawDate
                    }
                } catch (e: Exception) {
                    tvDate.text = rawDate.take(10)
                }
            } ?: run {
                tvDate.text = "-"
            }

            // Load Image
            com.bumptech.glide.Glide.with(this)
                .load(article.urlToImage)
                .placeholder(R.drawable.bg_placeholder_image)
                .error(R.drawable.bg_placeholder_image)
                .centerCrop()
                .into(ivImage)
        }
    }
}
