package com.example.tugas_pab.data.model

data class TransactionResponse(
    val message: String,
    val data: List<TransactionItem>
)

data class TransactionItem(
    val id: String,
    val user_id: String,
    val jenis_transaksi: String,
    val jenis_sampah: String?,
    val berat_kg: Double?,
    val nominal_rp: Double,
    val status: String,
    val metode_penarikan: String?,
    val rekening_tujuan: String?,
    val created_at: String
)
