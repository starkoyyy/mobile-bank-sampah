package com.example.tugas_pab.data.model

data class NotifikasiResponse(
    val data: List<NotifikasiItem>
)

data class NotifikasiItem(
    val id: String,
    val jenis_transaksi: String,
    val nominal_rp: Double?,
    val status: String,
    val created_at: String
)
