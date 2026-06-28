const express = require('express');
const router = express.Router();
const upload = require('../middlewares/upload');

const userController = require('../controllers/userController');
const transactionController = require('../controllers/transactionController');
const adminController = require('../controllers/adminController');
const kategoriController = require('../controllers/kategoriController');

// User Routes
router.post('/register', userController.register);
router.post('/login', userController.login);
router.get('/user/:id', userController.getUserProfile);
router.get('/admin/nasabah', userController.getNasabah);
router.put('/admin/nasabah/:id', userController.updateNasabah);
router.delete('/admin/nasabah/:id', userController.deleteNasabah);

// Transaction Routes
router.post('/setor', upload.single('foto_bukti'), transactionController.setorSampah);
router.post('/tarik', transactionController.tarikSaldo);
router.post('/approve-transaction', transactionController.approveTransaction);
router.get('/riwayat/:user_id', transactionController.getRiwayat);
router.get('/notifikasi/:user_id', transactionController.getNotifikasiUser);

// Admin Dashboard Route
router.get('/admin/dashboard', adminController.getDashboard);

// Kategori Routes
router.get('/kategori', kategoriController.getAllKategori);
router.post('/kategori', kategoriController.tambahKategori);
router.put('/kategori/:id', kategoriController.updateKategori);
router.delete('/kategori/:id', kategoriController.hapusKategori);

// Laporan Routes
router.get('/admin/laporan/transaksi', adminController.getLaporanTransaksi);
router.get('/admin/laporan/nasabah', adminController.getLaporanNasabah);

module.exports = router;
