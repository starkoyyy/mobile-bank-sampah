package com.example.tugas_pab

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CetakLaporanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cetak_laporan)

        val btnPrintPdf = findViewById<Button>(R.id.btn_print_pdf)

        btnPrintPdf.setOnClickListener {
            generateFormalPdfReport()
        }
    }

    /**
     * LOGIKA SISTEM PENCETAKAN PDF
     * 
     * Fungsi ini bertugas untuk men-generate dokumen PDF.
     * Sesuai standar, layout dokumen cetak (PDF) DIPISAHKAN sepenuhnya dari layout antarmuka (UI).
     * Dokumen akan digambar menggunakan library khusus (seperti PdfDocument bawaan Android atau iText)
     * untuk membentuk margin, kop surat, tabel rekapitulasi, dan tanda tangan sesuai standar baku instansi.
     */
    private fun generateFormalPdfReport() {
        // TODO: Ambil data dari dropdown Jenis Laporan dan Date Pickers
        
        // TODO: Inisialisasi dokumen PDF (misal: PdfDocument)
        // val document = PdfDocument()
        // val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard size
        // val page = document.startPage(pageInfo)
        // val canvas = page.canvas

        // TODO: Gambar Kop Surat (Logo, Nama Instansi, Alamat)
        // TODO: Gambar Judul Laporan & Rentang Waktu
        // TODO: Gambar Tabel Data Transaksi/Nasabah (Terpisah dari UI RecyclerView)
        // TODO: Gambar Kolom Tanda Tangan Mengetahui/Menyetujui di bagian bawah

        // document.finishPage(page)
        
        // TODO: Simpan file PDF ke penyimpanan lokal perangkat (Downloads/Documents)
        
        // Simulasi bahwa laporan berhasil dibuat
        Toast.makeText(this, "Laporan PDF sedang diunduh...", Toast.LENGTH_LONG).show()
    }
}
