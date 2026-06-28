package com.example.tugas_pab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _kategoriState = MutableStateFlow<UiState<List<KategoriSampah>>>(UiState.Loading)
    val kategoriState: StateFlow<UiState<List<KategoriSampah>>> = _kategoriState.asStateFlow()

    private val _nasabahState = MutableStateFlow<UiState<List<Nasabah>>>(UiState.Loading)
    val nasabahState: StateFlow<UiState<List<Nasabah>>> = _nasabahState.asStateFlow()

    fun fetchKategori() {
        viewModelScope.launch {
            _kategoriState.value = UiState.Loading
            try {
                val data = repository.getKategoriSampah()
                _kategoriState.value = UiState.Success(data)
            } catch (e: Exception) {
                _kategoriState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun tambahKategori(nama: String, deskripsi: String, harga: Int) {
        viewModelScope.launch {
            try {
                val newKategori = KategoriSampah(nama = nama, deskripsi = deskripsi, harga = harga)
                repository.tambahKategori(newKategori)
                fetchKategori() // Refresh data
            } catch (e: Exception) {
                _kategoriState.value = UiState.Error(e.message ?: "Gagal menambah kategori")
            }
        }
    }

    fun updateKategori(id: Int, nama: String, deskripsi: String, harga: Int) {
        viewModelScope.launch {
            try {
                val kategori = KategoriSampah(id = id, nama = nama, deskripsi = deskripsi, harga = harga)
                repository.updateKategori(id, kategori)
                fetchKategori() // Refresh data
            } catch (e: Exception) {
                _kategoriState.value = UiState.Error(e.message ?: "Gagal update kategori")
            }
        }
    }

    fun hapusKategori(id: Int) {
        viewModelScope.launch {
            try {
                repository.hapusKategori(id)
                fetchKategori() // Refresh data
            } catch (e: Exception) {
                _kategoriState.value = UiState.Error(e.message ?: "Gagal menghapus kategori")
            }
        }
    }

    fun fetchNasabah() {
        viewModelScope.launch {
            _nasabahState.value = UiState.Loading
            try {
                val data = repository.getNasabah()
                _nasabahState.value = UiState.Success(data)
            } catch (e: Exception) {
                _nasabahState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
