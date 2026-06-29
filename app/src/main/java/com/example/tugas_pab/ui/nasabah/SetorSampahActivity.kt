package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.R
import com.example.tugas_pab.network.KategoriResponse

import com.example.tugas_pab.network.RetrofitClient

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText

class SetorSampahActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val ivPreview = findViewById<ImageView>(R.id.iv_preview_foto)
            val llPlaceholder = findViewById<View>(R.id.ll_upload_placeholder)
            
            ivPreview.setImageURI(it)
            ivPreview.visibility = View.VISIBLE
            llPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setor_sampah)
        
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val actvJenisSampah = findViewById<AutoCompleteTextView>(R.id.actv_jenis_sampah)
        
        // Fetch kategori sampah dari backend
        RetrofitClient.instance.getKategori().enqueue(object : retrofit2.Callback<KategoriResponse> {
            override fun onResponse(call: retrofit2.Call<KategoriResponse>, response: retrofit2.Response<KategoriResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val kategoriList = response.body()!!.data
                    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
                    val namaKategoriList = kategoriList.map { 
                        val hargaRp = formatter.format(it.harga).replace(",00", "").replace("Rp", "Rp ")
                        "${it.nama} - $hargaRp/Kg" 
                    }
                    val adapter = ArrayAdapter(this@SetorSampahActivity, android.R.layout.simple_dropdown_item_1line, namaKategoriList)
                    actvJenisSampah.setAdapter(adapter)
                    
                    actvJenisSampah.setOnClickListener { actvJenisSampah.showDropDown() }
                    actvJenisSampah.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) actvJenisSampah.showDropDown() }
                }
            }

            override fun onFailure(call: retrofit2.Call<KategoriResponse>, t: Throwable) {
                // Fallback local list jika gagal
                val fallbackList = arrayOf("Plastik - Rp 2.000/Kg", "Kertas - Rp 1.500/Kg", "Kardus - Rp 2.500/Kg", "Logam - Rp 4.000/Kg", "Kaca - Rp 1.000/Kg")
                val adapter = ArrayAdapter(this@SetorSampahActivity, android.R.layout.simple_dropdown_item_1line, fallbackList)
                actvJenisSampah.setAdapter(adapter)
                
                actvJenisSampah.setOnClickListener { actvJenisSampah.showDropDown() }
                actvJenisSampah.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) actvJenisSampah.showDropDown() }
            }
        })

        // Setup Photo Picker
        findViewById<View>(R.id.ll_upload_foto).setOnClickListener {
            getContent.launch("image/*")
        }

        val btnProses = findViewById<View>(R.id.btn_proses_setoran)
        btnProses.setOnClickListener {
            btnProses.isEnabled = false
            val jenisSampahRaw = actvJenisSampah.text.toString().trim()
            val jenisSampah = jenisSampahRaw.substringBefore(" - Rp").trim()
            val beratSampah = findViewById<TextInputEditText>(R.id.et_berat_sampah).text.toString().trim()

            if (jenisSampah.isEmpty() || beratSampah.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                btnProses.isEnabled = true
                return@setOnClickListener
            }
            
            if (selectedImageUri == null) {
                Toast.makeText(this, "Harap unggah foto bukti", Toast.LENGTH_SHORT).show()
                btnProses.isEnabled = true
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            var userId = sharedPreferences.getString("USER_ID", "") ?: ""
            
            if (userId.isEmpty()) {
                val userDataString = sharedPreferences.getString("user_data", "")
                if (!userDataString.isNullOrEmpty()) {
                    try {
                        val jsonObject = org.json.JSONObject(userDataString)
                        val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                        if (userObj.has("id")) {
                            userId = userObj.optString("id", "")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (userId.isEmpty() || userId == "-1") {
                Toast.makeText(this, "Sesi login tidak valid (ID tidak ditemukan), harap relogin.", Toast.LENGTH_LONG).show()
                btnProses.isEnabled = true
                return@setOnClickListener
            }

            Toast.makeText(this, "Sedang memproses...", Toast.LENGTH_SHORT).show()

            // Create RequestBody
            val userIdBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), userId)
            val jenisSampahBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), jenisSampah)
            val beratKgBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), (beratSampah.toDoubleOrNull() ?: 0.0).toString())
            
            // Create MultipartBody.Part for file
            var fotoPart: okhttp3.MultipartBody.Part? = null
            try {
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                if (inputStream != null) {
                    val tempFile = java.io.File.createTempFile("upload", ".jpg", cacheDir)
                    val outputStream = java.io.FileOutputStream(tempFile)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    
                    val reqFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), tempFile)
                    fotoPart = okhttp3.MultipartBody.Part.createFormData("foto_bukti", tempFile.name, reqFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            RetrofitClient.instance.setorSampah(userIdBody, jenisSampahBody, beratKgBody, fotoPart).enqueue(object : retrofit2.Callback<Any> {
                override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SetorSampahActivity, "Setoran berhasil dicatat", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        android.app.AlertDialog.Builder(this@SetorSampahActivity)
                            .setTitle("Gagal Setor")
                            .setMessage("Kode: ${response.code()}\nError: $errorBody")
                            .setPositiveButton("Tutup", null)
                            .show()
                        btnProses.isEnabled = true
                    }
                }

                override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                    Toast.makeText(this@SetorSampahActivity, "Koneksi error: ${t.message}", Toast.LENGTH_SHORT).show()
                    btnProses.isEnabled = true
                }
            })
        }
    }
}
