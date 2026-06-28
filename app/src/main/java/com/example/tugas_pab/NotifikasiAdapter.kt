package com.example.tugas_pab

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotifikasiAdapter(private val notifikasiList: List<NotifikasiItem>) :
    RecyclerView.Adapter<NotifikasiAdapter.NotifikasiViewHolder>() {

    class NotifikasiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_notif_icon)
        val tvTitle: TextView = view.findViewById(R.id.tv_notif_title)
        val tvDesc: TextView = view.findViewById(R.id.tv_notif_desc)
        val tvDate: TextView = view.findViewById(R.id.tv_notif_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifikasi, parent, false)
        return NotifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val item = notifikasiList[position]

        val isSetor = item.jenis_transaksi == "setor"
        val actionText = if (isSetor) "Setor Sampah" else "Tarik Saldo"
        
        if (item.status == "disetujui") {
            holder.tvTitle.text = "$actionText Disetujui"
            holder.tvTitle.setTextColor(Color.parseColor("#1B5E20")) // Green
            holder.ivIcon.setImageResource(R.drawable.ic_info_green)
        } else {
            holder.tvTitle.text = "$actionText Ditolak"
            holder.tvTitle.setTextColor(Color.parseColor("#D32F2F")) // Red
            holder.ivIcon.setImageResource(R.drawable.ic_notif_error) // Assuming this icon exists
        }

        val nominal = item.nominal_rp?.toInt() ?: 0
        holder.tvDesc.text = "Nominal: Rp $nominal"
        
        holder.tvDate.text = item.created_at.take(10)
    }

    override fun getItemCount() = notifikasiList.size
}
