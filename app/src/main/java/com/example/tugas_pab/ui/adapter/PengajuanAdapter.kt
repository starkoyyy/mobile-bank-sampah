package com.example.tugas_pab.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugas_pab.R
import com.example.tugas_pab.network.PersetujuanItem
import com.example.tugas_pab.ui.admin.DetailPersetujuanActivity

class PengajuanAdapter(private var list: List<PersetujuanItem>) : RecyclerView.Adapter<PengajuanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val btnLihat: View = view.findViewById(R.id.btn_lihat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengajuan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.nama_lengkap
        
        if (item.jenis_transaksi == "setor") {
            holder.tvDesc.text = "Setor Sampah ${item.jenis_sampah} ${item.berat_kg} Kg"
        } else {
            holder.tvDesc.text = "Tarik Tunai Rp ${item.nominal_rp.toInt()} - ${item.metode_penarikan}"
        }
        
        holder.btnLihat.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetailPersetujuanActivity::class.java).apply {
                putExtra("TRANSAKSI_ID", item.id)
                putExtra("USER_ID", item.user_id)
                putExtra("NAMA_LENGKAP", item.nama_lengkap)
                putExtra("JENIS_TRANSAKSI", item.jenis_transaksi)
                putExtra("JENIS_SAMPAH", item.jenis_sampah)
                putExtra("BERAT_KG", item.berat_kg ?: 0.0)
                putExtra("FOTO_BUKTI", item.foto_bukti)
                putExtra("NOMINAL", item.nominal_rp)
                putExtra("METODE", item.metode_penarikan)
                putExtra("TANGGAL", item.created_at)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<PersetujuanItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
