const supabase = require('../config/supabase');

exports.getAllKategori = async (req, res) => {
    try {
        const { data, error } = await supabase
            .from('kategori_sampah')
            .select('*')
            .order('id', { ascending: true });

        if (error) throw error;

        res.status(200).json({
            message: 'Berhasil mengambil data kategori',
            data: data
        });
    } catch (error) {
        console.error('Error get kategori:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.tambahKategori = async (req, res) => {
    try {
        const { nama, deskripsi, harga, icon } = req.body;
        if (!nama || !harga) {
            return res.status(400).json({ error: 'Nama dan harga wajib diisi!' });
        }

        const { error } = await supabase
            .from('kategori_sampah')
            .insert([{ nama, deskripsi, harga, icon }]);

        if (error) throw error;

        res.status(201).json({ message: 'Kategori berhasil ditambahkan' });
    } catch (error) {
        console.error('Error tambah kategori:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.updateKategori = async (req, res) => {
    try {
        const { id } = req.params;
        const { nama, deskripsi, harga, icon } = req.body;

        if (!nama || !harga) {
            return res.status(400).json({ error: 'Nama dan harga wajib diisi!' });
        }

        const { error } = await supabase
            .from('kategori_sampah')
            .update({ nama, deskripsi, harga, icon })
            .eq('id', id);

        if (error) throw error;

        res.status(200).json({ message: 'Kategori berhasil diupdate' });
    } catch (error) {
        console.error('Error update kategori:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.hapusKategori = async (req, res) => {
    try {
        const { id } = req.params;
        const { error } = await supabase
            .from('kategori_sampah')
            .delete()
            .eq('id', id);

        if (error) throw error;

        res.status(200).json({ message: 'Kategori berhasil dihapus' });
    } catch (error) {
        console.error('Error hapus kategori:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};
