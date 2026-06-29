package com.example.tugas_pab.data.repository

import com.example.tugas_pab.data.model.KategoriSampah
import com.example.tugas_pab.data.model.Nasabah
import com.example.tugas_pab.network.RetrofitClient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository {

    // -- Kategori Sampah --

    suspend fun getKategoriSampah(): List<KategoriSampah> = withContext(Dispatchers.IO) {
        val response = RetrofitClient.instance.getKategori().execute()
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception("Gagal mengambil kategori")
        }
    }

    suspend fun tambahKategori(kategori: KategoriSampah) = withContext(Dispatchers.IO) {
        val response = RetrofitClient.instance.tambahKategori(kategori).execute()
        if (!response.isSuccessful) {
            throw Exception("Gagal menambah kategori")
        }
    }

    suspend fun updateKategori(id: Int, kategori: KategoriSampah) = withContext(Dispatchers.IO) {
        val response = RetrofitClient.instance.updateKategori(id, kategori).execute()
        if (!response.isSuccessful) {
            throw Exception("Gagal update kategori")
        }
    }

    suspend fun hapusKategori(id: Int) = withContext(Dispatchers.IO) {
        val response = RetrofitClient.instance.hapusKategori(id).execute()
        if (!response.isSuccessful) {
            throw Exception("Gagal menghapus kategori")
        }
    }

    // -- Nasabah --

    suspend fun getNasabah(): List<Nasabah> = withContext(Dispatchers.IO) {
        val response = RetrofitClient.instance.getNasabah().execute()
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception("Gagal mengambil daftar nasabah")
        }
    }
}
