package com.example.tugas_pab

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    // Registrasi
    @POST("api/register")
    fun register(@Body request: Map<String, String>): Call<Any>

    // Login
    @POST("api/login")
    fun login(@Body request: Map<String, String>): Call<Any>

    // Setor Sampah
    @Multipart
    @POST("api/setor")
    fun setorSampah(
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part("jenis_sampah") jenisSampah: okhttp3.RequestBody,
        @Part("berat_kg") beratKg: okhttp3.RequestBody,
        @Part fotoBukti: okhttp3.MultipartBody.Part?
    ): Call<Any>

    @POST("api/tarik")
    fun tarikSaldo(@Body request: TarikSaldoRequest): Call<okhttp3.ResponseBody>
    
    @POST("api/approve-transaction")
    fun approveTransaction(@Body request: ApproveTransactionRequest): Call<okhttp3.ResponseBody>
    
    @GET("api/riwayat/{userId}")
    fun getRiwayat(@Path("userId") userId: String): Call<TransactionResponse>

    @GET("api/user/{userId}")
    fun getUserProfile(@Path("userId") userId: String): Call<UserProfileResponse>
    
    @GET("api/admin/nasabah")
    fun getNasabah(): Call<NasabahResponse>

    @PUT("api/admin/nasabah/{id}")
    fun updateNasabah(@Path("id") id: String, @Body request: Map<String, String>): Call<Any>

    @DELETE("api/admin/nasabah/{id}")
    fun deleteNasabah(@Path("id") id: String): Call<Any>

    @GET("api/kategori")
    fun getKategori(): Call<KategoriResponse>

    @POST("api/kategori")
    fun tambahKategori(@Body kategori: KategoriSampah): Call<Any>

    @PUT("api/kategori/{id}")
    fun updateKategori(@Path("id") id: Int, @Body kategori: KategoriSampah): Call<Any>

    @DELETE("api/kategori/{id}")
    fun hapusKategori(@Path("id") id: Int): Call<Any>
    
    // Admin Dashboard
    @GET("api/admin/dashboard")
    fun getAdminDashboard(): Call<AdminDashboardResponse>

    // Laporan
    @GET("api/admin/laporan/transaksi")
    fun getLaporanTransaksi(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Call<LaporanTransaksiResponse>

    @GET("api/admin/laporan/nasabah")
    fun getLaporanNasabah(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Call<LaporanNasabahResponse>

    // Notifikasi
    @GET("api/notifikasi/{user_id}")
    fun getNotifikasi(@Path("user_id") userId: String): Call<NotifikasiResponse>
}

data class NasabahResponse(
    val message: String,
    val data: List<Nasabah>
)

data class UserProfileResponse(
    val data: UserProfile
)

data class UserProfile(
    val id: String,
    val username: String,
    val role: String,
    val nama_lengkap: String,
    val saldo: Double
)

data class KategoriResponse(
    val message: String,
    val data: List<KategoriSampah>
)

data class AdminDashboardResponse(
    val message: String,
    val data: AdminDashboardData
)

data class AdminDashboardData(
    val total_saldo: Double,
    val total_nasabah: Int,
    val total_sampah: Double,
    val persetujuan: List<PersetujuanItem>
)

data class PersetujuanItem(
    val id: String,
    val user_id: String,
    val nama_lengkap: String,
    val jenis_transaksi: String,
    val jenis_sampah: String?,
    val berat_kg: Double?,
    val foto_bukti: String?,
    val nominal_rp: Double,
    val metode_penarikan: String?,
    val created_at: String
)

data class TarikSaldoRequest(
    val user_id: String,
    val nominal_rp: Double,
    val metode_penarikan: String,
    val rekening_tujuan: String
)

data class TarikSaldoResponse(
    val message: String
)


data class ApproveTransactionRequest(
    val transaksi_id: String,
    val status: String
)
