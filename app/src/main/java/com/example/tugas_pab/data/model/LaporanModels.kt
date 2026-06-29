package com.example.tugas_pab.data.model

data class LaporanTransaksiResponse(
    val data: List<LaporanTransaksi>
)

data class LaporanTransaksi(
    val id: String,
    val nama_nasabah: String,
    val jenis_transaksi: String,
    val jenis_sampah: String?,
    val berat_kg: Double?,
    val nominal_rp: Double?,
    val status: String,
    val created_at: String
)

data class LaporanNasabahResponse(
    val data: List<LaporanNasabah>
)

data class LaporanNasabah(
    val id: String,
    val username: String,
    val nama_lengkap: String,
    val email: String?,
    val no_telepon: String?,
    val alamat_lengkap: String?,
    val saldo: Double,
    val created_at: String
)
