package com.example.tugas_pab.data.model

import com.google.gson.annotations.SerializedName

data class Nasabah(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("nama_lengkap")
    val namaLengkap: String,
    @SerializedName("saldo")
    val saldo: Int? = 0,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("no_telepon")
    val noTelepon: String? = null,
    @SerializedName("alamat_lengkap")
    val alamatLengkap: String? = null
)
