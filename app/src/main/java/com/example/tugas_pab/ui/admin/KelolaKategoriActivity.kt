package com.example.tugas_pab.ui.admin

import com.example.tugas_pab.viewmodel.UiState
import com.example.tugas_pab.viewmodel.UiState.*

import com.example.tugas_pab.R

import com.example.tugas_pab.ui.adapter.KategoriAdapter
import com.example.tugas_pab.data.model.KategoriSampah
import com.example.tugas_pab.viewmodel.AdminViewModel

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tugas_pab.databinding.ActivityKelolaKategoriBinding
import com.example.tugas_pab.databinding.DialogKategoriBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KelolaKategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaKategoriBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: KategoriAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()

        viewModel.fetchKategori()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = KategoriAdapter(
            onEditClick = { kategori -> showKategoriDialog(kategori) },
            onDeleteClick = { kategori -> showDeleteConfirmation(kategori) }
        )
        binding.rvKategori.layoutManager = LinearLayoutManager(this)
        binding.rvKategori.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showKategoriDialog(null)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.kategoriState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        binding.rvKategori.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.data.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvKategori.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvKategori.visibility = View.VISIBLE
                            adapter.submitList(state.data)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@KelolaKategoriActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showKategoriDialog(kategori: KategoriSampah?) {
        val dialogBinding = DialogKategoriBinding.inflate(layoutInflater)
        


        kategori?.let {
            dialogBinding.etNama.setText(it.nama)
            dialogBinding.etDeskripsi.setText(it.deskripsi)
            dialogBinding.etHarga.setText(it.harga.toString())
            

        }



        AlertDialog.Builder(this)
            .setTitle(if (kategori == null) "Tambah Kategori" else "Edit Kategori")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.etNama.text.toString()
                val deskripsi = dialogBinding.etDeskripsi.text.toString()
                val hargaStr = dialogBinding.etHarga.text.toString()

                if (nama.isNotEmpty() && deskripsi.isNotEmpty() && hargaStr.isNotEmpty()) {
                    val harga = hargaStr.toIntOrNull() ?: 0
                    if (kategori == null) {
                        viewModel.tambahKategori(nama, deskripsi, harga, null)
                    } else {
                        kategori.id?.let { id ->
                            viewModel.updateKategori(id, nama, deskripsi, harga, null)
                        }
                    }
                } else {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmation(kategori: KategoriSampah) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori ${kategori.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                kategori.id?.let {
                    viewModel.hapusKategori(it)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
