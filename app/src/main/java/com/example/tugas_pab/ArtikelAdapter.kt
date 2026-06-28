package com.example.tugas_pab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ArtikelAdapter(
    private var articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArtikelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_article_image)
        val tvTitle: TextView = view.findViewById(R.id.tv_article_title)
        val tvDesc: TextView = view.findViewById(R.id.tv_article_desc)
        val tvDate: TextView = view.findViewById(R.id.tv_article_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artikel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        
        holder.tvTitle.text = article.title ?: "Tanpa Judul"
        holder.tvDesc.text = article.description ?: article.content ?: ""
        
        // Format date
        article.publishedAt?.let { rawDate ->
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(rawDate)
                if (date != null) {
                    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    holder.tvDate.text = formatter.format(date)
                } else {
                    holder.tvDate.text = rawDate
                }
            } catch (e: Exception) {
                holder.tvDate.text = rawDate.take(10) // fallback to YYYY-MM-DD
            }
        } ?: run {
            holder.tvDate.text = ""
        }

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .placeholder(R.drawable.bg_placeholder_image)
            .error(R.drawable.bg_placeholder_image)
            .centerCrop()
            .into(holder.ivImage)

        holder.itemView.setOnClickListener {
            onItemClick(article)
        }
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
