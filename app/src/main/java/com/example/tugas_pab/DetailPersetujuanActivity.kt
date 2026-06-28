package com.example.tugas_pab

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPersetujuanActivity : AppCompatActivity() {

    private var transaksiId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_persetujuan)
        
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        transaksiId = intent.getStringExtra("TRANSAKSI_ID") ?: ""
        val namaLengkap = intent.getStringExtra("NAMA_LENGKAP") ?: "Unknown"
        val jenisTransaksi = intent.getStringExtra("JENIS_TRANSAKSI") ?: "tarik"
        
        if (transaksiId.isEmpty()) {
            Toast.makeText(this, "Tidak ada data transaksi.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set UI values
        val tvTitle = findViewById<TextView>(R.id.tv_detail_title)
        val tvName = findViewById<TextView>(R.id.tv_detail_name)
        val tvDate = findViewById<TextView>(R.id.tv_detail_date)
        val tvNominal = findViewById<TextView>(R.id.tv_detail_nominal)
        
        val llMetode = findViewById<View>(R.id.ll_metode)
        val tvMetode = findViewById<TextView>(R.id.tv_detail_metode)
        val llRekening = findViewById<View>(R.id.ll_rekening)
        
        val llSetorInfo = findViewById<View>(R.id.ll_setor_info)
        val tvJenisSampah = findViewById<TextView>(R.id.tv_jenis_sampah)
        val tvBeratSampah = findViewById<TextView>(R.id.tv_berat_sampah)
        val ivFotoBukti = findViewById<ImageView>(R.id.iv_foto_bukti)
        
        tvName.text = namaLengkap
        
        val tanggalStr = intent.getStringExtra("TANGGAL") ?: ""
        if (tanggalStr.isNotEmpty()) {
            try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                val dateObj = parser.parse(tanggalStr)
                if (dateObj != null) {
                    val formatterDate = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id", "ID"))
                    tvDate.text = "Pengajuan: ${formatterDate.format(dateObj)}"
                }
            } catch (e: Exception) {
                tvDate.text = "Pengajuan: $tanggalStr"
            }
        }
        
        val nominal = intent.getDoubleExtra("NOMINAL", 0.0)
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        val nominalRp = formatter.format(nominal).replace(",00", "").replace("Rp", "Rp ")
        tvNominal.text = nominalRp
        
        if (jenisTransaksi == "setor") {
            tvTitle.text = "Persetujuan Setor"
            llMetode.visibility = View.GONE
            llRekening.visibility = View.GONE
            llSetorInfo.visibility = View.VISIBLE
            
            val jenisSampah = intent.getStringExtra("JENIS_SAMPAH") ?: "-"
            val beratKg = intent.getDoubleExtra("BERAT_KG", 0.0)
            val fotoBukti = intent.getStringExtra("FOTO_BUKTI")
            
            tvJenisSampah.text = jenisSampah
            tvBeratSampah.text = "$beratKg Kg"
            
            if (!fotoBukti.isNullOrEmpty()) {
                val baseUrl = RetrofitClient.instance.javaClass.declaredFields.find { it.name == "BASE_URL" }?.let {
                    it.isAccessible = true
                    it.get(RetrofitClient) as String
                } ?: "http://192.168.0.2:3000/"
                
                val imageUrl = baseUrl + fotoBukti.replace("\\", "/")
                Glide.with(this).load(imageUrl).into(ivFotoBukti)
            } else {
                ivFotoBukti.visibility = View.GONE
            }
            
        } else {
            tvTitle.text = "Persetujuan Tarik"
            llMetode.visibility = View.VISIBLE
            llSetorInfo.visibility = View.GONE
            
            val metode = intent.getStringExtra("METODE") ?: "N/A"
            tvMetode.text = metode
            
            if (metode.equals("Tunai", ignoreCase = true)) {
                llRekening.visibility = View.GONE
            } else {
                llRekening.visibility = View.VISIBLE
            }
        }
        
        findViewById<View>(R.id.btn_setujui).setOnClickListener {
            processApproval("disetujui")
        }

        findViewById<View>(R.id.btn_tolak).setOnClickListener {
            processApproval("ditolak")
        }
    }

    private fun processApproval(status: String) {
        val btnSetujui = findViewById<View>(R.id.btn_setujui)
        val btnTolak = findViewById<View>(R.id.btn_tolak)
        
        btnSetujui.isEnabled = false
        btnTolak.isEnabled = false
        
        Toast.makeText(this, "Sedang memproses...", Toast.LENGTH_SHORT).show()
        
        try {
            val request = ApproveTransactionRequest(
                transaksi_id = transaksiId,
                status = status
            )

            RetrofitClient.instance.approveTransaction(request).enqueue(object : Callback<okhttp3.ResponseBody> {
                override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetailPersetujuanActivity, "Transaksi $status", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@DetailPersetujuanActivity, "Gagal memproses", Toast.LENGTH_SHORT).show()
                        btnSetujui.isEnabled = true
                        btnTolak.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                    Toast.makeText(this@DetailPersetujuanActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_LONG).show()
                    btnSetujui.isEnabled = true
                    btnTolak.isEnabled = true
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Terjadi kesalahan internal", Toast.LENGTH_SHORT).show()
            btnSetujui.isEnabled = true
            btnTolak.isEnabled = true
        }
    }
}
