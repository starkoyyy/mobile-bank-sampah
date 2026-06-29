package com.example.tugas_pab.ui.adapter

import com.example.tugas_pab.R
import com.example.tugas_pab.data.model.TransactionItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatAdapter(private val transactions: List<TransactionItem>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvWeight: TextView = view.findViewById(R.id.tv_weight)
        val tvAmount: TextView = view.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]

        // Format Date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            val date = inputFormat.parse(item.created_at)
            holder.tvDate.text = date?.let { outputFormat.format(it) } ?: item.created_at
        } catch (e: Exception) {
            holder.tvDate.text = item.created_at
        }

        val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val nominal = formatRp.format(item.nominal_rp).replace("Rp", "Rp ").substringBefore(",")
        
        if (item.jenis_transaksi == "setor") {
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_down)
            holder.ivIcon.setBackgroundResource(R.drawable.bg_circle_green_light)
            holder.tvTitle.text = "Setoran ${item.jenis_sampah ?: "Sampah"}"
            holder.tvWeight.text = "${item.berat_kg ?: 0.0} Kg"
            holder.tvWeight.visibility = View.VISIBLE
            holder.tvAmount.text = "+ $nominal"
            holder.tvAmount.setTextColor(android.graphics.Color.parseColor("#1B5E20")) // Green
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_up)
            holder.ivIcon.setBackgroundResource(R.drawable.bg_circle_red_light)
            holder.tvTitle.text = "Tarik Tunai (${item.metode_penarikan ?: "-"})"
            holder.tvWeight.visibility = View.GONE
            holder.tvAmount.text = "- $nominal"
            holder.tvAmount.setTextColor(android.graphics.Color.parseColor("#D32F2F")) // Red
        }
    }

    override fun getItemCount() = transactions.size
}
