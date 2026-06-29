package com.example.tugas_pab.ui.adapter

import com.example.tugas_pab.data.model.Nasabah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tugas_pab.databinding.ItemNasabahBinding
import java.text.NumberFormat
import java.util.Locale

class NasabahAdapter(
    private val onMoreClick: (Nasabah, android.view.View) -> Unit
) : ListAdapter<Nasabah, NasabahAdapter.NasabahViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NasabahViewHolder {
        val binding = ItemNasabahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NasabahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NasabahViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class NasabahViewHolder(private val binding: ItemNasabahBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nasabah: Nasabah) {
            binding.tvNama.text = nasabah.namaLengkap
            
            // Generate initials for avatar
            val initials = nasabah.namaLengkap.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
            binding.tvInitials.text = if (initials.isNotEmpty()) initials else "?"

            // Format to Rupiah
            val saldo = nasabah.saldo ?: 0
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val saldoStr = formatter.format(saldo).replace(",00", "")
            binding.tvSaldo.text = "Saldo: $saldoStr"

            binding.ivMore.setOnClickListener { view ->
                onMoreClick(nasabah, view)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Nasabah>() {
        override fun areItemsTheSame(oldItem: Nasabah, newItem: Nasabah): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Nasabah, newItem: Nasabah): Boolean {
            return oldItem == newItem
        }
    }
}
