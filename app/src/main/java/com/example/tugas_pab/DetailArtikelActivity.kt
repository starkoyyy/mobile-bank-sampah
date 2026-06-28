package com.example.tugas_pab

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

            tvTitle.text = article.title ?: "Tanpa Judul"
            tvAuthor.text = article.author ?: article.source?.name ?: "Anonim"
            tvContent.text = article.content ?: article.description ?: "Tidak ada konten tersedia."
            
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
