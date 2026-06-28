package com.example.tugas_pab

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class TarikSaldoActivity : AppCompatActivity() {
    private var metodePenarikan = "Transfer"
    private var currentSaldo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarik_saldo)
        
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        // Fetch Real Saldo dari Backend (Jangan ambil dari SharedPreferences karena bisa out of sync)
        val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val tvSaldo = findViewById<TextView>(R.id.tv_saldo_tersedia_amount)
        var userId = sharedPreferences.getString("USER_ID", "") ?: ""
        if (userId.isEmpty()) {
            val userDataStr = sharedPreferences.getString("user_data", "")
            if (!userDataStr.isNullOrEmpty()) {
                try {
                    val jsonObject = JSONObject(userDataStr)
                    val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                    userId = userObj.optString("id", "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        if (userId.isNotEmpty()) {
            RetrofitClient.instance.getUserProfile(userId).enqueue(object : retrofit2.Callback<UserProfileResponse> {
                override fun onResponse(call: retrofit2.Call<UserProfileResponse>, response: retrofit2.Response<UserProfileResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        currentSaldo = response.body()!!.data.saldo.toInt()
                        val formatRp = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
                        tvSaldo.text = formatRp.format(currentSaldo).replace("Rp", "Rp ").substringBefore(",")
                    }
                }
                override fun onFailure(call: retrofit2.Call<UserProfileResponse>, t: Throwable) {
                    // fall back to 0
                }
            })
        }

        val chipTunai = findViewById<LinearLayout>(R.id.chip_tunai)
        val chipTransfer = findViewById<LinearLayout>(R.id.chip_transfer)
        val chipEwallet = findViewById<LinearLayout>(R.id.chip_ewallet)
        
        val tvTunai = findViewById<TextView>(R.id.tv_tunai)
        val tvTransfer = findViewById<TextView>(R.id.tv_transfer)
        val tvEwallet = findViewById<TextView>(R.id.tv_ewallet)
        
        val ivTunai = findViewById<ImageView>(R.id.iv_check_tunai)
        val ivTransfer = findViewById<ImageView>(R.id.iv_check_transfer)
        val ivEwallet = findViewById<ImageView>(R.id.iv_check_ewallet)

        val spinnerProvider = findViewById<Spinner>(R.id.spinner_provider)
        val tvLabelProvider = findViewById<TextView>(R.id.tv_label_provider)
        val tvLabelRekening = findViewById<TextView>(R.id.tv_label_rekening)
        val etRekening = findViewById<EditText>(R.id.et_rekening)

        val bankList = arrayOf("BCA", "BNI", "BRI", "Mandiri", "BSI", "CIMB Niaga")
        val ewalletList = arrayOf("GoPay", "OVO", "DANA", "ShopeePay", "LinkAja")

        fun updateUI() {
            // Reset all
            chipTunai.setBackgroundResource(R.drawable.bg_chip_unselected)
            chipTransfer.setBackgroundResource(R.drawable.bg_chip_unselected)
            chipEwallet.setBackgroundResource(R.drawable.bg_chip_unselected)
            
            tvTunai.setTextColor(android.graphics.Color.parseColor("#555555"))
            tvTransfer.setTextColor(android.graphics.Color.parseColor("#555555"))
            tvEwallet.setTextColor(android.graphics.Color.parseColor("#555555"))
            
            ivTunai.visibility = View.GONE
            ivTransfer.visibility = View.GONE
            ivEwallet.visibility = View.GONE

            if (metodePenarikan == "Tunai") {
                chipTunai.setBackgroundResource(R.drawable.bg_chip_selected)
                tvTunai.setTextColor(android.graphics.Color.WHITE)
                ivTunai.visibility = View.VISIBLE
                
                spinnerProvider.visibility = View.GONE
                tvLabelProvider.visibility = View.GONE
                tvLabelRekening.visibility = View.GONE
                etRekening.visibility = View.GONE
            } else if (metodePenarikan == "Transfer") {
                chipTransfer.setBackgroundResource(R.drawable.bg_chip_selected)
                tvTransfer.setTextColor(android.graphics.Color.WHITE)
                ivTransfer.visibility = View.VISIBLE
                
                spinnerProvider.visibility = View.VISIBLE
                tvLabelProvider.visibility = View.VISIBLE
                tvLabelProvider.text = "Pilih Bank"
                tvLabelRekening.visibility = View.VISIBLE
                tvLabelRekening.text = "Nomor Rekening Tujuan"
                etRekening.visibility = View.VISIBLE
                etRekening.hint = "Contoh: 1234567890"
                
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bankList)
                spinnerProvider.adapter = adapter
            } else if (metodePenarikan == "E-Wallet") {
                chipEwallet.setBackgroundResource(R.drawable.bg_chip_selected)
                tvEwallet.setTextColor(android.graphics.Color.WHITE)
                ivEwallet.visibility = View.VISIBLE
                
                spinnerProvider.visibility = View.VISIBLE
                tvLabelProvider.visibility = View.VISIBLE
                tvLabelProvider.text = "Pilih E-Wallet"
                tvLabelRekening.visibility = View.VISIBLE
                tvLabelRekening.text = "Nomor Telepon (E-Wallet)"
                etRekening.visibility = View.VISIBLE
                etRekening.hint = "Contoh: 08123456789"
                
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ewalletList)
                spinnerProvider.adapter = adapter
            }
        }

        chipTunai.setOnClickListener { metodePenarikan = "Tunai"; updateUI() }
        chipTransfer.setOnClickListener { metodePenarikan = "Transfer"; updateUI() }
        chipEwallet.setOnClickListener { metodePenarikan = "E-Wallet"; updateUI() }

        // Initial setup
        updateUI()

        findViewById<View>(R.id.btn_ajukan).setOnClickListener {
            val nominalStr = findViewById<EditText>(R.id.et_nominal).text.toString().trim()
            val rekeningInput = etRekening.text.toString().trim()

            if (nominalStr.isEmpty()) {
                Toast.makeText(this, "Harap isi nominal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cleanNominalStr = nominalStr.replace(".", "").replace(",", "")
            val nominalDouble = cleanNominalStr.toDoubleOrNull() ?: 0.0
            if (nominalDouble < 50000) {
                Toast.makeText(this, "Minimal penarikan adalah Rp 50.000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nominalDouble > currentSaldo) {
                Toast.makeText(this, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalRekening = if (metodePenarikan == "Tunai") {
                "Tunai"
            } else {
                if (rekeningInput.isEmpty()) {
                    Toast.makeText(this, "Harap isi nomor rekening/telepon", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val provider = spinnerProvider.selectedItem.toString()
                "$provider - $rekeningInput"
            }

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
                return@setOnClickListener
            }

            val request = TarikSaldoRequest(
                user_id = userId,
                nominal_rp = nominalDouble,
                metode_penarikan = metodePenarikan,
                rekening_tujuan = finalRekening
            )

            RetrofitClient.instance.tarikSaldo(request).enqueue(object : retrofit2.Callback<okhttp3.ResponseBody> {
                override fun onResponse(call: retrofit2.Call<okhttp3.ResponseBody>, response: retrofit2.Response<okhttp3.ResponseBody>) {
                    if (response.isSuccessful) {
                        android.app.AlertDialog.Builder(this@TarikSaldoActivity)
                            .setTitle("Penarikan Berhasil")
                            .setMessage("Pengajuan sedang menunggu verifikasi admin.")
                            .setPositiveButton("OK") { _, _ ->
                                finish()
                            }
                            .show()
                    } else {
                        val errorStr = response.errorBody()?.string()?.take(50) ?: "Unknown error"
                        Toast.makeText(this@TarikSaldoActivity, "Gagal: ${response.code()} - $errorStr", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<okhttp3.ResponseBody>, t: Throwable) {
                    Toast.makeText(this@TarikSaldoActivity, "Koneksi error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
