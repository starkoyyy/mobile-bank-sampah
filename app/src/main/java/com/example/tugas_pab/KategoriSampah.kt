package com.example.tugas_pab

import com.google.gson.annotations.SerializedName

data class KategoriSampah(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("harga")
    val harga: Int,
    @SerializedName("icon")
    val icon: String? = null
)
