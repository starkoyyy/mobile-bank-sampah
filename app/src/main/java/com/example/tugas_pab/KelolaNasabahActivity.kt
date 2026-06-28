package com.example.tugas_pab

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugas_pab.databinding.ActivityKelolaNasabahBinding
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KelolaNasabahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaNasabahBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapter: NasabahAdapter
    private var allNasabah: List<Nasabah> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaNasabahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        setupBottomNavigation()
        observeViewModel()

        viewModel.fetchNasabah()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = NasabahAdapter(
            onMoreClick = { nasabah, view ->
                showPopupMenu(view, nasabah)
            }
        )
        binding.rvNasabah.layoutManager = LinearLayoutManager(this)
        binding.rvNasabah.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        if (query.isEmpty()) {
            adapter.submitList(allNasabah)
        } else {
            val filteredList = allNasabah.filter {
                it.namaLengkap.contains(query, ignoreCase = true)
            }
            adapter.submitList(filteredList)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.nasabahState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        binding.rvNasabah.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        allNasabah = state.data
                        if (allNasabah.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvNasabah.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvNasabah.visibility = View.VISIBLE
                            adapter.submitList(allNasabah)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@KelolaNasabahActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationAdmin.selectedItemId = R.id.navigation_more
        binding.bottomNavigationAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_admin_home -> {
                    startActivity(Intent(this, DashboardAdminActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    true
                }
                R.id.navigation_report -> {
                    Toast.makeText(this, "Menu Laporan", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_submission -> true
                R.id.navigation_more -> {
                    startActivity(Intent(this, MenuAdminActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    true
                }
                else -> false
            }
        }
    }
    private fun setupFab() {
        binding.fabAddNasabah.setOnClickListener {
            showNasabahFormDialog(null)
        }
    }

    private fun showPopupMenu(view: View, nasabah: Nasabah) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(0, 1, 0, "Edit")
        popupMenu.menu.add(0, 2, 0, "Hapus")
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showNasabahFormDialog(nasabah)
                    true
                }
                2 -> {
                    showDeleteConfirmation(nasabah)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showNasabahFormDialog(nasabah: Nasabah?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_form_nasabah, null)
        val etUsername = dialogView.findViewById<EditText>(R.id.et_username)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)
        val etNamaLengkap = dialogView.findViewById<EditText>(R.id.et_nama_lengkap)
        val etNoHp = dialogView.findViewById<EditText>(R.id.et_no_hp)
        val etAlamat = dialogView.findViewById<EditText>(R.id.et_alamat)
        val btnBatal = dialogView.findViewById<Button>(R.id.btn_batal)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btn_simpan)

        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_title)

        if (nasabah != null) {
            tvTitle.text = "Edit Nasabah"
            etUsername.setText(nasabah.username)
            etNamaLengkap.setText(nasabah.namaLengkap)
            etNoHp.setText(nasabah.noTelepon)
            etAlamat.setText(nasabah.alamatLengkap)
            etPassword.hint = "Kosongkan jika tidak ingin ganti"
        } else {
            tvTitle.text = "Tambah Nasabah"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        // Atur background dialog agar transparan sehingga rounded corners terlihat
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val req = mapOf(
                "username" to etUsername.text.toString(),
                "password" to etPassword.text.toString(),
                "nama_lengkap" to etNamaLengkap.text.toString(),
                "no_hp" to etNoHp.text.toString(),
                "alamat" to etAlamat.text.toString()
            )
            
            if (nasabah == null) {
                // Tambah Nasabah
                RetrofitClient.instance.register(req).enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@KelolaNasabahActivity, "Berhasil menambah nasabah", Toast.LENGTH_SHORT).show()
                            viewModel.fetchNasabah()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this@KelolaNasabahActivity, "Gagal menambah nasabah", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(this@KelolaNasabahActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // Edit Nasabah
                nasabah.id?.let { id ->
                    RetrofitClient.instance.updateNasabah(id, req).enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@KelolaNasabahActivity, "Berhasil mengupdate nasabah", Toast.LENGTH_SHORT).show()
                                viewModel.fetchNasabah()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this@KelolaNasabahActivity, "Gagal mengupdate nasabah", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(this@KelolaNasabahActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(nasabah: Nasabah) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Nasabah")
            .setMessage("Yakin ingin menghapus ${nasabah.namaLengkap}?\nSeluruh riwayat transaksinya akan ikut terhapus.")
            .setPositiveButton("Hapus") { _, _ ->
                nasabah.id?.let { id ->
                    RetrofitClient.instance.deleteNasabah(id).enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@KelolaNasabahActivity, "Berhasil menghapus", Toast.LENGTH_SHORT).show()
                                viewModel.fetchNasabah()
                            } else {
                                Toast.makeText(this@KelolaNasabahActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(this@KelolaNasabahActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
