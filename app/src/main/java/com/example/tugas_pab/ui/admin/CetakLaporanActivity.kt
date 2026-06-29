package com.example.tugas_pab.ui.admin

import com.example.tugas_pab.R
import com.example.tugas_pab.data.model.LaporanTransaksiResponse
import com.example.tugas_pab.data.model.LaporanTransaksi
import com.example.tugas_pab.data.model.LaporanNasabahResponse
import com.example.tugas_pab.data.model.LaporanNasabah

import com.example.tugas_pab.data.model.Nasabah
import com.example.tugas_pab.network.RetrofitClient

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CetakLaporanActivity : AppCompatActivity() {

    private lateinit var actvJenisLaporan: AutoCompleteTextView
    private lateinit var etStartDate: TextInputEditText
    private lateinit var etEndDate: TextInputEditText

    private var startDateRaw: String? = null
    private var endDateRaw: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cetak_laporan)

        actvJenisLaporan = findViewById(R.id.actv_jenis_laporan)
        etStartDate = findViewById(R.id.et_start_date)
        etEndDate = findViewById(R.id.et_end_date)
        val btnPrintPdf = findViewById<Button>(R.id.btn_print_pdf)

        // Setup dropdown
        val jenisLaporanList = arrayOf("Laporan Transaksi", "Laporan Nasabah")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jenisLaporanList)
        actvJenisLaporan.setAdapter(adapter)

        // Setup date pickers
        etStartDate.setOnClickListener { showDatePicker { date, display -> 
            startDateRaw = date
            etStartDate.setText(display)
        }}
        etEndDate.setOnClickListener { showDatePicker { date, display -> 
            endDateRaw = date
            etEndDate.setText(display)
        }}

        btnPrintPdf.setOnClickListener {
            val jenis = actvJenisLaporan.text.toString()
            if (jenis.isEmpty()) {
                Toast.makeText(this, "Pilih jenis laporan terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            fetchAndGeneratePdf(jenis)
        }
    }

    private fun showDatePicker(onDateSelected: (String, String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formatRaw = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatDisplay = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                onDateSelected(formatRaw.format(selectedDate.time), formatDisplay.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun fetchAndGeneratePdf(jenis: String) {
        Toast.makeText(this, "Mengambil data...", Toast.LENGTH_SHORT).show()
        
        if (jenis == "Laporan Transaksi") {
            RetrofitClient.instance.getLaporanTransaksi(startDateRaw, endDateRaw).enqueue(object : Callback<LaporanTransaksiResponse> {
                override fun onResponse(call: Call<LaporanTransaksiResponse>, response: Response<LaporanTransaksiResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        generateTransaksiPdf(response.body()!!.data)
                    } else {
                        Toast.makeText(this@CetakLaporanActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LaporanTransaksiResponse>, t: Throwable) {
                    Toast.makeText(this@CetakLaporanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else if (jenis == "Laporan Nasabah") {
            RetrofitClient.instance.getLaporanNasabah(startDateRaw, endDateRaw).enqueue(object : Callback<LaporanNasabahResponse> {
                override fun onResponse(call: Call<LaporanNasabahResponse>, response: Response<LaporanNasabahResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        generateNasabahPdf(response.body()!!.data)
                    } else {
                        Toast.makeText(this@CetakLaporanActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LaporanNasabahResponse>, t: Throwable) {
                    Toast.makeText(this@CetakLaporanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun generateTransaksiPdf(data: List<LaporanTransaksi>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, paint, "LAPORAN TRANSAKSI BANK SAMPAH")

        var yPos = 120f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        // Table Header
        canvas.drawText("Tanggal", 50f, yPos, paint)
        canvas.drawText("Nasabah", 120f, yPos, paint)
        canvas.drawText("Jenis", 230f, yPos, paint)
        canvas.drawText("Berat", 300f, yPos, paint)
        canvas.drawText("Nominal", 350f, yPos, paint)
        canvas.drawText("Status", 450f, yPos, paint)
        
        yPos += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        
        for (item in data) {
            val dateStr = item.created_at.take(10)
            canvas.drawText(dateStr, 50f, yPos, paint)
            canvas.drawText(item.nama_nasabah.take(15), 120f, yPos, paint)
            canvas.drawText(item.jenis_transaksi, 230f, yPos, paint)
            canvas.drawText(item.berat_kg?.toString() ?: "-", 300f, yPos, paint)
            canvas.drawText(item.nominal_rp?.toString() ?: "-", 350f, yPos, paint)
            canvas.drawText(item.status, 450f, yPos, paint)
            yPos += 20f
            
            if (yPos > 750f) {
                // Simplified handling for single page overflow
                canvas.drawText("...lanjut di halaman berikutnya (oversize)", 50f, yPos, paint)
                break
            }
        }

        drawFooter(canvas, paint, yPos + 30f)
        document.finishPage(page)
        savePdfFile(document, "Laporan_Transaksi.pdf")
    }

    private fun generateNasabahPdf(data: List<LaporanNasabah>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        drawHeader(canvas, paint, "LAPORAN DATA NASABAH BANK SAMPAH")

        var yPos = 120f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        // Table Header
        canvas.drawText("Terdaftar", 50f, yPos, paint)
        canvas.drawText("Nama Nasabah", 150f, yPos, paint)
        canvas.drawText("Username", 270f, yPos, paint)
        canvas.drawText("Telepon", 360f, yPos, paint)
        canvas.drawText("Saldo Akhir", 450f, yPos, paint)
        
        yPos += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        
        for (item in data) {
            val dateStr = item.created_at.take(10)
            canvas.drawText(dateStr, 50f, yPos, paint)
            canvas.drawText(item.nama_lengkap.take(20), 150f, yPos, paint)
            canvas.drawText(item.username, 270f, yPos, paint)
            canvas.drawText(item.no_telepon ?: "-", 360f, yPos, paint)
            canvas.drawText(item.saldo.toString(), 450f, yPos, paint)
            yPos += 20f
            
            if (yPos > 750f) break
        }

        drawFooter(canvas, paint, yPos + 30f)
        document.finishPage(page)
        savePdfFile(document, "Laporan_Nasabah.pdf")
    }

    private fun drawHeader(canvas: Canvas, paint: Paint, title: String) {
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(title, 595f / 2, 50f, paint)
        
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val periode = if (startDateRaw != null && endDateRaw != null) {
            "Periode: ${startDateRaw} s/d ${endDateRaw}"
        } else {
            "Semua Waktu"
        }
        canvas.drawText(periode, 595f / 2, 70f, paint)
        
        paint.strokeWidth = 1f
        canvas.drawLine(50f, 90f, 545f, 90f, paint)
        paint.textAlign = Paint.Align.LEFT
    }
    
    private fun drawFooter(canvas: Canvas, paint: Paint, yPos: Float) {
        val y = if (yPos < 700f) 700f else yPos
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Mengetahui,", 500f, y, paint)
        canvas.drawText("Admin Bank Sampah", 500f, y + 60f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun savePdfFile(document: PdfDocument, fileName: String) {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
            Toast.makeText(this, "Berhasil! Tersimpan di folder Downloads", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
