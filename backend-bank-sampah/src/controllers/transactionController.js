const supabase = require('../config/supabase');

exports.setorSampah = async (req, res) => {
    try {
        const { user_id, jenis_sampah, berat_kg } = req.body;
        const foto_bukti = req.file ? req.file.filename : null;

        if (!user_id || !jenis_sampah || !berat_kg) {
            return res.status(400).json({ error: 'Data setoran tidak lengkap!' });
        }

        const { data: kategoriList, error: katError } = await supabase
            .from('kategori_sampah')
            .select('harga')
            .ilike('nama', jenis_sampah)
            .limit(1);

        let harga_per_kg = 0;
        if (kategoriList && kategoriList.length > 0 && kategoriList[0].harga) {
            harga_per_kg = kategoriList[0].harga;
        }

        if (harga_per_kg === 0) {
            return res.status(400).json({ error: 'Kategori sampah tidak ditemukan atau harga tidak valid!' });
        }

        const calculated_nominal = parseFloat(berat_kg) * harga_per_kg;

        let payload = {
            user_id,
            jenis_transaksi: 'setor',
            jenis_sampah,
            berat_kg: parseFloat(berat_kg),
            nominal_rp: calculated_nominal,
            status: 'menunggu'
        };

        if (foto_bukti) {
            payload.foto_bukti = foto_bukti;
        }

        const { error } = await supabase
            .from('transactions')
            .insert([payload]);

        if (error) {
            if (error.code === 'PGRST204') {
                return res.status(500).json({ error: 'Database belum memiliki kolom foto_bukti. Harap tambahkan kolom foto_bukti (tipe text) di Supabase.' });
            }
            throw error;
        }

        res.status(201).json({ message: 'Setoran berhasil dicatat dan menunggu persetujuan admin.' });

    } catch (error) {
        console.error('Error saat setor:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.tarikSaldo = async (req, res) => {
    try {
        const { user_id, nominal_rp, metode_penarikan, rekening_tujuan } = req.body;

        if (!user_id || !nominal_rp || !metode_penarikan || !rekening_tujuan) {
            return res.status(400).json({ error: 'Data penarikan tidak lengkap!' });
        }

        const { data: user, error: userError } = await supabase
            .from('users')
            .select('saldo')
            .eq('id', user_id)
            .single();

        if (userError || user.saldo < nominal_rp) {
            return res.status(400).json({ error: 'Saldo tidak mencukupi atau user tidak ditemukan.' });
        }

        const { error: insertError } = await supabase
            .from('transactions')
            .insert([{
                user_id,
                jenis_transaksi: 'tarik',
                nominal_rp: parseFloat(nominal_rp),
                metode_penarikan,
                rekening_tujuan,
                status: 'menunggu'
            }]);

        if (insertError) throw insertError;

        res.status(201).json({ message: 'Pengajuan penarikan berhasil dikirim dan menunggu verifikasi admin!' });

    } catch (error) {
        console.error('Error saat tarik saldo:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.approveTransaction = async (req, res) => {
    try {
        const { transaksi_id, status } = req.body;

        if (!transaksi_id || !status) {
            return res.status(400).json({ error: 'Data tidak lengkap!' });
        }

        // Fetch transaction to know the type and nominal
        const { data: trx, error: trxError } = await supabase
            .from('transactions')
            .select('*')
            .eq('id', transaksi_id)
            .single();

        if (trxError || !trx) {
            return res.status(404).json({ error: 'Transaksi tidak ditemukan!' });
        }

        if (trx.status !== 'menunggu') {
            return res.status(400).json({ error: 'Transaksi ini sudah diproses sebelumnya.' });
        }

        const { error: updateError } = await supabase
            .from('transactions')
            .update({ status: status })
            .eq('id', transaksi_id);

        if (updateError) throw updateError;

        if (status === 'disetujui') {
            const { data: user } = await supabase
                .from('users')
                .select('saldo')
                .eq('id', trx.user_id)
                .single();

            let newSaldo = user ? user.saldo : 0;
            if (trx.jenis_transaksi === 'setor') {
                newSaldo += trx.nominal_rp;
            } else if (trx.jenis_transaksi === 'tarik') {
                newSaldo -= trx.nominal_rp;
            }

            await supabase
                .from('users')
                .update({ saldo: newSaldo })
                .eq('id', trx.user_id);
        }

        res.status(200).json({ message: `Transaksi telah ${status}!` });

    } catch (error) {
        console.error('Error saat approve:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getRiwayat = async (req, res) => {
    try {
        const { user_id } = req.params;

        if (!user_id) {
            return res.status(400).json({ error: 'User ID tidak ditemukan!' });
        }

        const { data, error } = await supabase
            .from('transactions')
            .select('*')
            .eq('user_id', user_id)
            .order('created_at', { ascending: false });

        if (error) throw error;

        // Optionally, prepend the server URL to foto_bukti so Android can load it easily
        // But since we might not know the exact IP dynamically without req.protocol, 
        // the Android app will just append the BASE_URL itself.

        res.status(200).json({
            message: 'Berhasil mengambil riwayat transaksi',
            data: data
        });

    } catch (error) {
        console.error('Error saat mengambil riwayat:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getNotifikasiUser = async (req, res) => {
    try {
        const { user_id } = req.params;

        const { data, error } = await supabase
            .from('transactions')
            .select('*')
            .eq('user_id', user_id)
            .neq('status', 'menunggu')
            .order('created_at', { ascending: false });

        if (error) throw error;

        res.status(200).json({ data });
    } catch (error) {
        console.error('Error saat mengambil notifikasi:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};
