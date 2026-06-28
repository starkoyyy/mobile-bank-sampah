package com.example.tugas_pab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tugas_pab.databinding.ItemKategoriSampahBinding
import java.text.NumberFormat
import java.util.Locale

class KategoriAdapter(
    private val onEditClick: (KategoriSampah) -> Unit,
    private val onDeleteClick: (KategoriSampah) -> Unit
) : ListAdapter<KategoriSampah, KategoriAdapter.KategoriViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val binding = ItemKategoriSampahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KategoriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class KategoriViewHolder(private val binding: ItemKategoriSampahBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(kategori: KategoriSampah) {
            binding.tvNama.text = kategori.nama
            binding.tvDeskripsi.text = kategori.deskripsi
            
            // Format to Rupiah
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val hargaStr = formatter.format(kategori.harga).replace(",00", "")
            binding.tvHarga.text = "$hargaStr\n/ Kg"

            binding.ivEdit.setOnClickListener {
                onEditClick(kategori)
            }
            binding.ivDelete.setOnClickListener {
                onDeleteClick(kategori)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<KategoriSampah>() {
        override fun areItemsTheSame(oldItem: KategoriSampah, newItem: KategoriSampah): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: KategoriSampah, newItem: KategoriSampah): Boolean {
            return oldItem == newItem
        }
    }
}
