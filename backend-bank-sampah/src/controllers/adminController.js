const supabase = require('../config/supabase');

exports.getDashboard = async (req, res) => {
    try {
        // 1. Total Saldo (sum of all users' saldo where role='nasabah')
        const { data: usersData, error: usersError } = await supabase
            .from('users')
            .select('saldo')
            .eq('role', 'nasabah');

        if (usersError) throw usersError;

        const total_saldo = usersData.reduce((sum, user) => sum + (user.saldo || 0), 0);
        const total_nasabah = usersData.length;

        // 2. Total Sampah (sum of berat_kg for jenis_transaksi = 'setor')
        const { data: setorData, error: setorError } = await supabase
            .from('transactions')
            .select('berat_kg')
            .eq('jenis_transaksi', 'setor')
            .eq('status', 'disetujui'); // We only sum up approved sets

        if (setorError) throw setorError;

        const total_sampah = setorData.reduce((sum, item) => sum + (item.berat_kg || 0), 0);

        // 3. Persetujuan (transactions with status='menunggu')
        const { data: pendingData, error: pendingError } = await supabase
            .from('transactions')
            .select(`
                id,
                user_id,
                jenis_transaksi,
                jenis_sampah,
                berat_kg,
                foto_bukti,
                nominal_rp,
                metode_penarikan,
                created_at,
                users ( nama_lengkap )
            `)
            .eq('status', 'menunggu')
            .order('created_at', { ascending: true });

        if (pendingError) throw pendingError;

        const persetujuan = pendingData.map(item => ({
            id: item.id,
            user_id: item.user_id,
            nama_lengkap: item.users ? item.users.nama_lengkap : 'Unknown',
            jenis_transaksi: item.jenis_transaksi,
            jenis_sampah: item.jenis_sampah,
            berat_kg: item.berat_kg,
            foto_bukti: item.foto_bukti,
            nominal_rp: item.nominal_rp,
            metode_penarikan: item.metode_penarikan,
            created_at: item.created_at
        }));

        res.status(200).json({
            message: 'Berhasil mengambil data dashboard admin',
            data: {
                total_saldo,
                total_nasabah,
                total_sampah,
                persetujuan
            }
        });

    } catch (error) {
        console.error('Error saat mengambil dashboard admin:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getLaporanTransaksi = async (req, res) => {
    try {
        const { start_date, end_date } = req.query;
        let query = supabase
            .from('transactions')
            .select(`
                id,
                jenis_transaksi,
                jenis_sampah,
                berat_kg,
                nominal_rp,
                status,
                created_at,
                users!inner ( nama_lengkap, username )
            `)
            .order('created_at', { ascending: false });

        if (start_date) {
            query = query.gte('created_at', start_date);
        }
        if (end_date) {
            // Include up to end of the day by adding time part
            query = query.lte('created_at', end_date + 'T23:59:59.999Z');
        }

        const { data, error } = await query;
        if (error) throw error;

        const formattedData = data.map(item => ({
            id: item.id,
            nama_nasabah: item.users ? item.users.nama_lengkap || item.users.username : 'Unknown',
            jenis_transaksi: item.jenis_transaksi,
            jenis_sampah: item.jenis_sampah,
            berat_kg: item.berat_kg,
            nominal_rp: item.nominal_rp,
            status: item.status,
            created_at: item.created_at
        }));

        res.status(200).json({ data: formattedData });
    } catch (error) {
        console.error('Error saat mengambil laporan transaksi:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};

exports.getLaporanNasabah = async (req, res) => {
    try {
        const { start_date, end_date } = req.query;
        let query = supabase
            .from('users')
            .select('id, username, nama_lengkap, email, no_telepon, alamat_lengkap, saldo, created_at')
            .eq('role', 'nasabah')
            .order('created_at', { ascending: false });

        if (start_date) {
            query = query.gte('created_at', start_date);
        }
        if (end_date) {
            query = query.lte('created_at', end_date + 'T23:59:59.999Z');
        }

        const { data, error } = await query;
        if (error) throw error;

        res.status(200).json({ data });
    } catch (error) {
        console.error('Error saat mengambil laporan nasabah:', error);
        res.status(500).json({ error: 'Terjadi kesalahan pada server.' });
    }
};
